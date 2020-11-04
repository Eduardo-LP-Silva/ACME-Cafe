const mongoose = require('mongoose');
const uuid = require('uuid');
const Customer = require('./customer');
const Item = require('./item');
const Voucher = require('./voucher');

const orderSchema = new mongoose.Schema({
  _id: { type: String, default: uuid.v1 },
  customer_id: { type: String, ref: 'Customer', required: true },
  items: [ new mongoose.Schema({
    item_id: { type: String, ref: 'Item', required: true },
    quantity: {
      type: Number,
      required: true
    },
    price: {
      type: Number
    }
  })],
  vouchers: [
    { type: String, ref: 'Voucher' }
  ],
  totalPrice: { type: Number }
}, { timestamps: true })

orderSchema.pre('save', async function(next) {
  let total_price = 0;

  // Validate customer
  try {
    let customer = await Customer.findById(this.customer_id);
    if(customer == null) {
      next(new Error(`No Customer with id ${this.customer_id}`))
      return;
    }
  } catch (error) {
    next(new Error(`Error validating customer: ${error}`))
    return;
  }

  // Validate items
  let num_coffee_order = 0;
  let coffee_price = 0;
  for (let i = 0; i < this.items.length; i++) {
    const item_temp = this.items[i];
    try {
      const item = await Item.findById(item_temp.item_id);

      if(item != null){
        const available = item.get('quantity');

        if(item_temp.quantity > available){
          next(new Error(`Not enough stock for item with id ${item_temp.item_id}`))
          return;
        }

        const price = item.get('price');
        item_temp.set('price', item.get('price'));
        total_price += (price * item_temp.quantity);

        if(item.get("name") === "Coffee") {
          num_coffee_order += item_temp.quantity;
          coffee_price = price;
        }
      } else{
        next(new Error(`No item with id ${item_temp.item_id}`))
        return;
      }
    } catch (error) {
      next(new Error(`Error validating items: ${error}`))
      return;
    }
  }

  // Validate vouchers
  let usable_vouchers = [];
  let voucher_type_0 = 0;
  let voucher_type_1 = false;
  for (let i = 0; i < this.vouchers.length; i++) {
    const voucher = await Voucher.findById(this.vouchers[i])
    if(voucher && !voucher.used) {
      if(!voucher_type_1 && voucher.type === 1) {
        voucher_type_1 = true;
        usable_vouchers.push(this.vouchers[i])
      }

      if(voucher.type === 0 && voucher_type_0 < num_coffee_order) {
        usable_vouchers.push(this.vouchers[i])
        voucher_type_0++;
      }
    }
  }

  if (voucher_type_0 > 0) total_price -= (voucher_type_0 * coffee_price)
  if (voucher_type_1) total_price -= (total_price * 0.05)

  this.set('totalPrice', total_price.toFixed(2));
  this.set('vouchers', usable_vouchers)

  next();
})

orderSchema.post('save', async function (doc) {

  // Update items available quantity
  doc.items.forEach(async el => {
    try {
      const item = await Item.findById(el.item_id);
      const available = item.get('quantity');
      item.set('quantity', available - el.quantity);
      item.save();
    } catch (error) {
      console.log(error);
    }
  });

  // Update customer accumulate paid value and paid coffees
  try {
    const customer = await Customer.findById(doc.customer_id);
    const accumulated_paid_value = customer.get("accumulatedPaidValue") || 0;
    const paid_coffees = customer.get("paidCoffees") || 0;
    const new_accumulated_paid_value = (accumulated_paid_value + doc.totalPrice).toFixed(2);
    customer.set("accumulatedPaidValue", new_accumulated_paid_value); // update accumulated paid value

    let num_coffee_vouchers = 0;
    const vouchers = doc.vouchers;
    for (let i = 0; i < vouchers.length; i++) {
      const voucher = await Voucher.findById(vouchers[i])

      if(voucher.type === 0) num_coffee_vouchers++;
      voucher.set("used", true);
      voucher.save();
    }

    let new_paid_coffees = 0;
    const coffee_item = await Item.findOne({ name: "Coffee" });
    if(coffee_item) {
      const coffee_item_id = coffee_item.get("_id");
      let num_coffee_order = 0;
      doc.items.forEach(item => {
        if(item.item_id === coffee_item_id) num_coffee_order += item.quantity;
      });

      new_paid_coffees = paid_coffees + num_coffee_order - num_coffee_vouchers;
      customer.set("paidCoffees", new_paid_coffees); // update number of paid coffees

    } else {
      console.log("No Coffee item found");
    }

    // New vouchers
    for (let i = Math.floor(paid_coffees / 3); i < Math.floor(new_paid_coffees / 3); i++) {
      try {
        const new_voucher = new Voucher({ customer_id: doc.customer_id, type: 0})
        new_voucher.save();
      } catch (error) {
        console.log("Error creating voucher of type 0", error)
      }
    }
    for (let i = Math.floor(accumulated_paid_value / 100); i < Math.floor(new_accumulated_paid_value / 100); i++) {
      try {
        const new_voucher = new Voucher({ customer_id: doc.customer_id, type: 1})
        new_voucher.save();
      } catch (error) {
        console.log("Error creating voucher of type 1", error)
      }

    }
    customer.save();
  } catch (error) {
    console.log(error)
  }

})

module.exports = mongoose.model('Order', orderSchema)

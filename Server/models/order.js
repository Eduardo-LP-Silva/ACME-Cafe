const mongoose = require('mongoose');
const uuid = require('uuid');
const Customer = require('./customer');
const Item = require('./item');

const orderSchema = new mongoose.Schema({
  _id: { type: String, default: uuid.v1 },
  customer: { type: String, ref: 'Customer', required: true },
  items: [ new mongoose.Schema({
    item: { type: String, ref: 'Item', required: true },
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
  let totalPrice = 0;

  // Validate customer
  try {
    let customer = await Customer.findById(this.customer);
    if(customer == null) {
      next(new Error(`No Customer with id ${this.customer}`))
      return;
    }
  } catch (error) {
    next(new Error(`Error validating customer: ${error}`))
    return;
  }

  // Validate items
  for (let i = 0; i < this.items.length; i++) {
    const item_temp = this.items[i];
    try {
      const item = await Item.findById(item_temp.item);

      if(item != null){
        const available = item.get('quantity');

        if(item_temp.quantity > available){
          next(new Error(`Not enough stock for item with id ${item_temp.item}`))
          return;
        }

        const price = item.get('price');
        item_temp.set('price', item.get('price'));
        totalPrice += (price * item_temp.quantity);
      } else{
        next(new Error(`No item with id ${item_temp.item}`))
        return;
      }
    } catch (error) {
      next(new Error(`Error validating items: ${error}`))
      return;
    }
  }

  // TODO: Validate vouchers, remove from order the ones that were not used

  this.set('totalPrice', totalPrice);

  console.log(this)
  next();
})

orderSchema.post('save', async function (doc) {
  doc.items.forEach(async el => {
    try {
      const item = await Item.findById(el.item);
      const available = item.get('quantity');
      item.set('quantity', available - el.quantity);
      item.save();
    } catch (error) {
      console.log(error);
    }
  });

  // TODO: Remove vouchers and add new ones

})

module.exports = mongoose.model('Order', orderSchema)

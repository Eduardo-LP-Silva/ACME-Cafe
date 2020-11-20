const mongoose = require('mongoose');
const uuid = require('uuid');
const Customer = require('./customer');
const Item = require('./item');
const Voucher = require('./voucher');

const orderSchema = new mongoose.Schema({
  _id: { type: String, default: uuid.v1 },
  customerId: { type: String, ref: 'Customer', required: true },
  items: [new mongoose.Schema({
    itemId: { type: String, ref: 'Item', required: true },
    quantity: {
      type: Number,
      required: true,
    },
    price: {
      type: Number,
    },
  }, { _id: false })],
  vouchers: [
    { type: String, ref: 'Voucher' },
  ],
  receipt: { type: Boolean, default: false },
  totalPrice: { type: Number },
}, { timestamps: true });

orderSchema.pre('save', async function (next) {
  let totalPrice = 0;

  // Validate customer
  try {
    const customer = await Customer.findById(this.customerId);
    if (customer == null) {
      next(new Error(`No Customer with id ${this.customerId}`));
      return;
    }
  } catch (error) {
    next(new Error(`Error validating customer: ${error}`));
    return;
  }

  // Validate items
  let numCoffeeOrders = 0;
  let coffeePrice = 0;
  for (let i = 0; i < this.items.length; i++) {
    const itemTemp = this.items[i];
    try {
      const item = await Item.findById(itemTemp.itemId);

      if (item != null) {
        const available = item.get('quantity');

        if (itemTemp.quantity > available) {
          next(new Error(`Not enough stock for item with id ${itemTemp.itemId}`));
          return;
        }

        const price = item.get('price');
        itemTemp.set('price', item.get('price'));
        totalPrice += (price * itemTemp.quantity);

        if (item.get('name') === 'Coffee') {
          numCoffeeOrders += itemTemp.quantity;
          coffeePrice = price;
        }
      } else {
        next(new Error(`No item with id ${itemTemp.itemId}`));
        return;
      }
    } catch (error) {
      next(new Error(`Error validating items: ${error}`));
      return;
    }
  }

  // Validate vouchers
  const usableVouchers = [];
  let voucherType0 = 0;
  let voucherType1 = false;
  for (let i = 0; i < this.vouchers.length; i++) {
    const voucher = await Voucher.findById(this.vouchers[i]);
    if (voucher && !voucher.used) {
      if (!voucherType1 && voucher.type === 1) {
        voucherType1 = true;
        usableVouchers.push(this.vouchers[i]);
      }

      if (voucher.type === 0 && voucherType0 < numCoffeeOrders) {
        usableVouchers.push(this.vouchers[i]);
        voucherType0++;
      }
    }
  }

  if (voucherType0 > 0) totalPrice -= (voucherType0 * coffeePrice);
  if (voucherType1) totalPrice -= (totalPrice * 0.05);

  this.set('totalPrice', totalPrice.toFixed(2));
  this.set('vouchers', usableVouchers);

  next();
});

orderSchema.post('save', async (doc) => {
  // Update items available quantity
  doc.items.forEach(async (el) => {
    try {
      const item = await Item.findById(el.itemId);
      const available = item.get('quantity');
      item.set('quantity', available - el.quantity);
      item.save();
    } catch (error) {
      console.log(error);
    }
  });

  // Update customer accumulate paid value and paid coffees
  try {
    const customer = await Customer.findById(doc.customerId);
    const accumulatedPaidValue = customer.get('accumulatedPaidValue') || 0;
    const paidCoffees = customer.get('paidCoffees') || 0;
    const newAccumulatedPaidValue = (accumulatedPaidValue + doc.totalPrice).toFixed(2);
    customer.set('accumulatedPaidValue', newAccumulatedPaidValue); // update accumulated paid value

    let numCoffeeVouchers = 0;
    const { vouchers } = doc;
    for (let i = 0; i < vouchers.length; i++) {
      const voucher = await Voucher.findById(vouchers[i]);

      if (voucher.type === 0) numCoffeeVouchers++;
      voucher.set('used', true);
      voucher.save();
    }

    let newPaidCoffees = 0;
    const coffeeItem = await Item.findOne({ name: 'Coffee' });
    if (coffeeItem) {
      const coffeeItemId = coffeeItem.get('_id');
      let numCoffeeOrders = 0;
      doc.items.forEach((item) => {
        if (item.itemId === coffeeItemId) numCoffeeOrders += item.quantity;
      });

      newPaidCoffees = paidCoffees + numCoffeeOrders - numCoffeeVouchers;
      customer.set('paidCoffees', newPaidCoffees); // update number of paid coffees
    } else {
      console.log('No Coffee item found');
    }

    // New vouchers
    for (let i = Math.floor(paidCoffees / 3); i < Math.floor(newPaidCoffees / 3); i++) {
      try {
        const newVoucher = new Voucher({ customerId: doc.customerId, type: 0 });
        newVoucher.save();
      } catch (error) {
        console.log('Error creating voucher of type 0', error);
      }
    }
    for (let i = Math.floor(accumulatedPaidValue / 100); i < Math.floor(newAccumulatedPaidValue / 100); i++) {
      try {
        const newVoucher = new Voucher({ customerId: doc.customerId, type: 1 });
        newVoucher.save();
      } catch (error) {
        console.log('Error creating voucher of type 1', error);
      }
    }
    customer.save();
  } catch (error) {
    console.log(error);
  }
});

module.exports = mongoose.model('Order', orderSchema);

const mongoose = require('mongoose');
const uuid = require('uuid');

// TODO: Check if this model is correct
const orderSchema = new mongoose.Schema({
  _id: { type: String, default: uuid.v1 },
  customer: { type: Schema.Types.ObjectId, ref: 'Customer', required: true },
  items: [ new mongoose.Schema({
    item_id: { type: Schema.Types.ObjectId, ref: 'Item', required: true },
    quantity: {
      type: Number,
      required: true
    },
    price: {
      type: Number,
      required: true
    }
  })],
  vouchers: [
    { type: Schema.Types.ObjectId, ref: 'Voucher' }
  ],
  totalPrice: { type: Number }
}, { timestamps: true })

module.exports = mongoose.model('Order', orderSchema)

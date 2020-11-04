const mongoose = require('mongoose');
const uuid = require('uuid');

const voucherSchema = new mongoose.Schema({
  _id: { type: String, default: uuid.v1 },
  customerId: { type: String, ref: 'Customer', required: true },
  type: {
    type: Number,
    required: true,
    min: 0,
    max: 1
  },
  used: { type: Boolean, default: false }
}, { timestamps: true })

// type = 0 -> One free coffee voucher is offered to the customer whenever he consumes 3 payed coffees.
// type = 1 -> A 5% discount voucher is offered whenever the accumulated payed value of all orders from the customer surpasses a new multiple of €100.00.

module.exports = mongoose.model('Voucher', voucherSchema)

const mongoose = require('mongoose');
const uuid = require('uuid');

const voucherSchema = new mongoose.Schema({
  _id: { type: String, default: uuid.v1 },
  customer: { type: Schema.Types.ObjectId, ref: 'Customer', required: true },
  type: {
    type: Number,
    required: true,
    min: 0,
    max: 1
  },
}, { timestamps: true })

module.exports = mongoose.model('Voucher', voucherSchema)

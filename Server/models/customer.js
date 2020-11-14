const mongoose = require('mongoose');
const uuid = require('uuid');

const customerSchema = new mongoose.Schema({
  _id: { type: String, default: uuid.v1 },
  name: {
    type: String,
    required: true,
  },
  bankCardNumber: {
    type: String,
    required: true,
  },
  bankCardExpiry: {
    type: String,
    required: true,
  },
  bankCardCVV: {
    type: String,
    required: true,
  },
  nif: {
    type: String,
    required: true,
  },
  publicKey: {
    type: String,
    required: true,
  },
  paidCoffees: {
    type: Number,
    default: 0,
  },
  accumulatedPaidValue: {
    type: Number,
    default: 0.0,
  },
}, { timestamps: true });

module.exports = mongoose.model('Customer', customerSchema);

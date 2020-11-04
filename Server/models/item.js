const mongoose = require('mongoose');
const uuid = require('uuid');

const itemSchema = new mongoose.Schema({
  _id: { type: String, default: uuid.v1 },
  name: {
    type: String,
    required: true
  },
  quantity: {
    type: Number,
    required: true
  },
  price: {
    type: Number,
    required: true
  },
  icon: {
    type: String
  }
}, { timestamps: true })

itemSchema.methods.toJSON = function() {
  var obj = this.toObject()
  delete obj.__v
  delete obj.createdAt
  delete obj.updatedAt
  return obj
}

module.exports = mongoose.model('Item', itemSchema)
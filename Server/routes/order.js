const express = require('express');

const router = express.Router();
const Joi = require('joi');
const { statusCode, handleError, errorTypes } = require('../utils/errorHandler');
const Order = require('../models/order');
const { authenticateRequest } = require('../utils/authentication');
const { validateGETRequest, validatePOSTRequest } = require('../utils/validator');

const getSchema = Joi.object({
  customerId: Joi.string().required(),
  timestamp: Joi.string().required(),
  signature: Joi.string().required(),
});

const postSchema = Joi.object({
  data: Joi.object({
    customerId: Joi.string().guid().required(),
    items: Joi.array().items(
      Joi.object({
        itemId: Joi.string().guid().required(),
        quantity: Joi.number().integer().min(0).required(),
      }),
    ).min(1).required(),
    vouchers: Joi.array().items(Joi.string().guid()),
    timestamp: Joi.string().required(),
  }),
  signature: Joi.string().required(),
});

router.get('/receipt', async (req, res) => {
  if (!validateGETRequest(res, getSchema, req.query)) {
    return;
  }

  const { customerId, signature, timestamp } = req.query;
  const data = JSON.stringify({ customerId, timestamp });

  authenticateRequest(res, customerId, data, signature, timestamp).then(() => {
    Order
      .find({ customerId, receipt: false })
      .sort({ createdAt: -1 })
      .populate('items.itemId')
      .populate('vouchers')
      .exec((error, orders) => {
        if (error || orders.length === 0) {
          handleError(errorTypes.NO_RECEIPTS_FOUND, null, res);
        } else {
          Order.updateMany({ customerId, receipt: false }, { $set: { receipt: true } }, { multi: true }, () => {});
          res.json(orders);
        }
      });
  }).catch(() => {});
});

router.get('/:orderId', async (req, res) => {
  Order
    .findOne({ _id: req.params.orderId })
    .populate('items.itemId')
    .exec((err, order) => {
      if (err || order === null) {
        handleError(errorTypes.INVALID_ORDER_ID, req.params.orderId, res);
      } else {
        res.json(order);
      }
    });
});

router.post('/', async (req, res) => {
  if (!validatePOSTRequest(res, postSchema, req.body)) {
    return;
  }

  const { data, signature } = req.body;

  authenticateRequest(res, data.customerId, JSON.stringify(data), signature, data.timestamp).then(() => {
    const order = new Order(data);
    order.save().then((newOrder) => {
      res.status(statusCode.CREATED).json({ orderId: newOrder._id, totalPrice: newOrder.totalPrice, vouchers: newOrder.vouchers });
    }).catch((err) => {
      handleError(errorTypes.ERROR_CREATING_ORDER, err.message, res);
    });
  }).catch(() => {});
});

module.exports = router;

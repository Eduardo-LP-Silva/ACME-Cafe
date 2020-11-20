const express = require('express');

const router = express.Router();
const async = require('async');
const Customer = require('../models/customer');
const Item = require('../models/item');
const Order = require('../models/order');
const Voucher = require('../models/voucher');

router.get('/', (req, res) => {
  async.parallel({
    customers_count(callback) {
      Customer.countDocuments({}, callback);
    },
    items_count(callback) {
      Item.countDocuments({}, callback);
    },
    orders_count(callback) {
      Order.countDocuments({}, callback);
    },
    vouchers_used_count(callback) {
      Voucher.countDocuments({ used: true }, callback);
    },
    vouchers_unused_count(callback) {
      Voucher.countDocuments({ used: false }, callback);
    },
    gains_count(callback) {
      Order.aggregate([{
        $group: {
          _id: null,
          total: {
            $sum: '$totalPrice',
          },
        },
      }], callback);
    },
  }, (err, results) => {
    res.render('index', { title: 'ACME Café', error: err, data: results });
  });
});

router.get('/item', (req, res) => {
  Item.find({}, (error, items) => {
    res.render('items', { title: 'ACME Café', error, items });
  });
});

router.get('/order', (req, res) => {
  Order
    .find({})
    .sort({ createdAt: -1 })
    .populate('customerId')
    .exec((error, orders) => {
      res.render('orders', { title: 'ACME Café', error, orders });
    });
});

router.get('/order/:orderId', (req, res) => {
  Order
    .findOne({ _id: req.params.orderId })
    .sort({ createdAt: -1 })
    .populate('items.itemId')
    .populate('vouchers')
    .populate('customerId')
    .exec((error, order) => {
      res.render('order', { title: 'ACME Café', error, order });
    });
});

router.get('/voucher', (req, res) => {
  Voucher
    .find({})
    .sort({ createdAt: -1 })
    .populate('customerId')
    .exec((error, vouchers) => {
      res.render('vouchers', { title: 'ACME Café', error, vouchers });
    });
});

router.get('/customer', (req, res) => {
  Customer
    .find({})
    .exec((error, customers) => {
      res.render('customers', { title: 'ACME Café', error, customers });
    });
});

module.exports = router;

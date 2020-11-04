var express = require("express");
var router = express.Router();
const Joi = require('joi');
const Order = require('../models/order');

router.get("/receipt", async function (req, res) {

  if(!req.query.customerId){
    res.status(400).send(`Missing customer`)
    return;
  }
  // TODO: Delete orders?
  Order.
    find({ customerId: req.query.customerId }).
    populate('items.itemId').
    exec(function (err, orders) {
      if (err){
        console.log(err)
        res.status(404).send(`No orders found`)
      }
      else{
        res.json(orders)
      }
   });

});

router.get("/:orderId", async function (req, res) {

  Order.
    findOne({ _id: req.params.orderId }).
    populate('items.itemId').
    exec(function (err, order) {
      if (err){
        console.log(err)
        res.status(404).send(`No order with id ${req.params.orderId} found`)
      }
      else{
        res.json(order)
      }
    });

});

router.post("/", async function (req, res) {

  if (!validatePOSTRequest(req.body)) {
    res.status(400).send("Request body is wrong");
    return;
  }

  const order = new Order(req.body);
  order.save().then(val => {
    res.status(201).json({ "orderId": val._id, "totalPrice": val.totalPrice, "vouchers": val.vouchers })
  }).catch(err => {
    res.status(500).send(err.message)
  })

});

function validatePOSTRequest(request) {

  try {
    const schema = Joi.object({
      customerId: Joi.string().guid().required(),
      items: Joi.array().items(
        Joi.object({
          itemId: Joi.string().guid().required(),
          quantity: Joi.number().integer().min(0).required()
        })
      ).min(1).required(),
      vouchers: Joi.array().items(Joi.string().guid().required()),
    });

    const result = schema.validate(request)

    if (result.error) {
      console.log(result.error)
      return false;
    }
  } catch (error) {
    console.log(error)
    return false
  }

  return true
}

module.exports = router;

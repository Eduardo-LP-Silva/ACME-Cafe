var express = require("express");
var router = express.Router();
const Joi = require('joi');
const Order = require('../models/order');

router.get("/:order_id", async function (req, res) {

  Order.
    findOne({ _id: req.params.order_id }).
    populate('items.item').
    exec(function (err, order) {
      if (err){
        console.log(err)
        res.status(404).send(`No order with id ${req.params.order_id} found`)
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
    res.status(201).json({ "order_id": val._id, "totalPrice": val.totalPrice, "vouchers": val.vouchers })
  }).catch(err => {
    res.status(500).send(err.message)
  })

});

function validatePOSTRequest(request) {

  try {
    const schema = Joi.object({
      customer_id: Joi.string().guid().required(),
      items: Joi.array().items(
        Joi.object({
          item_id: Joi.string().guid().required(),
          quantity: Joi.number().required()
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

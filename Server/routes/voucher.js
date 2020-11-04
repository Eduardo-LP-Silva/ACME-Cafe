var express = require("express");
var router = express.Router();
const Joi = require('joi');
const Voucher = require('../models/voucher');

router.get("/:voucher_id", async function (req, res) {

  Voucher.findById(req.params.voucher_id, function (err, val) {
    if (err){
      console.log(err)
      res.status(404).send(`No voucher with id ${req.params.voucher_id} found`)
    }
    else{
      res.json(val)
    }
  });

});

router.get("/", async function (req, res) {

  if(!req.query.customer_id){
    res.status(400).send(`Missing customer`)
    return;
  }

  Voucher.find({ customer_id: req.query.customer_id, used: false }, function (err, val) {
    if (err){
      console.log(err)
      res.status(404).send(`No vouchers found`)
    }
    else{
      res.json(val)
    }
  });

});

router.post("/", async function (req, res) {

  if (!validatePOSTRequest(req.body)) {
    res.status(400).send("Request body is wrong");
    return;
  }

  const voucher = new Voucher(req.body);
  voucher.save().then(val => {
    res.status(201).json({ "voucher_id": val._id })
  }).catch(err => {
    console.log(err)
    res.status(500).send("Error creating voucher")
  })

});

function validatePOSTRequest(request) {

  try {
    const schema = Joi.object({
      customer_id: Joi.string().required(),
      type: Joi.number().integer().min(0).max(1).required()
    });

    const result = schema.validate(request)
    if (result.error) {
      return false;
    }
  } catch (error) {
    return false
  }

  return true
}


module.exports = router;

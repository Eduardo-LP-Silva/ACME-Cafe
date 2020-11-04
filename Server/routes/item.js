var express = require("express");
var router = express.Router();
const Joi = require('joi');
const Item = require('../models/item');

router.get("/:itemId", async function (req, res) {

  Item.findById(req.params.itemId, function (err, val) {
    if (err){
      console.log(err)
      res.status(404).send(`No item with id ${req.params.itemId} found`)
    }
    else{
      res.json(val)
    }
  });

});

router.get("/", async function (req, res) {

  Item.find({}, function (err, val) {
    if (err){
      console.log(err)
      res.status(404).send(`No items found`)
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

  const item = new Item(req.body);
  item.save().then(val => {
    res.status(201).json({ "itemId": val._id })
  }).catch(err => {
    console.log(err)
    res.status(500).send("Error creating item")
  })

});

function validatePOSTRequest(request) {

  try {
    const schema = Joi.object({
      name: Joi.string().required(),
      quantity: Joi.number().integer().required(),
      price: Joi.number().required(),
      icon: Joi.string()
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

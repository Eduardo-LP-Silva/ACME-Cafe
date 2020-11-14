const express = require('express');

const router = express.Router();
const Joi = require('joi');
const Item = require('../models/item');
const { validatePOSTRequest } = require('../utils/validator');

const postSchema = Joi.object({
  name: Joi.string().required(),
  quantity: Joi.number().integer().min(0).required(),
  price: Joi.number().min(0).required(),
  icon: Joi.string(),
});

router.get('/:itemId', async (req, res) => {
  Item.findById(req.params.itemId, (err, item) => {
    if (err || item === null) {
      res.status(404).send(`No item with id ${req.params.itemId} found`);
    } else {
      res.json(item);
    }
  });
});

router.get('/', async (req, res) => {
  Item.find({ quantity: { $gt: 0 } }, (err, items) => {
    if (err || items.length === 0) {
      res.status(404).send('No items found');
    } else {
      res.json(items);
    }
  });
});

router.post('/', async (req, res) => {
  if (!validatePOSTRequest(res, postSchema, req.body)) {
    return;
  }

  const item = new Item(req.body);
  item.save().then((val) => {
    res.status(201).json({ itemId: val._id });
  }).catch((err) => {
    res.status(500).send(`Error creating item: ${err}`);
  });
});

module.exports = router;

const express = require('express');

const router = express.Router();
const Joi = require('joi');
const { statusCode, handleError, errorTypes } = require('../utils/errorHandler');
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
      handleError(errorTypes.INVALID_ITEM_ID, req.params.itemId, res);
    } else {
      res.json(item);
    }
  });
});

router.get('/', async (req, res) => {
  Item.find({ quantity: { $gt: 0 } }, (err, items) => {
    if (err || items.length === 0) {
      handleError(errorTypes.NO_ITEMS_FOUND, null, res);
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
    res.status(statusCode.CREATED).json({ itemId: val._id });
  }).catch((err) => {
    handleError(errorTypes.ERROR_CREATING_ITEM, err.message, res);
  });
});

module.exports = router;

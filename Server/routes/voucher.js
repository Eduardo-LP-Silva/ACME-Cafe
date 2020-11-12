const express = require('express');

const router = express.Router();
const Joi = require('joi');
const Voucher = require('../models/voucher');
const { authenticateRequest } = require('../utils/authentication');
const { validateGETRequest, validatePOSTRequest } = require('../utils/validator');

const getSchema = Joi.object({
  customerId: Joi.string().required(),
  timestamp: Joi.string().required(),
  signature: Joi.string().required(),
});

const postSchema = Joi.object({
  customerId: Joi.string().guid().required(),
  type: Joi.number().integer().min(0).max(1)
    .required(),
});

router.get('/:voucherId', async (req, res) => {
  Voucher.findById(req.params.voucherId, (err, voucher) => {
    if (err || voucher === null) {
      console.log(err);
      res.status(404).send(`No voucher with id ${req.params.voucherId} found`);
    } else {
      res.json(voucher);
    }
  });
});

router.get('/', async (req, res) => {
  if (!validateGETRequest(res, getSchema, req.query)) {
    return;
  }

  const { customerId, signature, timestamp } = req.query;
  const data = JSON.stringify({ customerId, timestamp });

  authenticateRequest(res, customerId, data, signature, timestamp).then(() => {
    Voucher.find({ customerId, used: false }, (error, vouchers) => {
      if (error || vouchers.length === 0) {
        res.status(404).send('No vouchers found');
      } else {
        res.json(vouchers);
      }
    });
  }).catch(() => {});
});

router.post('/', async (req, res) => {
  if (!validatePOSTRequest(res, postSchema, req.body)) {
    return;
  }

  const voucher = new Voucher(req.body);
  voucher.save().then((val) => {
    res.status(201).json({ voucherId: val._id });
  }).catch((err) => {
    res.status(500).send(`Error creating voucher: ${err}`);
  });
});

module.exports = router;

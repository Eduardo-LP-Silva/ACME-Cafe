const express = require('express');

const router = express.Router();
const Joi = require('joi');
const Customer = require('../models/customer');
const { validatePOSTRequest } = require('../utils/validator');

const postSchema = Joi.object({
  name: Joi.string().required(),
  bankCardNumber: Joi.string().length(16).pattern(/^[0-9]+$/).required(),
  bankCardExpiry: Joi.string().length(4).pattern(/^[0-9]+$/).required(),
  bankCardCVV: Joi.string().length(3).pattern(/^[0-9]+$/).required(),
  nif: Joi.string().length(9).pattern(/^[0-9]+$/).required(),
  publicKey: Joi.string().required(),
});

router.get('/:customerId', async (req, res) => {
  Customer.findById(req.params.customerId, (err, customer) => {
    if (err || customer === null) {
      res.status(404).send(`No customer with id ${req.params.customerId} found`);
    } else {
      res.json(customer);
    }
  });
});

router.post('/', async (req, res) => {
  if (!validatePOSTRequest(res, postSchema, req.body)) {
    return;
  }

  const customer = new Customer(req.body);
  customer.save().then((val) => {
    res.status(201).json({ customerId: val._id });
  }).catch((err) => {
    res.status(500).send(`Error creating customer: ${err}`);
  });
});

module.exports = router;

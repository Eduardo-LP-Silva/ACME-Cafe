const express = require('express');

const router = express.Router();
const Joi = require('joi');
const { statusCode, handleError, errorTypes } = require('../utils/errorHandler');
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
      handleError(errorTypes.INVALID_CUSTOMER_ID, req.params.customerId, res);
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
    res.status(statusCode.CREATED).json({ customerId: val._id });
  }).catch((err) => {
    handleError(errorTypes.ERROR_CREATING_CUSTOMER, err.message, res);
  });
});

module.exports = router;

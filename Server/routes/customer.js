var express = require("express");
var router = express.Router();
const Joi = require('joi');
const Customer = require('../models/customer');

router.get("/:customer_id", async function (req, res) {

  if (!validateGETQuery(req.query)) {
    res.status(400).send("Missing offset and/or limit query params in request!");
    return;
  }

  let paper = await Paper.findAndCountAll(
    {
      where: { paper_id: req.params.paper_id },
      attributes: ['id', 'paper_id', 'message', 'author', 'createdAt'],
      order: [
        ['createdAt', 'DESC']
      ],
      distinct: true,
      limit: req.query.limit,
      offset: req.query.offset
    })
  if (paper.length === 0) {
    res.status(404).send(`No curation history for paper with id ${req.params.paper_id}`)
    return
  }
  res.json(paper);
});

router.post("/", async function (req, res) {

  if (!validatePOSTRequest(req.body)) {
    res.status(400).send("Request body is wrong");
    return;
  }

  const customer = new Customer(req.body);
  customer.save().then(val => {
    res.status(201).json(val)
  }).catch(err => {
    console.log(err)
    res.status(500).send("Error creating customer")
  })

});

function validatePOSTRequest(request) {

  try {
    const schema = Joi.object({
      name: Joi.string().required(),
      bankCardNumber: Joi.string().length(16).pattern(/^[0-9]+$/).required(),
      bankCardExpiry: Joi.string().length(4).pattern(/^[0-9]+$/).required(),
      bankCardCVV: Joi.string().length(3).pattern(/^[0-9]+$/).required(),
      nif: Joi.string().length(9).pattern(/^[0-9]+$/).required(),
      publicKey: Joi.string().required() //TODO: public key validation
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

const crypto = require('crypto');
const Customer = require('../models/customer');

const ALGORITHM = 'sha256WithRSAEncryption';
const SIGNATURE_FORMAT = 'hex';
const REQUEST_TOLERANCE = 5; // seconds

const validateSignature = (certificate, data, signature) => {
  try {
    const verify = crypto.createVerify(ALGORITHM);
    verify.update(data);
    return verify.verify(certificate, signature, SIGNATURE_FORMAT);
  } catch (error) {
    return false;
  }
};

const authenticateRequest = (res, customerId, data, signature, timestamp) => new Promise((resolve, reject) => {
  Customer.findById(customerId, (err, customer) => {
    if (err || customer === null) {
      res.status(404).send(`Invalid customerId ${customerId}`);
      reject();
    } else {
      const certificate = `-----BEGIN CERTIFICATE-----\n${customer.publicKey}\n-----END CERTIFICATE-----`;

      if (!validateSignature(certificate, data, signature)) {
        res.status(400).send('Invalid request: Signature does not match data');
        reject();
      }

      // TODO: UNCOMMENT TO VERIFY REQUESTS TOLERANCE
      // const currentTime = Math.floor(Date.now() / 1000);
      // const requestTime = parseInt(timestamp, 10);
      // if ((requestTime + REQUEST_TOLERANCE < currentTime) || (requestTime > currentTime)) {
      //   res.status(400).send('Invalid request: timestamp does not fulfil the requests tolerance');
      //   reject();
      // }

      resolve();
    }
  });
});

module.exports.authenticateRequest = authenticateRequest;

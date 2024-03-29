const crypto = require('crypto');
const Customer = require('../models/customer');
const { handleError, errorTypes } = require('./errorHandler');

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
      handleError(errorTypes.INVALID_CUSTOMER_ID, customerId, res);
      reject();
    } else {
      const certificate = `-----BEGIN CERTIFICATE-----\n${customer.publicKey}\n-----END CERTIFICATE-----`;

      if (!validateSignature(certificate, data, signature)) {
        handleError(errorTypes.INVALID_SIGNATURE, null, res);
        reject();
      }

      const currentTime = Math.floor(Date.now() / 1000);
      const requestTime = parseInt(timestamp, 10);
      if ((requestTime + REQUEST_TOLERANCE < currentTime) || (requestTime > currentTime)) {
        handleError(errorTypes.INVALID_TIMESTAMP, null, res);
        reject();
      }

      resolve();
    }
  });
});

module.exports.authenticateRequest = authenticateRequest;

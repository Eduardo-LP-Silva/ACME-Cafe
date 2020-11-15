const { handleError, errorTypes } = require('./errorHandler');

function validateGETRequest(res, schema, query) {
  try {
    const result = schema.validate(query);

    if (result.error) {
      handleError(errorTypes.INVALID_QUERY, result.error.message, res);
      return false;
    }
  } catch (error) {
    handleError(errorTypes.INVALID_QUERY, error.message, res);
    return false;
  }

  return true;
}

function validatePOSTRequest(res, schema, body) {
  try {
    const result = schema.validate(body);

    if (result.error) {
      handleError(errorTypes.INVALID_BODY, result.error.message, res);
      return false;
    }
  } catch (error) {
    handleError(errorTypes.INVALID_BODY, error.message, res);
    return false;
  }

  return true;
}

module.exports = {
  validateGETRequest,
  validatePOSTRequest,
};

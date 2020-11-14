function validateGETRequest(res, schema, query) {
  try {
    const result = schema.validate(query);

    if (result.error) {
      res.status(400).send(`Invalid query: ${result.error.message}`);
      return false;
    }
  } catch (error) {
    res.status(400).send(`Invalid query: ${error.message}`);
    return false;
  }

  return true;
}

function validatePOSTRequest(res, schema, body) {
  try {
    const result = schema.validate(body);

    if (result.error) {
      res.status(400).send(`Invalid body: ${result.error.message}`);
      return false;
    }
  } catch (error) {
    res.status(400).send(`Invalid body: ${error.message}`);
    return false;
  }

  return true;
}

module.exports = {
  validateGETRequest,
  validatePOSTRequest,
};

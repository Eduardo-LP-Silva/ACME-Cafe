const statusCode = {
  OK: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  NOT_FOUND: 404,
  INTERNAL_ERROR: 500,
};

const errorTypes = {
  // Customer
  INVALID_CUSTOMER_ID: 'INVALID_CUSTOMER_ID',
  ERROR_CREATING_CUSTOMER: 'ERROR_CREATING_CUSTOMER',
  // Item
  INVALID_ITEM_ID: 'INVALID_ITEM_ID',
  NO_ITEMS_FOUND: 'NO_ITEMS_FOUND',
  ERROR_CREATING_ITEM: 'ERROR_CREATING_ITEM',
  // Order
  NO_RECEIPTS_FOUND: 'NO_RECEIPTS_FOUND',
  INVALID_ORDER_ID: 'INVALID_ORDER_ID',
  ERROR_CREATING_ORDER: 'ERROR_CREATING_ORDER',
  // Voucher
  INVALID_VOUCHER_ID: 'INVALID_VOUCHER_ID',
  NO_VOUCHERS_FOUND: 'NO_VOUCHERS_FOUND',
  ERROR_CREATING_VOUCHERS: 'ERROR_CREATING_VOUCHERS',
  // Authentication
  INVALID_SIGNATURE: 'INVALID_SIGNATURE',
  INVALID_TIMESTAMP: 'INVALID_TIMESTAMP',
  // Validator
  INVALID_QUERY: 'INVALID_QUERY',
  INVALID_BODY: 'INVALID_BODY',
  // Internal
  INTERNAL_ERROR: 'INTERNAL_ERROR',
};

const handleError = (type, msg, res) => {
  let errorStatusCode;
  let errorMessage;
  switch (type) {
    case 'INVALID_CUSTOMER_ID':
      errorStatusCode = statusCode.NOT_FOUND;
      errorMessage = `No customer with id ${msg} found`;
      break;
    case 'ERROR_CREATING_CUSTOMER':
      errorStatusCode = statusCode.INTERNAL_ERROR;
      errorMessage = msg;
      break;
    case 'INVALID_ITEM_ID':
      errorStatusCode = statusCode.NOT_FOUND;
      errorMessage = `No item with id ${msg} found`;
      break;
    case 'NO_ITEMS_FOUND':
      errorStatusCode = statusCode.NOT_FOUND;
      errorMessage = 'No items found';
      break;
    case 'ERROR_CREATING_ITEM':
      errorStatusCode = statusCode.INTERNAL_ERROR;
      errorMessage = msg;
      break;
    case 'NO_RECEIPTS_FOUND':
      errorStatusCode = statusCode.NOT_FOUND;
      errorMessage = 'No receipts found';
      break;
    case 'INVALID_ORDER_ID':
      errorStatusCode = statusCode.NOT_FOUND;
      errorMessage = `No order with id ${msg} found`;
      break;
    case 'ERROR_CREATING_ORDER':
      errorStatusCode = statusCode.INTERNAL_ERROR;
      errorMessage = msg;
      break;
    case 'INVALID_VOUCHER_ID':
      errorStatusCode = statusCode.NOT_FOUND;
      errorMessage = `No voucher with id ${msg} found`;
      break;
    case 'NO_VOUCHERS_FOUND':
      errorStatusCode = statusCode.NOT_FOUND;
      errorMessage = 'No vouchers found';
      break;
    case 'ERROR_CREATING_VOUCHERS':
      errorStatusCode = statusCode.INTERNAL_ERROR;
      errorMessage = msg;
      break;
    case 'INVALID_SIGNATURE':
      errorStatusCode = statusCode.BAD_REQUEST;
      errorMessage = 'Signature does not match data';
      break;
    case 'INVALID_TIMESTAMP':
      errorStatusCode = statusCode.BAD_REQUEST;
      errorMessage = 'Timestamp provided does not fulfil the requests tolerance';
      break;
    case 'INVALID_QUERY':
      errorStatusCode = statusCode.BAD_REQUEST;
      errorMessage = msg;
      break;
    case 'INVALID_BODY':
      errorStatusCode = statusCode.BAD_REQUEST;
      errorMessage = msg;
      break;
    default:
      errorStatusCode = statusCode.INTERNAL_ERROR;
      errorMessage = 'Something wrong happened in the server';
      break;
  }

  res.status(errorStatusCode).json({ type, message: errorMessage });
};

module.exports = {
  statusCode, errorTypes, handleError,
};

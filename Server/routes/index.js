const express = require('express');

const router = express.Router();

router.get('/', (req, res) => {
  res.send('ACME Café Server welcomes you!');
});

module.exports = router;

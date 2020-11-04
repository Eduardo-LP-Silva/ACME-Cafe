const express = require("express");
const router = express.Router();

router.get("/", function(req, res) {
  res.send('ACME-Cafe Server welcomes you!')
});

module.exports = router;

require('dotenv').config()
const express = require('express')
const app = express()
const mongoose = require('mongoose');
const bodyParser  = require('body-parser');
const port = 8080

const indexRouter = require("./routes/index");
const customerRouter = require("./routes/customer");
const itemRouter = require("./routes/item");

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use("/", indexRouter);
app.use("/customer", customerRouter);
app.use("/item", itemRouter);

mongoose.connect(`mongodb://${process.env.DB_USER}:${process.env.DB_PASS}@${process.env.DB_HOST}:27017/${process.env.DB_NAME}`, {useNewUrlParser: true, useUnifiedTopology: true });

mongoose.connection.once('open',function(){
  console.log('Database connected Successfully');
}).on('error',function(err){
  console.log('Database error', err);
})

app.listen(port, () => {
  console.log(`Server listening at http://localhost:${port}`)
})

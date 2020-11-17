require('dotenv').config();
const express = require('express');

const app = express();
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const path = require('path');

const port = 8080;

const indexRouter = require('./routes/index');
const customerRouter = require('./routes/customer');
const itemRouter = require('./routes/item');
const orderRouter = require('./routes/order');
const voucherRouter = require('./routes/voucher');
const adminRouter = require('./routes/admin');

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'pug');
app.use(express.static(path.join(__dirname, 'public')));
app.use('/', indexRouter);
app.use('/customer', customerRouter);
app.use('/item', itemRouter);
app.use('/order', orderRouter);
app.use('/voucher', voucherRouter);
app.use('/admin', adminRouter);

function hasEnv(envVar) {
  return envVar !== undefined && envVar !== '';
}

const hasMongoUsername = hasEnv(process.env.DB_USER) && hasEnv(process.env.DB_PASS !== undefined);
const mongoURL = `mongodb://${hasMongoUsername ? `${process.env.DB_USER}:${process.env.DB_PASS}@` : ''}${process.env.DB_HOST}:27017/${process.env.DB_NAME}`;
mongoose.connect(mongoURL, { useNewUrlParser: true, useUnifiedTopology: true });

mongoose.connection.once('open', () => {
  console.log('Database connected Successfully');
}).on('error', (err) => {
  console.log('Database error', err);
});

app.listen(port, () => {
  console.log(`Server listening at http://localhost:${port}`);
});

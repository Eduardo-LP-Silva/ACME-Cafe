## Development

`npm run start-dev`

## Production

`npm start`

## Seed Database

`./node_modules/mongo-seeding-cli/bin/seed.js -u mongodb://admin:admin@localhost:27017/cmov ./data`

* Note: replace with your mongodb address and authentication credentials


## Server To Do

### Registration
- [x] Make a registration (name, credit/debit bank card and NIF (fiscal identification number – 9 digits) and a public key (in the form of a certificate, cryptographic RSA key pair)).
- [x] If operation succeeds, return a unique ‘user id’ (UUID)

### Available Items
- [x] Get available items in the cafeterias (always the same).
- [x] Save update date because the app needs to know.
- [x] MUST HAVE COFFEE.

### Vouchers

- [x] Get available emitted vouchers to that customer ()
- [x] Validate and emit a voucher

#### Voucher generation
- [x] One free coffee voucher is offered to the customer whenever he consumes 3 payed coffees (so the server should track these purchases).
- [x] A discount voucher is offered whenever the accumulated payed value of all orders from the customer surpasses a new multiple of €100.00 (the server should take note, for each customer, of this total). The discount voucher offers a 5% discount in the total of a new order.
- [x] A voucher has a unique serial number (uuid), and a type (1 byte) (there are only two types).
- [x] When they are generated the server associates them to the owner’s ‘user id’.


### Validate Order
- [x] The user is identified (uuid), the signature verified (with the public key), and the validity of the vouchers is checked.
- [x] (consider that the server always succeeds in performing that payment).
- [x] Validation result - The server replies with the validation result, accepted vouchers and total value payed.
- [x] The server also generates a receipt and keep it associated with the user.
- [x] Non-applicable vouchers are ignored but can be retrieved again.

### Authentication
- [ ] Signed user-id for authentication
- [ ] To avoid capture and replay attacks a timestamp should also be included in the authentication.
- [ ] The server should define a small tolerance for verification

### Receipts
- [x] The user can also ask for the receipts of previous purchases. If the server has them, they are transmitted to the user (only once) and deleted from the server.

### New functionalities
- [x] emission of receipts electronically by the server
- [ ] consultation of past transactions (in the server or locally)
- [ ] gains obtained so far
- [ ] ...

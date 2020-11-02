## Server To Do


### Registration
- [x] Make a registration (name, credit/debit bank card and NIF (fiscal identification number – 9 digits) and a public key (in the form of a certificate, cryptographic RSA key pair)).
-[x] If operation succeeds, return a unique ‘user id’ (UUID)

### Available Items
- [ ] Get available items in the cafeterias (always the same).
- [ ] Save update date because the app needs to know.
- [ ] MUST HAVE COFFEE.

### Vouchers

- [ ] Get available emitted vouchers to that customer ()
- [ ] Validate and emit a voucher

#### Voucher generation
- [ ] One free coffee voucher is offered to the customer whenever he consumes 3 payed coffees (so the server should track these purchases).
- [ ] A discount voucher is offered whenever the accumulated payed value of all orders from the customer surpasses a new multiple of €100.00 (the server should take note, for each customer, of this total). The discount voucher offers a 5% discount in the total of a new order.
- [ ] A voucher has a unique serial number (uuid), and a type (1 byte) (there are only two types).
- [ ] When they are generated the server associates them to the owner’s ‘user id’.


### Validate Order
- [ ] The user is identified (uuid), the signature verified (with the public key), and the validity of the vouchers is checked.
- [ ] (consider that the server always succeeds in performing that payment).
- [ ] Validation result - The server replies with the validation result, accepted vouchers and total value payed.
- [ ] The server also generates a receipt and keep it associated with the user.
- [ ] Non-applicable vouchers are ignored but can be retrieved again.

### Authentication
- [ ] Signed user-id for authentication
- [ ] To avoid capture and replay attacks a timestamp should also be included in the authentication.
- [ ] The server should define a small tolerance for verification

### Receipts
- [ ] The user can also ask for the receipts of previous purchases. If the server has them, they are transmitted to the user (only once) and deleted from the server.

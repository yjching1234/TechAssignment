## Table of contents

- [User module](#user-module)
- [Transaction](#transaction)
  - [Make Transaction](#make-transaction)
  - [Manage Transaction](#manage-transaction)



## User Module

- [x] Register
  - [ ] Existing Username, email, contact
- [ ] Login
  - [ ] User status is Invalid
- [ ] Logout

## Transaction

### Make Transaction

#### Global

- amount cannot less than 0
- description cannot be empty
- transaction type must be (1 - DEPOSIT, 2 WITHDRAW, 3 - TRANSFER, 4 - DUITNOW)
- account status must be ACTIVE

#### Deposit
- NA

#### WITHDRAW

- the withdrawal amount must not greater than account balance.

#### TRANSFER
- Target account must exist and account status is active
- The amount will store to temp balance

#### DUITNOW
- Target account must exist and account status is active

### Manage Transaction

#### Transaction Cancellation by User

- transaction id should exist and mandatory.
- transaction status allow (4 - CANCELED) only
- remarks are mandatory
- can do cancellation for transaction status PENDING only.





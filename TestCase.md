## Table of contents

- [User module](#user-module)
- [Transaction](#transaction)
  - [Make Transaction](#make-transaction)
  - [Manage Transaction](#manage-transaction)



## User Module

#### Register

- Auto create Account Also

#### Login
- will generate JWT Token

#### User profile

##### Edit user (USER)

- Edit own profile

##### Edit user (STAFF)

- STAFF can edit USER only
- ADMIN can edit STAFF and USER (Not SUPER ADMIN "admin" hardcoded)


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

#### Get transaction history

- User will get own transaction list only, STAFF or ADMIN can select all or by other user id
- Can filter by (transaction id, status, type, period, sort A - ASD, D -DSN by transaction date time, page)

#### Transaction Cancellation by User

- transaction id should exist and mandatory.
- transaction status allow (4 - CANCELED) only
- remarks are mandatory
- can do cancellation for transaction status PENDING only.

#### Transaction Approval/Reject by employee

- transaction id should exist and mandatory.
- transaction status allow (3 - COMPLETED, 4 - CANCELED) only
- remarks are mandatory
- can do cancellation for transaction status PENDING only.






# openblockchain: Spark

Spark is one of the microservices which comprise the openblockchain application.

### Architecture

The project is managed through the [openblockchain](https://github.com/open-blockchain/openblockchain)

The project is split into several services:

- **Cassandra** persists the data: blocks, transactions, and visualisations (analysed data).
- **Bitcoin** is used to connect to the Bitcoin blockchain. It's a simple Bitcoin Core node whose role is to index all the blocks and transactions and make them consumable through a HTTP JSON RPC interface.
- [Scanner](https://github.com/open-blockchain/scanner) connects to the bitcoin service through its APIs and stores all the blocks and transactions in the Cassandra database.
- [Spark](https://github.com/open-blockchain/spark) analyses the Bitcoin blockchain data stored in Cassandra.
- [API](https://github.com/open-blockchain/node-api) is a REST interface that allows clients to consume the data stored in Cassandra.
- [Frontend](https://github.com/open-blockchain/frontend) is the web app used to explore the blockchain and visualise analysed data.

Each service contains 1 or more containers and can be scaled independently from each other.

## License

Copyright (C) 2016 Dan Hassan

Designed, developed and maintained by Dan Hassan <daniel.san@dyne.org>

```
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```

### Dependencies

[Spark Service](https://github.com/open-blockchain/spark) dependencies
https://github.com/apache/spark
Apache License, Version 2.0, January 2004, http://www.apache.org/licenses/

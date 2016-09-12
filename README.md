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

# Peer-to-Peer with Centralized Index (P2P-CI) System

> A P2P-CI system in which peers can download RFC files from another active peer with TCP connection.

Internet protocol standards are defined in documents called “Requests for Comments” (RFCs).

There is a centralized server which keeps information about active peers and maintains an index of RFCs available at each active peer.

When peers join the P2P-CI system, it will register itself at the server and provide information about RFCs that it makes available to other peers.

When a peer wishes to download a specific RFC, it will get a list of other peers who have that RFC by requiring the central server. 

The TCP connection between a peer and central server is long-lived, and the TCP connection between peers is short-lived.

## Features

*  Join/Leave P2P System
*  Add RFCs to central server
*  Look up a RFC from central server
*  List all RFCs in P2P System
*  Download a RFC from other peer

## How to Run
1. Entry point

   Server.jar is under `/jar/Server/`   

   Client.jar is under `/jar/Client/`

2. Run Server:

   `$java -jar Server.jar`

3. Run Client:

   `$java -jar Client.jar`

## RFC files

* Local RFCs are stored under `/localRFCs/`
* RFCs downloaded from other peers are stored under `/downloadedRFCs/`
* The format of RFC name is `rfcxxx.txt`

## License

MIT
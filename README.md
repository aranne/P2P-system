# Peer-to-Peer with Centralized Index (P2P-CI) System for Downloading RFCs
Internet protocol standards are defined in documents called “Requests for Comments” (RFCs).

A P2P-CI system in which peers who wish to download an RFC that they do not have in their hard drive, may download it from another active peer who does. All communication among peers or between a peer and the server will take place over TCP.

* There is a centralized server, running on a well-known host and listening on a well-known port, which keeps information about the active peers and maintains an index of the RFCs available at each active peer.
* When a peer decides to join the P2P-CI system, it opens a connection to the server to register itself and provide information about the RFCs that it makes available to other peers. This connection remains open as long as the peer remains active; the peer closes the connection when it leaves the system (becomes inactive).
* Since the server may have connections open to multiple peers simultaneously, it spawns a new thread to handle the communication to each new peer.
* When a peer wishes to download a specific RFC, it provides the RFC number to the server over the open connection, and in response the server provides the peer with a list of other peers who have the RFC; if no such active peer exists, an appropriate message is transmitted to the requesting peer. Additionally, each peer may at any point query the server to obtain the whole index of RFCs available at all other active peers.
* Each peer runs a *upload server* process that listens on a port *specific to the peer*; in other words, this port is not known in advance to any of the peers. When a peer A needs to download an RFC from a peer B, it opens a connection to the upload port of peer B, provides the RFC number to B, and B responds by sending the (text) file containing the RFC to A over the same connection; once the file transmission is completed, the connection is closed.

## How to run
1. Go to `/jar` directory
* Client.jar is in `/Client` directory
* Server.jar is in `/Server` directory

2. Run Server:
`$java -jar Server.jar`

3. Run Client1:
`$java -jar Client.jar`

4. Run Client2:
`$java -jar Client.jar`

## RFC files
* Local RFC files is stored in `/UploadRFCs` directory
* RFC downloaded from other peers will be in `/DownloadRFCs` directory
* The name of RFC is `rfcxxx.txt`

## Join P2P System
1. Choose option 1
2. Input server hostname (input 127.0.0.1 if running on localhost)
3. Input uploading port 

## Add RFC to P2P System
1. Choose option 2
2. Input RFC number (must be same as the number of RFC file in `UploadRFCs` directory)
3. Input RFC title 

## Lookup a RFC in P2P System
1. Choose option 3
2. Input RFC number
3. Input RFC title

## List all RFCs in P2P System
1. Choose option 4

## Download a RFC from other peers
1. Choose option 5
2. Input RFC number (must exist in P2P server)
3. Input other peer's hostname (input 127.0.0.1 if running on localhost)
4. Input other peer's uploading port number

## Leave P2P system
1. Choose option 6


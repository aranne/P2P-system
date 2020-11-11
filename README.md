# P2P-system

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


chatroom
========
[![Build Status](https://travis-ci.com/nstebbins/chatroom.svg?token=wq8kpkt8TaRN17x6BNtj&branch=master)](https://travis-ci.com/nstebbins/chatroom)

a multi-threaded chatroom written in Java

commands supported
------------------
* `whoelse`: see who else is currently in the chatroom
* `broadcast`: sends messages to all online clients
* `message`: sends private message to client
* `help`: gives an overview of commands available

to run chatroom's server
-------------
to build the `chatroom` server image:
```r
docker build -t chatroom-server . -f server.Dockerfile
```
and to run the container (also named `chatroom-server`):
```r
docker run -p 4000:4000 --name=chatroom-server chatroom-server
```

to run chatroom's client
-------------
to build the `chatroom` client image:
```r
docker build -t chatroom-client . -f client.Dockerfile
```
and to run the container (also named `chatroom-client`):
```r
docker run -it --name=chatroom-client --network=host chatroom-client
```

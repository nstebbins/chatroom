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
to build the `chatroom` image:
```r
docker build -t chatroom .
```
and to run the container (also named `chatroom`):
```r
docker run -p 4000:4000 --name chatroom chatroom
```

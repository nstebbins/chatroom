chatroom
========
[![Build Status](https://travis-ci.com/nstebbins/chatroom.svg?token=wq8kpkt8TaRN17x6BNtj&branch=master)](https://travis-ci.com/nstebbins/chatroom)

`chatroom` is a multi-threaded chatroom written in Java and uses the client-server model. `chatroom` provides a command-line interface to communicate with clients and obtain information about them. 

commands supported
------------------
* `whoelse`: see who else is currently in the chatroom
* `broadcast`: sends messages to all online clients
* `message`: sends private message to client
* `help`: gives an overview of commands available

built with
-------------
* [maven](https://maven.apache.org/) - dependency management

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

license
-------------
this project is licensed under the MIT License - see [LICENSE](https://github.com/nstebbins/chatroom/blob/master/LICENSE) for details

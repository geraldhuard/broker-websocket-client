# Serveur de websocket ActiveMQ consumer    

## Installation

- Installer JDK 8+ : [http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- Installer Tomcat 8+ : [https://tomcat.apache.org/download-90.cgi](https://tomcat.apache.org/download-90.cgi)
- Installer Maven 3+ : [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
- Installer ActiveMQ : [http://activemq.apache.org/download.html](http://activemq.apache.org/download.html)

## Build

Il s'agit d'un projet maven donc :

`mvn clean install package`

Cela va générer un fichier war dans `./target`
Deployer ce fichier war dans un tomcat.

Endpoint d'accès au websocket : [http://localhost:8080/notifier/websock](http://localhost:8080/notifier/websock)

## ActiveMQ

Un activeMQ doit etre installé et avec la conf standard.

Console ActiveMQ : [http://localhost:8161](http://localhost:8161) (`admin/admin` by default)

Ce war se connecte via tcp à l'ActiveMQ en suivant l'adresse : [tcp://broker:61616](tcp://broker:61616). _Il faut donc déclarer dans son fichier hosts_ :

`127.0.0.1 broker`

## Client web

Un fichier html permet de tester la connexion et les retours du serveur : `src/main/resources/client.html`

exemple de JS : 

```javascript
var url = settings.url || "ws://localhost:8080/notifier/websock";

websocket = new WebSocket(url);

websocket.onopen = function(evt) {
    log("Connected");

    /*** 
     *  A la connexion le client doit envoyer 
     *  le userid et l'orderid afin de s'identifier aupres du serveur 
     ***/
    websocket.send(JSON.stringify({
        "userid": settings.user.userid, "orderid": settings.user.orderid}));
};

websocket.onmessage = function(evt) {

    var message=evt.data;
    var data=JSON.parse(message);
    if (settings.onMessage)
        settings.onMessage(data);
    };

websocket.onerror = function(evt) {
    if (settings.onError)
        settings.onError(evt);
    };
```


## Simuler des messages dans le broker 

Pour simuler des messages sur l'ActiveMQ, on peut utiliser une commande curl : 

`
curl -X POST -d ""  "http://admin:admin@localhost:8161/api/message?destination=queue://notifications&userid=1&orderid=2"
`

Modifier `userid` et `orderid` dans la QueryString pour simuler d'autres user/commande.


## Precisions :

La classe principale de l'appli est `io.gg.broker.AppNotifier` ([https://github.com/geraldhuard/broker-websocket-client/blob/master/src/main/java/io/gg/broker/notifier/AppNotifier.java](https://github.com/geraldhuard/broker-websocket-client/blob/master/src/main/java/io/gg/broker/notifier/AppNotifier.java)) qui est la classe lancée à travers par  listener de deploiement de package (`NotifierContextListener`).
Cette class lance le websocket (et en le delegué) et le consumer dans un thread dédiée (afin de ne pas bloquer le serveur) et en est également le délégué.

Dans `AppNotifier`  : 

- A chaque connexion cliente : `onConnect()`
- A chaque message depuis le client : `onMessage()`

- A chaque nouveau message intercepté depuis le broker : `newMessageIncomingFromBroker()`
C'est dans cette fonction que l'on parse le message (du broker) et que l'on broadcast les clients (websocket) concerné par le message (c'est à dire s'ils se sont identifié à la connexion avec le bon userId et le bon orderId).

_-> A perfectionner_
:shipit:
 
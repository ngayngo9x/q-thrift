[![Build Status](https://travis-ci.org/librato/disco-java.svg?branch=master)](https://travis-ci.org/librato/disco-java)

# q-microservice
Q-Microservice: Utilities of microservice: thrift pool client, service discovery (client side)

# Disco

Simple zookeeper-based service discovery.

## Requirements

 * Java 1.7
 * Zookeeper 3.4.5+

This library uses Zookeeper nodes to maintain a set of available hosts for
a configured service. A client is automatically updated by Zookeeper/Curator
when a service is added or removed.

## Client usage

The server allows you to save arbitrary data per node that is accessible from
the client via the `Decoder<T>` interface.

```java
CuratorFramework framework; // Initialize this
SelectorStrategy selector = new RoundRobinSelectorStrategy();
Decoder<T> decoder; // Initialize this
DiscoClient<> client = new DiscoClient<T>(framework, serviceName, selector, decoder);
client.start(host, port);

Optional<Node<T>> node = client.getServiceNode();
```

Based on the selector strategy, the service will return the nodename of a
connected service, or Optional.absent() if none are connected.

Stop the client on shutdown to cleanly disconnect from Zookeeper.

```java
client.stop();
```

## Service usage

```java
CuratorFramework framework; // Initialize this
byte[] payload; // Initialize this
DiscoService service = new DiscoService(framework, "myservice");
service.start("hostname", 4321, true, payload);
```

As long as the service is running, this configuration will be associated with the
Zookeeper node `/services/myservice/nodes/hostname:4321` and the `byte[] 
payload` as the node's data. Upon stopping the service, the node will be
removed from Zookeeper. The third parameter dictates whether the service adds a
shutdown hook to stop the disco service. This is useful because of you want to
remove the service from discovery _before_ peforming a full shutdown, for
example before shutting down the HTTP port.

```java
service.stop();
```

## Testing

Run tests with `mvn test`. **Note**: tests assume you have Zookeeper running on
`localhost:2181`
=======

>>>>>>> 56efc28ab7984a5dc5c6134ecb47e591b22b94fa

thrift-pool-client
=======================

A Thrift Client pool for Java

* raw and type safe TServiceClient pool
* Multi backend servers support
* Backend servers replace on the fly
* Backend route by hash or random
* Failover and failback support
* jdk 1.8 only

## Get Started

```xml
<dependency>
    <groupId>com.github.phantomthief</groupId>
    <artifactId>thrift-pool-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

```Java

// init a thrift client
ThriftClient thriftClient = new ThriftClientImpl(() -> Arrays.asList(//
        ThriftServerInfo.of("127.0.0.1", 9090), //
        ThriftServerInfo.of("127.0.0.1", 9091) //
        // or you can return a dynamic result.
        ));
// get iface and call
System.out.println(thriftClient.iface(Client.class).echo("hello world."));

// get iface with custom hash, the same hash return the same thrift backend server
System.out.println(thriftClient.iface(Client.class, "hello world".hashCode()).echo(
        "hello world"));

// customize protocol
System.out.println(thriftClient.iface(Client.class, TBinaryProtocol::new,
        "hello world".hashCode()).echo("hello world"));

// customize pool config
GenericKeyedObjectPoolConfig poolConfig = new GenericKeyedObjectPoolConfig();
// ... customize pool config here
// customize transport, while if you expect pooling the connection, you should use TFrameTransport.
Function<ThriftServerInfo, TTransport> transportProvider = info -> {
    TSocket socket = new TSocket(info.getHost(), info.getPort());
    TFramedTransport transport = new TFramedTransport(socket);
    return transport;
};
ThriftClient customizeThriftClient = new ThriftClientImpl(() -> Arrays.asList(//
        ThriftServerInfo.of("127.0.0.1", 9090), //
        ThriftServerInfo.of("127.0.0.1", 9091) //
        ), new DefaultThriftConnectionPoolImpl(poolConfig, transportProvider));
customizeThriftClient.iface(Client.class).echo("hello world.");

// init a failover thrift client
ThriftClient failoverThriftClient = new FailoverThriftClientImpl(() -> Arrays.asList(//
        ThriftServerInfo.of("127.0.0.1", 9090), //
        ThriftServerInfo.of("127.0.0.1", 9091) //
        ));
failoverThriftClient.iface(Client.class).echo("hello world.");

// a customize failover client, if the call fail 10 times in 30 seconds, the backend server will be marked as fail for 1 minutes.
FailoverCheckingStrategy<ThriftServerInfo> failoverCheckingStrategy = new FailoverCheckingStrategy<>(
        10, TimeUnit.SECONDS.toMillis(30), TimeUnit.MINUTES.toMillis(1));
ThriftClient customizedFailoverThriftClient = new FailoverThriftClientImpl(
        failoverCheckingStrategy, () -> Arrays.asList(//
                ThriftServerInfo.of("127.0.0.1", 9090), //
                ThriftServerInfo.of("127.0.0.1", 9091) //
                ), DefaultThriftConnectionPoolImpl.getInstance());
customizedFailoverThriftClient.iface(Client.class).echo("hello world.");
    
```

## Know issues

You shouldn't reuse iface returned by client.

## Special Thanks

perlmonk with his great team gives me a huge help.
(https://github.com/aloha-app/thrift-client-pool-java)


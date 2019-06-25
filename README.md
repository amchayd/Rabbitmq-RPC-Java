# Rabbitmq RPC With Java 
## Prerequisites:
Install [Rabbitmq-server](https://www.rabbitmq.com/download.html)
## Components:
### Publisher:
The Publisher create a Request Object which contains three fields two operands and oprator
```java
public class Request implements Serializable{
	private double valeur1;
	private double valeur2;
	private String operator;
  //...
  }
```
Then send it to the Publisher using basicPublish() of Channel Object
```java
  channel.basicPublish("", requestQueueName, props, data);
```
### Consumer:
The Consumer receive the Request Object then parse it. Then send the response to the Publisher using Queues.
```java
public class Response implements Serializable{
	private String msg;
	private Status status;
  //...
  }
  public static enum Status {
		SUCCESS,
		ERROR
		}
```
### Communication:
The communication between the two Components ensured RabbitMQ

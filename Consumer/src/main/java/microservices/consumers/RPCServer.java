package microservices.consumers;

import org.apache.commons.lang.SerializationUtils;

import com.rabbitmq.client.*;

import microservices.pojo.Request;
import microservices.pojo.Response;

public class RPCServer  implements AutoCloseable{

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private static Response process(Request req) {
       
        Response res = new Response();
        res.setMsg(operate(req.getValeur1(), req.getValeur2(), req.getOperator()));
        return res;
    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try  {
        	Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(RPC_QUEUE_NAME);

            channel.basicQos(1);

            System.out.println("Awaiting RPC requests");

            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                Response response = null;

                try {
                	Object obj =    SerializationUtils.deserialize(delivery.getBody());
                	Request request = (Request)obj;
                    //String message = new String(delivery.getBody(), "UTF-8");
                	response = process(request);

                    System.out.println(" [.] operate(" + request.getValeur1() +"+"+ request.getValeur2() + ")");
                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                	byte[] data = SerializationUtils.serialize(response);
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, data);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };

            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
            // Wait and be prepared to consume the message from RPC client.
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }catch (Exception e) {
			// TODO: handle exception
		}
    }

    /**
     * Operations
     * */
    private static String operate(double a, double b, String operator) {
    	try {
    		double result=0;
	    	switch (operator) {
			case "+":
				result = a+b;
				break;
			case "-":
				result = a-b;
				break;
			case "/":
				result = a/b;
				break;
			case "*":
				result = a*b;
				break;
			case "%":
				result = a%b;
				break;
	
			default:
				return "Error : Invalide operator";
			}
	    	return String.valueOf(result);
    	}catch (Exception e) {
			return "Error:"+e.getMessage();
		}
    }
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
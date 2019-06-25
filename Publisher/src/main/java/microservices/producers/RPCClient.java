package microservices.producers;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import microservices.pojo.Request;
import microservices.pojo.Response;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.SerializationUtils;

public class RPCClient implements AutoCloseable{

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";

    public RPCClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    @SuppressWarnings("resource")
	public static void main(String[] argv) throws Exception {
        try (RPCClient rpcClient = new RPCClient()) {
            while (true) {
            	System.out.println("\n=======A(+-/%)B========");
            	Request req = new Request();
            	
            	Scanner sc = new Scanner(System.in);
            	
                System.out.print("A=");                
                String va = sc.nextLine();
                
                if(!isNumeric(va)) {
                	System.err.println("Please use a valide number");
                	continue;
                }
                
                System.out.print("B=");                
                String vb = sc.nextLine();
                if(!isNumeric(vb)) {
                	System.err.println("Please use a valide number");
                	continue;
                }
                double a = Double.valueOf(va);
                double b = Double.valueOf(vb);
               
                System.out.print("Operator=");                
                String opt = sc.nextLine();
        
        		req.setValeur1(a);
        		req.setValeur2(b);
        		req.setOperator(opt);
        		
                Response response = rpcClient.call(req);
                System.out.println("=>" + response.getMsg());
            }
        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Response call(Request req) throws IOException, InterruptedException {
        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        byte[] data = SerializationUtils.serialize(req);
        channel.basicPublish("", requestQueueName, props, data);

        final BlockingQueue<Response> response = new ArrayBlockingQueue<>(1);
        
        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
            	Object object =    SerializationUtils.deserialize(delivery.getBody());
                response.offer((Response) object);
            }
        }, consumerTag -> {
        });

        Response result = response.take();
        channel.basicCancel(ctag);
        return result;
    }

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
	public static boolean isNumeric(String str) {
		  return str.matches("-?\\d+(\\.\\d+)?"); 
	}
}
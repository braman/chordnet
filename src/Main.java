import java.io.IOException;
import java.net.InetAddress;

import com.rabbitmq.client.QueueingConsumer.Delivery;

import common.Request;
import common.Response;


public class Main {
	
//	public static final String TEST_NETWORK ="172.16.1.9";
//	public static final String SAM_NETWORK ="172.16.1.112";
//	public static final String ULAN_NETWORK ="172.16.1.197";
//	public static final String ZHASSAN_NETWORK ="172.16.1.247";
//	public static final String KUANYSH_NETWORK ="192.168.1.104";
//	public static final String LOCAL_NETWORK ="localhost";

	private static int successorKey;
	private static String successorIp;
	
	
	public static void main(String[] args) throws Throwable {
		InetAddress IP=InetAddress.getLocalHost();
		
		final String localIP = IP.getHostAddress();
		final int localKey = Utils.hash(localIP);
		
		if (args.length < 1) {
			throw new IllegalArgumentException("no successor ip");
		}
		
		successorIp = args[0];
		successorKey = Utils.hash(successorIp);
		
		System.out.println("-------------");
		System.out.printf("My IP: %s \n My Key: %s \n Successor Ip: %s \n Successor Key : %s \n", localIP, localKey, successorIp, successorKey);
		System.out.println("-------------");
		
		final ConnectionManager localCM = ConnectionManager.getInstance(localIP);
		
		ConsumerThread localFinder = new ConsumerThread(localCM, Constants.FIND_QUEUE, new ConnectionManager.Consumer() {
			@Override
			public void callback(Delivery d) {
				String taskStr = new String(d.getBody());
				
				Request req = Request.parse(taskStr);
				
				if (req == null) {
					System.err.println("Failed to parse request:" + taskStr);
					return;
				}
				
				try {

					if (successorKey == req.key) {
						Response resp = new Response();
						resp.key = req.key;
						resp.successorIp = successorIp;
						ConnectionManager.getInstance(req.requesterIp).publish(Constants.FOUND_QUEUE, resp.toString());
					} else {
						ConnectionManager.getInstance(successorIp).publish(Constants.FIND_QUEUE, req.toString());
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
				System.out.println("FIND TASK:" + taskStr);
			}
		});
		
		ConsumerThread localFounder = new ConsumerThread(localCM, Constants.FOUND_QUEUE, new ConnectionManager.Consumer() {
			@Override
			public void callback(Delivery d) {
				String s = new String(d.getBody());
				System.out.println("FOUND:" + s);
			}
		});
		
		Request r = new Request();
		r.requesterIp = localIP;
		r.key = Utils.hash(localIP);
		
		System.out.println("Initiating task: " + r.toString());
		
		
		ConnectionManager.getInstance(successorIp).publish(Constants.FIND_QUEUE, r.toString());
		
		
		localFinder.start();
		localFounder.start();
		
		
		
		
		
		
		//localCM.close();
		
	}
	
	
	
	
}

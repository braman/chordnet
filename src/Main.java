import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

import common.Notification;
import common.Request;
import common.Response;


public class Main {

	private static String localIP;
	private static Integer    localHash;

	private static String successorIp;
	private static Integer successorHash;

	private static String predecessorIp;
	private static Integer predecessorHash;


	

	public static void main(String[] args) throws Throwable {
		InetAddress IP = Utils.getLocalAddress();

		localIP = IP.getHostAddress();
		localHash = Utils.hash(localIP);

		Scanner in = new Scanner(System.in);

		System.out.printf("My IP: %s. My Hash: %s\n", localIP, localHash);

		System.out.print("Enter IP address you're going to join: ");

		String tryIP = null;

		while (true) {
			String line = in.nextLine();
			if (Utils.isValidIp(line)) {
				tryIP = line;
				break;
			} else {
				System.out.print("You've entered invalid ip! Please try again:");
			}
		}

		final ConnectionManager localCM = ConnectionManager.getInstance(localIP);
		
		successorIp = localIP;
		successorHash = localHash;

		ConsumerThread localFinder = new ConsumerThread(localCM, Constants.FIND_QUEUE, new ConnectionManager.Consumer() {
			@Override
			public boolean callback(String message) {
				String printPrefix = "----|";
				
				Request req = Request.parse(message);
				
				System.out.println(printPrefix + "Running localFinderThread");
				
				System.out.println(printPrefix + ">Request:" + message);
				
				if (req == null) {
					System.err.println("Failed to parse request:" + message);
					return false;
				}

				try {
					
					if (req.isFindSuccessor()) {
						if (localHash == successorHash || (localHash >  req.key && req.key <= successorHash)) {
							Response resp = new Response();
							resp.key = req.key;
							resp.ip = successorIp;
							
							ConnectionManager.getInstance(req.requesterIp).publish(Constants.FOUND_QUEUE, resp.toString());
						} else {
							ConnectionManager.getInstance(successorIp).publish(Constants.FIND_QUEUE, req.toString());
						}
						
					} else {
						
						Response resp = new Response();
						resp.type = Response.PREDECESSOR;
						
						resp.key = predecessorHash;
						resp.ip = predecessorIp;
						
						ConnectionManager.getInstance(req.requesterIp).publish(Constants.FOUND_QUEUE, resp.toString());
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

				return false;
			}
		});
		
		localFinder.start();
		
		
		ConnectionManager tryCM = ConnectionManager.getInstance(tryIP);
		
		Request findSuccessorRequest = new Request();
		findSuccessorRequest.key = localHash;
		findSuccessorRequest.requesterIp = localIP;

		tryCM.publish(Constants.FIND_QUEUE, findSuccessorRequest.toString());

		

		localCM.consume(Constants.FOUND_QUEUE, new ConnectionManager.Consumer() {
			@Override
			public boolean callback(String message) {
				Response findSuccessorResponse = Response.parse(message);
				successorIp = findSuccessorResponse.ip;
				return true;
			}
		});

		successorHash = Utils.hash(successorIp);

		predecessorIp = null;
		predecessorHash = null;
		
		System.out.println();
		System.out.println("-----------------------INFO-----------------------");
		System.out.printf("Successor IP: %s. Successor Hash: %s\n", successorIp, successorHash);
		System.out.println("--------------------------------------------------");
		System.out.println();

/*
		ConsumerThread localFounder = new ConsumerThread(localCM, Constants.FOUND_QUEUE, new ConnectionManager.Consumer() {
			@Override
			public boolean callback(String message) {
				System.out.println("Incoming Result:" + message);
				return false;
			}
		});
		
		localFounder.start();
*/
		
		
		Thread stabilizerThread = new Thread(new Runnable() {
			
			private String xIP = null;
			
			@Override
			public void run() {
				while (true) {
					try {
						
						final String printPrefix = "---------------|";
						
						System.out.println();
						System.out.println(printPrefix + "Running stabilizerThread");
						
						xIP = null;
						
						Request successorPredecessorRequest = new Request();
						successorPredecessorRequest.type = Request.PREDECESSOR;
						successorPredecessorRequest.key = localHash;
						successorPredecessorRequest.requesterIp = localIP;
						
						ConnectionManager.getInstance(successorIp).publish(Constants.FIND_QUEUE, successorPredecessorRequest.toString());
		
						System.out.println(printPrefix + "Asking successor "+successorIp + " about predecessor");
						
						
						localCM.consume(Constants.FOUND_QUEUE, new ConnectionManager.Consumer() {
							@Override
							public boolean callback(String message) {
								Response findSuccessorResponse = Response.parse(message);
								xIP = findSuccessorResponse.ip;
								return true;
							}
						});
						
						boolean emptyIp = (xIP == null || xIP.isEmpty()); 
						
						Integer x =  emptyIp ? null : Utils.hash(xIP);
						
						System.out.println(printPrefix + "My predecessor ip "+ xIP + " with hash " + x);
						
						if (x != null && (localHash < x && x < successorHash)) {
							successorIp = xIP;
							successorHash = x;
							
							System.out.println(printPrefix + "I have a new successor ip "+ xIP + " with hash " + x);
						}
						
						Notification n = new Notification();
						n.predecessorIP = localIP;				
						n.key = localHash;

						System.out.println(printPrefix + "Sending notification to successor ip "+ successorIp + " with hash " + successorHash);
						
						ConnectionManager.getInstance(successorIp).publish(Constants.NOTIFY_QUEUE, n.toString());
						
						Thread.sleep(Constants.DEFAULT_SLEEP_TO);
						
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
				}
			}
		});
		
		stabilizerThread.start();
		
		ConsumerThread notifConsumerThread = new ConsumerThread(localCM, Constants.NOTIFY_QUEUE, new ConnectionManager.Consumer() {
			
			@Override
			public boolean callback(String message) {
				
				String printPrefix = "-------------------------|";
				
				System.out.println(printPrefix + "Running notifConsumerThread");
				
				Notification n = Notification.parse(message);
				
				System.out.println(printPrefix + ">Notification:" + message);
				
				boolean isPredecessorNil = (predecessorIp == null || predecessorHash == null || predecessorIp.equals(localIP) || predecessorHash == localHash);
				
				if ( isPredecessorNil || (n.key > predecessorHash && n.key < localHash)) {
					predecessorIp = n.predecessorIP;
					predecessorHash = n.key;
					
					System.out.printf(printPrefix + "I have a new predecessor ip %s, predecessor hash %d\n", n.predecessorIP, n.key);
				}
				
				return false;
			}
		});
		
		notifConsumerThread.start();
		
		//System.out.printf("Predecessor IP: %s. Predecessor Hash: %s\n", predecessorIp, predecessorHash);

/*
		System.out.println("~Lookup request console~");

		while (true) {
			System.out.println("Enter hash, or any negative number for exit: ");

			int hashKey = in.nextInt();

			if (hashKey < 0) {
				break;
			}

			Request req = new Request();
			req.requesterIp = localIP;
			req.key = hashKey;

			ConnectionManager.getInstance(localIP).publish(Constants.FIND_QUEUE, req.toString());

		}
*/
		//localCM.closeAllConnections();
	}




}

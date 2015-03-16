import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

import common.Request;
import common.Response;


public class Main {
	
//	public static final String TEST_NETWORK ="172.16.1.9";
//	public static final String SAM_NETWORK ="172.16.1.112";
//	public static final String ULAN_NETWORK ="172.16.1.197";
//	public static final String ZHASSAN_NETWORK ="172.16.1.247";
//	public static final String KUANYSH_NETWORK ="192.168.1.104";
//	public static final String LOCAL_NETWORK ="localhost";

	private static int successorHash;
	private static String successorIp;
	
	//private static int predecessorHash;
	//private static String predecessorIp;
	
	
	public static void main(String[] args) throws Throwable {
		InetAddress IP = InetAddress.getLocalHost();
		
		final String localIP = IP.getHostAddress();
		final int localHash = Utils.hash(localIP);
		
		Scanner in = new Scanner(System.in);
		
		System.out.print("Enter successor ip: ");
		
		while (true) {
			String line = in.nextLine();
			if (Utils.isValidIp(line)) {
				successorIp = line;
				break;
			} else {
				System.out.print("You've entered invalid ip! Please try again:");
			}
		}
		
		successorHash = Utils.hash(successorIp);
		
		System.out.println();
		System.out.println("-----------------------INFO-----------------------");
		System.out.printf("My IP: %s. My Hash: %s\n", localIP, localHash);
		System.out.printf("Successor IP: %s. Successor Hash: %s\n", successorIp, successorHash);
		//System.out.printf("Predecessor IP: %s. Predecessor Hash: %s\n", predecessorIp, predecessorHash);
		System.out.println("--------------------------------------------------");
		System.out.println();
		
		final ConnectionManager localCM = ConnectionManager.getInstance(localIP);
		
		ConsumerThread localFinder = new ConsumerThread(localCM, Constants.FIND_QUEUE, new ConnectionManager.Consumer() {
			@Override
			public void callback(String message) {
				Request req = Request.parse(message);
				
				if (req == null) {
					System.err.println("Failed to parse request:" + message);
					return;
				}
				
				int min = Math.min(localHash, successorHash);
				int max = Math.min(localHash, successorHash);
				
				
				try {

					if (min >=  req.key && req.key <= max) {
						Response resp = new Response();
						resp.key = req.key;
						resp.successorIp = localIP;
						ConnectionManager.getInstance(req.requesterIp).publish(Constants.FOUND_QUEUE, resp.toString());
					} else {
						//ConnectionManager.getInstance(predecessorIp).publish(Constants.FIND_QUEUE, req.toString());
						ConnectionManager.getInstance(successorIp).publish(Constants.FIND_QUEUE, req.toString());
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
				System.out.println("FIND TASK:" + message);
			}
		});
		
		ConsumerThread localFounder = new ConsumerThread(localCM, Constants.FOUND_QUEUE, new ConnectionManager.Consumer() {
			@Override
			public void callback(String message) {
				System.out.println("Find Result:" + message);
			}
		});
		
		
		localFinder.start();
		localFounder.start();
		
		System.out.println("~Lookup request console~");
		
		while (true) {
			System.out.print("Enter hash, or any negative number for exit: ");
			
			int hashKey = in.nextInt();
			
			if (hashKey < 0) {
				break;
			}
			
			Request req = new Request();
			req.requesterIp = localIP;
			req.key = hashKey;
			
			ConnectionManager.getInstance(localIP).publish(Constants.FIND_QUEUE, req.toString());
			
		}
		
		localCM.closeAllConnections();
	}
	
	
	
	
}

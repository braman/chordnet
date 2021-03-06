import java.io.IOException;


public class ConsumerThread extends Thread {
	
	private ConnectionManager cm = null;
	private String queue;
	
	private ConnectionManager.Consumer consumeCallback;
	
	public ConsumerThread(ConnectionManager cm, String queue) throws IOException {
		this.cm = cm;
		this.queue = queue;
	}


	public ConsumerThread(ConnectionManager cm, String queue, ConnectionManager.Consumer callback) throws IOException {
		this.cm = cm;
		this.queue = queue;
		consumeCallback = callback;
	}

	
	@Override
	public void run() {
		try {
			
			if (consumeCallback != null) {
				cm.consume(queue, consumeCallback);
			} else {
				cm.consume(queue, new ConnectionManager.Consumer() {
					@Override
					public boolean callback(String message) {
						System.out.println("Consuming message: " + message);
						return false;
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
package listeners;

public class FindQueueListener extends Thread {

	private String queueName;
	
	public FindQueueListener(String queueName) {
		this.queueName = queueName;
	}
	
	
	
}

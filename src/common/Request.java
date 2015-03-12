package common;

public class Request {
	
	public String requesterIp;
	public int key;
	
	
	public Request() {}
	
	
	@Override
	public String toString() {
		return "REQUESTOR_IP:" + requesterIp + ",SUCCESSOR_KEY:" + key;
	}
	
	public static Request parse(String data) {
		try {
			String a[] = data.split(",");
			
			String v1[] = a[0].split(":");
			String v2[] = a[1].split(":");
			
			Request r = new Request();
			r.requesterIp = v1[1];
			r.key = Integer.parseInt(v2[1]);
			
			return r;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	
}

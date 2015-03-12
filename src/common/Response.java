package common;

public class Response {

	public Integer key;
	public String successorIp;
	
	@Override
	public String toString() {
		return "SUCCESSOR_KEY:" + key + ",SUCCESSOR_IP:" + successorIp;
	}
	
	public static Response parse(String data) {
		try {
			String a[] = data.split(",");
			
			String v1[] = a[0].split(":");
			String v2[] = a[1].split(":");
			
			Response r = new Response();
			r.key = Integer.parseInt(v1[1]);
			r.successorIp  = v2[1];
			
			return r;
		} catch (Exception e) {
			return null;
		}
	}
	
}

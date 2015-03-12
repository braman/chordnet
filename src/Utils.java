
public class Utils {

	public static int hash1(String ip) {
		final int prime = 31;
		
		int hash=7;

		int mod = (int) Math.pow(2, Constants.RING_SIZE);
		
		
		for (int i=0; i < ip.length(); i++) {
		    hash = ((hash * prime) + ip.charAt(i)) % mod;
		}
		
		return hash;
	}

	
	public static int hash(String key) {
		//int mod = (int) Math.pow(2, Constants.RING_SIZE);
		return key.hashCode();
	}
	
	public static int hash2(String key) {
	    int k = key.length();
	    int u = 0, n = 0;

	    for (int i=0; i<k; i++)
	    {
	        n = key.charAt(i);
	        u += i * n % 31;
	    }
	    return u % 139;
	}
	
	public static void main(String[] args) {
		//tesing hash
		
		String ip = "10.0.0.";
		
		for (int i=0;i<66;i++) {
			System.out.println(hash(ip + i));
		}
		
	}
	
	
}



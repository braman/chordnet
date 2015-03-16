import java.util.HashSet;
import java.util.Set;


public class Utils {

	public static int hash(String x){
        int hashcode=0;
        
        int MOD=(int) Math.pow(2, Constants.RING_SIZE);
        int shift=29;
        
        for (int i=0;i < x.length();i++){
            hashcode= ((shift * hashcode) % MOD + x.charAt(i)) % MOD;
        }
        
        return hashcode; 
    }
	
	
	public static boolean isValidIp(String ip) {
		final String PATTERN = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		return ip != null && ip.matches(PATTERN);
	}
	
	
	public static void main(String[] args) {
		//tesing hash
		
		String ip = "10.0.0.";
		
		Set<Integer> hashSet = new HashSet<Integer>(0);
		
		for (int i=0;i<66;i++) {
			String IP = ip + i;
			int h = hash(IP);
			System.out.println(IP +" - " + isValidIp(IP)+ "\\" + h);
			
			if (hashSet.contains(h)) {
				System.out.println("Failed");
			} else {
				hashSet.add(h);
			}
			
			
		}
		
	}
	
	
}



package common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Notification {
	
	public static final String NOTIF_PRED = "NOTIF_PRED";
	
	
	public String  format = "raman";
	public String  type = Notification.NOTIF_PRED;
	public String  predecessorIP;
	public Integer key;
	
	public static final String REG_EXP = "FORMAT:(\\w+),TYPE:(\\w+),PREDECESSOR_IP:(\\S+),KEY:(\\d*)";
	
	
	@Override
	public String toString() {
		return String.format("FORMAT:%s,TYPE:%s,PREDECESSOR_IP:%s,KEY:%s", format, type, predecessorIP, key);
		
	}
	
	public static Notification parse(String data) {
		Pattern pattern = Pattern.compile(Notification.REG_EXP);
		Matcher matcher = pattern.matcher(data);
		
		Notification n = new Notification();
		
		if (matcher.find()) {
			n.format = matcher.group(1);
			n.type = matcher.group(2);
			n.predecessorIP = matcher.group(3);
			
			String s = matcher.group(4);
			
			try {
				n.key = Integer.parseInt(s);
			} catch (NumberFormatException e) {
				n.key = null;
			}
			
		} else {
			return null;
		}
		
		return n;
	}
	
	
	public static void main(String[] args) {
		String s = "FORMAT:raman,TYPE:pred,PREDECESSOR_IP:12.12.3.12,KEY:11";
		
		
		System.out.println(s.matches(Notification.REG_EXP));
		
		
		Pattern pattern = Pattern.compile(Notification.REG_EXP);
		Matcher matcher = pattern.matcher(s);
		
		if (matcher.find()) {
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
			System.out.println(matcher.group(3));
			System.out.println(matcher.group(4));
		}
	}
}

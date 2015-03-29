package common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {
	
	public static final String FIND_SUCCESSOR = "FIND_SUCCESSOR";
	public static final String PREDECESSOR = "PREDECESSOR";
	
	public String  format = "raman";
	public String  type   = Request.FIND_SUCCESSOR;
	public String  requesterIp   = null;
	public Integer key = null;
	
	
	public static final String REG_EXP = "FORMAT:(\\w+),TYPE:(\\w+),REQUESTOR_IP:(\\S+),KEY:(\\d*)";
	
	@Override
	public String toString() {
		return String.format("FORMAT:%s,TYPE:%s,REQUESTOR_IP:%s,KEY:%s", format, type, requesterIp, key); 
	}
	
	public static Request parse(String data) {
		
		Pattern pattern = Pattern.compile(Request.REG_EXP);
		Matcher matcher = pattern.matcher(data);
		
		Request r = new Request();
		
		if (matcher.find()) {
			r.format = matcher.group(1);
			r.type = matcher.group(2);
			r.requesterIp = matcher.group(3);
			
			String s = matcher.group(4);
			
			try {
				r.key = Integer.parseInt(s);
			} catch (NumberFormatException e) {
				r.key = null;
			}
			
		} else {
			return null;
		}
		
		return r;
	}
	
	public static void main(String[] args) {
		
		String s = "FORMAT:raman,TYPE:find_successor,REQUESTOR_IP:12.12.12.12,KEY:";
		
		System.out.println(s.matches(Request.REG_EXP));
		
		
		Pattern pattern = Pattern.compile(Request.REG_EXP);
		Matcher matcher = pattern.matcher(s);
		
		if (matcher.find()) {
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
			System.out.println(matcher.group(3));
			System.out.println(matcher.group(4));
		}
		
	}
	
	public boolean isFindSuccessor() {
		return Request.FIND_SUCCESSOR.equals(type);
	}
	
	
}

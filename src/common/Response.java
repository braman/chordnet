package common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Response {
	
	public static final String FIND_SUCCESSOR = "FIND_SUCCESSOR";
	public static final String PREDECESSOR = "PREDECESSOR";
	
	public String  format = "raman";
	public String  type   = Response.FIND_SUCCESSOR;
	public String  ip = null;
	public Integer key = null;
	
	
	public static final String REG_EXP = "FORMAT:(\\w+),TYPE:(\\w+),IP:(\\S+),KEY:(\\d*)";
	
	@Override
	public String toString() {
		return String.format("FORMAT:%s,TYPE:%s,IP:%s,KEY:%s", format, type, ip, key); 
	}
	
	public static Response parse(String data) {
		
		Pattern pattern = Pattern.compile(Response.REG_EXP);
		Matcher matcher = pattern.matcher(data);
		
		Response r = new Response();
		
		if (matcher.find()) {
			r.format = matcher.group(1);
			r.type = matcher.group(2);
			r.ip = matcher.group(3);
			
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
		
		String s = "FORMAT:raman,TYPE:find_successor,IP:12.12.12.12,KEY:";
		
		System.out.println(s.matches(Response.REG_EXP));
		
		
		Pattern pattern = Pattern.compile(Response.REG_EXP);
		Matcher matcher = pattern.matcher(s);
		
		if (matcher.find()) {
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
			System.out.println(matcher.group(3));
			System.out.println(matcher.group(4));
		}
		
	}
	
	public boolean isFindSuccessor() {
		return Response.FIND_SUCCESSOR.equals(type);
	}
	
}

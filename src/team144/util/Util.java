package team144.util;

public class Util {
	
	/**
	 * Creates a String containing all elements of an array, separated by spaces
	 * @return aforementioned String
	 */
	public static String concat(String[] strings){
		if(strings.length == 0)
			return "";
		StringBuilder sb = new StringBuilder(strings[0]);
		for(int i = 1; i<strings.length; i++){
			sb.append(" ");
			sb.append(strings[i]);
		}
		return sb.toString();
	}
	
}

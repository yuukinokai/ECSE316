package DNS;

/**
 * Class used as a tester to run the DnsClient.
 * @author Sophie
 *
 */
public class Tester {
	
	public static void main(String[] args) {
		
		try {
			DnsClient.main(new String[] {"-t", "10", "-r", "9", "-mx", "@8.8.8.8", "facebook.com"});
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

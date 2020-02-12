package DNS;

public class Assignment1 {
	private int timeout = 5000; //time in ms
	private int maxRetries = 3;
	private int port = 53;
	private QueryType queryType = QueryType.A;
	private String name;
	private String serverAddress; //IPv4 address of the DNS server
	
	
	public static void main(String args[]) throws Exception {
        try {
        	int argsLength = args.length;
        	for(int i = 0; i < argsLength; i++) {
        		switch(args[i]) {
        		case "-t":
        			//get timeout
        			break;
        		case "-r":
        			//get retries
        			break;
        		case "mx":
        			//change query type
        			break;
        		case "ns":
        			//change query type
        			break;
        		default:
        			
        		}
        	}
        	//TODO: parse arguments
        	
            //DnsClient client = new DnsClient(args);
            //client.makeRequest();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

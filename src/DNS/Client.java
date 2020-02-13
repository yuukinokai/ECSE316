package DNS;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * DNS client class for our assignment.
 * This class makes the request via the socket and reads the response.
 * 
 * @author Sophie and Mia
 *
 */
public class Client {

	//constants
	private float MILISECONDS = 1000.0f;
	private int MAX_DATA_SIZE = 1024;

	/**
	 * This methods creates the request and sends it.
	 * @param name Name of the server
	 * @param serverAddress String of the IP address.
	 * @param queryType Type of query to send.
	 * @param timeout Max time to wait for request.
	 * @param ipAddress Byte of the IP address.
	 * @param port Port number.
	 * @param maxRetries Number of times to resend the request.
	 * @return response if there was no error.
	 * @throws Exception if the number of times to resend the request was exceeded.
	 */
	public Response sendRequest(String name, String serverAddress, QueryType queryType,int timeout, byte[] ipAddress,
			int port, int maxRetries) throws Exception {

		//Print the information
		System.out.println("DnsClient sending request for " + name);
		System.out.println("Server: " + serverAddress);
		System.out.println("Request type: " + queryType);	

		//Loop for the number of times it will retry
		for(int i = maxRetries; i > 0; i--) {
			try {
				
				//Open socket
	            DatagramSocket clientSocket = new DatagramSocket();
	            clientSocket.setSoTimeout(timeout);
	            InetAddress inetAddr = InetAddress.getByAddress(ipAddress);
	            Request request = new Request(queryType, name);

	            byte[] requestData = request.getDNSRequest();
	            byte[] responseData = new byte[MAX_DATA_SIZE];

	            long startTime = System.currentTimeMillis();
	            
	            //Rend Request
	            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, inetAddr, port);
	            clientSocket.send(requestPacket);
	            
	            //Receive Request
	            DatagramPacket responsePacket = new DatagramPacket(responseData, MAX_DATA_SIZE);
	            clientSocket.receive(responsePacket);
	            
	            long endTime = System.currentTimeMillis();
	            
	            //Close socket
	            clientSocket.close();

	            long timeTaken = endTime - startTime;
	            System.out.println("Response received after "+ timeTaken/MILISECONDS + " seconds ("+ (maxRetries-i) + " retries)");

	            Response response = new Response(responsePacket.getData());
	            return response;
			} catch (Exception e) {
				System.out.println("Error at try " + i + ": "+ e.getMessage());
			}
		}
		//If it got here, it exceeded the number of tries without a response.
		throw new Exception("Exceeded number of retries.");
	}

	//TODO: print read
	public void readResponse(Response response) throws Exception {
		response.readHeader();
		response.readAnswers();
		AnswerRecord[] ar = response.getAnswers();
		AnswerRecord[] adr = response.getAdditionalAnswers();
		if (ar.length == 0 && adr.length == 0) {
			System.out.println("NOTFOUND");
		}
		/* ***Answer Section ([num-answers] records)***
Then, if the response contains A (IP address) records, each should be printed on a line of the form:
IP <tab> [ip address] <tab> [seconds can cache] <tab> [auth | nonauth]
Where <tab> is replaced by a tab character. Similarly, if it receives CNAME, MX, or NS records, they should be printed on lines of the form:
CNAME <tab> [alias] <tab> [seconds can cache] <tab> [auth | nonauth]
MX <tab> [alias] <tab> [pref] <tab> [seconds can cache] <tab> [auth | nonauth] NS <tab> [alias] <tab> [seconds can cache] <tab> [auth | nonauth]
If the response contains records in the Additional section then also print:
		 ***Additional Section ([num-additional] records)***
along with appropriate lines for each additional record that matches one of the types A, CNAME, MX, or NS. You can ignore any records in the Authority section for this lab. */
	}
}
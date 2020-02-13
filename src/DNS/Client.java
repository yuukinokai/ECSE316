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

	            Response response = new Response(responsePacket.getData(), requestData.length);
	            return response;
			} catch (Exception e) {
				System.out.println("Error at try " + i + ": "+ e.getMessage());
			}
		}
		//If it got here, it exceeded the number of tries without a response.
		throw new Exception("Exceeded number of retries.");
	}

	/**
	 * Method that gets the response and prints it
	 * @param response
	 * @throws Exception
	 */
	public void readResponse(Response response) throws Exception {
		response.readHeader();
		response.readAnswers();
		AnswerRecord[] ar = response.getAnswers();
		AnswerRecord[] adr = response.getAdditionalAnswers();
		if(ar.length == 0 && adr.length == 0) {
			System.out.println("NOTFOUND");
		}
		if(ar.length != 0) {
			System.out.println("***Answer Section ("+ar.length+" records)***");
			for(int i=0; i< ar.length;i++) {
				AnswerRecord answer = ar[i];
				printAnswer(answer);
			}
		}
		if(adr.length != 0) {
			System.out.println("***Additional Section ("+adr.length+" records)***\n");
			for(int i=0; i< adr.length;i++) {
				AnswerRecord adrAnswer = adr[i];
				printAnswer(adrAnswer);
			}
		}
	}
	
	/**
	 * Helper method that prints the Answer record
	 * @param answer
	 */
	public void printAnswer(AnswerRecord answer) {
		QueryType qType = answer.getQueryType();
		if(qType.equals(QueryType.A)) {
			System.out.println("IP\t"+answer.getRData()+"\t"+answer.getTTL()+"\t"+"nonauth\n");
		}
		if(qType.equals(QueryType.CNAME)) {
			System.out.println("CNAME\t"+answer.getRData()+"\t"+answer.getTTL()+"\t"+"nonauth\n");
		}
		if(qType.equals(QueryType.MX)) {
			System.out.println("MX\t"+answer.getRData()+"\t"+answer.getTTL()+"\t"+"nonauth\n");
		}
		if(qType.equals(QueryType.NS)) {
			System.out.println("NS\t"+answer.getRData()+"\t"+answer.getTTL()+"\t"+"nonauth\n");
		}	
	}
}
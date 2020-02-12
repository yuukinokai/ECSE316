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
		Response response = new Response(queryType);
		
		//Print the information
		System.out.println("DnsClient sending request for " + name);
		System.out.println("Server: " + serverAddress);
		System.out.println("Request type: " + queryType);	
		
		//Loop for the number of times it will retry
		for(int i = maxRetries; i > 0; i--) {
			try {
				byte[] requestData = new byte[1024];
				byte[] responseData = new byte[1024];
				
				//Open socket
				DatagramSocket clientSocket = new DatagramSocket();
				clientSocket.setSoTimeout(timeout);
				InetAddress inetAddr = InetAddress.getByAddress(ipAddress);
				
				long startTime = System.currentTimeMillis();
				
				//Rend Request
				DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, inetAddr, port);
				clientSocket.send(requestPacket);
				
				//Receive Request
				DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);
				clientSocket.receive(requestPacket);
				
				long endTime = System.currentTimeMillis();
				
				//close socket
				clientSocket.close();
				
				//print time
				long timeTaken = endTime - startTime;
				System.out.println("Response received after "+ timeTaken/1000+ "seconds ("+ i + "retries)");
				
				return response;
			} catch (Exception e) {
				System.out.println("Error at try " + i + ": "+ e.getMessage());
			}
		}
		//If it got here, it exceeded the number of tries without a response.
		throw new Exception("Exceeded number of retries.");
	}
	
	//TODO: read
	public void readResponse(Response response) {
		
	}
	//close
}
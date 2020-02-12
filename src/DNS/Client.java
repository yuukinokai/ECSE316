package DNS;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Client {
	private String name;
	private String serverAddress;
	public QueryType queryType;
	
	byte[]  ipAddr;
	private int port = 53;
	private byte[] sendData;

	//TODO: constructor
	public Client(byte[] sendData) {
		this.sendData = sendData;
	}
	
	//TODO: send request
	public void sendRequest() {
		//create Datagram
		try {
			byte[] sendData = this.sendData;
			byte[] receiveData = new byte[1024];
			
			DatagramSocket clientSocket = new DatagramSocket();
			
			InetAddress inetAddr = InetAddress.getByAddress(ipAddr);
			
			//send
			DatagramPacket requestPacket = new DatagramPacket(sendData, sendData.length, inetAddr, port);
			clientSocket.send(requestPacket);
			//receive
			DatagramPacket responsePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(requestPacket);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		//Summary
		System.out.println("DnsClient sending request for" +this.name+"\n");
		System.out.println("Server:"+this.serverAddress+"\n");
		System.out.println("Request type:"+queryType+"\n");	
		
	
	}
	//TODO: read
	public void readResponse() {
		
	}
	//close
}
package DNS;

import java.nio.ByteBuffer;
import java.util.Random;

public class Request {
	QueryType queryType;
	String domain;
	
	//CONSTANTS 
	int QTYPE = 2;
	int QCLASS = 2;
	int ZERO_OCTET = 1;
	
	int HEADER_SIZE = 12;

	public Request(QueryType queryType, String domain) {
		this.queryType = queryType;
		this.domain = domain;
	}
	
	/**
	 * Getter method that returns the complete DNS request consisting of the header and DNSQuestions
	 * @return byte[]
	 */
	
	public byte[] getDNSRequest() {
		int qNameLen = domain.length()+1;
		int questionsLen = qNameLen +QTYPE +QCLASS +ZERO_OCTET;
		
		ByteBuffer request = ByteBuffer.allocate(HEADER_SIZE +questionsLen);
		
		request.put(createDNSheader());
		request.put(createDNSquestions(questionsLen));
		
		return request.array();
	}
	
	/**
	 * Method that creates the DNS header
	 * @return byte[]
	 */
	
	private byte[] createDNSheader() {
		ByteBuffer header = ByteBuffer.allocate(HEADER_SIZE);
		//ID
		byte[] clientID = new byte[2]; 
		new Random().nextBytes(clientID);
		header.put(clientID);
		
		//QR, OPCOODE, AA, TC, RD
		header.put((byte)0x001);
		
		//RA, Z, RCODE
		header.put((byte)0x001);
		
		//QDCOUNT
		header.put((byte)0x00);
		header.put((byte)0x001);
		
		return header.array();		
	}
	
	/**
	 * Method that creates the DNSQuestions header
	 * @param questionsLen: Length of DNSQuestions
	 * @return byte[]
	 */
	
	private byte[] createDNSquestions(int questionsLen) {
		ByteBuffer  question = ByteBuffer.allocate(questionsLen);
		qName(question);
		qType(question);
		qClass(question);
		
		return question.array();
	}
	
	/** 
	 * Method that fills the QNAME section of DNSquestions
	 * @param question
	 */
	private void qName(ByteBuffer question) {
		String[] labels= this.domain.split("\\.");
		
		for(int i=0; i<labels.length;i++) {
			String label = labels[i];
			question.put((byte) label.length());
			for(int j=0; j<label.length();i++) {
				question.put((byte)label.charAt(j));
			}
			
		}
		//zero-octet
		question.put((byte) 0x00);
	}
	
	/** 
	 * Method that fills the QTYPE section of DNSquestions 
	 * @param question
	 */
	
	private void qType(ByteBuffer question) {
		//0x0001 for a type-A query (host address)
		if(this.queryType.equals(QueryType.A)) {
			question.put(hexStringToByteArray("0001"));
		}
		//0x0002 for a type-NS query (name server)
		else if(this.queryType.equals(QueryType.NS)) {
			question.put(hexStringToByteArray("0002"));
		}
		else {
		//0x000f for a type-MX query (mail server)
		question.put(hexStringToByteArray("000f"));
		}
		
		question.put(hexStringToByteArray("0x00"));
	}
	
	/** 
	 * Method that fills the QCLASS section of DNSquestions 
	 * @param question
	 */
	
	private void qClass(ByteBuffer question) {
		//always 0x0001 for Internet
		question.put(hexStringToByteArray("0001"));
		question.put(hexStringToByteArray("0x00"));
	}
	
	/** 
	 * Helper method that converts hex string to a byte array
	 * @param hexType : hex string
	 * @return byte[]: byte array
	 */
	private byte[] hexStringToByteArray(String hexType) {
	    int len = hexType.length();
	    byte[] byteType = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	    	byteType[i / 2] = (byte) ((Character.digit(hexType.charAt(i), 16) << 4)
	                             + Character.digit(hexType.charAt(i+1), 16));
	    }
	    return byteType;
	}
}

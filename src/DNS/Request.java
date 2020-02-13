package DNS;

import java.nio.ByteBuffer;
import java.util.Random;

/**
 * This class creates a Request that the server can send via the socket.
 * @author Sophie and Mia
 *
 */
public class Request {
	private QueryType queryType;
	private String domain;

	//CONSTANTS 
	private int QTYPE_BYTES = 2;
	private int QCLASS_BYTES = 2;
	private int ZERO_OCTET_BYTES = 1;

	private int HEADER_SIZE = 12;

	public Request(QueryType queryType, String domain) {
		this.queryType = queryType;
		this.domain = domain;
	}

	/**
	 * Getter method that returns the complete DNS request consisting of the header and DNSQuestions.
	 * @return DNS request in the form of byte[].
	 * @throws Exception If query type is wrong.
	 */
	public byte[] getDNSRequest() throws Exception {
		//Give name the extra byte for the length indicator
		int qNameLen = domain.length()+ 1;
		int questionsLen = qNameLen + QTYPE_BYTES + QCLASS_BYTES + ZERO_OCTET_BYTES;

		ByteBuffer request = ByteBuffer.allocate(HEADER_SIZE + questionsLen);
		request.put(createDNSHeader());
		request.put(createDNSQuestions(questionsLen));	
		return request.array();
	}

	/**
	 * Method that creates the DNS header.
	 * @return The byte[] of the header.
	 */
	private byte[] createDNSHeader() {
		ByteBuffer header = ByteBuffer.allocate(HEADER_SIZE);
		//ID
		byte[] clientID = new byte[2]; 
		new Random().nextBytes(clientID);
		header.put(clientID);

		//QR, OPCOODE, AA, TC, RD
		header.put((byte)0x01);

		//RA, Z, RCODE
		header.put((byte)0x00);

		//QDCOUNT
		header.put((byte)0x00);
		header.put((byte)0x01);

		return header.array();		
	}

	/**
	 * Method that creates the DNSQuestions.
	 * @param questionsLen: Length of DNSQuestions
	 * @return The DNS question byte[].
	 * @throws Exception If the query type is a wrong type.
	 */
	private byte[] createDNSQuestions(int questionsLen) throws Exception {
		ByteBuffer question = ByteBuffer.allocate(questionsLen);
		//name
		qName(question);

		qType(question);

		qClass(question);

		return question.array();
	}

	/** 
	 * Method that fills the QNAME section of DNSquestions.
	 * @param question byte buffer
	 */
	private void qName(ByteBuffer question) {
		String[] labels = this.domain.split("\\.");

		for(int i = 0; i < labels.length; i++) {
			String label = labels[i];
			question.put((byte) label.length());

			for(int j = 0; j < label.length(); j++) {
				question.put((byte)label.charAt(j));
			}

		}
		//zero-octet
		question.put((byte) 0x00);
	}

	/** 
	 * Method that fills the QTYPE section of DNSquestions.
	 * @param question byte buffer
	 * @throws Exception If query type is wrong.
	 */
	private void qType(ByteBuffer question) throws Exception {
		//Query Type
		question.put((byte) 0x00);
		switch(queryType) {
		case A:
			question.put((byte) 0x0001);
			break;
		case NS:
			question.put((byte) 0x0002);
			break;
		case MX:
			question.put((byte) 0x000f);
			break;
		default:
			throw new Exception("Cannot request this type of query.");
		}

	}

	/** 
	 * Method that fills the QCLASS section of DNSquestions. 
	 * @param question byte buffer
	 */
	private void qClass(ByteBuffer question) {
		//always 0x0001 for Internet
		question.put((byte) 0x00);
		question.put((byte) 0x0001);	
	}
}

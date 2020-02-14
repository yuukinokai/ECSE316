package DNS;

import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * This class is the response class. It checks the header and the answers. The also creates the answer records.
 * @author Sophie and Mia
 *
 */
public class Response {

	/**
	 * Container class used to store a string and a integer.
	 * @author Sophie
	 *
	 */
	class Pair {
		String name;
		int i;
	}

	private byte[] response = new byte[1024];
	private short ID = 0;
	private boolean QR = false; //if it's response
	private boolean AA = false; //if it's authoritative
	private boolean TC = false; //if it was truncated
	private boolean RD =  false; //if desired recursion
	private boolean RA = false; //if server supports recursive queries
	private int OPCode = 0; //0 for standard query
	private int Z = 0; //can ignore for this assignment
	private int RCode = 0; //error code
	private short QDCount = 0; //entries to question section, should be 1
	private short ANCount = 0; //resource records
	private short NSCount = 0; //authority section, can ignore
	private short ARCount = 0; //additional records section
	private AnswerRecord[] answerRecords;
	private AnswerRecord[] additionalRecords;
	private int offset = 31;

	/**
	 * Constructor 
	 * @param r	The response bytes.
	 * @param offset The start of the answer bytes.
	 */
	public Response(byte[] r, int o) {
		response = r;
		offset = o;
	}

	/**
	 * Method that reads the received header, parses and checks the header for errors.
	 * @throws Exception If an error was encountered in the header.
	 */
	public void readHeader() throws Exception {
		if(response.length < 12) {
			throw new Exception("Header error: wrong response size.");
		}
		ID = bytesToShort(response[0], response[1]);

		QR = isTrueBit(response[2], 7);
		if(!QR) {
			throw new Exception("Response error: reponse expected, but header indicates otherwise.");
		}
		OPCode = response[2] & 0x38;
		AA = isTrueBit(response[2], 2);
		TC = isTrueBit(response[2], 1);
		RD = isTrueBit(response[2], 0);
		RA = isTrueBit(response[3], 7);
		if(!RA) {
			throw new Exception("Server error: server does not support recursiveness.");
		}
		Z = response[3] & 0x70;

		//Handle RCode errors
		RCode = response[3] & 0xF;
		switch(RCode) {
		case 1:
			throw new Exception("Format error: the name server was unable to interpret the query.");
		case 2:
			throw new Exception("Server failure: the name server was unable to process this query due to a problem with the\r\n" + 
					"name server.");
		case 3:
			throw new Exception("Name error: meaningful only for responses from an authoritative name server, this code\r\n" + 
					"signifies that the domain name referenced in the query does not exist.");
		case 4:
			throw new Exception("Not implemented: the name server does not support the requested kind of query.");
		case 5:
			throw new Exception("Refused: the name server refuses to perform the requested operation for policy reasons.");
		}

		QDCount = bytesToShort(response[4], response[5]);
		if(QDCount != 1) {
			throw new Exception("Header error: QDCount should be 1 but " + QDCount + " was found.");
		}
		ANCount = bytesToShort(response[6], response[7]);		
		NSCount = bytesToShort(response[8], response[9]);
		ARCount = bytesToShort(response[10], response[11]);

		answerRecords = new AnswerRecord[ANCount];
		additionalRecords = new AnswerRecord[ARCount];
	}

	/**
	 * Get the list of answer records.
	 * @return AnswerRecord list.
	 */
	public AnswerRecord[] getAnswers() {
		return answerRecords;
	}

	/**
	 * Get the additional answer records.
	 * @return AdditionalRecords list.
	 */
	public AnswerRecord[] getAdditionalAnswers() {
		return additionalRecords;
	}

	/**
	 * Method that collects the answers into the correct lists.
	 * @throws Exception If error was found in the answer.
	 */
	public void readAnswers() throws Exception {
		int index = offset;
		for(int i = 0; i < ANCount; i ++){
			answerRecords[i] = getAnswer(index);
			index += answerRecords[i].getSize();
		}
		//ignore authority section
		for(int i = 0; i < NSCount; i++){
			index += getAnswer(index).getSize();
		}

		for(int i = 0; i < ARCount; i++){
			additionalRecords[i] = getAnswer(index);
			index += additionalRecords[i].getSize();
		}
	}

	/**
	 * Collects the information into an AnswerRecord class.
	 * @return Next available AnswerRecord.
	 * @throws Exception If an error was encountered while reading.
	 */
	private AnswerRecord getAnswer(int readIndex) throws Exception {
		AnswerRecord ar = new AnswerRecord();
		ar.setAuth(AA);

		int readByte = readIndex;

		//NAME
		Pair p = readWordFromIndex(readByte);
		readByte += p.i;

		//Authority
		ar.setAuth(AA);

		//TYPE
		QueryType qt = getQueryTypeFromBytes(readByte);
		ar.setQueryType(qt);
		readByte += 2;

		//CLASS
		byte[] bClass = { response[readByte], response[readByte + 1] };
		ByteBuffer buf = ByteBuffer.wrap(bClass);
		short qClass = buf.getShort();
		if(qClass != (short) 0x01) {
			throw new Exception("Answer error: answer CLASS should be 0x01, but " + qClass + " was found instead.");
		}
		readByte += 2;

		//TTL
		byte[] ttlBytes = {response[readByte], response[readByte + 1], response[readByte + 2], response[readByte + 3]};
		ByteBuffer wrapTtl = ByteBuffer.wrap(ttlBytes);
		ar.setTTL(wrapTtl.getInt());
		readByte += 4;

		//RDLENGTH
		short rdLen = bytesToShort(response[readByte], response[readByte + 1]);
		readByte += 2;

		Pair rData = new Pair();
		//RDATA
		switch(qt) {
		case A:
			byte[] byteAddress= {response[readByte], response[readByte + 1], response[readByte + 2], response[readByte + 3]};
			InetAddress inetaddress = InetAddress.getByAddress(byteAddress);
			rData.name = inetaddress.toString().substring(1);
			break;
		case NS:
			rData = readWordFromIndex(readByte);
			break;
		case MX:
			//PREFERENCE
			rData = readWordFromIndex(readByte + 2);
			break;
		case CNAME:
			rData = readWordFromIndex(readByte);
		}
		readByte += rdLen;
		ar.setSize(readByte - readIndex);
		ar.setRData(rData.name);
		return ar;
	}

	/**
	 * Reads a word from a given index
	 * @param index Start index of word.
	 * @return Pair of both name and index.
	 */
	private Pair readWordFromIndex(int index) {
		int countByte = 0;
		String name = "";
		byte read = response[index];
		boolean start = true;
		while(read != (short) 0x0) {
			if(start) {
				start = false;
			}
			else {
				name += ".";
			}
			int wordSize = read;	
			index++;
			countByte++;
			//check if it is a pointer
			if ((wordSize & 0xC0) == (int) 0xC0) {
				//get pointer
				short pointer = bytesToShort((byte) (response[index-1] & 0x3F), response[index]);
				Pair word = readWordFromIndex(pointer);
				index++;
				countByte++;
				name += word.name;
				break;
			}
			else
			{
				for(int i = 0; i < wordSize; i++) {
					name += (char) response[index++] ;
					countByte++;
				}
			}
			read = response[index];
		}
		Pair p = new Pair();
		p.name = name;
		p.i = countByte;
		return p;
	}

	/**
	 * Method that checks if the bit is 1.
	 * @param b 	The byte to check
	 * @param shift	How many times to shift the byte to get the bit.
	 * @return 		if the bit is 1.
	 */
	private boolean isTrueBit(byte b, int shift) {
		return ((b >> shift) & 1) == 1;
	}

	/**
	 * Combines two bytes into a short.
	 * @param b1 Byte 1.
	 * @param b2 Byte 2.
	 * @return Short of the combined bytes.
	 */
	private short bytesToShort(byte b1, byte b2) {
		return (short) ((b1 << 8) | (b2 & 0xFF));
	}

	/**
	 * Maps the byte to query type.
	 * @param index Of the byte to read.
	 * @return Query type of the byte.
	 * @throws Exception If a bad type was encountered.
	 */
	private QueryType getQueryTypeFromBytes(int index) {
		short qt = bytesToShort(response[index], response[index+1]);
		switch(qt) {
		case ((short) 0x02):
			return QueryType.NS;
		case ((short) 0x0f):
			return QueryType.MX;
		case ((short) 0x05):
			return QueryType.CNAME;
		}
		return QueryType.A;
	}

}

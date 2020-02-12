package DNS;

public class Response {
	private byte[] response = new byte[1024];
    private short ID = 0;
    private boolean QR = false;
    private boolean AA = false;
    private boolean TC = false;
    private boolean RD =  false;
    private boolean RA = false;
    private int OPCode = 0;
    private int Z = 0;
    private int RCode = 0;
    private short QDCount = 0;
    private short ANCount = 0;
    private short NSCount = 0;
    private short ARCount = 0;
    private byte[] answerRecords;
    private byte[] additionalRecords;
    private QueryType queryType;
    private boolean noRecords = false;
    
    public Response(QueryType qt) {
    	queryType = qt;
    }
    
    public void setResponse(byte[] r) {
    	response = r;
    }
    
    public void readHeader() throws Exception {
    	if(response.length < 12) {
    		throw new Exception("Wrong response size.");
    	}
    	ID = bytesToShort(response[0], response[1]);
    	
    	QR = isTrueBit(response[2], 7);
    	OPCode = response[2] & 0x38;
    	AA = isTrueBit(response[2], 2);
    	TC = isTrueBit(response[2], 1);
    	RD = isTrueBit(response[2], 0);
    	RA = isTrueBit(response[3], 7);
    	Z = response[3] & 0x70;
    	RCode = response[3] & 0xF;
    	
    	QDCount = bytesToShort(response[4], response[5]);
    	ANCount = bytesToShort(response[6], response[7]);
    	NSCount = bytesToShort(response[8], response[9]);
    	ARCount = bytesToShort(response[10], response[11]);   	
    }
    
    private boolean isTrueBit(byte b, int shift) {
    	return (b >> shift) == 1;
    }
    
    private short bytesToShort(byte b1, byte b2) {
        return (short) ((b1 << 8) | (b2 & 0xFF));
    }
}

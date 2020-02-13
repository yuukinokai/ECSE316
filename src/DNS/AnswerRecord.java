package DNS;

public class AnswerRecord {
	private String name;
	private QueryType qType;
	private int ttl, rDLen;
	private String rData;
	
	public AnswerRecord() {
		
	}
	
	public AnswerRecord(String name, QueryType type, byte[] qClass, int ttl) {
		this.name = name;
		this.qType = type;
		this.ttl = ttl;
	}
	
	/* ======= SETTERS ======*/
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setQueryType(QueryType q) {
		this.qType = q;
	}
	
	public void setTTL(int ttl) {
		this.ttl = ttl;
	}
	
	public void setRDLen(int rd) {
		this.rDLen = rd;
	}
	
	public void setRData(String rData) {
		this.rData = rData;
	}
	
	/* ======= GETTERS ======*/
	
	public String getName() {
		return name;
	}
	
	public QueryType getQueryType() {
		return qType;
	}
	
	public int getTTL() {
		return ttl;
	}
	
	public String getRData() {
		return rData;
	}
	
	public int getRDLen() {
		return rDLen;
	}
}

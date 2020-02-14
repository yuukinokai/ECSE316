package DNS;

public class AnswerRecord {
	private boolean auth;
	private QueryType qType;
	private int ttl;
	private String rData;
	private int size;
	
	/* ======= SETTERS ======*/
	
	public void setAuth(Boolean a) {
		this.auth = a;
	}
	
	public void setSize(int s) {
		this.size = s;
	}
	
	public void setQueryType(QueryType q) {
		this.qType = q;
	}
	
	public void setTTL(int ttl) {
		this.ttl = ttl;
	}
	
	public void setRData(String rData) {
		this.rData = rData;
	}
	
	/* ======= GETTERS ======*/
	
	public boolean getAuth() {
		return auth;
	}
	
	public int getSize() {
		return size;
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
	
}

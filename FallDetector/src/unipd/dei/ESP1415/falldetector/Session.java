package unipd.dei.ESP1415.falldetector;

/**
 * This class describes the elements parameters of the ListView in main activity 
 * @author daniellando
 *
 */
public class Session {
	
	private int id;
	private String name;
	private long start; //timestamp
	private long end; //timestamp
	private int falls;
	
	
	public int getFalls() {
		return falls;
	}
	public void setFalls(int falls) {
		this.falls = falls;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}	

}

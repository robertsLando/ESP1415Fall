package unipd.dei.ESP1415.falldetector;

/**
 * This class describes the elements parameters of the ListView in main activity 
 * @author daniellando
 *
 */
public class Session {
	
	private long id;
	private String name;
	private long start; //timestamp
	private long end; //timestamp
	private int falls;
	private String bgColor;
	
	
	public String getBgColor() {
		return bgColor;
	}
	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}
	public int getFalls() {
		return falls;
	}
	public void setFalls(int falls) {
		this.falls = falls;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
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

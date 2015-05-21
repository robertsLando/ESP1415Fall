package unipd.dei.ESP1415.falldetector;

import java.io.Serializable;

/**
 * This class describes the elements parameters of the ListView in main activity 
 * @author daniellando
 *
 */

@SuppressWarnings("serial") 
public class Session implements Serializable{
	
	private long id;
	private String name;
	private long start; //timestamp
	private long end; //timestamp
	private int falls;
	private int bgColor;
	private int imgColor;
	private long timeElapsed;
	private boolean isRunning;
	
	
	public boolean isRunning() {
		return isRunning;
	}
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	public long getTimeElapsed() {
		return timeElapsed;
	}
	public void setTimeElapsed(long timeElapsed) {
		this.timeElapsed = timeElapsed;
	}
	public int getBgColor() {
		return bgColor;
	}
	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
	}
	public int getImgColor() {
		return imgColor;
	}
	public void setImgColor(int imgColor) {
		this.imgColor = imgColor;
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

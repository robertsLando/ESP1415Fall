package unipd.dei.ESP1415.falldetector.falldetailsactivity;

import java.io.Serializable;

/**
 This class describe a fall and is used to generate the graph of the fall event
 @author daniellando
*/

@SuppressWarnings("serial") 
public class FallData implements Serializable{
	
	private long id;
	private long timeX; //the time in the X-axis
	private double accelerationY; //the acceleration value at that time in Y-axis
	private long fallID; //the id of the associated fall
	
	public FallData(){
		id = 0;
		timeX = 0;
		accelerationY = 0;
		fallID = 0;
	}
	
	public long getFallID() {
		return fallID;
	}
	public void setFallID(long fallID) {
		this.fallID = fallID;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getTimeX() {
		return timeX;
	}
	public void setTimeX(long timeX) {
		this.timeX = timeX;
	}
	public double getAccelerationY() {
		return accelerationY;
	}
	public void setAccelerationY(double accelerationY) {
		this.accelerationY = accelerationY;
	}	
	
}
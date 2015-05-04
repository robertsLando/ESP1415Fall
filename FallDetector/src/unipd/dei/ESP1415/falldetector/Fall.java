package unipd.dei.ESP1415.falldetector;

/**
 This class describes the parameters of the ListView we use in the User Interface 2
 where we have to show the list of all falls the person had during the session.
 @author federicobergamin
*/

public class Fall{
	
	private int fallNumber;
	private String date; //we use Calendar class and we create a String
	private String time; //the same of date
	private double latitude; //we use LocationManager
	private double longitude; //we use Location Manager
	private int session; 
	
	//constructor
    public Fall(int id,String date,String time,double latitude,double longitude, int session){
    	this.fallNumber=id;
    	this.date=date;
    	this.time=time;
    	this.latitude=latitude;
    	this.longitude=longitude;
    	this.session=session;
    }
    
	public int getFallNumbe() {
		return fallNumber;
	}
	
	public String getFallDate() {
		return date;
	}
	
	public void setFallDate(String date) {
		this.date=date;
	}
	
	public String getFallTime() {
		return time;
	}
	
	public void setFallTime(String time) {
		this.time=time;
	}
	
	public double getFallLatitude() {
		return latitude;
	}
	
	public void setFallLatitude(double latitude) {
		this.latitude=latitude;
	}
	
	public double getFallLongitude() {
		return longitude;
	}
	
	public void setFallLongitude(double longitude) {
		this.longitude=longitude;
	}
	
	public int getSession() {
		return session;
	}
	
	public void setFallSession(int session) {
		this.session=session;
	}
}
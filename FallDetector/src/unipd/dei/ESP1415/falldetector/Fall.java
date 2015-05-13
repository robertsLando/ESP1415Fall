package unipd.dei.ESP1415.falldetector;

/**
 This class describes the parameters of the ListView we use in the User Interface 2
 where we have to show the list of all falls the person had during the session.
 @author federicobergamin
*/

public class Fall{
	
	private long id;
	private String location; 
	private long datef; 
	private int sessionID;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public long getDatef() {
		return datef;
	}
	public void setDatef(long datef) {
		this.datef = datef;
	}
	public int getSessionID() {
		return sessionID;
	}
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	} 	
	
	
}
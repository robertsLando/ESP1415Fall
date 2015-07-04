package unipd.dei.ESP1415.falldetector.sessiondetails;

import java.io.Serializable;

/**
 This class describes the parameters of the ListView we use in the User Interface 2
 where we have to show the list of all falls the person had during the session.
 @author federicobergamin
*/

@SuppressWarnings("serial") 
public class Fall implements Serializable{
	
	private long id;
	private String location; 
	private long datef; 
	private long sessionID;
	
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
	public long getSessionID() {
		return sessionID;
	}
	public void setSessionID(long sessionID) {
		this.sessionID = sessionID;
	} 	
	
	
}
package unipd.dei.ESP1415.falldetector.preferences;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Helper implements Serializable{
	
	private String name;
	private String surname;
	private long priority;
	private String email;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public long getPriority() {
		return priority;
	}
	public void setPriority(long priority) {
		this.priority = priority;
	}
	

}

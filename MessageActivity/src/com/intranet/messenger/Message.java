package com.color.speechbubble;

public class Message {
	
	private long id;
	private String message;
	private String sender;
	private String receiver;
	private int statusMessage;
	private String isMine;
	
	public long getId(){
		return id;
	}
	public void setId(long id){
		this.id=id;	
	}
	public String getMessage(){
		return message;
	}
	public void setMessage(String message){
		this.message=message;
	}
	public void setSender(String sender){
		this.sender=sender;
	}
	public String getSender(){
		return this.sender;
	}
	public void setReceiver(String receiver){
		this.receiver = receiver;
	}
	public String getReceiver(){
		return this.receiver;
	}
	public void setStatusMessage(int status){
		this.statusMessage = status;
	}
	public int isStatusMessage(){
		return statusMessage;
	}
	//will be used by the ArrayAdapter in the ListView
	@Override
	public String toString(){
		return message;
	}

}

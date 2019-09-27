package com.example.chatwithusingfirebase.Model;

public class Chat {
	private String message;
	private String reciver;
	private String sender, timestamp, image, video;
	private boolean isseen;

	public Chat(String message, String reciver, String sender, String timestamp, String image, String video, boolean isseen) {
		this.message = message;
		this.reciver = reciver;
		this.sender = sender;
		this.timestamp = timestamp;
		this.image = image;
		this.video = video;
		this.isseen = isseen;
	}

	public Chat() {
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReciver() {
		return reciver;
	}

	public void setReciver(String reciver) {
		this.reciver = reciver;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isIsseen() {
		return isseen;
	}

	public void setIsseen(boolean isseen) {
		this.isseen = isseen;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}
}

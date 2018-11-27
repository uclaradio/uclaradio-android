package com.uclaradio.uclaradio.chat;

public class ChatMessage {
  private String body, user, time;
  private int id;

  public ChatMessage(int id, String user, String body, String time) {
    this.id = id;
    this.user = user;
    this.body = body;
    this.time = time;
  }

  public int getId() { return id; }
  
  public String getUser() { return user; }

  public String getBody() { return body; }

  public String getTime() { return time; }

  public void setId(int id) { this.id = id; }

  public void setUser(String user) { this.user = user; }

  public void setBody(String body) { this.body = body; }

  public void setTime(String time) { this.time = time; }
}

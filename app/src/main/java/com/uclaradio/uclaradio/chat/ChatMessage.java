package com.uclaradio.uclaradio.chat;

import com.google.gson.annotations.SerializedName;

public class ChatMessage {

  @SerializedName("text")
  private String body;
  @SerializedName("user")
  private String user;
  @SerializedName("time")
  private String time;
  @SerializedName("id")
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

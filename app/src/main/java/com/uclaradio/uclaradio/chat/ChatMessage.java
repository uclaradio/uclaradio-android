package com.uclaradio.uclaradio.chat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.google.gson.annotations.SerializedName;

public class ChatMessage {

  @SerializedName("text")
  private String body;
  @SerializedName("user")
  private String user;
  @SerializedName("date")
  private String date;
  @SerializedName("id")
  private int id;

  public ChatMessage(int id, String user, String body, String date) {
    this.id = id;
    this.user = user;
    this.body = body;
    this.date = date;
  }

  public int getId() { return id; }
  
  public String getUser() { return user; }

  public String getBody() { return body; }

  public String getDate() { return date; }

  public String getTime() {
    SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    oldFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    SimpleDateFormat newFormat  = new SimpleDateFormat("HH:mm:ss");
    newFormat.setTimeZone(Calendar.getInstance().getTimeZone());
    Date formattedDate = new Date();
    try {
      formattedDate = oldFormat.parse(date);
      return newFormat.format(formattedDate).toString();
    } catch (ParseException ex) {
      ex.printStackTrace();
      return "--:--:--"; // Return "null" time
    }
  }

  public void setId(int id) { this.id = id; }

  public void setUser(String user) { this.user = user; }

  public void setBody(String body) { this.body = body; }

  public void setDate(String date) { this.date = date; }
}

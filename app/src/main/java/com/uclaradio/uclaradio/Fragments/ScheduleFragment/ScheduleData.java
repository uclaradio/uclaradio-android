package com.uclaradio.uclaradio.Fragments.ScheduleFragment;

import com.google.gson.annotations.SerializedName;

public class ScheduleData {

  @SerializedName("username")
  private String username;

  @SerializedName("title")
  private String title;

  @SerializedName("time")
  private String time;

  @SerializedName("day")
  private String day;

  @SerializedName("genre")
  private String genre;

  @SerializedName("picture")
  private String pictureUrl;


  public String getUsername() {
    return username;
  }

  public String getTitle() {
    return title;
  }

  public String getTime() {
    return time;
  }

  public String getDay() {
    return day;
  }

  public String getGenre() {
    return genre;
  }

  public String getPictureUrl() {
    return pictureUrl;
  }


}

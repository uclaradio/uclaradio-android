package com.uclaradio.uclaradio.Fragments.ScheduleFragment;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

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

  @SerializedName("blurb")
  private String blurb;

  @SerializedName("djs")
  private LinkedHashMap<String, String> djs;

  private boolean newDay = false;


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

  public String getBlurb() {
    return blurb;
  }

  public String getDjs() {
    StringBuilder djString = new StringBuilder();
    ArrayList<String> djList = new ArrayList<>(djs.values());

    switch (djList.size()) {
      case 0:
        break;
      case 1:
        djString.append(djList.get(0));
        break;
      case 2:
        djString.append(djList.get(0))
                .append(" and ")
                .append(djList.get(1));
        break;
      default:
        for (int i = 0; i < djList.size()-1; i++) {
          djString.append(djList.get(i))
                  .append(", ");
        }
        djString.append("and ")
                .append(djList.get(djList.size()-1));
    }
    return djString.toString();
  }

  public boolean isOnNewDay() {
    return newDay;
  }

  public void setIsNewDay(boolean isNewDay) {
    newDay = isNewDay;
  }
}

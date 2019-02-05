package com.uclaradio.uclaradio.fragments.schedule;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ScheduleList {

  @SerializedName("shows")
  private List<ScheduleData> showsList;

  public List<ScheduleData> getScheduleList() {
    return showsList;
  }
}

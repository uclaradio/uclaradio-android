package com.uclaradio.uclaradio.fragments.djs;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DjList {
  @SerializedName("djs")
  private List<DjData> djList;

  public List<DjData> getDjList() {
    return djList;
  }
}

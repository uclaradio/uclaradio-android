package com.uclaradio.uclaradio.fragments.djs;

import com.google.gson.annotations.SerializedName;

public class DjData {

  @SerializedName("username")
  private String username;

  @SerializedName("djName")
  private String djName;

  @SerializedName("picture")
  private String pictureUrl;

  public String getPictureUrl() {
    return pictureUrl;
  }

  public String getUsername() {
    return username;
  }

  public String getDjName() {
    return djName;
  }
}

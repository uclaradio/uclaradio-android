package com.uclaradio.uclaradio.Fragments.DJsFragment;

import com.google.gson.annotations.SerializedName;

public class DjData {

  @SerializedName("username")
  private String username;

  @SerializedName("djName")
  private String djName;

  @SerializedName("picture")
  private String pictureUrl;

//    @SerializedName("fullName")
//    private String fullName;

//    @SerializedName("email")
//    private String email;


  public String getPictureUrl() {
    return pictureUrl;
  }

  public String getUsername() {
    return username;
  }

  public String getDjName() {
    return djName;
  }

//    public String getFullName() {
//        return fullName;
//    }
//
//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
}

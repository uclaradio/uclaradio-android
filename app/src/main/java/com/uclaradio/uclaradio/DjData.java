package com.uclaradio.uclaradio;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tanzeelakhan on 4/1/17.
 */

public class DjData {

    @SerializedName("username")
    private String username;

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

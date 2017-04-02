package com.uclaradio.uclaradio;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tanzeelakhan on 4/1/17.
 */

public class ShowSchedule {

    @SerializedName("shows")
    private List<String> shows;

    public List<String> getShows() {
        return shows;
    }
}

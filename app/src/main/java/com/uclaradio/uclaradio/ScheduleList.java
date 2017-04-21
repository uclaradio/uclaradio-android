package com.uclaradio.uclaradio;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tanzeelakhan on 4/1/17.
 */

public class ScheduleList {

    @SerializedName("shows")
    private List<ScheduleData> showsList;

    public List<ScheduleData> getScheduleList() {
        return showsList;
    }
}

package com.uclaradio.uclaradio;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tanzeelakhan on 4/1/17.
 */

public class DjList {

    @SerializedName("djs")
    private List<DjData> djList;

    public List<DjData> getDjList() {
        return djList;
    }
}

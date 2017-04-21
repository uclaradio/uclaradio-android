package com.uclaradio.uclaradio;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by tanzeelakhan on 4/1/17.
 */

public interface RadioPlatform {

    @GET("/api/djs")
    Call<DjList> getDjs();

    @GET("/api/schedule")
    Call<ScheduleList> getSchedules();
}

package com.uclaradio.uclaradio.interfaces;

import com.uclaradio.uclaradio.fragments.djs.DjList;
import com.uclaradio.uclaradio.fragments.schedule.ScheduleData;
import com.uclaradio.uclaradio.fragments.schedule.ScheduleList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RadioPlatform {
  @GET("/api/djs")
  Call<DjList> getDjs();

  @GET("/api/schedule")
  Call<ScheduleList> getSchedules();

  @GET("/api/nowplaying")
  Call<ScheduleData> getCurrentShow();
}

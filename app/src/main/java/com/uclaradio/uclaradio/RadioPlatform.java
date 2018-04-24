package com.uclaradio.uclaradio;

import com.uclaradio.uclaradio.Fragments.DJsFragment.DjList;
import com.uclaradio.uclaradio.Fragments.ScheduleFragment.ScheduleData;
import com.uclaradio.uclaradio.Fragments.ScheduleFragment.ScheduleList;

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

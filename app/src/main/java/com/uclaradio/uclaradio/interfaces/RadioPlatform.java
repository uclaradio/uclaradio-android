package com.uclaradio.uclaradio.interfaces;

import java.util.List;

import com.uclaradio.uclaradio.fragments.djs.DjList;
import com.uclaradio.uclaradio.fragments.schedule.ScheduleData;
import com.uclaradio.uclaradio.fragments.schedule.ScheduleList;
import com.uclaradio.uclaradio.chat.ChatMessage;
import com.uclaradio.uclaradio.chat.ChatMessageRequest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;

public interface RadioPlatform {
  @GET("/api/djs")
  Call<DjList> getDjs();

  @GET("/api/schedule")
  Call<ScheduleList> getSchedules();

  @GET("/api/nowplaying")
  Call<ScheduleData> getCurrentShow();

  // For chat
  @POST("/chat/getNext")
  Call<List<ChatMessage>> getNextMessages(@Body ChatMessageRequest request);
}

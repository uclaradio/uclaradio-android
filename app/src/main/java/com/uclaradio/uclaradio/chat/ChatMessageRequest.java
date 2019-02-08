package com.uclaradio.uclaradio.chat;

import com.google.gson.annotations.SerializedName;
import java.lang.Integer;

public class ChatMessageRequest {

  @SerializedName("id")
  private Integer id;

  @SerializedName("volume")
  private int volume;

  public ChatMessageRequest(Integer id, int volume) {
    this.id = id;
    this.volume = volume;
  }

  public ChatMessageRequest(int volume) {
    this(null, volume);
  }
}

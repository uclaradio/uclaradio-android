package com.uclaradio.uclaradio.chat;

import java.lang.Integer;

public class ChatMessageRequest {
  private Integer id;
  private int volume;

  public ChatMessageRequest(Integer id, int volume) {
    this.id = id;
    this.volume = volume;
  }

  public ChatMessageRequest(int volume) {
    this(null, volume);
  }
}

package com.uclaradio.uclaradio.chat;

import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.uclaradio.uclaradio.R;

public class SentMessageHolder extends RecyclerView.ViewHolder {
  TextView bodyText, userText, timeText;

  SentMessageHolder(View itemView) {
    super(itemView);

    bodyText = (TextView) itemView.findViewById(R.id.message_body_text);
    userText = (TextView) itemView.findViewById(R.id.message_user_text);
    timeText = (TextView) itemView.findViewById(R.id.message_time_text);
  }

  void bind(ChatMessage message) {
    bodyText.setText(message.getBody());
    timeText.setText(message.getTime());
    userText.setText(message.getUser());
  }
}

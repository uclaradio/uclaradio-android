package com.uclaradio.uclaradio.chat;

import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;

import com.uclaradio.uclaradio.R;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
  TextView bodyText, userText, timeText;

  ReceivedMessageHolder(View itemView) {
    super(itemView);

    bodyText = (TextView) itemView.findViewById(R.id.message_body_text);
    userText = (TextView) itemView.findViewById(R.id.message_user_text);
    timeText = (TextView) itemView.findViewById(R.id.message_time_text);
  }

  void bind(ChatMessage message) {
    bodyText.setText(message.getBody());
    bodyText.getBackground()
      .setColorFilter(Color.parseColor("#ffffff"), Mode.SRC_IN);

    timeText.setText(message.getTime());
    userText.setText(message.getUser());
  }
}

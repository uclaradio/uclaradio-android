package com.uclaradio.uclaradio.chat;

import java.util.List;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;

import com.uclaradio.uclaradio.R;

public class MessageListAdapter extends RecyclerView.Adapter {
  private Context context;
  private List<ChatMessage> messages;

  private static final int VIEW_TYPE_MESSAGE_SENT     = 0;
  private static final int VIEW_TYPE_MESSAGE_RECEIVED = 1;

  public MessageListAdapter(Context context, List<ChatMessage> messages) {
    this.context = context;
    this.messages = messages;
  }

  @Override
  public int getItemCount() {
    return messages.size();
  }

  @Override
  public int getItemViewType(int position) {
    ChatMessage message = (ChatMessage) messages.get(position);

    // TODO: Change this condition to something better
    if (message.getUser().equals("Guest121"))
      return VIEW_TYPE_MESSAGE_SENT;
    else
      return VIEW_TYPE_MESSAGE_RECEIVED;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;

    if (viewType == VIEW_TYPE_MESSAGE_SENT) {
      view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_message_sent, parent, false);
      return new SentMessageHolder(view);
    } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
      view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_message_received, parent, false);
      return new ReceivedMessageHolder(view);
    }

    return null;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    ChatMessage message = (ChatMessage) messages.get(position);
    int viewType = getItemViewType(position);

    if (viewType == VIEW_TYPE_MESSAGE_SENT)
      ((SentMessageHolder) holder).bind(message);
    else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED)
      ((ReceivedMessageHolder) holder).bind(message);
  }
}

package com.example.chatwithusingfirebase.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.chatwithusingfirebase.MessageActivity;
import com.example.chatwithusingfirebase.Model.Chat;
import com.example.chatwithusingfirebase.Model.User;
import com.example.chatwithusingfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

	public static final int MSG_TYPE_LEFT = 0;
	public static final int MSG_TYPE_RIGHT = 1;
	private Context context;
	private List<Chat> chatList;
	private String imageURL;
	FirebaseUser firebaseUser;

	public MessageAdapter(Context context, List<Chat> chatList, String imageURL) {
		this.context = context;
		this.chatList = chatList;
		this.imageURL = imageURL;
	}

	@NonNull
	@Override
	public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int resource;
		if(viewType == MSG_TYPE_RIGHT)
		{
			resource = R.layout.chat_item_right;
		}
		else
		{
			resource = R.layout.chat_item_left;
		}
		View view = inflater.inflate(resource, parent, false);
		MessageAdapter.MessageViewHolder messageViewHolder = new MessageAdapter.MessageViewHolder(view);
		return messageViewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
		Chat chat = chatList.get(position);

		holder.show_message.setText(chat.getMessage());
		if(imageURL.equals("default"))
		{
			holder.profile_image.setImageResource(R.mipmap.ic_launcher);
		}
		else
		{
			Glide.with(context).load(imageURL).into(holder.profile_image);
		}

		if(position == chatList.size() - 1)
		{
			if(chat.isIsseen())
			{
				holder.txt_seen.setText("Seen");
			}
			else
			{
				holder.txt_seen.setText("Delivered");
			}
		}
		else
		{
			holder.txt_seen.setVisibility(View.GONE);
		}
		if (!chat.getImage().equals("noImage"))
		{
			holder.img_chat.setVisibility(View.VISIBLE);
			try {
				Glide.with(context).load(chat.getImage()).into(holder.img_chat);
			}
			catch (Exception ex)
			{

			}
		}
		else
		{
			holder.img_chat.setVisibility(View.GONE);
		}
		if (!chat.getVideo().equals("noVideo"))
		{
			holder.video_chat.setVisibility(View.VISIBLE);
			Uri uri = Uri.parse(chat.getVideo());
			holder.video_chat.setVideoURI(uri);
			holder.video_chat.start();
		}
		else
		{
			holder.video_chat.setVisibility(View.GONE);
		}
	}

	@Override
	public int getItemCount() {
		return chatList.size();
	}

	public class MessageViewHolder extends RecyclerView.ViewHolder {

		CircleImageView profile_image;
		TextView show_message, txt_seen;
		LinearLayout layout_message;
		ImageView img_chat;
		VideoView video_chat;

		public MessageViewHolder(@NonNull View itemView) {
			super(itemView);

			profile_image = itemView.findViewById(R.id.profile_image);
			show_message = itemView.findViewById(R.id.show_message);
			txt_seen = itemView.findViewById(R.id.txt_seen);
			layout_message = itemView.findViewById(R.id.layout_message);
			img_chat = itemView.findViewById(R.id.img_chat);
			video_chat = itemView.findViewById(R.id.video_chat);
		}
	}

	@Override
	public int getItemViewType(int position) {
		firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		if(chatList.get(position).getSender().equals(firebaseUser.getUid()))
		{
			return MSG_TYPE_RIGHT;
		}
		else
		{
			return MSG_TYPE_LEFT;
		}
	}
}
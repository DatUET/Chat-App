package com.example.chatwithusingfirebase.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatwithusingfirebase.MessageActivity;
import com.example.chatwithusingfirebase.Model.Chat;
import com.example.chatwithusingfirebase.Model.User;
import com.example.chatwithusingfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

	private Context context;
	private List<User> userList;
	private Boolean isChat;

	String theLastMessage;

	public UserAdapter(Context context, List<User> userList, Boolean isChat) {
		this.context = context;
		this.userList = userList;
		this.isChat = isChat;
	}

	@NonNull
	@Override
	public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.user_item, parent, false);
		UserViewHolder userViewHolder = new UserViewHolder(view);
		return userViewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
		final User user = userList.get(position);
		holder.username.setText(user.getUsername());
		if(user.getImageURL().equals("default"))
		{
			holder.circleImageView.setImageResource(R.mipmap.ic_launcher);
		}
		else
		{
			Glide.with(context).load(user.getImageURL()).into(holder.circleImageView);
		}

		if (isChat)
		{
			lastMessage(user.getId(), holder.last_msg);
		}
		else
		{
			holder.last_msg.setVisibility(View.GONE);
		}

		if(isChat)
		{
			if(user.getStatus().equals("online"))
			{
				holder.img_on.setVisibility(View.VISIBLE);
				holder.img_off.setVisibility(View.GONE);
			}
			else
			{
				holder.img_on.setVisibility(View.GONE);
				holder.img_off.setVisibility(View.VISIBLE);
			}
		}
		else
		{
			holder.img_on.setVisibility(View.GONE);
			holder.img_off.setVisibility(View.GONE);
		}
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, MessageActivity.class);
				intent.putExtra("userid", user.getId());
				context.startActivity(intent);
			}
		});
	}

	@Override
	public int getItemCount() {
		return userList.size();
	}

	public class UserViewHolder extends RecyclerView.ViewHolder {

		CircleImageView circleImageView, img_on, img_off;
		TextView username, last_msg;

		public UserViewHolder(@NonNull View itemView) {
			super(itemView);

			circleImageView = itemView.findViewById(R.id.profile_image);
			username = itemView.findViewById(R.id.username);
			img_on = itemView.findViewById(R.id.img_on);
			img_off = itemView.findViewById(R.id.img_off);
			last_msg = itemView.findViewById(R.id.last_msg);
		}
	}

	private void lastMessage(final String userid, final TextView last_msg)
	{
		theLastMessage = "default";
		final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				for(DataSnapshot snapshot : dataSnapshot.getChildren())
				{
					Chat chat = snapshot.getValue(Chat.class);
					assert chat != null;
					assert firebaseUser != null;
					if( (chat.getReciver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) ||
							(chat.getReciver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) )
					{
						theLastMessage = chat.getMessage();
					}
				}
				switch (theLastMessage)
				{
					case "default":
						last_msg.setText("No Message");
						break;

					default:
						last_msg.setText(theLastMessage);
						break;
				}
				theLastMessage = "default";
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}
}

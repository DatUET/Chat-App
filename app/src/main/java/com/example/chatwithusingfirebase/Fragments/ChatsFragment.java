package com.example.chatwithusingfirebase.Fragments;

import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatwithusingfirebase.Adapter.UserAdapter;
import com.example.chatwithusingfirebase.Model.ChatList;
import com.example.chatwithusingfirebase.Model.User;
import com.example.chatwithusingfirebase.Notification.Token;
import com.example.chatwithusingfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

	RecyclerView recycler_view;

	UserAdapter userAdapter;
	List<User> userList;

	FirebaseUser firebaseUser;
	DatabaseReference reference;

	List<ChatList> usernameList;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chats, container, false);

		recycler_view = view.findViewById(R.id.recycler_view);
		recycler_view.setHasFixedSize(true);
		recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

		firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		usernameList = new ArrayList<>();

		reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				usernameList.clear();
				for(DataSnapshot snapshot : dataSnapshot.getChildren())
				{
					ChatList chatlist = snapshot.getValue(ChatList.class);
					usernameList.add(chatlist);

				}
				Log.d("Chat list", dataSnapshot.toString());
				chatList();
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});

		updateToken(FirebaseInstanceId.getInstance().getToken());

		return view;
	}

	private void updateToken(String token)
	{
		DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Tokens");
		Token token1 = new Token(token);
		reference1.child(firebaseUser.getUid()).setValue(token1);
	}

	private void chatList() {
		userList = new ArrayList<>();
		reference = FirebaseDatabase.getInstance().getReference("Users");
		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				userList.clear();
				for(DataSnapshot snapshot : dataSnapshot.getChildren())
				{
					User user = snapshot.getValue(User.class);
					for (ChatList chatList : usernameList)
					{
						assert user != null;
						if(user.getId().equals(chatList.getId())) {
							userList.add(user);

						}
					}
				}
				Log.d("size userlist", userList.size() + " - " + usernameList.size() + dataSnapshot.toString());
				userAdapter = new UserAdapter(getContext(), userList, true);
				recycler_view.setAdapter(userAdapter);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}
}

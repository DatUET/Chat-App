package com.example.chatwithusingfirebase.Fragments;

import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.chatwithusingfirebase.Adapter.UserAdapter;
import com.example.chatwithusingfirebase.Model.User;
import com.example.chatwithusingfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class UserFragment extends Fragment {

	RecyclerView recyclerView;
	EditText search_user;
	UserAdapter userAdapter;
	List<User> userList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.fragment_user, container, false);

		recyclerView = view.findViewById(R.id.recycler_view);
		recyclerView.setHasFixedSize(true);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(layoutManager);

		userList = new ArrayList<>();
		readUser();

		search_user = view.findViewById(R.id.search_user);
		search_user.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				searchUser(charSequence.toString().toLowerCase());
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});
		return view;

	}

	private void searchUser(String name) {
		final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
				.startAt(name).endAt(name + "\uf8ff");
		query.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				userList.clear();
				for(DataSnapshot snapshot : dataSnapshot.getChildren())
				{
					User user = snapshot.getValue(User.class);

					assert user != null;
					assert firebaseUser != null;
					if(!user.getId().equals(firebaseUser.getUid()))
					{
						userList.add(user);
					}
				}

				userAdapter = new UserAdapter(getContext(), userList, false);
				recyclerView.setAdapter(userAdapter);

			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}

	private void readUser() {
		final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if (search_user.getText().toString().equals("")) {
					userList.clear();
					for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
						User user = snapshot.getValue(User.class);

						assert user != null;
						assert firebaseUser != null;
						if (!user.getId().equals(firebaseUser.getUid())) {
							userList.add(user);
						}
					}
					userAdapter = new UserAdapter(getContext(), userList, false);
					recyclerView.setAdapter(userAdapter);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}
}

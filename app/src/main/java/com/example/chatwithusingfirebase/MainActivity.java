package com.example.chatwithusingfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatwithusingfirebase.Adapter.ViewPagerAdapter;
import com.example.chatwithusingfirebase.Fragments.ChatsFragment;
import com.example.chatwithusingfirebase.Fragments.ProfileFragment;
import com.example.chatwithusingfirebase.Fragments.UserFragment;
import com.example.chatwithusingfirebase.Model.Chat;
import com.example.chatwithusingfirebase.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

	CircleImageView profile_image;
	TextView username;
	Toolbar toolbar;
	TabLayout tabLayout;
	ViewPager viewPager;

	FirebaseUser firebaseUser;
	DatabaseReference reference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		addControl();
		addEvent();
	}

	private void addEvent() {

	}

	private void addControl() {
		profile_image = findViewById(R.id.profile_image);
		username = findViewById(R.id.username);
		tabLayout = findViewById(R.id.tab_layout);
		viewPager = findViewById(R.id.view_pager);

		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("");
		getSupportActionBar().setDisplayShowTitleEnabled(true);

		firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

		reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				User user = dataSnapshot.getValue(User.class);
				username.setText(user.getUsername());
				if(user.getImageURL().equals("default"))
				{
					profile_image.setImageResource(R.mipmap.ic_launcher);
				}
				else
				{
					Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});

		reference = FirebaseDatabase.getInstance().getReference("Chats");
		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
				int unread = 0;
				for(DataSnapshot snapshot : dataSnapshot.getChildren())
				{
					Chat chat = snapshot.getValue(Chat.class);
					if(chat.getReciver().equals(firebaseUser.getUid()) && !chat.isIsseen())
					{
						unread++;
					}
				}
				if(unread == 0)
				{
					viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
				}
				else
				{
					viewPagerAdapter.addFragment(new ChatsFragment(), "(" + unread + ") Chats");
				}
				viewPagerAdapter.addFragment(new UserFragment(), "Users");
				viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");
				viewPager.setAdapter(viewPagerAdapter);
				tabLayout.setupWithViewPager(viewPager);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});



	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId())
		{
			case R.id.logout:
				FirebaseAuth.getInstance().signOut();
				Intent intent = new Intent(MainActivity.this, StartActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
		}
		return false;
	}

	private void status(String status)
	{
		reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

		HashMap<String, Object> hashMap = new HashMap<>();
		hashMap.put("status", status);

		reference.updateChildren(hashMap);
	}

	@Override
	protected void onResume() {
		super.onResume();

		status("online");
	}

	@Override
	protected void onPause() {
		super.onPause();

		status("offline");
	}
}

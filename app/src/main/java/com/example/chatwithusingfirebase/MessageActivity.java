package com.example.chatwithusingfirebase;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.chatwithusingfirebase.Adapter.MessageAdapter;
import com.example.chatwithusingfirebase.Notification.APIService;
import com.example.chatwithusingfirebase.Model.Chat;
import com.example.chatwithusingfirebase.Model.User;
import com.example.chatwithusingfirebase.Notification.Client;
import com.example.chatwithusingfirebase.Notification.Data;
import com.example.chatwithusingfirebase.Notification.MyReponse;
import com.example.chatwithusingfirebase.Notification.Sender;
import com.example.chatwithusingfirebase.Notification.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

	private static final int REQUEST_STORAGE = 400;
	private static final int REQUEST_GALLERY = 500;
	private static final int REQUEST_VIDEO = 600;
	private static final int REQUEST_STORAGE_VIDEO = 700;

	CircleImageView profile_image;
	TextView username;
	Toolbar toolbar;
	ImageButton btn_send, btn_img_send, btn_video_send;
	EditText text_send;
	MessageAdapter messageAdapter;
	List<Chat> chatList;
	RecyclerView recyclerView;
	LinearLayout layout_videochat, layout_imgchat;
	ImageView img_delete_video, img_delete, img_chat;
	VideoView video_chat;
	MediaController mediaController;

	FirebaseUser firebaseUser;
	DatabaseReference reference;

	ValueEventListener seenListener;
	String uid;

	Uri imgUri = null, videoUri = null;

	APIService apiService;
	boolean notify = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);

		profile_image = findViewById(R.id.profile_image);
		username = findViewById(R.id.username);
		btn_send = findViewById(R.id.btn_send);
		btn_img_send = findViewById(R.id.btn_img_send);
		btn_video_send = findViewById(R.id.btn_video_send);
		text_send = findViewById(R.id.text_send);
		layout_imgchat = findViewById(R.id.layout_imgchat);
		layout_videochat = findViewById(R.id.layout_videochat);
		img_chat = findViewById(R.id.img_chat);
		video_chat = findViewById(R.id.video_chat);
		img_delete_video = findViewById(R.id.img_delete_video);
		img_delete = findViewById(R.id.img_delete);

		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		recyclerView = findViewById(R.id.recycler_view);
		recyclerView.setHasFixedSize(true);
		LinearLayoutManager layoutManager = new LinearLayoutManager(MessageActivity.this);
		layoutManager.setStackFromEnd(true);
		recyclerView.setLayoutManager(layoutManager);

		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MessageActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

		Intent intent = getIntent();
		uid = intent.getStringExtra("userid");
		firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

		btn_send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				notify = true;
				String msg = text_send.getText().toString();
				if (msg.equals("") && layout_imgchat.getVisibility() == View.GONE && layout_videochat.getVisibility() == View.GONE)
				{

				}
				else
				{
					sendMessage(firebaseUser.getUid(), uid, msg);
					text_send.setText("");
					layout_imgchat.setVisibility(View.GONE);
					layout_videochat.setVisibility(View.GONE);
					imgUri = null;
					videoUri = null;
				}
			}
		});

		img_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layout_imgchat.setVisibility(View.GONE);
				img_chat.setImageDrawable(null);
				imgUri = null;
			}
		});

		img_delete_video.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layout_videochat.setVisibility(View.GONE);
				video_chat.setVideoURI(null);
				videoUri = null;
			}
		});

		assert uid != null;
		reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				User user = dataSnapshot.getValue(User.class);
				username.setText(user.getUsername());
				if (user.getImageURL().equals("default")) {
					profile_image.setImageResource(R.mipmap.ic_launcher);
				} else {
					Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
				}

				readMessage(firebaseUser.getUid(), uid, user.getImageURL());
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});

		btn_img_send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pickImage();
			}
		});

		btn_video_send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pickVideo();
			}
		});

		seenMessage(uid);
	}

	private void pickVideo() {
		if(ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
		{
			ActivityCompat.requestPermissions(MessageActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_VIDEO);
		}
		else
		{
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("video/*");
			startActivityForResult(intent, REQUEST_VIDEO);
		}
	}

	private void pickImage() {
		if(ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
		{
			ActivityCompat.requestPermissions(MessageActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
		}
		else
		{
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, REQUEST_GALLERY);
		}
	}

	private void seenMessage(final String userid)
	{
		reference = FirebaseDatabase.getInstance().getReference("Chats");
		seenListener = reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				for(DataSnapshot snapshot : dataSnapshot.getChildren())
				{
					Chat chat = snapshot.getValue(Chat.class);
					if(chat.getReciver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid))
					{
						HashMap<String, Object> hashMap = new HashMap<>();
						hashMap.put("isseen", true);
						snapshot.getRef().updateChildren(hashMap);

					}
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}

	private void sendMessage(String sender, final String reciver, String message)
	{
		final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
		final String timeStamp = String.valueOf(System.currentTimeMillis());
		final HashMap<String, Object> hashMap = new HashMap<>();
		hashMap.put("sender", sender);
		hashMap.put("reciver", reciver);
		hashMap.put("timestamp", timeStamp);
		hashMap.put("message", message);
		hashMap.put("isseen", false);
		if(imgUri == null && videoUri == null)
		{
			hashMap.put("image", "noImage");
			hashMap.put("video", "noVideo");
			databaseReference.child("Chats").push().setValue(hashMap);
		}
		else
		{
			if(imgUri != null)
			{
				String filepathAndName = "ChatsImg/" + "chat_" + timeStamp;
				StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filepathAndName);
				storageReference.putFile(imgUri)
						.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
							@Override
							public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
								Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
								while (!uriTask.isSuccessful());

								String downloadUri = uriTask.getResult().toString();
								if(uriTask.isSuccessful())
								{
									hashMap.put("image", downloadUri);
									hashMap.put("video", "noVideo");
									databaseReference.child("Chats").push().setValue(hashMap);
								}
							}
						})
						.addOnFailureListener(new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {
								Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							}
						});
			}
			else
			{
				if(videoUri != null) {
					String filepathAndName = "ChatsVideo/" + "chat_" + timeStamp;
					Log.d("load video", "cay vl");
					StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filepathAndName);
					storageReference.putFile(videoUri)
							.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
								@Override
								public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
									Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
									while (!uriTask.isSuccessful()) ;
									String downloadUri = uriTask.getResult().toString();
									if (uriTask.isSuccessful()) {
										hashMap.put("image", "noImage");
										hashMap.put("video", downloadUri);
										databaseReference.child("Chats").push().setValue(hashMap);
									}
								}
							})
							.addOnFailureListener(new OnFailureListener() {
								@Override
								public void onFailure(@NonNull Exception e) {
									Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
								}
							});
				}
			}
		}

		final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
				.child(firebaseUser.getUid())
				.child(uid);
		chatRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if(!dataSnapshot.exists())
				{
					chatRef.child("id").setValue(uid);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});

		final String msg = message;
		reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				User user = dataSnapshot.getValue(User.class);
				if(notify) {
					sendNotification(reciver, user.getUsername(), msg);
				}
				notify = false;
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}

	private void sendNotification(String reciver, final String username, final String msg) {
		DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
		Query query = tokens.orderByKey().equalTo(reciver);
		query.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				for (DataSnapshot snapshot : dataSnapshot.getChildren())
				{
					Token token = snapshot.getValue(Token.class);
					Log.d("Token", token + "");
					Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username + ": " + msg, "New Message", uid);
					Sender sender = new Sender(data, token.getToken());

					apiService.sendNotification(sender)
							.enqueue(new Callback<MyReponse>() {
								@Override
								public void onResponse(Call<MyReponse> call, Response<MyReponse> response) {

								}

								@Override
								public void onFailure(Call<MyReponse> call, Throwable t) {

								}
							});
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}

	private void readMessage(final String myid, final String userid, final String imgurl)
	{
		chatList = new ArrayList<>();

		reference = FirebaseDatabase.getInstance().getReference("Chats");
		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				chatList.clear();
				for(DataSnapshot snapshot : dataSnapshot.getChildren())
				{
					Log.d("data firebase", dataSnapshot.toString());
					Chat chat = snapshot.getValue(Chat.class);
					assert chat != null;
					if((chat.getReciver().equals(myid) && chat.getSender().equals(userid)) ||
							(chat.getReciver().equals(userid) && chat.getSender().equals(myid)))
					{
						chatList.add(chat);
					}
				}

				messageAdapter = new MessageAdapter(MessageActivity.this, chatList, imgurl);
				recyclerView.setAdapter(messageAdapter);
				messageAdapter.notifyDataSetChanged();
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}

	private void status(String status)
	{
		reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

		HashMap<String, Object> hashMap = new HashMap<>();
		hashMap.put("status", status);

		reference.updateChildren(hashMap);
	}

	private void currentUser(String userid)
	{
		SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
		editor.putString("currentuser", userid);
		editor.apply();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if(requestCode == REQUEST_STORAGE)
		{
			if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				pickImage();
			}
			else
			{
				Toast.makeText(MessageActivity.this, "Permission Denied!!!", Toast.LENGTH_LONG).show();

			}
		}
		else if (requestCode == REQUEST_STORAGE_VIDEO) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				pickVideo();
			} else {
				Toast.makeText(MessageActivity.this, "Permission Denied!!!", Toast.LENGTH_LONG).show();

			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if(resultCode == RESULT_OK)
		{
			if(requestCode == REQUEST_GALLERY)
			{
				imgUri = data.getData();
				layout_imgchat.setVisibility(View.VISIBLE);
				img_chat.setImageURI(imgUri);
			}
			else if(requestCode == REQUEST_VIDEO)
			{
				videoUri = data.getData();
				layout_videochat.setVisibility(View.VISIBLE);
				video_chat.setVideoURI(videoUri);
				mediaController = new MediaController(MessageActivity.this);
				mediaController.setAnchorView(video_chat);
				video_chat.setMediaController(mediaController);
				video_chat.start();
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();

		status("online");
		currentUser(uid);
	}

	@Override
	protected void onPause() {
		super.onPause();
		reference.removeEventListener(seenListener);
		status("offline");
		currentUser("none");
	}
}

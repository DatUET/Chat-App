package com.example.chatwithusingfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

	Button login, register;

	FirebaseUser firebaseUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		login = findViewById(R.id.login);
		register = findViewById(R.id.register);

		firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

		if(firebaseUser != null)
		{
			Intent intent = new Intent(StartActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}

		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(StartActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		});

		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

		if(firebaseUser != null)
		{
			Intent intent = new Intent(StartActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}
}

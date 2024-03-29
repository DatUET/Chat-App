package com.example.chatwithusingfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

	EditText send_email;
	Button btn_reset;
	Toolbar toolbar;

	FirebaseAuth firebaseAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);

		addControl();
		addEvent();
	}

	private void addEvent() {
		btn_reset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String email = send_email.getText().toString();

				if(email.equals(""))
				{
					Toast.makeText(ResetPasswordActivity.this, "Email is empty", Toast.LENGTH_LONG).show();
				}
				else
				{
					firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if(task.isSuccessful())
							{
								Toast.makeText(ResetPasswordActivity.this, "Please check your email!", Toast.LENGTH_LONG).show();
								Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
								startActivity(intent);
							}
							else
							{
								Toast.makeText(ResetPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
							}
						}
					});
				}
			}
		});
	}

	private void addControl() {
		send_email = findViewById(R.id.send_email);
		btn_reset = findViewById(R.id.btn_reset);

		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Register");
		getSupportActionBar().setDisplayShowTitleEnabled(true);

		firebaseAuth = FirebaseAuth.getInstance();

	}
}

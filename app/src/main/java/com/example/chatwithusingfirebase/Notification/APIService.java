package com.example.chatwithusingfirebase.Notification;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
	@Headers(
		{
			"Content-Type:application/json",
			"Authorization:key=AAAAGnNo_jI:APA91bFH3PINGvNpvf1FFJh5nXw7w9sLEvvPGKvxVpZb_CwmvW55amOhilGyWr81NNa8xVa0eoKZ484H0vC2bMu6A4LIpEYY6xqW4tyFjfaP409lppBroMr__Wpf81CASp2Im89Z8JoA"
		}
	)


	@POST("fcm/send")
	Call<MyReponse> sendNotification(@Body Sender body);
}

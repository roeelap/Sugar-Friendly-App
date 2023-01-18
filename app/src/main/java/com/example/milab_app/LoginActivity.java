package com.example.milab_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void fetchUser(final View view) {
        final UserFetcher userFetcher = new UserFetcher(view.getContext());

        final String username = "test"; // TODO: get username from login page
        final String password = "test"; // TODO: get password from login page

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();

        userFetcher.dispatchRequest(username, password, new UserFetcher.UserResponseListener() {
            // This method is called when the response is received
            @Override
            public void onResponse(UserFetcher.UserResponse response) {
                progressDialog.hide();

                if (response.isError) {
                    Toast.makeText(view.getContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // get user data from response
                User user = User.getInstance(response.username, response.favDishes, response.favRestaurants,
                        response.recentFoodTags, response.recentNutritionTags);

                // go to home page with user data
                Intent intent = new Intent(view.getContext(), HomePageActivity.class);
                intent.putExtra("user", (Parcelable) user);
                startActivity(intent);
            }
        });
    }
}
package com.example.go4lunch.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import com.example.go4lunch.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;

public class SigninActivity extends AppCompatActivity
{
    final int RC_SIGN_IN = 1;
    final String TAG = "SignInActivity";

    // Multidex purposes
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signIn();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void signIn(){
        Log.d(TAG, "signIn: signin in with Google and FB");
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.AppTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                              new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_baseline_group_24)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: waiting for signin in response");
        if (requestCode == RC_SIGN_IN)
            this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "handleResponseAfterSignIn: actually treating signin in response");
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                Toast.makeText(getApplicationContext(), getString(R.string.logged_in_success),Toast.LENGTH_LONG).show();
                Log.d(TAG, "handleResponseAfterSignIn: logged in successfully");
                finish();
            } else { // ERRORS
                if (response == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.logged_in_failed),Toast.LENGTH_LONG).show();
                    Log.d(TAG, "handleResponseAfterSignIn: logged in failed");
                    signIn();

                } else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(getApplicationContext(), getString(R.string.logged_in_failed_no_network),Toast.LENGTH_LONG).show();
                    Log.d(TAG, "handleResponseAfterSignIn: no network available");
                    signIn();
                } else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(getApplicationContext(), getString(R.string.logged_in_failed_unknown_error),Toast.LENGTH_LONG).show();
                    Log.d(TAG, "handleResponseAfterSignIn: an unknown error has occured during signin in");
                    signIn();
                }
            }
        }
    }

}


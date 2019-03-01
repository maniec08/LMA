package com.mani.lma.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mani.lma.R;
import com.mani.lma.utils.FireBaseHelper;
import com.mani.lma.utils.KeyConstants;
import com.mani.lma.utils.SessionVariables;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    FirebaseUser firebaseUser = null;
    EditText email = null;
    EditText password = null;
    Button loginButton = null;
    FirebaseAuth firebaseAuth = null;
    Context applicationContext = null;
    int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationContext = this;
        // try {
        FirebaseApp.initializeApp(this);

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
                , new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers).build(), RC_SIGN_IN);
        // authenticate();
        //  } catch (Exception e) {
        //     print(e.getMessage());
        // }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                final String email = firebaseUser.getEmail();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference().child(KeyConstants.USER_REF);
                Query query = myRef.orderByChild(KeyConstants.EMAIL_REF).equalTo(email);
                query.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                String lender = ((HashMap) issue.getValue()).get("type").toString();
                                if(lender.equalsIgnoreCase("lender")) {
                                    SessionVariables.isLender = true;
                                    SessionVariables.user = firebaseUser;
                                    Intent intent = new Intent(applicationContext, MainMenuActivity.class);
                                    startActivity(intent);

                                }

                            }
                           // if (dataSnapshot.child("type").getValue().toString().equalsIgnoreCase("lender")) {

                           // } else {

                          //  }


                        } else {
                           myRef.push().updateChildren(FireBaseHelper
                                   .getCustMap("",email,"", KeyConstants.BORROWER));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        String abc = "";
                    }
                });
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }

    }

    private void authenticate() {
        // setContentView(R.layout.login);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setEnabled(false);


            }
        });

    }

    private boolean validCredentials() {
        if (email.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void print(String string) {
        Log.d(TAG, string);
    }
}

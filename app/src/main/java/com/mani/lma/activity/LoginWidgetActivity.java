package com.mani.lma.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

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
import com.mani.lma.datastruct.CustDetails;
import com.mani.lma.datastruct.LoanDetails;
import com.mani.lma.datastruct.QueryDetails;
import com.mani.lma.db.AppDb;
import com.mani.lma.db.AppDbExecutors;
import com.mani.lma.utils.FireBaseHelper;
import com.mani.lma.utils.KeyConstants;
import com.mani.lma.utils.SessionVariables;
import com.mani.lma.utils.ViewHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginWidgetActivity extends AppCompatActivity {
    private static final String TAG = LoginWidgetActivity.class.getSimpleName();

    FirebaseUser firebaseUser = null;
    Context applicationContext = null;
    int RC_SIGN_IN = 123;
    List<String> loanIds;
    List<LoanDetails> loanDetailsList = new ArrayList<>();
    List<String> custIds = new ArrayList<>();
    List<CustDetails> custDetailsList = new ArrayList<>();
    Intent resultIntent = new Intent();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationContext = this;

        FirebaseApp.initializeApp(this);
        setUpUi();

    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
    }

    private void setUpUi() {
        setUpActionBar();
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
                , new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers).build(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {

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
                                try {
                                    HashMap map = ((HashMap) issue.getValue());
                                    String custId = issue.getKey();
                                    CustDetails custDetails = new CustDetails(custId,
                                            getStringValue(map, KeyConstants.NAME_REF),
                                            getStringValue(map, KeyConstants.EMAIL_REF),
                                            getStringValue(map, KeyConstants.PHONE_REF));
                                    updateCustDb(custDetails);
                                    String lender = getStringValue(map, KeyConstants.TYPE_REF);

                                    if (lender.equalsIgnoreCase(KeyConstants.LENDER)) {
                                        updateSharedPref(custId, true);
                                    } else {
                                        updateSharedPref(custId, false);
                                        pullLoanDetailsForCustomer(custId);
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, e.getMessage());
                                }
                            }
                        } else {
                            myRef.push().updateChildren(FireBaseHelper
                                    .getCustMap("", email, "", KeyConstants.BORROWER));
                        }
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                // ...
            } else {
            }
        }


    }

    private void updateLoanDb() {
        AppDbExecutors.getInstance().getDiskIo().execute(new Runnable() {
            @Override
            public void run() {
                AppDb.getLoanInstance(applicationContext).appDao().insertLoan(loanDetailsList);
            }
        });
    }

    private void updateCustDb(final CustDetails custDetails) {
        AppDbExecutors.getInstance().getDiskIo().execute(new Runnable() {
            @Override
            public void run() {
                AppDb.getCustInstance(applicationContext).appDao().insertCustomerInfo(custDetails);
            }
        });
    }

    private void pullLoanDetailsForCustomer(String custId) {
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child(KeyConstants.LOAN_REF);
        Query query = myRef.orderByChild(KeyConstants.CUST_REF).equalTo(custId);
        query.addValueEventListener(getValeEventListenerForUserId());
    }

    private void initialize() {
        custIds = new ArrayList<>();
        loanIds = new ArrayList<>();
        loanDetailsList = new ArrayList<>();
        custDetailsList = new ArrayList<>();
    }

    private void updateSharedPref(String custId, boolean isLender) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KeyConstants.custId, custId);
        editor.putBoolean(custId, isLender);
        editor.apply();
    }

    private ValueEventListener getValeEventListenerForUserId() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                if (dataSnapshots.exists()) {
                    initialize();
                    for (DataSnapshot dataSnapshot : dataSnapshots.getChildren()) {
                        try {

                            HashMap map = (HashMap) dataSnapshot.getValue();
                            addLoanDetailsFromSnapShot(map, dataSnapshot.getKey());

                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                    updateLoanDb();

                } else {
                    ViewHelper.showToastMessage(applicationContext, getString(R.string.error_loan_not_found));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void addLoanDetailsFromSnapShot(HashMap loanMap, String loanId) {
        if (loanId != null) {
            loanIds.add(loanId);
            String custID = getStringValue(loanMap, KeyConstants.CUST_REF);

            LoanDetails loanDetails = new LoanDetails(
                    loanId,
                    getStringValue(loanMap, KeyConstants.DATE_REF),
                    getLongValue(loanMap, KeyConstants.AMOUNT_REF),
                    getIntValue(loanMap, KeyConstants.INTEREST_REF),
                    getStringValue(loanMap, KeyConstants.PAID_DATE_REF),
                    getLongValue(loanMap, KeyConstants.PAID_AMOUNT_REF),
                    custID
            );
            loanDetailsList.add(loanDetails);
        }
    }

    private String getStringValue(HashMap map, String key) {
        if (map == null || map.isEmpty() || !map.containsKey(key)) {
            return "";
        }
        try {
            return (String) map.get(key);
        } catch (Exception e) {
            return "";
        }
    }

    private long getLongValue(HashMap map, String key) {
        if (map == null || map.isEmpty() || !map.containsKey(key)) {
            return 0L;
        }
        try {
            return (Long) map.get(key);
        } catch (Exception e) {
            return 0L;
        }
    }

    private int getIntValue(HashMap map, String key) {
        if (map == null || map.isEmpty() || !map.containsKey(key)) {
            return 0;
        }
        try {
            return ((Long) map.get(key)).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.login);
        }
    }

}

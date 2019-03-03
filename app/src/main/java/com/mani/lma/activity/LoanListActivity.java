package com.mani.lma.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mani.lma.R;
import com.mani.lma.adapters.LoanListAdapter;
import com.mani.lma.datastruct.CustDetails;
import com.mani.lma.datastruct.LoanDetails;
import com.mani.lma.datastruct.QueryDetails;
import com.mani.lma.db.AppDb;
import com.mani.lma.db.AppDbExecutors;
import com.mani.lma.utils.KeyConstants;
import com.mani.lma.utils.SessionVariables;
import com.mani.lma.utils.ViewHelper;
import com.mani.lma.viewmodel.QueryViewModel;
import com.mani.lma.viewmodel.QueryViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoanListActivity extends AppCompatActivity {

    private static final String TAG = LoanListActivity.class.getSimpleName();
    @BindView(R.id.list_recycler_view)
    RecyclerView recyclerView;

    private Context context;
    QueryDetails queryDetails;

    List<String> loanIds;
    List<LoanDetails> loanDetailsList = new ArrayList<>();
    List<String> custIds = new ArrayList<>();
    List<CustDetails> custDetailsList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan_list);
        ButterKnife.bind(this);
        context = this;
        setUpActionBar();
        Intent intent = getIntent();
        queryDetails = intent.getParcelableExtra(KeyConstants.queryDetails);
        loanDetailsList = intent.getParcelableArrayListExtra(KeyConstants.loanDeatils);
        setUpUi();
    }

    private void pullLoanDetailsForCustomer(String custId) {
        DatabaseReference loanRef = FirebaseDatabase.getInstance().getReference(KeyConstants.LOAN_REF);
        Query query = loanRef.orderByKey().equalTo(custId, KeyConstants.CUST_REF);
        query.addValueEventListener(getValeEventListenerForUserId());
    }


    private void setUpUi() {
       if (queryDetails.isUserIdSearch()) {
            setUpUiFromDB();
        } else {
            setUpUiFromFireBase();
        }

    }

    private void setUpUiFromDB() {
        if (queryDetails == null || isNullOrEmpty(queryDetails.getUserId())) {
            return;
        }
        AppDb loanDb = AppDb.getLoanInstance(context);
        QueryViewModelFactory factory = new QueryViewModelFactory(loanDb, queryDetails.getUserId());
        final QueryViewModel viewModel = ViewModelProviders.of(this, factory)
                .get(QueryViewModel.class);
        viewModel.getLoanDetails().observe(this, new Observer<List<LoanDetails>>() {
            @Override
            public void onChanged(@Nullable List<LoanDetails> loanDetails) {
                loanDetailsList = loanDetails;
                setUpRecyclerView();

            }
        });
    }

    private void setUpUiFromFireBase() {
        DatabaseReference loanRef = FirebaseDatabase.getInstance().getReference(KeyConstants.LOAN_REF);
        loanRef.addValueEventListener(getValeEventListener());
    }

    private ValueEventListener getValeEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                if (dataSnapshots.exists()) {
                    initialize();
                    for (DataSnapshot dataSnapshot : dataSnapshots.getChildren()) {
                        try {
                            HashMap map = (HashMap) dataSnapshot.getValue();
                            String loanDate = getStringValue(map, KeyConstants.DATE_REF);
                            if (isDateInRange(loanDate)) {
                                addLoanDetailsFromSnapShot(map, dataSnapshot.getKey());
                            }

                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }

                    updateLoanDb();
                    setUpRecyclerView();

                } else {
                    ViewHelper.showToastMessage(context, getString(R.string.error_loan_not_found));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
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
                    setUpRecyclerView();

                } else {
                    ViewHelper.showToastMessage(context, getString(R.string.error_loan_not_found));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void handleEmptyList() {
        if (!queryDetails.isUserIdSearch()) {
            FirebaseDatabase.getInstance().getReference(KeyConstants.LOAN_REF).removeEventListener(getValeEventListener());
        }
        ViewHelper.showToastMessage(this, getString(R.string.error_loan_not_found));
        finish();
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
            if (pullCustIdDetails(custID)) {
                addCustIdDetails(custID);
            }
        }
    }


    private void addCustIdDetails(final String custID) {
        DatabaseReference custRef = FirebaseDatabase.getInstance().getReference(KeyConstants.USER_REF).child(custID);
        custRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                if (dataSnapshots.exists()) {
                    HashMap map = (HashMap) dataSnapshots.getValue();
                    CustDetails custDetails = new CustDetails(custID, getStringValue(map, KeyConstants.NAME_REF),
                            getStringValue(map, KeyConstants.EMAIL_REF), getStringValue(map, KeyConstants.PHONE_REF));
                    custDetailsList.add(custDetails);
                }
                updateCustDb(custDetailsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean pullCustIdDetails(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }
        if (!custIds.contains(str)) {
            custIds.add(str);
            return true;
        }
        return false;
    }

    private boolean isDateInRange(String date) {
        if (isNullOrEmpty(date)) {
            return false;
        }
        String fromDateStr = queryDetails.getFromDate();
        String toDateStr = queryDetails.getToDate();
        if (isNullOrEmpty(toDateStr)) {
            toDateStr = fromDateStr;
        }
        if (isNullOrEmpty(fromDateStr)) {
            fromDateStr = toDateStr;
        }
        try {
            Date fromDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(fromDateStr);
            Date toDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(toDateStr);
            Date searchDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(date);
            if (searchDate.equals(fromDate) || searchDate.equals(toDate)) {
                return true;
            }
            if (searchDate.after(fromDate) && toDate.after(searchDate)) {
                return true;
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return false;

    }

    private boolean isNullOrEmpty(String loanID) {
        return loanID == null || loanID.isEmpty();
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

    private void updateCustDb(final List<CustDetails> custDetailsList) {
        AppDbExecutors.getInstance().getDiskIo().execute(new Runnable() {
            @Override
            public void run() {
                AppDb.getCustInstance(context).appDao().insertCustomerInfo(custDetailsList);
            }
        });
    }

    private void updateLoanDb() {
        AppDbExecutors.getInstance().getDiskIo().execute(new Runnable() {
            @Override
            public void run() {
                AppDb.getLoanInstance(context).appDao().insertLoan(loanDetailsList);
            }
        });
    }

    private void initialize() {
        custIds = new ArrayList<>();
        loanIds = new ArrayList<>();
        loanDetailsList = new ArrayList<>();
        custDetailsList = new ArrayList<>();
    }

    private void setUpRecyclerView() {
        if (loanDetailsList.size() <= 0) {
            handleEmptyList();
        } else {
            LoanListAdapter loanListAdapter =
                    new LoanListAdapter(this, loanDetailsList);
            LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(loanListAdapter);
        }
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.loan_list);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (!SessionVariables.isLender) {
            findViewById(R.id.action_refresh).setVisibility(View.VISIBLE);
        }
        return true;
    }
}

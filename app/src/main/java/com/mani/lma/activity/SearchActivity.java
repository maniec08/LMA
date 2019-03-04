package com.mani.lma.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mani.lma.R;
import com.mani.lma.datastruct.CustDetails;
import com.mani.lma.datastruct.LoanDetails;
import com.mani.lma.datastruct.QueryDetails;
import com.mani.lma.db.AppDb;
import com.mani.lma.db.AppDbExecutors;
import com.mani.lma.utils.KeyConstants;
import com.mani.lma.utils.ViewHelper;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.loan_id_radio)
    RadioButton loanIdRadio;

    @BindView(R.id.date_range_radio)
    RadioButton dateRangeRadio;

    @BindView(R.id.search_button)
    Button searchButton;

    @BindView(R.id.loan_id_ll)
    LinearLayout loanIdLl;
    @BindView(R.id.from_date_ll)
    LinearLayout fromDateLl;
    @BindView(R.id.to_date_ll)
    LinearLayout toDateLl;
    @BindView(R.id.loan_id)
    EditText loanId;
    @BindView(R.id.from_date)
    EditText fromDate;
    @BindView(R.id.to_date)
    EditText toDate;
    @BindView(R.id.from_calendar)
    ImageView fromDateCalender;
    @BindView(R.id.to_calendar)
    ImageView toDateCalender;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    List<String> loanIds = new ArrayList<>();
    List<String> custIds = new ArrayList<>();
    List<LoanDetails> loanDetailsList = new ArrayList<>();
    List<CustDetails> custDetailsList = new ArrayList<>();

    QueryDetails queryDetails;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        context = this;
        ButterKnife.bind(this);
        progressBar.setVisibility(View.GONE);
        setUpActionBar();
        setUpUiBasedOnToggle();
        setUpListeners();
    }
    @Override
    protected void onResume() {
        progressBar.setVisibility(View.GONE);
        super.onResume();
    }
    private void setUpListeners() {
        loanIdRadio.setOnCheckedChangeListener(getCheckChangeListenerLoanID());
        dateRangeRadio.setOnCheckedChangeListener(getCheckChangeListenerDate());
        searchButton.setOnClickListener(getOnClickListener());
        ViewHelper.displayDatePicker(fromDate, this);
        ViewHelper.displayDatePicker(fromDateCalender, fromDate, this);
        ViewHelper.displayDatePicker(toDate, this);
        ViewHelper.displayDatePicker(toDateCalender, toDate, this);
        loanId.addTextChangedListener(getTextWatcher());
    }

    private TextWatcher getTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchButton.setEnabled(true);
                } else {
                    searchButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!loanIdRadio.isChecked() && !dateRangeRadio.isChecked()) {
                    ViewHelper.showToastMessage(context,getString(R.string.error_invalid_selection));
                    return;
                }
                if (loanIdRadio.isChecked()) {
                    searchByLoanID();
                } else {
                    searchByDateRange();
                }
            }
        };
    }



    private void searchByDateRange() {
        String fromDateStr = fromDate.getText().toString();
        String toDateStr = toDate.getText().toString();
        if (isNullOrEmpty(fromDateStr) && isNullOrEmpty(toDateStr)) {
            progressBar.setVisibility(View.GONE);
            ViewHelper.showToastMessage(context,getString(R.string.error_enter_date));
            return;
        }
        if (isNullOrEmpty(toDateStr)) {
            toDate.setText(fromDate.getText().toString());
        }
        if (isNullOrEmpty(fromDateStr)) {
            fromDate.setText(toDate.getText().toString());
        }
        queryDetails = new QueryDetails(fromDateStr,toDateStr,false);
        startListActivity();
    }

    private void launchActivity() {
        if (loanIds.size() == 1) {
            Intent intent = new Intent(this, LoanDetailsActivity.class);
            intent.putExtra(KeyConstants.loanId, loanIds.get(0));
            intent.putExtra(KeyConstants.custId, loanDetailsList.get(0).getCustId());
            startActivity(intent);
        } else if (loanIds.size() > 1) {
            Intent intent = new Intent(this, LoanListActivity.class);
            intent.putExtra(KeyConstants.queryDetails, queryDetails);
            intent.putParcelableArrayListExtra(KeyConstants.loanDeatils, (ArrayList<LoanDetails>) loanDetailsList);
            startActivity(intent);
        }
    }

    private void startListActivity(){
        Intent intent = new Intent(this, LoanListActivity.class);
        intent.putExtra(KeyConstants.queryDetails, queryDetails);
        intent.putParcelableArrayListExtra(KeyConstants.loanDeatils, (ArrayList<LoanDetails>) loanDetailsList);
        startActivity(intent);
    }

    private void searchByLoanID() {
        String loanID = loanId.getText().toString();
        if (isNullOrEmpty(loanID)) {
            progressBar.setVisibility(View.GONE);
            ViewHelper.showToastMessage(context,getString(R.string.error_enter_loan_id));
            return;
        }
        searchByLoanID(loanID);
    }

    private void initialize() {
        custIds = new ArrayList<>();
        loanIds = new ArrayList<>();
        loanDetailsList = new ArrayList<>();
        custDetailsList = new ArrayList<>();
    }

    private void searchByLoanID(String loanID) {
        DatabaseReference loanRef = FirebaseDatabase.getInstance().getReference(KeyConstants.LOAN_REF).child(loanID);
        loanRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                processQuery(dataSnapshots);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void processQuery(DataSnapshot dataSnapshots) {
        initialize();
        if (dataSnapshots.exists()) {
            try {
                HashMap map = (HashMap) dataSnapshots.getValue();
                addLoanDetailsFromSnapShot(map, dataSnapshots.getKey());
            } catch (Exception e) {
                ViewHelper.showToastMessage(this,getString(R.string.error_loan_not_found));
            }

        } else {
            ViewHelper.showToastMessage(this, getString(R.string.error_loan_not_found));
        }
        updateLoanDb();
        progressBar.setVisibility(View.GONE);
        launchActivity();
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

    private boolean isNullOrEmpty(String loanID) {
        return loanID == null || loanID.isEmpty();
    }



    private CompoundButton.OnCheckedChangeListener getCheckChangeListenerLoanID() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    searchButton.setEnabled(false);
                }
                setUpUiBasedOnToggle();
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener getCheckChangeListenerDate() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    searchButton.setEnabled(true);
                }
                setUpUiBasedOnToggle();
            }
        };
    }

    private void setUpUiBasedOnToggle() {
        if (!loanIdRadio.isChecked() && !dateRangeRadio.isChecked()) {
            loanIdRadio.setChecked(true);
        }
        if (loanIdRadio.isChecked()) {
            loanIdLl.setVisibility(View.VISIBLE);
            fromDateLl.setVisibility(View.GONE);
            toDateLl.setVisibility(View.GONE);
        } else {
            loanIdLl.setVisibility(View.GONE);
            fromDateLl.setVisibility(View.VISIBLE);
            toDateLl.setVisibility(View.VISIBLE);
        }
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.search);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_log_off:
                finishAffinity();
                ViewHelper.logOff(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}

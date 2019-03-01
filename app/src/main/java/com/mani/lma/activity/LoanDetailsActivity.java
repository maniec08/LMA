package com.mani.lma.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mani.lma.R;
import com.mani.lma.datastruct.CustDetails;
import com.mani.lma.datastruct.LoanDetails;
import com.mani.lma.db.AppDb;
import com.mani.lma.db.AppDbExecutors;
import com.mani.lma.utils.FireBaseHelper;
import com.mani.lma.utils.KeyConstants;
import com.mani.lma.utils.SessionVariables;
import com.mani.lma.utils.ViewHelper;
import com.mani.lma.viewmodel.CustViewModel;
import com.mani.lma.viewmodel.CustViewModelFactory;
import com.mani.lma.viewmodel.LoanViewModel;
import com.mani.lma.viewmodel.LoanViewModelFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoanDetailsActivity extends AppCompatActivity {
    @BindView(R.id.loan_id)
    TextView loanIdTextView;
    @BindView(R.id.date)
    EditText loanDateEditText;
    @BindView(R.id.calendar)
    ImageView loanDateCalendar;
    @BindView(R.id.amount_text)
    EditText amountView;
    @BindView(R.id.interest_percent)
    EditText interestPercentEditText;
    @BindView(R.id.interest)
    TextView interestAmountTextView;
    @BindView(R.id.customer_name)
    EditText customerNameEditText;
    @BindView(R.id.customer_email)
    EditText customerEmailEditText;
    @BindView(R.id.phone_number)
    EditText customerPhoneEditText;
    @BindView(R.id.settlement_date)
    EditText paidDateEditText;
    @BindView(R.id.settlement_calendar)
    ImageView paidDateCalendar;
    @BindView(R.id.settlement_amount)
    EditText paidAmountEditText;
    @BindView(R.id.difference)
    TextView amountDifferenceTextView;
    @BindView(R.id.save_button)
    Button saveButton;

    private LoanDetails loanDetails;
    private CustDetails custDetails = new CustDetails();


    private boolean isNewLoan;

    AppDb loanDb;
    AppDb custDb;

    private Context context;

    private final String TAG = LoanDetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan_details);
        ButterKnife.bind(this);
        setUpActionBar();
        setUpUIBasedOnUser();
        context = this;
        loanDb = AppDb.getLoanInstance(this);
        custDb = AppDb.getCustInstance(this);

        Intent intent = getIntent();
        loanDetails = intent.getParcelableExtra(KeyConstants.loanDeatils);
        custDetails = intent.getParcelableExtra(KeyConstants.custDetails);

        isNewLoan = intent.getBooleanExtra(KeyConstants.newLoan, false);

        validateIntentContent();

        updateLoanUiFromDb();
        updateCustUiFromDb();

        setUpListeners();
    }

    @Override
    public void onBackPressed() {
        deleteNodeOnBack();
        super.onBackPressed();

    }
private void deleteNodeOnBack(){
    String loanId = loanDetails.getLoanId();
    if(!isNullOrEmpty(loanId) && isNewLoan){
        FirebaseDatabase.getInstance().getReference(KeyConstants.LOAN_REF).child(loanId).setValue(null);;
    }
}
    private void populateLoanUi(LoanDetails loanDetails) {
        this.loanDetails = loanDetails;
        populateLoanUi();
    }

    private void populateLoanUi() {
        if (loanDetails != null) {
            loanIdTextView.setText(loanDetails.getLoanId());
            if (!isNewLoan) {
                setTextOnView(loanDateEditText, loanDetails.getDate());
                setTextOnView(amountView, loanDetails.getAmount().toString());
                setTextOnView(interestPercentEditText, Integer.toString(loanDetails.getInterest()));
                setTextOnView(paidDateEditText, loanDetails.getSettlementDate());
                setTextOnView(paidAmountEditText, loanDetails.getSettlementAmount().toString());
            }
        }
    }

    private void populateCustomerUi(CustDetails custDetails) {
        this.custDetails = custDetails;
        populateCustomerUi();
    }

    private void populateCustomerUi() {
        if (custDetails != null && !isNewLoan) {
            setTextOnView(customerEmailEditText, custDetails.getEmailId());
            setTextOnView(customerNameEditText, custDetails.getCustName());
            setTextOnView(customerPhoneEditText, custDetails.getPhoneNumber());
        }
    }

    private void setTextOnView(TextView view, String str) {
        if (!isNullOrEmpty(str)) {
            view.setText(str);
        }
    }

    private void setUpUIBasedOnUser() {
        loanDateEditText.setEnabled(SessionVariables.isLender);
        amountView.setEnabled(SessionVariables.isLender);
        interestPercentEditText.setEnabled(SessionVariables.isLender);
        paidDateEditText.setEnabled(SessionVariables.isLender);
        paidAmountEditText.setEnabled(SessionVariables.isLender);

        customerEmailEditText.setEnabled(SessionVariables.isLender);
        customerNameEditText.setEnabled(true);
        customerPhoneEditText.setEnabled(true);
    }

    private void collectUiInfo() {
        loanDetails.setLoanId(loanIdTextView.getText().toString());
        loanDetails.setDate(loanDateEditText.getText().toString());
        loanDetails.setAmount(getLongFromView(amountView));
        loanDetails.setInterest(getIntFromView(interestPercentEditText));
        loanDetails.setSettlementDate(paidDateEditText.getText().toString());
        loanDetails.setSettlementAmount(getLongFromView(paidAmountEditText));
        if (custDetails == null) {
            custDetails = new CustDetails();
        }
        custDetails.setCustName(customerNameEditText.getText().toString());
        custDetails.setEmailId(customerEmailEditText.getText().toString());
        custDetails.setPhoneNumber(customerPhoneEditText.getText().toString());
    }

    private Long getLongFromView(TextView view) {
        if (view != null) {
            try {
                return Long.parseLong(view.getText().toString());
            } catch (Exception e) {
                Log.d(TAG, "Error parsing view");
            }
        }
        return 0L;

    }

    private int getIntFromView(TextView view) {
        if (view != null) {
            try {
                return Integer.parseInt(view.getText().toString());
            } catch (Exception e) {
                Log.d(TAG, "Error parsing view");
            }
        }
        return 0;

    }

    private void setUpListeners() {
        saveButton.setOnClickListener(saveButtonListener());
        ViewHelper.displayDatePicker(loanDateEditText, context);
        ViewHelper.displayDatePicker(loanDateCalendar, loanDateEditText, context);
        ViewHelper.displayDatePicker(paidDateEditText, context);
        ViewHelper.displayDatePicker(paidDateCalendar, paidDateEditText, context);
    }

    private View.OnClickListener saveButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectUiInfo();
                //order matters, as updateCustomerInfo generates customer id
                updateCustomerInfo();
                finish();
            }
        };
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("0");
    }

    private void updateLoanInfo() {
        final DatabaseReference myRef = FirebaseDatabase.getInstance()
                .getReference().child(KeyConstants.LOAN_REF).child(loanDetails.getLoanId());
        myRef.updateChildren(FireBaseHelper.getLoanMap(loanDetails));

    }

    private void updateCustomerInfo() {
        final DatabaseReference myRef = FirebaseDatabase.getInstance()
                .getReference().child(KeyConstants.USER_REF);
        final Query query = myRef.orderByChild(KeyConstants.EMAIL_REF).equalTo(custDetails.getEmailId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String custId = "";
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        custId = child.getKey();
                    }
                }
                if (isNullOrEmpty(custId)) {
                    custId = myRef.push().getKey();
                }
                if (!isNullOrEmpty(custId)) {
                    loanDetails.setCustId(custId);
                    custDetails.setCustid(custId);
                    myRef.child(custId).updateChildren(FireBaseHelper.getCustMap(
                            custDetails.getCustName(),
                            custDetails.getEmailId(),
                            custDetails.getPhoneNumber(),
                            KeyConstants.BORROWER
                    ));
                } else {
                    Log.e(TAG, "Customer info creation issue");
                }
                updateLoanInfo();
                updateDb();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Customer info creation issue");
            }
        });
    }

    private void updateDb() {
        if (isNullOrEmpty(loanDetails.getLoanId()) || isNullOrEmpty(custDetails.getCustid())) {
            return;
        }
        AppDbExecutors.getInstance().getDiskIo().execute(new Runnable() {
            @Override
            public void run() {
                //int id, String date, Long amount, int interest, String paidDate, String paidAmount, String CustID
                AppDb.getLoanInstance(context).appDao().updateLoanDb(
                        loanDetails.getLoanId(),
                        loanDetails.getDate(),
                        loanDetails.getAmount(),
                        loanDetails.getInterest(),
                        loanDetails.getSettlementDate(),
                        loanDetails.getSettlementAmount(),
                        loanDetails.getCustId()
                );
                CustDetails custDetailsTemp = AppDb.getCustInstance(context).appDao().getCustDetail(custDetails.getCustid());
                if (custDetailsTemp == null) {
                    AppDb.getCustInstance(context).appDao().insertCustomerInfo(custDetails);
                } else {
                    AppDb.getCustInstance(context).appDao().updateCustDb(
                            custDetails.getCustid(),
                            custDetails.getCustName(),
                            custDetails.getPhoneNumber(),
                            custDetails.getEmailId()
                    );
                }
            }
        });
    }

    private void validateIntentContent() {
        if (loanDetails == null || loanDetails.getLoanId() == null || loanDetails.getLoanId().isEmpty()) {
            showErrorDialog();
        }
    }

    public void showErrorDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context).setMessage(R.string.error_message)
                .setPositiveButton(R.string.go_back, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();

                    }
                })
                .create();
        dialog.show();
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.loan_details);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                deleteNodeOnBack();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void updateLoanUiFromDb() {
        if (loanDetails == null || isNullOrEmpty(loanDetails.getLoanId())) {
            return;
        }
        LoanViewModelFactory factory = new LoanViewModelFactory(loanDb, loanDetails.getLoanId());
        final LoanViewModel viewModel = ViewModelProviders.of(this, factory)
                .get(LoanViewModel.class);
        viewModel.getLoanDetails().observe(this, new Observer<LoanDetails>() {
            @Override
            public void onChanged(@Nullable LoanDetails loanDetails) {
                populateLoanUi(loanDetails);
            }
        });
    }

    public void updateCustUiFromDb() {
        if (custDetails == null || isNullOrEmpty(custDetails.getCustid())) {
            return;
        }
        CustViewModelFactory factory = new CustViewModelFactory(custDb, custDetails.getCustid());
        final CustViewModel viewModel = ViewModelProviders.of(this, factory)
                .get(CustViewModel.class);
        viewModel.getCustDetails().observe(this, new Observer<CustDetails>() {
            @Override
            public void onChanged(@Nullable CustDetails custDetails) {
                populateCustomerUi(custDetails);
            }
        });
    }
}

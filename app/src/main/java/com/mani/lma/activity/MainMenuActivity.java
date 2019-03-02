package com.mani.lma.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mani.lma.R;
import com.mani.lma.datastruct.LoanDetails;
import com.mani.lma.db.AppDb;
import com.mani.lma.db.AppDbExecutors;
import com.mani.lma.utils.KeyConstants;
import com.mani.lma.utils.ViewHelper;

import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainMenuActivity extends AppCompatActivity {
    @BindView(R.id.lender_menu_list)
    ListView mainMenu;
    private Context context;
    private String newLoanId = "AA000";
    private final String TAG = MainMenuActivity.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        context = this;
        ButterKnife.bind(this);

        setUpActionBar();

        mainMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    createNewIdAndLaunchDetails();
                }

                if (position == 1) {
                    Intent intent = new Intent(context, SearchActivity.class);
                    context.startActivity(intent);
                }
            }
        });
    }

    /**
     * New loan id is System generated alphanumeric and the format is AA123
     * (First two character as letter and next 3 as digits)
     */
    private void createNewIdAndLaunchDetails() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(KeyConstants.LOAN_REF);
        Query lastQuery = databaseReference.orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        newLoanId = child.getKey();
                    }
                }
                if (newLoanId == null || newLoanId.isEmpty()) {
                    showErrorDialog();
                } else {
                    LoanDetails loanDetails = getNewLoanDetails();
                    pushNewKey(newLoanId);
                    pushNewKeyToDb(loanDetails);
                    Intent intent = new Intent(context, LoanDetailsActivity.class);
                    intent.putExtra(KeyConstants.loanId, newLoanId);
                    intent.putExtra(KeyConstants.newLoan, true);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Handle possible errors.
            }
        });
    }

    private LoanDetails getNewLoanDetails() {
        return new LoanDetails(
                getNewLoanId(),
                ViewHelper.getToday(),
                0,
                0,
                "",
                0L,
                "0"
        );
    }

    private void pushNewKey(String id) {
        Date date = new Date();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(KeyConstants.LOAN_REF);
        databaseReference.child(id).setValue(date);
    }

    private void pushNewKeyToDb(final LoanDetails loanDetails) {
        AppDbExecutors.getInstance().getDiskIo().execute(new Runnable() {
            @Override
            public void run() {
                AppDb.getLoanInstance(context).appDao().insertLoan(loanDetails);
            }
        });
    }

    public String getNewLoanId() {
        String lastThreeDigits = "";
        String firstDigit = "";
        String secondDigit = "";
        char[] chars = newLoanId.toCharArray();
        boolean incrementFirstChar = false;
        boolean incrementSecondChar = false;
        try {
            int digit = Integer.parseInt(newLoanId.substring(2)) + 1;
            if (digit >= 1000) {
                lastThreeDigits = "001";
                incrementSecondChar = true;
            } else if (digit < 10) {
                lastThreeDigits = "00" + digit;
            } else {
                lastThreeDigits = "0" + digit;
            }

            if (incrementSecondChar) {
                if (chars[1] == 'Z') {
                    secondDigit = "A";
                    incrementFirstChar = true;
                } else {
                    secondDigit = Character.toString(++chars[1]);
                }
            } else {
                secondDigit = Character.toString(chars[1]);
            }

            if (incrementFirstChar) {
                firstDigit = Character.toString(++chars[0]);
            } else {
                firstDigit = Character.toString(chars[0]);
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        newLoanId = firstDigit + secondDigit + lastThreeDigits;
        return newLoanId;
    }

    public void showErrorDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context).setMessage(R.string.loan_creation_error_message)
                .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.main_menu);

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
        return true;
    }
}

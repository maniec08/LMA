package com.mani.lma.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyCharacterMap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mani.lma.R;
import com.mani.lma.datastruct.LoanDetails;
import com.mani.lma.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainMenuActivity extends AppCompatActivity {
    @BindView(R.id.lender_menu_list)
    ListView mainMenu;
    private Context context;
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
                if(position == 0){
                    Intent intent = new Intent(context, LoanDetailsActivity.class);
                    intent.putExtra(KeyConstants.loanId, 0);
                    intent.putExtra(KeyConstants.custId, 0);
                    context.startActivity(intent);
                }

                if(position == 1){
                    Intent intent = new Intent(context, SearchActivity.class);
                    context.startActivity(intent);
                }
            }
        });
    }
    private void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!= null) {
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

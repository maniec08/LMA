package com.mani.lma.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mani.lma.R;
import com.mani.lma.activity.LoanDetailsActivity;
import com.mani.lma.datastruct.LoanDetails;
import com.mani.lma.utils.KeyConstants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoanListAdapter extends RecyclerView.Adapter<LoanListAdapter.ViewHolder> {
    private Context context;
    private List<LoanDetails> loanDetailsList;

    public LoanListAdapter(Context context, List<LoanDetails> loanDetailsList) {
        this.context = context;
        this.loanDetailsList = loanDetailsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.loan_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder,final int i) {
        viewHolder.loanItem.setText(loanDetailsList.get(i).getLoanId());
        viewHolder.loanItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(loanDetailsList!=null){
            return loanDetailsList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder  {
        @BindView(R.id.loan_list_id)
        TextView loanItem;

        ViewHolder(View itemView) {
            super(itemView);
           ButterKnife.bind(this,itemView);

        }
    }

    private void launchActivity(int adapterPosition) {
        Intent intent = new Intent(context, LoanDetailsActivity.class);
        intent.putExtra(KeyConstants.loanId, loanDetailsList.get(adapterPosition).getLoanId());
        intent.putExtra(KeyConstants.custId, loanDetailsList.get(adapterPosition).getCustId());
        context.startActivity(intent);
    }
}

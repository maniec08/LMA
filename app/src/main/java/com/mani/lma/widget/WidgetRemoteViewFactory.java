package com.mani.lma.widget;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import com.mani.lma.R;
import com.mani.lma.datastruct.LoanDetails;
import com.mani.lma.datastruct.ReportDetails;
import com.mani.lma.db.AppDb;
import com.mani.lma.utils.ViewHelper;

import java.util.List;

public class WidgetRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<LoanDetails> loanDetailsList;
    private List<ReportDetails> reportDetailsList;

    public WidgetRemoteViewFactory(Context applicationContext) {
        mContext = applicationContext;
        if (WidgetProvider.isLender) {
            getReportDetails(applicationContext);
        } else {
            loanDetailsList = AppDb.getLoanInstance(applicationContext).appDao().getLoanDetailsWidget(WidgetProvider.custId);
        }
    }

    private void getReportDetails(Context applicationContext) {
        List<Long> amount = AppDb.getLoanInstance(applicationContext).appDao().getLoanDetailsWithLoanDate(ViewHelper.getToday());
        List<Long> settlementAmount = AppDb.getLoanInstance(applicationContext).appDao().getLoanDetailsWithPaidDate(ViewHelper.getToday());
        Long loanAmount = calculateTotal(amount);
        Long paidAmount = calculateTotal(settlementAmount);
        ReportDetails loanReportDetails = new ReportDetails("Loan Amount", loanAmount);
        ReportDetails paidReportDetails = new ReportDetails("paid Amount", paidAmount);
        ReportDetails netReportDetails = new ReportDetails("paid Amount", paidAmount);
        reportDetailsList.add(loanReportDetails);
        reportDetailsList.add(paidReportDetails);
        reportDetailsList.add(netReportDetails);
    }

    private Long calculateTotal( List<Long> amounts ){
        Long amount = 0L;
        for(Long amnt:amounts){
            amount = amount + amnt;
        }
        return amount;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return loanDetailsList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.loan_row_widget);
        if(!WidgetProvider.isLender) {
            rv.setTextViewText(R.id.loan_id_widget, loanDetailsList.get(position).getLoanId());
            rv.setTextViewText(R.id.loan_date_widget, loanDetailsList.get(position).getDate());
            rv.setTextViewText(R.id.loan_amount_widget, Long.toString(loanDetailsList.get(position).getAmount()));
        } else {
            rv.setTextViewText(R.id.loan_date_widget, reportDetailsList.get(position).getReportName());
            rv.setTextViewText(R.id.loan_amount_widget, Long.toString(reportDetailsList.get(position).getTotalAmount()));
        }
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
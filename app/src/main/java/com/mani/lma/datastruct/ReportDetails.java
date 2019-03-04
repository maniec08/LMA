package com.mani.lma.datastruct;

public class ReportDetails {

    private String reportName;

    public ReportDetails(String reportName, Long totalAmount) {
        this.reportName = reportName;
        this.totalAmount = totalAmount;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    private Long totalAmount;
}

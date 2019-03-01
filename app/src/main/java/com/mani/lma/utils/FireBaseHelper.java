package com.mani.lma.utils;

import com.mani.lma.datastruct.LoanDetails;

import java.util.HashMap;
import java.util.Map;

public class FireBaseHelper {

    public static Map<String, Object> getCustMap(String name, String email, String phone, String type){
        Map<String,Object> custMap = new HashMap<>();
        custMap.put(KeyConstants.EMAIL_REF, email);
        custMap.put(KeyConstants.PHONE_REF, phone);
        custMap.put(KeyConstants.TYPE_REF, type);
        custMap.put(KeyConstants.NAME_REF, name);
        return custMap;
    }

    public static Map<String, Object> getLoanMap(LoanDetails loanDetails){
        Map<String,Object> loanMap = new HashMap<>();
        loanMap.put(KeyConstants.DATE_REF, loanDetails.getDate());
        loanMap.put(KeyConstants.AMOUNT_REF, loanDetails.getAmount());
        loanMap.put(KeyConstants.INTEREST_REF, loanDetails.getInterest());
        loanMap.put(KeyConstants.PAID_DATE_REF, loanDetails.getSettlementDate());
        loanMap.put(KeyConstants.PAID_AMOUNT_REF, loanDetails.getSettlementAmount());
        loanMap.put(KeyConstants.CUST_REF, loanDetails.getCustId());
        return loanMap;
    }
}

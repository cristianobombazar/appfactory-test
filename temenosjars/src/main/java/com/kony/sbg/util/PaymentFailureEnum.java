package com.kony.sbg.util;

public enum PaymentFailureEnum {
    AC01("AC01","The “From” account is invalid."),
    AC04("AC04", "The “From” account is invalid."),
    AG09("AG09", "Unable to process payment. Please contact your bank representative for assistance."),
    AM04("AM04", "Insufficient funds."),
    DS02("DS02", "Unable to process payment. Please contact your bank representative for assistance."),
    RR05("RR05", "Incomplete regulatory information and/or documents."),
    MS03("MS03", "Unable to process payment. Please contact your bank representative for assistance."),
    AC06("AC06", "Unable to process payment. Please contact your bank representative for assistance."),
    DUPL("DUPL", "Duplicate Payment"),
    TD02("TD02", "Unable to process payment. Please contact your bank representative for assistance."),
    AM11("AM11", "Invalid currency"),
    DC01("DC01","Value date falls on a %s holiday."),
    CC01("CC01","Value date falls on a %s holiday."),
    DC02("DC02", "Cut-off time was passed."),
    CC02("CC02", "Cut-off time was passed."),
    DT01("DT01", "Invalid value date"),
    DT03("DT03", "Invalid value date");

    private final String failureCode;
    private final String failureReason;

    PaymentFailureEnum(String failureCode, String failureReason){
        this.failureCode = failureCode;
        this.failureReason = failureReason;
    }

    public String getFailureCode() {
        return failureCode;
    }

    public String getFailureReason() {
        return failureReason;
    }
}

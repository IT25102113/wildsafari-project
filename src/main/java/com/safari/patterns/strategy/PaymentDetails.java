package com.safari.patterns.strategy;

import org.springframework.web.multipart.MultipartFile;

public class PaymentDetails {
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String bankRef;
    private MultipartFile slipFile;

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public String getBankRef() { return bankRef; }
    public void setBankRef(String bankRef) { this.bankRef = bankRef; }

    public MultipartFile getSlipFile() { return slipFile; }
    public void setSlipFile(MultipartFile slipFile) { this.slipFile = slipFile; }
}

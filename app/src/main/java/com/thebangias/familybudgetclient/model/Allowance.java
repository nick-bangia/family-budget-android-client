package com.thebangias.familybudgetclient.model;

import com.thebangias.familybudgetclient.model.abstractions.DataObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Represents the data returned by the /allowances operation
 */
public class Allowance extends DataObject {

    private String accountName;
    private BigDecimal reconciledAmount;
    private BigDecimal pendingAmount;
    private Date latestTransactionDate;
    private List<CategoryAllowance> categories;

    // getters & setters
    public String getAccountName() {
        return this.accountName;
    }

    public void setAccountName(String value) {
        this.accountName = value;
    }

    public BigDecimal getReconciledAmount() {
        return this.reconciledAmount;
    }

    public void setReconciledAmount(BigDecimal value) {
        this.reconciledAmount = value;
    }

    public BigDecimal getPendingAmount() {
        return this.pendingAmount;
    }

    public void setPendingAmount(BigDecimal value) {
        this.pendingAmount = value;
    }

    public Date getLatestTransactionDate() {
        return this.latestTransactionDate;
    }

    public void setLatestTransactionDate(Date value) {
        this.latestTransactionDate = value;
    }

    public List<CategoryAllowance> getCategories() {
        return this.categories;
    }

    public void setCategories(List<CategoryAllowance> value) {
        this.categories = value;
    }
}

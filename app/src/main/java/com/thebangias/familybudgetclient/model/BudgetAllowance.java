package com.thebangias.familybudgetclient.model;

import com.thebangias.familybudgetclient.model.abstractions.DataObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents the data returned by the /allowances operation
 */
public class BudgetAllowance extends DataObject {

    private String accountName;
    private String categoryName;
    private String subcategoryName;
    private BigDecimal reconciledAmount;
    private BigDecimal pendingAmount;
    private Date latestTransactionDate;

    // getters & setters
    public String getAccountName() {
        return this.accountName;
    }

    public void setAccountName(String value) {
        this.accountName = value;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String value) {
        this.categoryName = value;
    }

    public String getSubcategoryName() {
        return this.subcategoryName;
    }

    public void setSubcategoryName(String value) {
        this.subcategoryName = value;
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


}

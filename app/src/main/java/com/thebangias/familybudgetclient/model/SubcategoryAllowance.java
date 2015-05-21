package com.thebangias.familybudgetclient.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents a Subcategory's rolled up data
 */
public class SubcategoryAllowance {

    private String subcategoryName;
    private BigDecimal reconciledAmount;
    private BigDecimal pendingAmount;
    private Date latestTransactionDate;

    // getters & setters
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

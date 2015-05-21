package com.thebangias.familybudgetclient.model;

import com.thebangias.familybudgetclient.model.abstractions.DataObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Represents a category's rolled up data
 */
public class CategoryAllowance extends DataObject {

    private String accountName;
    private String categoryName;
    private BigDecimal reconciledAmount;
    private BigDecimal pendingAmount;
    private Date latestTransactionDate;
    private List<SubcategoryAllowance> subcategories;

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

    public List<SubcategoryAllowance> getSubcategories() {
        return this.subcategories;
    }

    public void setSubcategories(List<SubcategoryAllowance> value) {
        this.subcategories = value;
    }
}

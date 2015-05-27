package com.thebangias.familybudgetclient.views;

/**
 * Represents the drawer item view
 */
public class DrawerAccountItem {

    public String accountLabel;
    public String accountBalance;

    public DrawerAccountItem(String label, String balance) {
        accountLabel = label;
        accountBalance = balance;
    }
}

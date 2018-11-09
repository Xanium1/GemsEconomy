/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.data;

import me.xanium.gemseconomy.economy.Account;
import me.xanium.gemseconomy.economy.Currency;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public abstract class DataStore {

    private String name;
    private boolean topSupported;

    public DataStore(String name, boolean topSupported) {
        this.name = name;
        this.topSupported = topSupported;
    }

    public abstract void initialize();

    public abstract void close();

    public abstract void loadCurrencies();

    public abstract void saveCurrency(Currency currency);

    public abstract void deleteCurrency(Currency currency);

    public abstract Map<String, Double> getTopList(Currency currency, int offset, int amount);

    public abstract Account loadAccount(String string);

    public abstract Account loadAccount(UUID uuid);

    public abstract void saveAccount(Account account);

    public abstract void deleteAccount(Account account);

    public abstract void createAccount(Account account);

    public abstract ArrayList<Account> getOfflineAccounts();

    public String getName() {
        return this.name;
    }

    public boolean isTopSupported() {
        return this.topSupported;
    }
}


/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

/*
 * Decompiled with CFR 0_123.
 */
package me.xanium.gemseconomy.data;

import me.xanium.gemseconomy.economy.Account;
import me.xanium.gemseconomy.economy.Currency;

import java.util.Map;
import java.util.UUID;

public abstract class DataStore {

    private String name;
    private boolean topSupported;

    public DataStore(final String name, final boolean topSupported) {
        this.name = name;
        this.topSupported = topSupported;
    }

    public abstract void initalize();

    public abstract void close();

    public abstract void loadCurrencies();

    public abstract void saveCurrency(final Currency p0);

    public abstract void deleteCurrency(final Currency p0);

    public abstract Map<String, Double> getTopList(final Currency p0, final int p1, final int p2);

    public abstract Account loadAccount(final String p0);

    public abstract Account loadAccount(final UUID p0);

    public abstract void saveAccount(final Account p0);

    public abstract void deleteAccount(final Account p0);

    public String getName() {
        return this.name;
    }

    public boolean isTopSupported() {
        return this.topSupported;
    }
}


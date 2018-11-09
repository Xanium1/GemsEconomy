/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.economy;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

public class CachedTopList {

    private Currency currency;
    private int amount;
    private int offset;
    private long cacheTime;
    private LinkedHashMap<String, Double> results;

    public CachedTopList(Currency currency, int amount, int offset, long cacheTime) {
        this.results = new LinkedHashMap<>();
        this.currency = currency;
        this.amount = amount;
        this.offset = offset;
        this.cacheTime = cacheTime;
    }

    public boolean matches(Currency currency, int offset, int amount) {
        return currency.getUuid().equals(this.getCurrency().getUuid()) && offset == this.getOffset() && amount == this.getAmount();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - this.getCacheTime() > TimeUnit.MINUTES.toMillis(1L);
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public int getAmount() {
        return this.amount;
    }

    public int getOffset() {
        return this.offset;
    }

    public long getCacheTime() {
        return this.cacheTime;
    }

    public LinkedHashMap<String, Double> getResults() {
        return this.results;
    }

    public void setResults(LinkedHashMap<String, Double> results) {
        this.results = results;
    }
}

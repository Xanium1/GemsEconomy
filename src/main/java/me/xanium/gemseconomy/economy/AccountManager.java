/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.economy;

import me.xanium.gemseconomy.GemsEconomy;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountManager {

    private static List<Account> accounts = new ArrayList<Account>();
    private static List<Currency> currencies = new ArrayList<Currency>();

    public static Currency getDefaultCurrency() {
        for (Currency currency : AccountManager.getCurrencies()) {
            if (!currency.isDefaultCurrency()) continue;
            return currency;
        }
        return null;
    }

    public static Currency getCurrency(String name) {
        for (Currency currency : AccountManager.getCurrencies()) {
            if (!currency.getSingular().equalsIgnoreCase(name) && !currency.getPlural().equalsIgnoreCase(name)) continue;
            return currency;
        }
        return null;
    }

    public static Currency getCurrency(UUID uuid) {
        for (Currency currency : AccountManager.getCurrencies()) {
            if (!currency.getUuid().equals(uuid)) continue;
            return currency;
        }
        return null;
    }

    public static List<Currency> getCurrencies() {
        return currencies;
    }

    public static Account getAccount(Player player) {
        return AccountManager.getAccount(player.getUniqueId());
    }

    public static Account getAccount(String name) {
        for (Account account : AccountManager.getAccounts()) {
            if (account.getNickname() == null || !account.getNickname().equalsIgnoreCase(name)) continue;
            return account;
        }
        return GemsEconomy.getDataStore().loadAccount(name);
    }

    public static Account getAccount(UUID uuid) {
        for (Account account : AccountManager.getAccounts()) {
            if (!account.getUuid().equals(uuid)) continue;
            return account;
        }
        return GemsEconomy.getDataStore().loadAccount(uuid);
    }

    public static List<Account> getAccounts() {
        return accounts;
    }
}


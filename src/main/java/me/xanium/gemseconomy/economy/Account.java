/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.economy;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.logging.EconomyLogger;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Account {

    private UUID uuid;
    private String nickname;
    private Map<Currency, Double> balances = new HashMap<>();
    private boolean canReceiveCurrency = true;

    public Account(UUID uuid, String nickname) {
        this.uuid = uuid;
        this.nickname = nickname;
    }

    public boolean withdraw(Currency currency, double amount) {
        if (hasEnough(currency, amount)) {
            setBalance(currency, getBalance(currency) - amount);
            GemsEconomy.getDataStore().saveAccount(this);
            if(Bukkit.getPlayer(getUuid()) != null) {
                EconomyLogger.log("Withdraw", nickname, String.valueOf(amount), null, Bukkit.getPlayer(getUuid()).getLocation(), GemsEconomy.getInstance());
            }else{
                EconomyLogger.log("Withdraw", nickname, String.valueOf(amount), null, null, GemsEconomy.getInstance());
            }
            return true;
        }
        return false;
    }

    public boolean deposit(Currency currency, double amount) {
        if (isCanReceiveCurrency()) {
            setBalance(currency, getBalance(currency) + amount);
            GemsEconomy.getDataStore().saveAccount(this);
            if(Bukkit.getPlayer(getUuid()) != null) {
                EconomyLogger.log("Deposit", null, String.valueOf(amount), nickname, Bukkit.getPlayer(getUuid()).getLocation(), GemsEconomy.getInstance());
            }else{
                EconomyLogger.log("Deposit", null, String.valueOf(amount), nickname, null, GemsEconomy.getInstance());
            }
            return true;
        }
        return false;
    }

    public String getDisplayName() {
        return getNickname() != null ? getNickname() : getUuid().toString();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setBalance(Currency currency, double amount) {
        getBalances().put(currency, amount);
        EconomyLogger.log("Balance Set", null, String.valueOf(amount), nickname, null, GemsEconomy.getInstance());
    }

    public double getBalance(Currency currency) {
        if (getBalances().containsKey(currency)) {
            return getBalances().get(currency);
        }
        return currency.getDefaultBalance();
    }

    public double getBalance(String identifier){
        for(Currency currency : getBalances().keySet()){
            if(currency.getPlural().equalsIgnoreCase(identifier) || currency.getSingular().equalsIgnoreCase(identifier)){
                return getBalances().get(currency);
            }
        }
        return -1;
    }

    public boolean hasEnough(double amount){
        return hasEnough(AccountManager.getDefaultCurrency(), amount);
    }

    public boolean hasEnough(Currency currency, double amount){
        return getBalance(currency) >= amount;
    }

    public boolean isCanReceiveCurrency() {
        return canReceiveCurrency;
    }

    public void setCanReceiveCurrency(boolean canReceiveCurrency) {
        this.canReceiveCurrency = canReceiveCurrency;
    }

    public Map<Currency, Double> getBalances() {
        return balances;
    }
}


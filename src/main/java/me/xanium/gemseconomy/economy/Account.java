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
        if (this.getBalance(currency) >= amount) {
            this.setBalance(currency, this.getBalance(currency) - amount);
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

        if (this.isCanReceiveCurrency()) {
            this.setBalance(currency, this.getBalance(currency) + amount);
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
        return this.getNickname() != null ? this.getNickname() : this.getUuid().toString();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setBalance(Currency currency, double amount) {
        this.getBalances().put(currency, amount);
    }

    public double getBalance(Currency currency) {
        if (this.getBalances().containsKey(currency)) {
            return this.getBalances().get(currency);
        }
        return currency.getDefaultBalance();
    }

    public boolean hasEnough(double amount){
        return hasEnough(AccountManager.getDefaultCurrency(), amount);
    }

    public boolean hasEnough(Currency currency, double amount){
        if(amount >= getBalance(currency)){
            return false;
        }
        return true;
    }

    public boolean isCanReceiveCurrency() {
        return this.canReceiveCurrency;
    }

    public void setCanReceiveCurrency(boolean canReceiveCurrency) {
        this.canReceiveCurrency = canReceiveCurrency;
    }

    public Map<Currency, Double> getBalances() {
        return this.balances;
    }
}


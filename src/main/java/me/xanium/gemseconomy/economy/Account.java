/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.economy;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.utils.UtilServer;

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
            GemsEconomy.getInstance().getEconomyLogger().log("[WITHDRAW] Account: " + getDisplayName() + " were withdrawn: " + currency.format(amount));
            return true;
        }
        return false;
    }

    public boolean deposit(Currency currency, double amount) {
        if (isCanReceiveCurrency()) {
            setBalance(currency, getBalance(currency) + amount);
            GemsEconomy.getDataStore().saveAccount(this);
            GemsEconomy.getInstance().getEconomyLogger().log("[DEPOSIT] Account: " + getDisplayName() + " were deposited: " + currency.format(amount));
            return true;
        }
        return false;
    }

    public boolean convert(Currency exchanged, double exchangeAmount, Currency received, double amount) {
        if (amount != -1) {
            modifyBalance(exchanged, getBalance(exchanged) - exchangeAmount);
            modifyBalance(received, getBalance(received) + amount);
            GemsEconomy.getDataStore().saveAccount(this);
            GemsEconomy.getInstance().getEconomyLogger().log("[CONVERSION - Custom Rate] Account: " + getDisplayName() + " converted " + exchanged.format(exchangeAmount) + " to " + received.format(amount));
            return true;
        }
        double rate;
        boolean receiveRate = false;

        if(exchanged.getExchangeRate() > received.getExchangeRate()){
            rate = exchanged.getExchangeRate();
        }else{
            rate = received.getExchangeRate();
            receiveRate = true;
        }

        if(!receiveRate){

            double finalAmount = Math.round(exchangeAmount * rate);
            double removed = getBalance(exchanged) - exchangeAmount;
            double added = getBalance(received) + finalAmount;

            if(GemsEconomy.getInstance().isDebug()){
                UtilServer.consoleLog("Rate: " + rate);
                UtilServer.consoleLog("Finalized amount: " + finalAmount);
                UtilServer.consoleLog("Amount to remove: " + exchanged.format(removed));
                UtilServer.consoleLog("Amount to add: " + received.format(added));
            }

            if(hasEnough(exchanged, exchangeAmount)){
                this.modifyBalance(exchanged, removed);
                this.modifyBalance(received, added);
                GemsEconomy.getDataStore().saveAccount(this);
                GemsEconomy.getInstance().getEconomyLogger().log("[CONVERSION - Preset Rate] Account: " + getDisplayName() + " converted " + exchanged.format(removed) + " to " + received.format(added));
                return true;
            }
            return false;
        }

        double finalAmount = Math.round(exchangeAmount * rate);
        double removed = getBalance(exchanged) - finalAmount;
        double added = getBalance(received) + exchangeAmount;

        if(GemsEconomy.getInstance().isDebug()){
            UtilServer.consoleLog("Rate: " + rate);
            UtilServer.consoleLog("Finalized amount: " + finalAmount);
            UtilServer.consoleLog("Amount to remove: " + exchanged.format(removed));
            UtilServer.consoleLog("Amount to add: " + received.format(added));
        }

        if(hasEnough(exchanged, finalAmount)){
            this.modifyBalance(exchanged, removed);
            this.modifyBalance(received, added);
            GemsEconomy.getDataStore().saveAccount(this);
            GemsEconomy.getInstance().getEconomyLogger().log("[CONVERSION - Preset Rate] Account: " + getDisplayName() + " converted " + exchanged.format(removed) + " to " + received.format(added));
            return true;
        }

        return false;
    }

    public void setBalance(Currency currency, double amount) {
        getBalances().put(currency, amount);
        GemsEconomy.getInstance().getEconomyLogger().log("[BALANCE SET] Account: " + getDisplayName() + " were set to: " + currency.format(amount));
        GemsEconomy.getDataStore().saveAccount(this);
    }

    private void modifyBalance(Currency currency, double amount){
        getBalances().put(currency, amount);
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
        return -100; // Do not edit this because the datastore conversion needs this value to decide if the player does have a balance in this currency.
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


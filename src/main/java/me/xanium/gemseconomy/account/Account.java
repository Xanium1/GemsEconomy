/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.account;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.currency.Currency;
import me.xanium.gemseconomy.event.GemsConversionEvent;
import me.xanium.gemseconomy.event.GemsTransactionEvent;
import me.xanium.gemseconomy.utils.SchedulerUtils;
import me.xanium.gemseconomy.utils.TranactionType;
import me.xanium.gemseconomy.utils.UtilServer;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Account {

    private final UUID uuid;
    private String nickname;
    private Map<Currency, Double> balances;
    private boolean canReceiveCurrency = true;

    public Account(UUID uuid, String nickname) {
        this.uuid = uuid;
        this.nickname = nickname;
        this.balances = new HashMap<>();
    }

    public boolean withdraw(Currency currency, double amount) {
        if (hasEnough(currency, amount)) {
            GemsTransactionEvent event = new GemsTransactionEvent(currency, this, amount, TranactionType.WITHDRAW);
            SchedulerUtils.run(() -> Bukkit.getPluginManager().callEvent(event));
            if(event.isCancelled())return false;

            double finalAmount = getBalance(currency) - amount;
            this.modifyBalance(currency, finalAmount, true);
            GemsEconomy.getInstance().getEconomyLogger().log("[WITHDRAW] Account: " + getDisplayName() + " were withdrawn: " + currency.format(amount) + " and now has " + currency.format(finalAmount));
            return true;
        }
        return false;
    }

    public boolean deposit(Currency currency, double amount) {
        if (canReceiveCurrency()) {

            GemsTransactionEvent event = new GemsTransactionEvent(currency, this, amount, TranactionType.DEPOSIT);
            SchedulerUtils.run(() -> Bukkit.getPluginManager().callEvent(event));
            if(event.isCancelled())return false;

            double finalAmount = getBalance(currency) + amount;
            this.modifyBalance(currency, finalAmount, true);
            GemsEconomy.getInstance().getEconomyLogger().log("[DEPOSIT] Account: " + getDisplayName() + " were deposited: " + currency.format(amount) + " and now has " + currency.format(finalAmount));
            return true;
        }
        return false;
    }

    public boolean convert(Currency exchanged, double exchangeAmount, Currency received, double amount) {
        GemsConversionEvent event = new GemsConversionEvent(exchanged, received, this, exchangeAmount, amount);
        SchedulerUtils.run(() -> Bukkit.getPluginManager().callEvent(event));
        if(event.isCancelled())return false;

        if (amount != -1) {
            double removed = getBalance(exchanged) - exchangeAmount;
            double added = getBalance(received) + amount;
            modifyBalance(exchanged, removed, false);
            modifyBalance(received, added, false);
            GemsEconomy.getInstance().getDataStore().saveAccount(this);
            GemsEconomy.getInstance().getEconomyLogger().log("[CONVERSION - Custom Amount] Account: " + getDisplayName() + " converted " + exchanged.format(exchangeAmount) + " to " + received.format(amount));
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
                this.modifyBalance(exchanged, removed, false);
                this.modifyBalance(received, added, false);
                GemsEconomy.getInstance().getDataStore().saveAccount(this);
                GemsEconomy.getInstance().getEconomyLogger().log("[CONVERSION - Preset Rate] Account: " + getDisplayName() + " converted " + exchanged.format(removed) + " (Rate: " + rate + ") to " + received.format(added));
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
            this.modifyBalance(exchanged, removed, false);
            this.modifyBalance(received, added, false);
            GemsEconomy.getInstance().getDataStore().saveAccount(this);
            GemsEconomy.getInstance().getEconomyLogger().log("[CONVERSION - Preset Rate] Account: " + getDisplayName() + " converted " + exchanged.format(removed) + " (Rate: " + rate + ") to " + received.format(added));
            return true;
        }

        return false;
    }

    public void setBalance(Currency currency, double amount) {
        GemsTransactionEvent event = new GemsTransactionEvent(currency, this, amount, TranactionType.SET);
        SchedulerUtils.run(() -> Bukkit.getPluginManager().callEvent(event));
        if(event.isCancelled())return;

        getBalances().put(currency, amount);
        GemsEconomy.getInstance().getEconomyLogger().log("[BALANCE SET] Account: " + getDisplayName() + " were set to: " + currency.format(amount));
        GemsEconomy.getInstance().getDataStore().saveAccount(this);
    }

    /**
     * DO NOT USE UNLESS YOU HAVE VIEWED WHAT THIS DOES!
     *
     * This directly modifies the account balance for a currency, with the option of saving.
     *
     * @param currency - Currency to modify
     * @param amount - Amount of cash to modify.
     * @param save - Save the account or not. Should be done async!
     */
    public void modifyBalance(Currency currency, double amount, boolean save){
        getBalances().put(currency, amount);
        if(save){
            GemsEconomy.getInstance().getDataStore().saveAccount(this);
        }
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
        return 0; // Do not edit this
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
        return hasEnough(GemsEconomy.getInstance().getCurrencyManager().getDefaultCurrency(), amount);
    }

    public boolean hasEnough(Currency currency, double amount){
        return getBalance(currency) >= amount;
    }

    public boolean canReceiveCurrency() {
        return canReceiveCurrency;
    }

    public void setCanReceiveCurrency(boolean canReceiveCurrency) {
        this.canReceiveCurrency = canReceiveCurrency;
    }

    public Map<Currency, Double> getBalances() {
        return balances;
    }
}


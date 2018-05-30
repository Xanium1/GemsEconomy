/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.api;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.economy.Account;
import me.xanium.gemseconomy.economy.AccountManager;
import me.xanium.gemseconomy.economy.Currency;

import java.util.UUID;

public class GemsEconomyAPI {

    public GemsEconomyAPI(){
        if(AccountManager.getDefaultCurrency() == null){
            GemsEconomy.getInstance().getLogger().warning("||");
            GemsEconomy.getInstance().getLogger().warning("||");
            GemsEconomy.getInstance().getLogger().warning("||");
            GemsEconomy.getInstance().getLogger().warning("There is no default currency, so therefore none of the API will work!!!");
            GemsEconomy.getInstance().getLogger().warning("There is no default currency, so therefore none of the API will work!!!");
            GemsEconomy.getInstance().getLogger().warning("||");
            GemsEconomy.getInstance().getLogger().warning("||");
            GemsEconomy.getInstance().getLogger().warning("||");
        }
    }

    /**
     *
     * @param uuid - The users unique ID.
     * @param amount - An amount of the default currency.
     */
    public void deposit(UUID uuid, double amount){
        Account acc = AccountManager.getAccount(uuid);
        acc.deposit(AccountManager.getDefaultCurrency(), amount);
    }

    /**
     *
     * @param uuid - The users unique ID.
     * @param amount - An amount of a currency, if the currency is null, the default will be used.
     * @param currency - A specified currency.
     */
    public void deposit(UUID uuid, double amount, Currency currency){
        Account acc = AccountManager.getAccount(uuid);
        if(currency != null) {
            acc.deposit(currency, amount);
        }else{
            acc.deposit(AccountManager.getDefaultCurrency(), amount);
        }
    }

    /**
     *
     * @param uuid - The users unique ID.
     * @param amount - An amount of the default currency.
     */
    public void withdraw(UUID uuid, double amount){
        Account acc = AccountManager.getAccount(uuid);
        acc.withdraw(AccountManager.getDefaultCurrency(), amount);
    }

    /**
     *
     * @param uuid - The users unique ID.
     * @param amount - An amount of the currency.
     * @param currency - The currency you withdraw from.
     */
    public void withdraw(UUID uuid, double amount, Currency currency){
        Account acc = AccountManager.getAccount(uuid);
        if(currency != null) {
            acc.withdraw(currency, amount);
        }else{
            acc.withdraw(AccountManager.getDefaultCurrency(), amount);
        }
    }

    /**
     *
     * @param uuid - The users unique ID.
     * @return - The default currency balance of the user.
     */
    public double getBalance(UUID uuid){
        Account acc = AccountManager.getAccount(uuid);
        return acc.getBalance(AccountManager.getDefaultCurrency());
    }

    /**
     *
     * @param uuid - The users unique ID.
     * @param currency - An amount of the default currency.
     * @return - The balance of the specified currency.
     */
    public double getBalance(UUID uuid, Currency currency) {
        Account acc = AccountManager.getAccount(uuid);
        if (currency != null) {
            return acc.getBalance(currency);
        }else{
            return acc.getBalance(AccountManager.getDefaultCurrency());
        }
    }


}

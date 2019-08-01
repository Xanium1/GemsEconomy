/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.vault;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.economy.Account;
import me.xanium.gemseconomy.economy.AccountManager;
import me.xanium.gemseconomy.economy.Currency;
import me.xanium.gemseconomy.utils.UtilServer;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

public class GEVaultHook extends AbstractEconomy {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "GemsEconomy";
    }

    @Override
    public String format(double amount) {
        Currency currency = AccountManager.getDefaultCurrency();
        if(currency == null) return String.valueOf(amount);
        return currency.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        Currency currency = AccountManager.getDefaultCurrency();
        if(currency == null) return "";
        if(currency.getPlural() != null) {
            return currency.getPlural();
        }else{
            return "";
        }
    }

    @Override
    public String currencyNameSingular() {
        Currency currency = AccountManager.getDefaultCurrency();
        if(currency == null) return "";
        if(currency.getSingular() != null) {
            return currency.getSingular();
        }else{
            return "";
        }
    }

    @Override
    public boolean has(String playerName, double amount) {
        Account user = AccountManager.getAccount(playerName);
        if(user != null){
            return user.hasEnough(amount);
        }
        return false;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return has(player.getName(), amount);
    }

    @Override
    public boolean hasAccount(String playerName) {
        return AccountManager.getAccount(playerName) != null;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return AccountManager.getAccount(player.getUniqueId()) != null;
    }

    @Override
    public double getBalance(String playerName) {
        if(GemsEconomy.getInstance().isDebug())UtilServer.consoleLog("Lookup name: " + playerName);
        Account user = AccountManager.getAccount(playerName);
        Currency currency = AccountManager.getDefaultCurrency();
        return user.getBalance(currency);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        if(GemsEconomy.getInstance().isDebug())UtilServer.consoleLog("Lookup name: " + player.getName() + "(" + player.getUniqueId() + ")");
        Account user = AccountManager.getAccount(player.getUniqueId());
        Currency currency = AccountManager.getDefaultCurrency();
        return user.getBalance(currency);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if(GemsEconomy.getInstance().isDebug())UtilServer.consoleLog("Lookup name: " + player.getName() + "(" + player.getUniqueId() + ")");

        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");
        }

        double balance;
        EconomyResponse.ResponseType type = EconomyResponse.ResponseType.FAILURE;
        String error = null;

        Account user = AccountManager.getAccount(player.getUniqueId());
        Currency currency = AccountManager.getDefaultCurrency();

        if(user.withdraw(currency, amount)){
            balance = user.getBalance(currency);
            type = EconomyResponse.ResponseType.SUCCESS;
        }else{
            balance = user.getBalance(currency);
            error = "Could not withdraw " + amount + " to " + player.getName() + " because something went wrong.";
        }
        return new EconomyResponse(amount, balance, type, error);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if(GemsEconomy.getInstance().isDebug())UtilServer.consoleLog("Lookup name: " + player.getName() + "(" + player.getUniqueId()+ ")");

        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");
        }

        double balance;
        EconomyResponse.ResponseType type = EconomyResponse.ResponseType.FAILURE;
        String error = null;

        Account user = AccountManager.getAccount(player.getUniqueId());

        Currency currency = AccountManager.getDefaultCurrency();

        if(user.deposit(currency, amount)){
            balance = user.getBalance(currency);
            type = EconomyResponse.ResponseType.SUCCESS;
        }else{
            balance = user.getBalance(currency);
            error = "Could not deposit " + amount + " to " + player.getName() + " because something went wrong.";
        }
        return new EconomyResponse(amount, balance, type, error);
    }

    @Override
    public EconomyResponse withdrawPlayer(String player, double amount) {
        if(GemsEconomy.getInstance().isDebug())UtilServer.consoleLog("Lookup name: " + player);

        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");
        }

        double balance;
        EconomyResponse.ResponseType type = EconomyResponse.ResponseType.FAILURE;
        String error = null;

        Account user = AccountManager.getAccount(player);
        Currency currency = AccountManager.getDefaultCurrency();

        if(user.withdraw(currency, amount)){
            balance = user.getBalance(currency);
            type = EconomyResponse.ResponseType.SUCCESS;
        }else{
            balance = user.getBalance(currency);
            error = "Could not withdraw " + amount + " to " + player + " because something went wrong.";
        }
        return new EconomyResponse(amount, balance, type, error);
    }

    @Override
    public EconomyResponse depositPlayer(String player, double amount) {
        if(GemsEconomy.getInstance().isDebug())UtilServer.consoleLog("Lookup name: " + player);
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");
        }

        double balance;
        EconomyResponse.ResponseType type = EconomyResponse.ResponseType.FAILURE;
        String error = null;

        Account user = AccountManager.getAccount(player);
        Currency currency = AccountManager.getDefaultCurrency();

        if(user.deposit(currency, amount)){
            balance = user.getBalance(currency);
            type = EconomyResponse.ResponseType.SUCCESS;
        }else{
            balance = user.getBalance(currency);
            error = "Could not deposit " + amount + " to " + player + " because something went wrong.";
        }
        return new EconomyResponse(amount, balance, type, error);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GemsEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GemsEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GemsEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GemsEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GemsEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GemsEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GemsEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GemsEconomy does not support bank accounts!");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

}

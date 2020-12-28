/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.file;

import me.xanium.gemseconomy.GemsEconomy;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

public class Configuration {

    private GemsEconomy plugin;

    public Configuration(GemsEconomy plugin) {
        this.plugin = plugin;
    }

    public void loadDefaultConfig() {

        FileConfiguration config = plugin.getConfig();

        config.options().header(plugin.getDescription().getName()
                + "\n"
                + "Version: " + plugin.getDescription().getVersion()
                + "\nGemsEconomy Main Configuration file."
                + "\n"
                + "Developer(s): " + plugin.getDescription().getAuthors()
                + "\n\n"
                + "You have three valid storage methods, yaml, mysql or sqlite. If you choose mysql you would have to enter the database credentials down below."
                + "\n"
                + "All messages below are configurable, I hope you use them because it took 1 hour to make all of them into the plugin and configurable.");

        String path = "Messages.";

        config.addDefault("storage", "yaml");
        config.addDefault("debug", false);
        config.addDefault("vault", false);
        config.addDefault("transaction_log", true);

        config.addDefault("mysql.database", "minecraft");
        config.addDefault("mysql.tableprefix", "gemseconomy");
        config.addDefault("mysql.host", "localhost");
        config.addDefault("mysql.port", 3306);
        config.addDefault("mysql.username", "root");
        config.addDefault("mysql.password", "password");

      //  config.addDefault("sqlite.file", "database.sqlite");

        config.addDefault("cheque.material", Material.PAPER.toString());
        config.addDefault("cheque.name", "&aBank Note");
        config.addDefault("cheque.lore", Arrays.asList("&7Worth: {value}.", "&7&oWritten by {player}"));
        config.addDefault("cheque.console_name", "Console");
        config.addDefault("cheque.enabled", true);

        config.addDefault(path + "prefix", "&2&lGemsEconomy> ");
        config.addDefault(path + "nopermission", "&7You don't have permission to do this.");
        config.addDefault(path + "noconsole", "&7Console cannot do this.");
        config.addDefault(path + "invalidamount", "&7Not a valid amount.");
        config.addDefault(path + "invalidpage", "&7Not a valid page number.");
        config.addDefault(path + "pay_yourself", "&7You can't pay yourself.");
        config.addDefault(path + "player_is_null", "&7The specified player does not exist.");
        config.addDefault(path + "unknownCurrency", "&7Unknown Currency.");
        config.addDefault(path + "unknownCommand", "&7Unknown sub-command.");
        config.addDefault(path + "noDefaultCurrency", "&7No default currency.");
        config.addDefault(path + "currencyExists", "&7Currency already exists.");
        config.addDefault(path + "accountMissing", "&7Your account is missing. Please relog the server.");
        config.addDefault(path + "cannotReceiveMoney", "&a{player}&7 can't receive money.");
        config.addDefault(path + "insufficientFunds", "&7You don't have enough {currencycolor}{currency}&7!");
        config.addDefault(path + "targetInsufficientFunds", "&e{target} &7don't have enough {currencycolor}{currency}&7!");
        config.addDefault(path + "paid", "&7You were paid {currencycolor}{amount} &7from &a{player}&7.");
        config.addDefault(path + "payer", "&7You paid {currencycolor}{amount} &7to &a{player}&7.");
        config.addDefault(path + "payNoPermission", "&7You don't have permission to pay {currencycolor}{currency}&7.");
        config.addDefault(path + "currencyNotPayable", "{currencycolor}{currency} &7is not payable.");
        config.addDefault(path + "add", "&7You gave &a{player}&7: {currencycolor}{amount}. ");
        config.addDefault(path + "take", "&7You took {currencycolor}{amount} &7from &a{player}&7.");
        config.addDefault(path + "set", "&7You set &a{player}&7's balance to {currencycolor}{amount}&7.");

        config.addDefault(path + "exchange_rate_set", "&7Set the exchange rate for {currencycolor}{currency} &7to &a{amount}&7.");
        config.addDefault(path + "exchange_success_custom_other", "&7Successfully exchanged {currencycolor}({currEx}) &7for {currencycolor2}{receivedCurr}&7 to player {player}&7.");
        config.addDefault(path + "exchange_success_custom", "&7Successfully exchanged {currencycolor}({currEx}) &7for {currencycolor2}{receivedCurr}&7.");
        config.addDefault(path + "exchange_success", "&7Successfully exchanged {currencycolor}{ex_curr} &7for equivalent value in {currencycolor2}{re_curr}&7.");
        config.addDefault(path + "exchange_command.no_perms.preset", "&7You don't have permission to exchange currencies with a preset rate.");
        config.addDefault(path + "exchange_command.no_perms.custom", "&7You don't have permission to exchange currencies with a custom rate.");

        config.addDefault(path + "balance.current", "&a{player}&7's balance is: {currencycolor}{balance}");
        config.addDefault(path + "balance.multiple", "&a{player}&7's balances:");
        config.addDefault(path + "balance.list", "&a&l>> {currencycolor}{format}");
        config.addDefault(path + "balance.none", "&7No balances to show for &c{player}&7.");

        config.addDefault(path + "balance_top.balance", "&a&l-> {number}. {currencycolor}{player} &7- {currencycolor}{balance}");
        config.addDefault(path + "balance_top.header", "&f----- {currencycolor} Top Balances for {currencyplural} &7(Page {page})&f -----");
        config.addDefault(path + "balance_top.empty", "&7No accounts to display.");
        config.addDefault(path + "balance_top.next", "{currencycolor}/gbaltop {currencyplural} {page} &7for more.");
        config.addDefault(path + "balance_top.nosupport", "&a{storage} &7doesn't support /baltop.");

        config.addDefault(path + "cheque.success", "&7Cheque successfully written.");
        config.addDefault(path + "cheque.redeemed", "&7Cheque has been cashed in.");
        config.addDefault(path + "cheque.invalid", "&7This is not a valid cheque.");

        config.addDefault(path + "help.eco_command", Arrays.asList(
                "{prefix}&e&lEconomy Help",
                "&2&l>> &a/eco give <user> <amount> [currency] &8- &7Give a player an amount of a currency.",
                "&2&l>> &a/eco take <user> <amount> [currency] &8- &7Take an amount of a currency from a player.",
                "&2&l>> &a/eco set <user> <amount> [currency] &8- &7Set a players amount of a currency."));

        config.addDefault(path + "help.exchange_command", Arrays.asList(
                "{prefix}&b&lExchange Help",
                "&2&l>> &a/exchange <account> <currency_to_exchange> <amount> <currency_to_receive> <amount> &8- &7Exchange between currencies with a custom rate for an account.",
                "&2&l>> &a/exchange <currency_to_exchange> <amount> <currency_to_receive> <amount> &8- &7Exchange between currencies with a custom rate.",
                "&2&l>> &a/exchange <currency_to_exchange> <amount> <currency_to_receive> &8- &7Exchange with a pre-set exchange rate."));

        config.addDefault(path + "usage.pay_command", "&2&l>> &a/pay <user> <amount> [currency] &8- &7Pay the specified user the specified amount.");
        config.addDefault(path + "usage.give_command", "&2&l>> &a/eco give <user> <amount> [currency] &8- &7Give a player an amount of a currency.");
        config.addDefault(path + "usage.take_command", "&2&l>> &a/eco take <user> <amount> [currency] &8- &7Take an amount of a currency from a player.");
        config.addDefault(path + "usage.set_command", "&2&l>> &a/eco set <user> <amount> [currency] &8- &7Set a players amount of a currency.");

        config.addDefault(path + "help.cheque_command", Arrays.asList("{prefix}&e&lCheque Help",
                "&2&l>> &a/cheque write <amount> [currency] &8- &7Write a cheque with a specified amount and currency.",
                "&2&l>> &a/cheque redeem &8- &7Redeem the cheque."));

        config.addDefault(path + "help.currency_command", Arrays.asList("{prefix}&e&lCurrency Help",
                "&2&l>> &a/currency create <singular> <plural> &8- &7Create a currency.",
                "&2&l>> &a/currency delete <plural> &8- &7Delete a currency.",
                "&2&l>> &a/currency convert <method> &8- &7Convert storage method. WARN: Take backups first and make sure the storage you are switching to is empty!",
                "&2&l>> &a/currency backend <method> &8- &7Switch backend. This does not convert.",
                "&2&l>> &a/currency view <plural> &8- &7View information about a currency.",
                "&2&l>> &a/currency list &8- &7List of currencies.",
                "&2&l>> &a/currency symbol <plural> <char|remove> &8- &7Select a symbol for a currency or remove it.",
                "&2&l>> &a/currency color <plural> <color> &8- &7Select a color for a currency.",
                "&2&l>> &a/currency colorlist &8- &7List of Colors.",
                "&2&l>> &a/currency decimals <plural> &8- &7Enable decimals for a currency.",
                "&2&l>> &a/currency payable <plural> &8- &7Set a currency payable or not.",
                "&2&l>> &a/currency default <plural> &8- &7Set a currency as default.",
                "&2&l>> &a/currency startbal <plural> <amount> &8- &7Set the starting balance for a currency.",
                "&2&l>> &a/currency setrate <plural> <amount> &8- &7Sets the currency's exchange rate."));

        config.addDefault(path + "usage.currency_create", "&2&l>> &a/currency create <singular> <plural> &8- &7Create a currency.");
        config.addDefault(path + "usage.currency_delete", "&2&l>> &a/currency delete <plural> &8- &7Delete a currency.");
        config.addDefault(path + "usage.currency_convert", "&2&l>> &a/currency convert <method> &8- &7Convert storage method. WARN: Take backups first and make sure the storage you are switching to is empty!");
        config.addDefault(path + "usage.currency_backend", "&2&l>> &a/currency backend <method> &8- &7Switch backend. This does not convert.");
        config.addDefault(path + "usage.currency_view", "&2&l>> &a/currency view <plural> &8- &7View information about a currency.");
        config.addDefault(path + "usage.currency_list", "&2&l>> &a/currency list &8- &7List of currencies.");
        config.addDefault(path + "usage.currency_symbol", "&2&l>> &a/currency symbol <plural> <char|remove> &8- &7Select a symbol for a currency or remove it.");
        config.addDefault(path + "usage.currency_color", "&2&l>> &a/currency color <plural> <color> &8- &7Select a color for a currency.");
        config.addDefault(path + "usage.currency_colorlist", "&2&l>> &a/currency colorlist &8- &7List of Colors.");
        config.addDefault(path + "usage.currency_payable", "&2&l>> &a/currency payable <plural> &8- &7Set a currency payable or not.");
        config.addDefault(path + "usage.currency_default", "&2&l>> &a/currency default <plural> &8- &7Set a currency as default.");
        config.addDefault(path + "usage.currency_decimals", "&2&l>> &a/currency decimals <plural> &8- &7Enable decimals for a currency.");
        config.addDefault(path + "usage.currency_startbal", "&2&l>> &a/currency startbal <plural> <amount> &8- &7Set the starting balance for a currency.");
        config.addDefault(path + "usage.currency_setrate", "&2&l>> &a/currency setrate <plural> <amount> &8- &7Sets the currency's exchange rate.");

        config.options().copyDefaults(true);
        plugin.saveConfig();
        plugin.reloadConfig();
    }

}

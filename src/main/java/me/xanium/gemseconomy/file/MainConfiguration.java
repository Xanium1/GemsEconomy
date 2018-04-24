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

public class MainConfiguration {

    private GemsEconomy plugin;
    private String s = "Messages.";

    public MainConfiguration(GemsEconomy plugin) {
        this.plugin = plugin;
    }

    public void loadDefaultConfig() {

        FileConfiguration config = plugin.getConfig();

        config.options().header(plugin.getDescription().getName()
                + "\n"
                + "Version: " + plugin.getDescription().getVersion()
                + "\nGemsEco Main Configuration file."
                + "\n"
                + "Developer(s): " + plugin.getDescription().getAuthors()
                + "\n\n"
                + "You have two valid storage methods, yaml or mysql. If you choose mysql you would have to enter the database credentials down below."
                + "\n"
                + "All messages below are configurable, I hope you use them because it took 1 hour to make all of them into the plugin and configurable."
                + "\n"
                + "The setting 'migrate_old_accounts' is for migrating the old balances from the old system to the new system." +
                "\nThe player will receive the money in the servers default currency.");



        config.addDefault("storage", "yaml");
        config.addDefault("debug", false);
        config.addDefault("vault", false);
        config.addDefault("transaction_log", true);
        config.addDefault("migrate_old_accounts", false);

        config.addDefault("mysql.database", "minecraft");
        config.addDefault("mysql.tableprefix", "gemseconomy");
        config.addDefault("mysql.host", "localhost");
        config.addDefault("mysql.port", 3306);
        config.addDefault("mysql.username", "root");
        config.addDefault("mysql.password", "password");

        config.addDefault("cheque.material", Material.PAPER.toString());
        config.addDefault("cheque.name", "&aBank Note");
        config.addDefault("cheque.lore", Arrays.asList("&7Worth: {amount} {currency}.", "&7&oWritten by {player}"));
        config.addDefault("cheque.console_name", "Console");

        config.addDefault("Messages.prefix", "&a&lGemsEconomy> ");
        config.addDefault("Messages.nopermission", "&7You don't have permission to do this.");
        config.addDefault("Messages.noconsole", "&7Console cannot do this.");
        config.addDefault("Messages.invalidamount", "&7Not a valid amount.");
        config.addDefault("Messages.invalidpage", "&7Not a valid page number.");
        config.addDefault("Messages.pay_yourself", "&7You can't pay yourself.");
        config.addDefault("Messages.player_is_null", "&7The specified player does not exist.");
        config.addDefault("Messages.unknownCurrency", "§7Unknown Currency.");
        config.addDefault("Messages.noDefaultCurrency", "&7No default currency.");
        config.addDefault("Messages.currencyExists", "&7Currency already exists.");
        config.addDefault("Messages.accountMissing", "&7Your account is missing. Please relog the server.");
        config.addDefault("Messages.cannotReceiveMoney", "&a{player}&7 can't receive money.");
        config.addDefault("Messages.insufficientFunds", "&7You don't have enough {currencycolor}{currency}&7!");

        config.addDefault("Messages.managehelp", Arrays.asList(
                "{prefix}&e&lEconomy Help",
                "&2&l>> &a/geco give <user> <amount> [currency] &8- &7Give a player an amount of a currency.",
                "&2&l>> &a/geco take <user> <amount> [currency] &8- &7Take an amount of a currency from a player.",
                "&2&l>> &a/geco set <user> <amount> [currency] &8- &7Set a players amount of a currency."));

        config.addDefault(s + "pay_usage", "&2&l>> &a/gpay <user> <amount> [currency] &8- &7Pay the specified user the specified amount.");
        config.addDefault(s + "give_usage", "&2&l>> &a/geco give <user> <amount> [currency] &8- &7Give a player an amount of a currency.");
        config.addDefault(s + "take_usage", "&2&l>> &a/geco take <user> <amount> [currency] &8- &7Take an amount of a currency from a player.");
        config.addDefault(s + "set_usage", "&2&l>> &a/geco set <user> <amount> [currency] &8- &7Set a players amount of a currency.");

        config.addDefault("Messages.paid", "&7You were paid {currencycolor}{amount} &7from &a{player}&7.");

        config.addDefault("Messages.payer", "&7You paid {currencycolor}{amount} &7to &a{player}&7.");

        config.addDefault("Messages.payNoPermission", "&7You don't have permission to pay {currencycolor}{currency}&7.");
        config.addDefault("Messages.currencyNotPayable", "{currencycolor}{currency} &7is not payable.");

        config.addDefault("Messages.add", "&7You gave &a{player}&7: {currencycolor}{amount}. ");

        config.addDefault("Messages.take", "&7You took {currencycolor}{amount} &7from &a{player}&7.");

        config.addDefault("Messages.set", "&7You set &a{player}&7's {currencycolor} &7to {currencycolor}{amount}&7.");

        config.addDefault("Messages.balance", "&a{player}&7's balance is: {currencycolor}{balance}");
        config.addDefault("Messages.balance.multiple", "&a{player}&7's balances:");

        config.addDefault("Messages.balance.none", "&7No balances to show for &c{player}&7.");

        config.addDefault("Messages.balance_top_header", "&f----- {currencycolor} Top Balances for {currencyplural} &7(Page {page})&f -----");

        config.addDefault("Messages.balance_top_empty", "&7No accounts to display.");

        config.addDefault("Messages.balance_top_next", "{currencycolor}/gbaltop {currencyplural} {page} &7for more.");

        config.addDefault("Messages.balance_top", "&a&l-> {number}. {currencycolor}{player} &7- {currencycolor}{currencysymbol}{balance}");

        config.addDefault("Messages.balance_top_nosupport", "&a{storage} &7doesn't support /baltop.");

        config.addDefault("Messages.chequehelp", Arrays.asList(
                "&2&l>> &a/cheque write <amount> &8- &7Write a cheque with a specified amount.",
                "&2&l>> &a/cheque redeem &8- &7&oRedeem the cheque."));

        config.addDefault("Messages.cheque_success", "&7Cheque successfully written.");
        config.addDefault("Messages.cheque_redeemed", "&7Cheque has been cashed in.");
        config.addDefault("Messages.cheque_invalid", "&7This is not a valid cheque.");

        config.addDefault("Messages.currency_help", Arrays.asList("{prefix}&7Currency Help",
                "&2&l>> &a/gcurr create <singular> <plural> &8- &7Create a currency.",
                "&2&l>> &a/gcurr delete <plural> &8- &7Delete a currency.",
                "&2&l>> &a/gcurr view <plural> &8- &7View information about a currency.",
                "&2&l>> &a/gcurr list &8- &7List of currencies.",
                "&2&l>> &a/gcurr symbol <plural> <char|remove> &8- &7Select a symbol for a currency or remove it.",
                "&2&l>> &a/gcurr color <plural> <color> &8- &7Select a color for a currency.",
                "&2&l>> &a/gcurr colorlist &8- &7List of Colors.",
                "&2&l>> &a/gcurr decimals <plural> &8- &7Enable decimals for a currency.",
                "&2&l>> &a/gcurr payable <plural> &8- &7Set a currency payable or not.",
                "&2&l>> &a/gcurr default <plural> &8- &7Set a currency as default.",
                "&2&l>> &a/gcurr startbal <plural> <amount> &8- &7Set the starting balance for a currency."));

        config.addDefault("Messages.usage.currency_create", "&2&l>> &a/gcurr create <singular> <plural> &8- &7Create a currency.");
        config.addDefault("Messages.usage.currency_delete", "&2&l>> &a/gcurr delete <plural> &8- &7Delete a currency.");
        config.addDefault("Messages.usage.currency_view", "&2&l>> &a/gcurr view <plural> &8- &7View information about a currency.");
        config.addDefault("Messages.usage.currency_list", "&2&l>> &a/gcurr list &8- &7List of currencies.");
        config.addDefault("Messages.usage.currency_symbol", "&2&l>> &a/gcurr symbol <plural> <char|remove> &8- &7Select a symbol for a currency or remove it.");
        config.addDefault("Messages.usage.currency_color", "&2&l>> &a/gcurr color <plural> <color> &8- &7Select a color for a currency.");
        config.addDefault("Messages.usage.currency_colorlist", "&2&l>> &a/gcurr colorlist &8- &7List of Colors.");
        config.addDefault("Messages.usage.currency_payable", "&2&l>> &a/gcurr payable <plural> &8- &7Set a currency payable or not.");
        config.addDefault("Messages.usage.currency_default", "&2&l>> &a/gcurr default <plural> &8- &7Set a currency as default.");
        config.addDefault("Messages.usage.currency_decimals", "&2&l>> &a/gcurr decimals <plural> &8- &7Enable decimals for a currency.");
        config.addDefault("Messages.usage.currency_startbal", "&2&l>> &a/gcurr startbal <plural> <amount> &8- &7Set the starting balance for a currency.");

        config.addDefault("Messages.currency.view", Arrays.asList(""));

        config.options().copyDefaults(true);
        plugin.saveConfig();
        plugin.reloadConfig();
    }

}

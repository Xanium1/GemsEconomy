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
                + "All messages below are configurable, I hope you use them because it took 1 hour to make all of them into the plugin and configurable.");



        config.addDefault("storage", "yaml");
        config.addDefault("debug", false);
        config.addDefault("vault", false);
        config.addDefault("transaction_log", true);

        config.addDefault("mysql.database", "minecraft");
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
        config.addDefault("Messages.balanceconsole", "&2&l>> &a/bal <user> &8- &7Shows user balance.");
        config.addDefault("Messages.unsufficientfunds", "&7You don't have enough gems.");
        config.addDefault("Messages.negativevalue", "&7You can't remove that much.");
        config.addDefault("Messages.unvalidamount", "&7Not a valid amount.");
        config.addDefault("Messages.unvalidpage", "&7Not a valid page number.");
        config.addDefault("Messages.pay_yourself", "&7You can't pay yourself.");
        config.addDefault("Messages.player_is_null", "&7The specified player does not exist.");
        config.addDefault("Messages.unknownCurrency", "§7Unknown Currency.");

        config.addDefault("Messages.managehelp", Arrays.asList(
                "&2&l>> &a/geco give <user> <amount> [currency] &8- &7Give a player an amount of a currency.",
                "&2&l>> &a/geco take <user> <amount> [currency] &8- &7Take an amount of a currency from a player.",
                "&2&l>> &a/geco set <user> <amount> [currency] &8- &7Set a players amount of a currency."));

        config.addDefault(s + "pay_usage", "&2&l>> &a/gpay <user> <amount> [currency] &8- &7Pay the specified user the specified amount.");
        config.addDefault(s + "give_usage", "&2&l>> &a/geco give <user> <amount> [currency] &8- &7Give a player an amount of a currency.");
        config.addDefault(s + "take_usage", "&2&l>> &a/geco take <user> <amount> [currency] &8- &7Take an amount of a currency from a player.");
        config.addDefault(s + "set_usage", "&2&l>> &a/geco set <user> <amount> [currency] &8- &7Set a players amount of a currency.");

        config.addDefault("Messages.paid", "&7You were paid {currencycolor}{amount} {currencyplural} &7from &a{player}&7.");

        config.addDefault("Messages.payer", "&7You paid &a{player} &7an amount of &f{amount}&7.");

        config.addDefault("Messages.add", "&7You gave &a{player}&7: {currencycolor}{amount}. ");

        config.addDefault("Messages.take", "&7You took {currencycolor}{amount} &7from &a{player}&7.");

        config.addDefault("Messages.set", "&7You set &a{player}&7's {currencycolor} &7to {currencycolor}{amount}&7.");

        config.addDefault("Messages.balance", "&7Your balance is: &f{gems} &7gem(s).");

        config.addDefault("Messages.balanceother", "&a{player}&7's balance is: &f{gems} &7gem(s).");

        config.addDefault("Messages.balance_top_header", "&f----- {currencycolor} Top Balances for {currencyplural} &7(Page {page})&f -----");

        config.addDefault("Messages.balance_top_empty", "&7No accounts to display.");

        config.addDefault("Messages.balance_top_next", "{currencycolor}/gbaltop {currencyplural} {page} &7for more.");

        config.addDefault("Messages.balance_top", "&a&l-> {number}. {currencycolor}{player} &7- {currencycolor}{currencysymbol}{balance}");

        config.addDefault("Messages.balance_top_nosupport", "{storage} doesn't support /baltop.");

        config.addDefault("Messages.chequehelp", Arrays.asList(
                "&2&l>> &a/cheque write <amount> &8- &7Write a cheque with a specified amount.",
                "&2&l>> &a/cheque redeem &8- &7&oRedeem the cheque."));

        config.addDefault("Messages.cheque_success", "&7Cheque successfully written.");
        config.addDefault("Messages.cheque_redeemed", "&7Cheque has been cashed in.");
        config.addDefault("Messages.cheque_invalid", "&7This is not a valid cheque.");

        config.options().copyDefaults(true);
        plugin.saveConfig();
        plugin.reloadConfig();
    }

}

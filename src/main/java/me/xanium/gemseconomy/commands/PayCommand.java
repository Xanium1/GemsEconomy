/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package me.xanium.gemseconomy.commands;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.economy.Account;
import me.xanium.gemseconomy.economy.AccountManager;
import me.xanium.gemseconomy.economy.Currency;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PayCommand implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, Command command, String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cYou must be a player to use this command.");
            return true;
        }
        new BukkitRunnable(){

            public void run() {
                if (!sender.hasPermission("eco.pay")) {
                    sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cYou don't have permission to pay.");
                    return;
                }
                if (args.length < 2) {
                    sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cAllows you to pay other players.");
                    sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUsage: \u00a7f/pay <Account> <Amount> [Currency]");
                    return;
                }
                Currency currency = AccountManager.getDefaultCurrency();
                if (args.length == 3) {
                    currency = AccountManager.getCurrency(args[2]);
                }
                if (currency != null) {
                    double amount;
                    block22 : {
                        if (!currency.isPayable()) {
                            sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7c" + currency.getPlural() + " are not payable.");
                            return;
                        }
                        if (!sender.hasPermission("eco.pay." + currency.getPlural().toLowerCase()) && !sender.hasPermission("eco.pay." + currency.getSingular().toLowerCase())) {
                            sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cYou don't have permission to pay " + currency.getPlural() + ".");
                            return;
                        }
                        if (currency.isDecimalSupported()) {
                            try {
                                amount = Double.parseDouble(args[1]);
                                if (amount <= 0.0) {
                                    throw new NumberFormatException();
                                }
                                break block22;
                            }
                            catch (NumberFormatException ex) {
                                sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cPlease provide a valid amount.");
                                return;
                            }
                        }
                        try {
                            amount = Integer.parseInt(args[1]);
                            if (amount <= 0.0) {
                                throw new NumberFormatException();
                            }
                        }
                        catch (NumberFormatException ex) {
                            sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cPlease provide a valid amount.");
                            return;
                        }
                    }
                    Account account = AccountManager.getAccount((Player)sender);
                    if (account != null) {
                        Account target = AccountManager.getAccount(args[0]);
                        if (target != null) {
                            if (target.isCanReceiveCurrency()) {
                                if (account.getBalance(currency) >= amount) {
                                    account.setBalance(currency, account.getBalance(currency) - amount);
                                    target.setBalance(currency, target.getBalance(currency) + amount);
                                    GemsEconomy.getDataStore().saveAccount(account);
                                    GemsEconomy.getDataStore().saveAccount(target);
                                    sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aYou sent " + (Object)currency.getColor() + currency.format(amount) + "\u00a7a to " + target.getDisplayName() + ".");
                                } else {
                                    sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cYou don't have enough " + currency.getPlural() + " to pay " + target.getDisplayName() + ".");
                                }
                            } else {
                                sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7c" + target.getDisplayName() + " can't receive money.");
                            }
                        } else {
                            sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cTarget account not found.");
                        }
                    } else {
                        sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cYou don't have an account.");
                    }
                } else {
                    sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUnknown currency.");
                }
            }
        }.runTaskAsynchronously(GemsEconomy.getInstance());
        return true;
    }

}


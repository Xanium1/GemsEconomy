/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.commands;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.account.Account;
import me.xanium.gemseconomy.currency.Currency;
import me.xanium.gemseconomy.file.F;
import me.xanium.gemseconomy.utils.UtilServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EconomyCommand implements CommandExecutor {

    private final GemsEconomy plugin = GemsEconomy.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s124, String[] args) {
        if (!sender.hasPermission("gemseconomy.command.economy")) {
            sender.sendMessage(F.getNoPerms());
            return true;
        }

        if (args.length == 0) {
            F.getManageHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("add")) {
            if (!sender.hasPermission("gemseconomy.command.give")) {
                sender.sendMessage(F.getNoPerms());
                return true;
            }
            changeBalance(sender, args, false);
        } else if (args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("remove")) {
            if (!sender.hasPermission("gemseconomy.command.take")) {
                sender.sendMessage(F.getNoPerms());
                return true;
            }
            changeBalance(sender, args, true);
        } else if (args[0].equalsIgnoreCase("set")) {
            if (!sender.hasPermission("gemseconomy.command.set")) {
                sender.sendMessage(F.getNoPerms());
                return true;
            }
            set(sender, args);
        } else if (args[0].equalsIgnoreCase("cache")) {
            for(Account a : plugin.getAccountManager().getAccounts()){
                UtilServer.consoleLog("Account: " + a.getNickname() + " cached");
            }
        } else {
            sender.sendMessage(F.getUnknownSubCommand());
        }
        return true;
    }

    private void changeBalance(CommandSender sender, String[] args, boolean withdraw) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!withdraw) {
                if (args.length < 3) {
                    sender.sendMessage(F.getGiveUsage());
                    return;
                }
                Currency currency = plugin.getCurrencyManager().getDefaultCurrency();
                if (args.length == 4) {
                    currency = plugin.getCurrencyManager().getCurrency(args[3]);
                }
                if (currency != null) {
                    double amount;
                    if (currency.isDecimalSupported()) {
                        try {
                            amount = Double.parseDouble(args[2]);
                            if (amount <= 0.0) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(F.getUnvalidAmount());
                            return;
                        }
                    } else {
                        try {
                            amount = Integer.parseInt(args[2]);
                            if (amount <= 0.0) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(F.getUnvalidAmount());
                            return;
                        }
                    }

                    Account target = plugin.getAccountManager().getAccount(args[1]);
                    if (target != null) {
                        if (target.deposit(currency, amount)) {
                            sender.sendMessage(F.getAddMessage()
                                    .replace("{player}", target.getNickname())
                                    .replace("{currencycolor}", currency.getColor() + "")
                                    .replace("{amount}", currency.format(amount)));
                        }
                    } else {
                        sender.sendMessage(F.getPlayerDoesNotExist());
                    }
                } else {
                    sender.sendMessage(F.getUnknownCurrency());
                }
            } else {
                if (args.length < 3) {
                    sender.sendMessage(F.getTakeUsage());
                    return;
                }
                Currency currency = plugin.getCurrencyManager().getDefaultCurrency();
                if (args.length == 4) {
                    currency = plugin.getCurrencyManager().getCurrency(args[3]);
                }
                if (currency != null) {
                    double amount;

                    if (currency.isDecimalSupported()) {
                        try {
                            amount = Double.parseDouble(args[2]);
                            if (amount < 0.0) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(F.getUnvalidAmount());
                            return;
                        }
                    } else {
                        try {
                            amount = Integer.parseInt(args[2]);
                            if (amount < 0.0) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(F.getUnvalidAmount());
                            return;
                        }
                    }
                    Account target = plugin.getAccountManager().getAccount(args[1]);
                    if (target != null) {
                        if (target.withdraw(currency, amount)) {
                            sender.sendMessage(F.getTakeMessage()
                                    .replace("{player}", target.getNickname())
                                    .replace("{currencycolor}", currency.getColor() + "")
                                    .replace("{amount}", currency.format(amount)));
                        } else {
                            sender.sendMessage(F.getTargetInsufficientFunds()
                                    .replace("{currencycolor}", currency.getColor() + "")
                                    .replace("{currency}", currency.getPlural())
                                    .replace("{target}", target.getDisplayName()));
                        }
                    } else {
                        sender.sendMessage(F.getPlayerDoesNotExist());
                    }
                } else {
                    sender.sendMessage(F.getUnknownCurrency());
                }
            }
        });
    }

    private void set(CommandSender sender, String[] args){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (args.length < 3) {
                sender.sendMessage(F.getSetUsage());
                return;
            }
            Currency currency = plugin.getCurrencyManager().getDefaultCurrency();
            if (args.length == 4) {
                currency = plugin.getCurrencyManager().getCurrency(args[3]);
            }
            if (currency != null) {
                double amount;
                if (currency.isDecimalSupported()) {
                    try {
                        amount = Double.parseDouble(args[2]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(F.getUnvalidAmount());
                        return;
                    }
                } else {
                    try {
                        amount = Integer.parseInt(args[2]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(F.getUnvalidAmount());
                        return;
                    }
                }
                Account target = plugin.getAccountManager().getAccount(args[1]);
                if (target != null) {
                    target.setBalance(currency, amount);
                    sender.sendMessage(F.getSetMessage()
                            .replace("{player}", target.getNickname())
                            .replace("{currencycolor}", currency.getColor() + "")
                            .replace("{amount}", currency.format(amount)));
                } else {
                    sender.sendMessage(F.getPlayerDoesNotExist());
                }
            } else {
                sender.sendMessage(F.getUnknownCurrency());
            }
        });
    }
}

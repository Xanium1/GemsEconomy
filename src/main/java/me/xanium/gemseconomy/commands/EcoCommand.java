/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.commands;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.economy.Account;
import me.xanium.gemseconomy.economy.AccountManager;
import me.xanium.gemseconomy.economy.Currency;
import me.xanium.gemseconomy.file.F;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EcoCommand implements CommandExecutor {

    private final GemsEconomy plugin = GemsEconomy.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s124, String[] args) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

            if (!sender.hasPermission("gemseconomy.command.economy")) {
                sender.sendMessage(F.getNoPerms());
                return;
            }

            if (args.length == 0) {
                F.getManageHelp(sender);
                return;
            }

            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("add")) {
                if (!sender.hasPermission("gemseconomy.command.give")) {
                    sender.sendMessage(F.getNoPerms());
                    return;
                }
                changeBalance(sender, args, false);
            } else if (args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("remove")) {
                if (!sender.hasPermission("gemseconomy.command.take")) {
                    sender.sendMessage(F.getNoPerms());
                    return;
                }
                changeBalance(sender, args, true);
            } else if (args[0].equalsIgnoreCase("set")) {
                if (!sender.hasPermission("gemseconomy.command.set")) {
                    sender.sendMessage(F.getNoPerms());
                    return;
                }
                if (args.length < 3) {
                    sender.sendMessage(F.getSetUsage());
                    return;
                }
                Currency currency = AccountManager.getDefaultCurrency();
                if (args.length == 4) {
                    currency = AccountManager.getCurrency(args[3]);
                }
                if (currency != null) {
                    double amount;
                    if (currency.isDecimalSupported()) {
                        try {
                            amount = Double.parseDouble(args[2]);
                        }
                        catch (NumberFormatException ex) {
                            sender.sendMessage(F.getUnvalidAmount());
                            return;
                        }
                    }else {
                        try {
                            amount = Integer.parseInt(args[2]);
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(F.getUnvalidAmount());
                            return;
                        }
                    }
                    Account target = AccountManager.getAccount(args[1]);
                    if (target != null) {
                        target.setBalance(currency, amount);
                        GemsEconomy.getDataStore().saveAccount(target);
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
            } else {
                sender.sendMessage(F.getPrefix() + "§cUnknown command");
            }

        });
        return true;
    }

    private void changeBalance(CommandSender sender, String[] args, boolean withdraw){
        if(!withdraw){
            if (args.length < 3) {
                sender.sendMessage(F.getGiveUsage());
                return;
            }
            Currency currency = AccountManager.getDefaultCurrency();
            if (args.length == 4) {
                currency = AccountManager.getCurrency(args[3]);
            }
            if (currency != null) {
                double amount;
                block14:
                {
                    if (currency.isDecimalSupported()) {
                        try {
                            amount = Double.parseDouble(args[2]);
                            if (amount <= 0.0) {
                                throw new NumberFormatException();
                            }
                            break block14;
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(F.getUnvalidAmount());
                            return;
                        }
                    }
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
                Account target = AccountManager.getAccount(args[1]);
                if (target != null) {
                    target.setBalance(currency, target.getBalance(currency) + amount);
                    GemsEconomy.getDataStore().saveAccount(target);
                    sender.sendMessage(F.getAddMessage()
                            .replace("{player}", target.getNickname())
                            .replace("{currencycolor}", currency.getColor() + "")
                            .replace("{amount}", currency.format(amount)));
                } else {
                    sender.sendMessage(F.getPlayerDoesNotExist());
                }
            } else {
                sender.sendMessage(F.getUnknownCurrency());
            }
        }else{
            if (args.length < 3) {
                sender.sendMessage(F.getTakeUsage());
                return;
            }
            Currency currency = AccountManager.getDefaultCurrency();
            if (args.length == 4) {
                currency = AccountManager.getCurrency(args[3]);
            }
            if (currency != null) {
                double amount;
                block14:
                {
                    if (currency.isDecimalSupported()) {
                        try {
                            amount = Double.parseDouble(args[2]);
                            if (amount <= 0.0) {
                                throw new NumberFormatException();
                            }
                            break block14;
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(F.getUnvalidAmount());
                            return;
                        }
                    }
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
                Account target = AccountManager.getAccount(args[1]);
                if (target != null) {
                    target.setBalance(currency, target.getBalance(currency) - amount);
                    GemsEconomy.getDataStore().saveAccount(target);
                    sender.sendMessage(F.getTakeMessage()
                            .replace("{player}", target.getNickname())
                            .replace("{currencycolor}", currency.getColor() + "")
                            .replace("{amount}", currency.format(amount)));
                } else {
                    sender.sendMessage(F.getPlayerDoesNotExist());
                }
            } else {
                sender.sendMessage(F.getUnknownCurrency());
            }
        }

    }
}

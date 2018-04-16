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
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final GemsEconomy plugin = GemsEconomy.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s13542415, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(F.getNoConsole());
            return true;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!sender.hasPermission("gemseconomy.command.pay")) {
                sender.sendMessage(F.getNoPerms());
                return;
            }
            if (args.length < 2) {
                sender.sendMessage(F.getPayUsage());
                return;
            }
            Currency currency = AccountManager.getDefaultCurrency();
            if (args.length == 3) {
                currency = AccountManager.getCurrency(args[2]);
            }
            if (currency != null) {
                double amount;
                block22:
                {
                    if (!currency.isPayable()) {
                        sender.sendMessage(F.getCurrencyNotPayable().replace("{currencycolor}", ""+currency.getColor()).replace("{currency}", currency.getPlural()));
                        return;
                    }
                    if (!sender.hasPermission("gemseconomy.command.pay." + currency.getPlural().toLowerCase()) && !sender.hasPermission("gemseconomy.command.pay." + currency.getSingular().toLowerCase())) {
                        sender.sendMessage(F.getPayNoPerms().replace("{currencycolor}", ""+currency.getColor()).replace("{currency}", currency.getPlural()));
                        return;
                    }
                    if (currency.isDecimalSupported()) {
                        try {
                            amount = Double.parseDouble(args[1]);
                            if (amount <= 0.0) {
                                throw new NumberFormatException();
                            }
                            break block22;
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(F.getUnvalidAmount());
                            return;
                        }
                    }
                    try {
                        amount = Integer.parseInt(args[1]);
                        if (amount <= 0.0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(F.getUnvalidAmount());
                        return;
                    }
                }
                Account account = AccountManager.getAccount((Player) sender);
                if (account != null) {
                    Account target = AccountManager.getAccount(args[0]);
                    if (target != null) {
                        if(target.getUuid() != account.getUuid()) {
                            if (target.isCanReceiveCurrency()) {
                                if (account.getBalance(currency) >= amount) {
                                    account.setBalance(currency, account.getBalance(currency) - amount);
                                    target.setBalance(currency, target.getBalance(currency) + amount);
                                    GemsEconomy.getDataStore().saveAccount(account);
                                    GemsEconomy.getDataStore().saveAccount(target);
                                    sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aYou sent " + currency.getColor() + currency.format(amount) + "\u00a7a to " + target.getDisplayName() + ".");
                                } else {
                                    sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cYou don't have enough " + currency.getPlural() + " to pay " + target.getDisplayName() + ".");
                                }
                            } else {
                                sender.sendMessage(F.getCannotReceive().replace("{player}", target.getDisplayName()));
                            }
                        }else{
                            sender.sendMessage(F.getPayYourself());
                        }
                    } else {
                        sender.sendMessage(F.getPlayerDoesNotExist());
                    }
                } else {
                    sender.sendMessage(F.getAccountMissing());
                }
            } else {
                sender.sendMessage(F.getUnknownCurrency());
            }
        });
        return true;
    }

}


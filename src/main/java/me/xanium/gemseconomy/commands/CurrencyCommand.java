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
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package me.xanium.gemseconomy.commands;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.economy.AccountManager;
import me.xanium.gemseconomy.economy.Currency;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class CurrencyCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String s124, String[] args) {
        new BukkitRunnable(){

            public void run() {
                if (!sender.hasPermission("eco.currencies")) {
                    sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cYou don't have permission to manage currencies.");
                    return;
                }
                if (args.length == 0) {
                    sender.sendMessage("\u00a7e\u00a7l[Eco] \u00a7eCurrency Management");
                    sender.sendMessage("\u00a7e\u00a7l[Eco] \u00a7b\u00a7l-> \u00a7b/currency create \u00a77<Singular> <Plural>");
                    sender.sendMessage("\u00a7e\u00a7l[Eco] \u00a7b\u00a7l-> \u00a7b/currency delete \u00a77<Name>");
                    sender.sendMessage("\u00a7e\u00a7l[Eco] \u00a7b\u00a7l-> \u00a7b/currency view \u00a77<Name>");
                    sender.sendMessage("\u00a7e\u00a7l[Eco] \u00a7b\u00a7l-> \u00a7b/currency list");
                    sender.sendMessage("\u00a7e\u00a7l[Eco] \u00a7b\u00a7l-> \u00a7b/currency symbol \u00a77<Name> <Char|Remove>");
                    sender.sendMessage("\u00a7e\u00a7l[Eco] \u00a7b\u00a7l-> \u00a7b/currency color \u00a77<Name> <ChatColor>");
                    sender.sendMessage("\u00a7e\u00a7l[Eco] \u00a7b\u00a7l-> \u00a7b/currency decimals \u00a77<Name>");
                    sender.sendMessage("\u00a7e\u00a7l[Eco] \u00a7b\u00a7l-> \u00a7b/currency payable \u00a77<Name>");
                    sender.sendMessage("\u00a7e\u00a7l[Eco] \u00a7b\u00a7l-> \u00a7b/currency default \u00a77<Name>");
                    sender.sendMessage("\u00a7e\u00a7l[Eco] \u00a7b\u00a7l-> \u00a7b/currency startingbal \u00a77<Name> <Amount>");
                } else {
                    String cmd = args[0];
                    if (cmd.equalsIgnoreCase("create")) {
                        if (args.length == 3) {
                            String single = args[1];
                            String plural = args[2];
                            if (AccountManager.getCurrency(single) == null && AccountManager.getCurrency(plural) == null) {
                                Currency currency = new Currency(UUID.randomUUID(), single, plural);
                                sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aCreated currency: " + currency.getPlural());
                                AccountManager.getCurrencies().add(currency);
                                if (AccountManager.getCurrencies().size() == 1) {
                                    currency.setDefaultCurrency(true);
                                }
                                GemsEconomy.getDataStore().saveCurrency(currency);
                            } else {
                                sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cCurrency already exists.");
                            }
                        } else {
                            sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUsage: \u00a7f/currency create <Singular> <Plural>");
                        }
                    } else if (cmd.equalsIgnoreCase("list")) {
                        sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aThere are \u00a7f" + AccountManager.getCurrencies().size() + "\u00a7a currencies.");
                        for (Currency currency : AccountManager.getCurrencies()) {
                            sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7b\u00a7l-> \u00a7b" + currency.getSingular());
                        }
                    } else if (cmd.equalsIgnoreCase("view")) {
                        if (args.length == 2) {
                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null) {
                                sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aInfo for " + currency.getUuid().toString());
                                sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aSingular: \u00a7f" + currency.getSingular() + "\u00a7a, Plural: \u00a7f" + currency.getPlural());
                                sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aNew players start with \u00a7f" + currency.format(currency.getDefaultBalance()) + "\u00a7a.");
                                sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aDecimals? \u00a7f" + (currency.isDecimalSupported() ? "Yes" : "No"));
                                sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aDefault? \u00a7f" + (currency.isDefaultCurrency() ? "Yes" : "No"));
                                sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aPayable? \u00a7f" + (currency.isPayable() ? "Yes" : "No"));
                                sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aColor: " + (Object)currency.getColor() + currency.getColor().name());
                            } else {
                                sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUnknown currency.");
                            }
                        } else {
                            sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUsage: \u00a7f/currency create <Singular> <Plural>");
                        }
                    } else if (cmd.equalsIgnoreCase("startingbal")) {
                        if (args.length == 3) {
                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null) {
                                double amount;
                                block76 : {
                                    if (currency.isDecimalSupported()) {
                                        try {
                                            amount = Double.parseDouble(args[2]);
                                            if (amount <= 0.0) {
                                                throw new NumberFormatException();
                                            }
                                            break block76;
                                        }
                                        catch (NumberFormatException ex) {
                                            sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cPlease provide a valid amount.");
                                            return;
                                        }
                                    }
                                    try {
                                        amount = Integer.parseInt(args[2]);
                                        if (amount <= 0.0) {
                                            throw new NumberFormatException();
                                        }
                                    }
                                    catch (NumberFormatException ex) {
                                        sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cPlease provide a valid amount.");
                                        return;
                                    }
                                }
                                currency.setDefaultBalance(amount);
                                sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aStarting balance for " + currency.getPlural() + " set: " + currency.getDefaultBalance());
                                GemsEconomy.getDataStore().saveCurrency(currency);
                            } else {
                                sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUnknown currency.");
                            }
                        } else {
                            sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUsage: \u00a7f/currency create <Singular> <Plural>");
                        }
                    } else if (cmd.equalsIgnoreCase("color")) {
                        if (args.length == 3) {
                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null) {
                                try {
                                    ChatColor color = ChatColor.valueOf((String)args[2].toUpperCase());
                                    if (color.isFormat()) {
                                        throw new Exception();
                                    }
                                    currency.setColor(color);
                                    sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aColor for " + currency.getPlural() + " updated: " + (Object)color + color.name());
                                    GemsEconomy.getDataStore().saveCurrency(currency);
                                }
                                catch (Exception ex) {
                                    sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cInvalid chat color.");
                                }
                            } else {
                                sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUnknown currency.");
                            }
                        } else {
                            sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUsage: \u00a7f/currency create <Singular> <Plural>");
                        }
                    } else if (cmd.equalsIgnoreCase("symbol")) {
                        if (args.length == 3) {
                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null) {
                                String symbol = args[2];
                                if (symbol.equalsIgnoreCase("remove")) {
                                    currency.setSymbol(null);
                                    sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aCurrency symbol removed for " + currency.getPlural());
                                    GemsEconomy.getDataStore().saveCurrency(currency);
                                } else if (symbol.length() == 1) {
                                    currency.setSymbol(symbol);
                                    sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aCurrency symbol for " + currency.getPlural() + " updated: " + symbol);
                                    GemsEconomy.getDataStore().saveCurrency(currency);
                                } else {
                                    sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cSymbol must be 1 character, or say \"remove\".");
                                }
                            } else {
                                sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUnknown currency.");
                            }
                        } else {
                            sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUsage: \u00a7f/currency create <Singular> <Plural>");
                        }
                    } else if (cmd.equalsIgnoreCase("default")) {
                        if (args.length == 2) {
                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null) {
                                Currency c = AccountManager.getDefaultCurrency();
                                if (c != null) {
                                    c.setDefaultCurrency(false);
                                    GemsEconomy.getDataStore().saveCurrency(c);
                                }
                                currency.setDefaultCurrency(true);
                                sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aSet default currency to " + currency.getPlural());
                                GemsEconomy.getDataStore().saveCurrency(currency);
                            } else {
                                sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUnknown currency.");
                            }
                        } else {
                            sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUsage: \u00a7f/currency create <Singular> <Plural>");
                        }
                    } else if (cmd.equalsIgnoreCase("payable")) {
                        if (args.length == 2) {
                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null) {
                                currency.setPayable(!currency.isPayable());
                                sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aToggled payability for " + currency.getPlural() + ": " + currency.isPayable());
                                GemsEconomy.getDataStore().saveCurrency(currency);
                            } else {
                                sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUnknown currency.");
                            }
                        } else {
                            sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUsage: \u00a7f/currency create <Singular> <Plural>");
                        }
                    } else if (cmd.equalsIgnoreCase("decimals")) {
                        if (args.length == 2) {
                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null) {
                                currency.setDecimalSupported(!currency.isDecimalSupported());
                                sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aToggled Decimal Support for " + currency.getPlural() + ": " + currency.isDecimalSupported());
                                GemsEconomy.getDataStore().saveCurrency(currency);
                            } else {
                                sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUnknown currency.");
                            }
                        } else {
                            sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUsage: \u00a7f/currency create <Singular> <Plural>");
                        }
                    } else if (cmd.equalsIgnoreCase("delete")) {
                        if (args.length == 2) {
                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null) {
                                AccountManager.getAccounts().stream().filter(account -> account.getBalances().containsKey(currency)).forEach(account -> {
                                    account.getBalances().remove(currency);
                                }
                                );
                                GemsEconomy.getDataStore().deleteCurrency(currency);
                                AccountManager.getCurrencies().remove(currency);
                                sender.sendMessage("\u00a7a\u00a7l[Eco] \u00a7aDeleted currency: " + currency.getPlural());
                            } else {
                                sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUnknown currency.");
                            }
                        } else {
                            sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUsage: \u00a7f/currency create <Singular> <Plural>");
                        }
                    } else {
                        sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cUnknown currency sub-command.");
                    }
                }
            }
        }.runTaskAsynchronously((Plugin)GemsEconomy.getInstance());
        return true;
    }

}


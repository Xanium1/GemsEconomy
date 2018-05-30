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
import me.xanium.gemseconomy.file.F;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CurrencyCommand implements CommandExecutor {

    private final GemsEconomy plugin = GemsEconomy.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s124, String[] args) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                if (!sender.hasPermission("gemseconomy.command.currency")) {
                    sender.sendMessage(F.getNoPerms());
                    return;
                }
                if (args.length == 0) {
                    F.sendCurrencyUsage(sender);
                } else {
                    String cmd = args[0];
                    if (cmd.equalsIgnoreCase("create")) {
                        if (args.length == 3) {
                            String single = args[1];
                            String plural = args[2];
                            if (AccountManager.getCurrency(single) == null && AccountManager.getCurrency(plural) == null) {
                                Currency currency = new Currency(UUID.randomUUID(), single, plural);
                                sender.sendMessage(F.getPrefix() + "§7Created currency: §a" + currency.getPlural());
                                AccountManager.getCurrencies().add(currency);
                                if (AccountManager.getCurrencies().size() == 1) {
                                    currency.setDefaultCurrency(true);
                                }
                                GemsEconomy.getDataStore().saveCurrency(currency);
                            } else {
                                sender.sendMessage(F.getPrefix() + "§cCurrency already exists.");
                            }
                        } else {
                            sender.sendMessage(F.getCurrencyUsage_Create());
                        }
                    } else if (cmd.equalsIgnoreCase("list")) {
                        sender.sendMessage(F.getPrefix() + "§7There are §f" + AccountManager.getCurrencies().size() + "§7 currencies.");
                        for (Currency currency : AccountManager.getCurrencies()) {
                            sender.sendMessage("§a§l>> §e" + currency.getSingular());
                        }
                    } else if (cmd.equalsIgnoreCase("view")) {
                        if (args.length == 2) {
                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null) {
                                sender.sendMessage(F.getPrefix() + "§7ID: §c" + currency.getUuid().toString());
                                sender.sendMessage(F.getPrefix() + "§7Singular: §a" + currency.getSingular() + "§7, Plural: §a" + currency.getPlural());
                                sender.sendMessage(F.getPrefix() + "§7Start Balance: " + currency.getColor() + currency.format(currency.getDefaultBalance()) + "§7.");
                                sender.sendMessage(F.getPrefix() + "§7Decimals: " + (currency.isDecimalSupported() ? "§aYes" : "§cNo"));
                                sender.sendMessage(F.getPrefix() + "§7Default: " + (currency.isDefaultCurrency() ? "§aYes" : "§cNo"));
                                sender.sendMessage(F.getPrefix() + "§7Payable: " + (currency.isPayable() ? "§aYes" : "§cNo"));
                                sender.sendMessage(F.getPrefix() + "§7Color: " + currency.getColor() + currency.getColor().name());
                            } else {
                                sender.sendMessage(F.getUnknownCurrency());
                            }
                        } else {
                            sender.sendMessage(F.getCurrencyUsage_View());
                        }
                    } else if (cmd.equalsIgnoreCase("startbal")) {
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
                                            sender.sendMessage(F.getUnvalidAmount());
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
                                        sender.sendMessage(F.getUnvalidAmount());
                                        return;
                                    }
                                }
                                currency.setDefaultBalance(amount);
                                sender.sendMessage(F.getPrefix() + "§7Starting balance for §f" + currency.getPlural() + " §7set: §a" + currency.getDefaultBalance());
                                GemsEconomy.getDataStore().saveCurrency(currency);
                            } else {
                                sender.sendMessage(F.getUnknownCurrency());
                            }
                        } else {
                            sender.sendMessage(F.getCurrencyUsage_Startbal());
                        }
                    } else if (cmd.equalsIgnoreCase("color")) {
                        if (args.length == 3) {
                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null) {
                                try {
                                    ChatColor color = ChatColor.valueOf(args[2].toUpperCase());
                                    if (color.isFormat()) {
                                        throw new Exception();
                                    }
                                    currency.setColor(color);
                                    sender.sendMessage(F.getPrefix() + "§7Color for §f" + currency.getPlural() + " §7updated: " + color + color.name());
                                    GemsEconomy.getDataStore().saveCurrency(currency);
                                }
                                catch (Exception ex) {
                                    sender.sendMessage(F.getPrefix() + "§cInvalid chat color.");
                                }
                            } else {
                                sender.sendMessage(F.getUnknownCurrency());
                            }
                        } else {
                            sender.sendMessage(F.getCurrencyUsage_Color());
                        }
                    } else if(cmd.equalsIgnoreCase("colorlist")){
                        sender.sendMessage("§0§lBLACK §7= black");
                        sender.sendMessage("§1§lDARK BLUE §7= dark_blue");
                        sender.sendMessage("§2§lDARK GREEN §7= dark_green");
                        sender.sendMessage("§3§lDARK AQUA §7= dark_aqua");
                        sender.sendMessage("§4§lDARK RED §7= dark_red");
                        sender.sendMessage("§5§lDARK PURPLE §7= dark_purple");
                        sender.sendMessage("§6§lGOLD §7= gold");
                        sender.sendMessage("§7§lGRAY §7= gray");
                        sender.sendMessage("§8§lDARK GRAY §7= dark_gray");
                        sender.sendMessage("§9§lBLUE §7= blue");
                        sender.sendMessage("§a§lGREEN §7= green");
                        sender.sendMessage("§b§lAQUA §7= aqua");
                        sender.sendMessage("§c§lRED §7= red");
                        sender.sendMessage("§d§lLIGHT PURPLE §7= light_purple");
                        sender.sendMessage("§e§lYELLOW §7= yellow");
                        sender.sendMessage("§f§lWHITE §7= white|reset");
                    } else if (cmd.equalsIgnoreCase("symbol")) {
                        if (args.length == 3) {
                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null) {
                                String symbol = args[2];
                                if (symbol.equalsIgnoreCase("remove")) {
                                    currency.setSymbol(null);
                                    sender.sendMessage(F.getPrefix() + "§7Currency symbol removed for §a" + currency.getPlural());
                                    GemsEconomy.getDataStore().saveCurrency(currency);
                                } else if (symbol.length() == 1) {
                                    currency.setSymbol(symbol);
                                    sender.sendMessage(F.getPrefix() + "§7Currency symbol for §f" + currency.getPlural() + " §7updated: §a" + symbol);
                                    GemsEconomy.getDataStore().saveCurrency(currency);
                                } else {
                                    sender.sendMessage(F.getPrefix() + "§7Symbol must be 1 character, or remove it with \"remove\".");
                                }
                            } else {
                                sender.sendMessage(F.getUnknownCurrency());
                            }
                        } else {
                            sender.sendMessage(F.getCurrencyUsage_Symbol());
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
                                sender.sendMessage(F.getPrefix() + "§7Set default currency to §a" + currency.getPlural());
                                GemsEconomy.getDataStore().saveCurrency(currency);
                            } else {
                                sender.sendMessage(F.getUnknownCurrency());
                            }
                        } else {
                            sender.sendMessage(F.getCurrencyUsage_Default());
                        }
                    } else if (cmd.equalsIgnoreCase("payable")) {
                        if (args.length == 2) {
                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null) {
                                currency.setPayable(!currency.isPayable());
                                sender.sendMessage(F.getPrefix() + "§7Toggled payability for §f" + currency.getPlural() + "§7: " + (currency.isPayable() ? "§aYes" : "§cNo"));
                                GemsEconomy.getDataStore().saveCurrency(currency);
                            } else {
                                sender.sendMessage(F.getUnknownCurrency());
                            }
                        } else {
                            sender.sendMessage(F.getCurrencyUsage_Payable());
                        }
                    } else if (cmd.equalsIgnoreCase("decimals")) {
                        if (args.length == 2) {
                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null) {
                                currency.setDecimalSupported(!currency.isDecimalSupported());
                                sender.sendMessage(F.getPrefix() + "§7Toggled Decimal Support for §f" + currency.getPlural() + "§7: " + (currency.isDecimalSupported() ? "§aYes" : "§cNo"));
                                GemsEconomy.getDataStore().saveCurrency(currency);
                            } else {
                                sender.sendMessage(F.getUnknownCurrency());
                            }
                        } else {
                            sender.sendMessage(F.getCurrencyUsage_Decimals());
                        }
                    } else if (cmd.equalsIgnoreCase("delete")) {
                        if (args.length == 2) {
                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null) {
                                AccountManager.getAccounts().stream().filter(account -> account.getBalances().containsKey(currency)).forEach(account -> account.getBalances().remove(currency));
                                GemsEconomy.getDataStore().deleteCurrency(currency);
                                AccountManager.getCurrencies().remove(currency);
                                sender.sendMessage(F.getPrefix() + "§7Deleted currency: §a" + currency.getPlural());
                            } else {
                                sender.sendMessage(F.getUnknownCurrency());
                            }
                        } else {
                            sender.sendMessage(F.getCurrencyUsage_Delete());
                        }
                    } else {
                        sender.sendMessage(F.getPrefix() + "§cUnknown currency sub-command.");
                    }
                }

        });
        return true;
    }

}


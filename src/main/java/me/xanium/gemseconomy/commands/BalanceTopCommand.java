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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class BalanceTopCommand implements CommandExecutor {

    private final GemsEconomy plugin = GemsEconomy.getInstance();
    private static final int ACCOUNTS_PER_PAGE = 10;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s670, String[] args) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

            if (!sender.hasPermission("gemseconomy.command.baltop")) {
                sender.sendMessage(F.getNoPerms());
                return;
            }
            if (!GemsEconomy.getDataStore().isTopSupported()) {
                sender.sendMessage(F.getBalanceTopNoSupport().replace("{storage}", GemsEconomy.getDataStore().getName()));
                return;
            }
            Currency currency = AccountManager.getDefaultCurrency();
            int page = 1;
            if (args.length > 0) {
                currency = AccountManager.getCurrency(args[0]);
                if (args.length == 2) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(F.getUnvalidPage());
                        return;
                    }
                }
            }
            if (page < 1) {
                page = 1;
            }
            int offset = 10 * (page - 1);
            if (currency != null) {
                Map<String, Double> toplist = GemsEconomy.getDataStore().getTopList(currency, offset, ACCOUNTS_PER_PAGE);
                sender.sendMessage(F.getBalanceTopHeader()
                        .replace("{currencycolor}", "" + currency.getColor())
                        .replace("{currencyplural}", currency.getPlural())
                        .replace("{page}", String.valueOf(page)));

                int num = (10 * (page - 1)) + 1;
                for (String name : toplist.keySet()) {
                    double balance = toplist.get(name);
                    sender.sendMessage(F.getBalanceTop()
                            .replace("{number}", String.valueOf(num))
                            .replace("{currencycolor}", ""+currency.getColor())
                            .replace("{player}", name)
                            .replace("{currencyplural}", currency.getPlural())
                            .replace("{currencysymbol}", currency.getSymbol())
                            .replace("{balance}", currency.format(balance)));
                    num++;
                }
                if (toplist.isEmpty()) {
                    sender.sendMessage(F.getBalanceTopEmpty());
                } else {
                    sender.sendMessage(F.getBalanceTopNext().replace("{currencycolor}", "" + currency.getColor()).replace("{currencyplural}", currency.getPlural()).replace("{page}", String.valueOf((page + 1))));
                }

            } else {
                sender.sendMessage(F.getUnknownCurrency());
            }
        });
        return true;
    }

}


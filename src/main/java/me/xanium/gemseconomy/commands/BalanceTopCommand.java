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
import me.xanium.gemseconomy.currency.CachedTopListEntry;
import me.xanium.gemseconomy.currency.Currency;
import me.xanium.gemseconomy.file.F;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BalanceTopCommand implements CommandExecutor {

    private final GemsEconomy plugin = GemsEconomy.getInstance();
    private final int ACCOUNTS_PER_PAGE = 10;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s670, String[] args) {
        if (!sender.hasPermission("gemseconomy.command.baltop")) {
            sender.sendMessage(F.getNoPerms());
            return true;
        }
        if (!plugin.getDataStore().isTopSupported()) {
            sender.sendMessage(F.getBalanceTopNoSupport().replace("{storage}", plugin.getDataStore().getName()));
            return true;
        }

        Currency currency = plugin.getCurrencyManager().getDefaultCurrency();
        int page = 1;
        if (args.length > 0) {
            currency = plugin.getCurrencyManager().getCurrency(args[0]);
            if (currency == null) {
                sender.sendMessage(F.getUnknownCurrency());
                return true;
            }

            if (args.length == 2) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(F.getUnvalidPage());
                    return true;
                }
            }
        }
        if (page < 1) {
            page = 1;
        }
        int offset = 10 * (page - 1);
        final int pageNumber = page;
        final Currency curr = currency;

        if (currency != null) {
            plugin.getDataStore().getTopList(currency, offset, ACCOUNTS_PER_PAGE, cachedTopListEntries -> {
                sender.sendMessage(F.getBalanceTopHeader()
                        .replace("{currencycolor}", "" + curr.getColor())
                        .replace("{currencyplural}", curr.getPlural())
                        .replace("{page}", String.valueOf(pageNumber)));

                int num = (10 * (pageNumber - 1)) + 1;
                for (CachedTopListEntry entry : cachedTopListEntries) {
                    double balance = entry.getAmount();
                    sender.sendMessage(F.getBalanceTop().replace("{number}", String.valueOf(num)).replace("{currencycolor}", "" + curr.getColor())
                            .replace("{player}", entry.getName()).replace("{balance}", curr.format(balance)));
                    num++;
                }
                if (cachedTopListEntries.isEmpty()) {
                    sender.sendMessage(F.getBalanceTopEmpty());
                } else {
                    sender.sendMessage(F.getBalanceTopNext().replace("{currencycolor}", "" + curr.getColor()).replace("{currencyplural}", curr.getPlural()).replace("{page}", String.valueOf((pageNumber + 1))));
                }
            });
        }
        return true;
    }

}


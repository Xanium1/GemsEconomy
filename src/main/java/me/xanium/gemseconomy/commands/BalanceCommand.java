/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.commands;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.file.F;
import me.xanium.gemseconomy.economy.Account;
import me.xanium.gemseconomy.economy.AccountManager;
import me.xanium.gemseconomy.economy.Currency;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    private GemsEconomy plugin = GemsEconomy.getInstance();

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String s, final String[] args) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

            if (!sender.hasPermission("gemseconomy.command.balance")) {
                sender.sendMessage(F.getNoPerms());
                return;
            }
            Account account = null;
            if (args.length == 0 && sender instanceof Player) {
                account = AccountManager.getAccount((Player) sender);
            } else if (sender.hasPermission("gemseconomy.command.balance.other") && args.length == 1) {
                account = AccountManager.getAccount(args[0]);
            }
            if (account != null) {
                if (AccountManager.getCurrencies().size() == 0) {
                    sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cNo balances to show for \"" + account.getDisplayName() + "\".");
                } else if (AccountManager.getCurrencies().size() == 1) {
                    Currency currency = AccountManager.getDefaultCurrency();
                    if (currency == null) {
                        sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cNo default currency.");
                        return;
                    }
                    double balance = account.getBalance(currency);
                    sender.sendMessage("\u00a7e\u00a7l[Eco] \u00a7e" + account.getDisplayName() + "'s balance: " + currency.getColor() + currency.format(balance));
                } else {
                    sender.sendMessage("\u00a7e\u00a7l[Eco] \u00a7e" + account.getDisplayName() + "'s balance(s):");
                    for (Currency currency : AccountManager.getCurrencies()) {
                        double balance = account.getBalance(currency);
                        sender.sendMessage("\u00a7e\u00a7l[Eco]     " + currency.getColor() + currency.format(balance));
                    }
                }
            } else {
                sender.sendMessage("\u00a7c\u00a7l[Eco] \u00a7cAccount not found.");
            }
        });
        return true;
    }

}





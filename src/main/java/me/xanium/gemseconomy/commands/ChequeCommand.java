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
import me.xanium.gemseconomy.economy.Cheque;
import me.xanium.gemseconomy.economy.Currency;
import me.xanium.gemseconomy.file.F;
import me.xanium.gemseconomy.nbt.NBTItem;
import me.xanium.gemseconomy.utils.UtilString;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChequeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(F.getNoConsole());
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("gemseconomy.command.cheque")) {
            player.sendMessage(F.getNoPerms());
            return true;
        }
        if (args.length == 0) {
            F.getChequeHelp(player);
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("redeem")) {
                if (player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().getType().equals(Material.valueOf(GemsEconomy.getInstance().getConfig().getString("cheque.material")))) {
                    NBTItem item = new NBTItem(player.getInventory().getItemInMainHand());
                    if (item.getItem().getItemMeta().hasDisplayName() && item.getItem().getItemMeta().hasLore() && item.hasKey("value") && item.hasKey("currency")) {
                        if (Cheque.isAValidCheque(item)) {
                            if (item.getString("value") != null) {
                                double value = Double.parseDouble(item.getString("value"));
                                if (item.getItem().getAmount() > 1) {
                                    item.getItem().setAmount(item.getItem().getAmount() - 1);
                                    Account user = AccountManager.getAccount(player);
                                    Currency currency = Cheque.getChequeCurrency(item);
                                    user.deposit(currency, value);
                                    player.sendMessage(F.getChequeRedeemed());
                                    return true;
                                }
                                player.getInventory().remove(item.getItem());
                                Account user = AccountManager.getAccount(player);
                                Currency currency = Cheque.getChequeCurrency(item);
                                user.deposit(currency, value);
                                player.sendMessage(F.getChequeRedeemed());
                                return true;
                            }
                        }
                    }
                }
            }
        }
        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("write")) {
                if (UtilString.validateInput(sender, args[1])) {
                    double amount = Double.valueOf(args[1]);
                    if (amount != 0) {
                        if (args.length == 3) {
                            Currency currency = AccountManager.getCurrency(args[2]);
                            if (currency != null) {
                                Account user = AccountManager.getAccount(player);
                                user.withdraw(currency, amount);
                                Cheque c = new Cheque();
                                player.getInventory().addItem(c.writeCheque(player.getName(), currency, amount));
                                player.sendMessage(F.getChequeSucess());
                                return true;
                            } else {
                                player.sendMessage(F.getUnknownCurrency());
                            }
                        }
                        Cheque c = new Cheque();
                        Account user = AccountManager.getAccount(player);
                        user.withdraw(AccountManager.getDefaultCurrency(), amount);
                        player.getInventory().addItem(c.writeCheque(player.getName(), AccountManager.getDefaultCurrency(), amount));
                        player.sendMessage(F.getChequeSucess());
                        return true;

                    } else {
                        player.sendMessage(F.getUnvalidAmount());
                    }
                } else {
                    player.sendMessage(F.getUnvalidAmount());
                }
            }
        }
        return true;
    }
}

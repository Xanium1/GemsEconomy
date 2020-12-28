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
import me.xanium.gemseconomy.nbt.NBTItem;
import me.xanium.gemseconomy.utils.UtilString;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChequeCommand implements CommandExecutor {

    private final GemsEconomy plugin = GemsEconomy.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!plugin.isChequesEnabled()){
            return true;
        }

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

                if (player.getInventory().getItemInMainHand().getType().equals(Material.valueOf(plugin.getConfig().getString("cheque.material")))) {

                    NBTItem item = new NBTItem(player.getInventory().getItemInMainHand());
                    if (item.getItem().getItemMeta().hasDisplayName() && item.getItem().getItemMeta().hasLore() && item.hasKey("value") && item.hasKey("currency")) {

                        if (plugin.getChequeManager().isValid(item)) {

                            if (item.getString("value") != null) {
                                double value = Double.parseDouble(item.getString("value"));

                                if (item.getItem().getAmount() > 1) {
                                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);

                                    Account user = plugin.getAccountManager().getAccount(player);
                                    Currency currency = plugin.getChequeManager().getCurrency(item);
                                    user.deposit(currency, value);
                                    player.sendMessage(F.getChequeRedeemed());
                                    return true;
                                } else {

                                    player.getInventory().remove(item.getItem());
                                    Account user = plugin.getAccountManager().getAccount(player);
                                    Currency currency = plugin.getChequeManager().getCurrency(item);
                                    user.deposit(currency, value);
                                    player.sendMessage(F.getChequeRedeemed());
                                    return true;
                                }
                            } else {
                                player.sendMessage(F.getChequeInvalid());
                            }
                        } else {
                            player.sendMessage(F.getChequeInvalid());
                        }
                    } else {
                        player.sendMessage(F.getChequeInvalid());
                    }
                } else {
                    player.sendMessage(F.getChequeInvalid());
                }
            } else {
                player.sendMessage(F.getUnknownSubCommand());
            }
        }

        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("write")) {

                if (UtilString.validateInput(sender, args[1])) {

                    double amount = Double.parseDouble(args[1]);
                    if (amount != 0) {

                        if (args.length == 3) {

                            Currency currency = plugin.getCurrencyManager().getCurrency(args[2]);
                            Account user = plugin.getAccountManager().getAccount(player);
                            if (currency != null) {
                                if(user.hasEnough(currency, amount)) {

                                    user.withdraw(currency, amount);
                                    player.getInventory().addItem(plugin.getChequeManager().write(player.getName(), currency, amount));
                                    player.sendMessage(F.getChequeSucess());
                                    return true;
                                }else{
                                    player.sendMessage(F.getInsufficientFunds().replace("{currencycolor}", currency.getColor()+"").replace("{currency}", currency.getSingular()));
                                }
                            } else {
                                player.sendMessage(F.getUnknownCurrency());
                            }
                        }

                        Currency defaultCurrency = plugin.getCurrencyManager().getDefaultCurrency();
                        Account user = plugin.getAccountManager().getAccount(player);
                        if(user.hasEnough(amount)) {
                            user.withdraw(plugin.getCurrencyManager().getDefaultCurrency(), amount);
                            player.getInventory().addItem(plugin.getChequeManager().write(player.getName(), plugin.getCurrencyManager().getDefaultCurrency(), amount));
                            player.sendMessage(F.getChequeSucess());
                            return true;
                        }else{
                            player.sendMessage(F.getInsufficientFunds().replace("{currencycolor}", defaultCurrency.getColor()+"").replace("{currency}", defaultCurrency.getSingular()));
                        }
                    } else {
                        player.sendMessage(F.getUnvalidAmount());
                    }
                } else {
                    player.sendMessage(F.getUnvalidAmount());
                }
            } else {
                player.sendMessage(F.getUnknownSubCommand());
            }
        }
        return true;
    }
}

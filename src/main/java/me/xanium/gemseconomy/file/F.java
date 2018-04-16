/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.file;

import me.xanium.gemseconomy.GemsEconomy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class F {

    private static GemsEconomy plugin = GemsEconomy.getInstance();
    private static FileConfiguration cfg = plugin.getConfig();

    public static String getPrefix() {
        return colorize(cfg.getString("Messages.prefix"));
    }

    public static String getNoPerms() {
        return getPrefix() + colorize(cfg.getString("Messages.nopermission"));
    }

    public static String getNoConsole() {
        return getPrefix() + colorize(cfg.getString("Messages.noconsole"));
    }

    public static String getPaidMessage() {
        return getPrefix() + colorize(cfg.getString("Messages.paid"));
    }

    public static String getPayerMessage() {
        return getPrefix() + colorize(cfg.getString("Messages.payer"));
    }

    public static String getPayUsage() {
        return getPrefix() + colorize(cfg.getString("Messages.pay_usage"));
    }

    public static String getAddMessage() {
        return getPrefix() + colorize(cfg.getString("Messages.add"));
    }

    public static String getTakeMessage() {
        return getPrefix() + colorize(cfg.getString("Messages.take"));
    }

    public static String getSetMessage() {
        return getPrefix() + colorize(cfg.getString("Messages.set"));
    }

    public static String getPlayerDoesNotExist() {
        return getPrefix() + colorize(cfg.getString("Messages.player_is_null"));
    }

    public static String getUnsufficientfunds() {
        return getPrefix() + colorize(cfg.getString("Messages.unsufficientfunds"));
    }

    public static String getPayYourself() {
        return getPrefix() + colorize(cfg.getString("Messages.pay_yourself"));
    }

    public static String getUnknownCurrency() {
        return getPrefix() + colorize(cfg.getString("Messages.unknownCurrency"));
    }

    public static void getManageHelp(CommandSender sender) {
        for (String s : cfg.getStringList("Messages.managehelp")) {
            sender.sendMessage(colorize(s.replace("{prefix}", getPrefix())));
        }
    }

    public static String getBalance() {
        return getPrefix() + colorize(cfg.getString("Messages.balance"));
    }
    public static String getBalanceMultiple() {
        return getPrefix() + colorize(cfg.getString("Messages.balance.multiple"));
    }

    public static String getUnvalidAmount() {
        return getPrefix() + colorize(cfg.getString("Messages.unvalidamount"));
    }
    public static String getUnvalidPage() {
        return getPrefix() + colorize(cfg.getString("Messages.unvalidpage"));
    }

    public static void getChequeHelp(CommandSender sender) {
        for (String s : cfg.getStringList("Messages.chequehelp")) {
            sender.sendMessage(colorize(s.replace("{prefix}", getPrefix())));
        }
    }

    public static String getChequeSucess() {
        return getPrefix() + colorize(cfg.getString("Messages.cheque_success"));
    }

    public static String getChequeRedeemed() {
        return getPrefix() + colorize(cfg.getString("Messages.cheque_redeemed"));
    }

    public static String getChequeInvalid() {
        return getPrefix() + colorize(cfg.getString("Messages.cheque_invalid"));
    }

    public static String getGiveUsage(){
        return getPrefix() + colorize(cfg.getString("Messages.give_usage"));
    }

    public static String getTakeUsage(){
        return getPrefix() + colorize(cfg.getString("Messages.take_usage"));
    }

    public static String getSetUsage(){
        return getPrefix() + colorize(cfg.getString("Messages.set_usage"));
    }

    public static String getBalanceTopHeader(){
        return colorize(cfg.getString("Messages.balance_top_header"));
    }

    public static String getBalanceTopEmpty(){
        return colorize(cfg.getString("Messages.balance_top_empty"));
    }

    public static String getBalanceTopNext(){
        return colorize(cfg.getString("Messages.balance_top_next"));
    }

    public static String getBalanceTop(){
        return colorize(cfg.getString("Messages.balance_top"));
    }

    public static String getNoDefaultCurrency(){
        return colorize(cfg.getString("Messages.noDefaultCurrency"));
    }

    public static String getBalanceNone(){
        return colorize(cfg.getString("Messages.balance.none"));
    }

    public static String getBalanceTopNoSupport(){
        return getPrefix() + colorize(cfg.getString("Messages.balance_top_nosupport"));
    }

    public static String getPayNoPerms(){
        return getPrefix() + colorize(cfg.getString("Messages.payNoPermission"));
    }

    public static String getCurrencyNotPayable(){
        return getPrefix() + colorize(cfg.getString("Messages.currencyNotPayable"));
    }

    public static String getAccountMissing(){
        return getPrefix() + colorize(cfg.getString("Messages.accountMissing"));
    }

    public static String getCannotReceive(){
        return getPrefix() + colorize(cfg.getString("Messages.cannotReceiveMoney"));
    }

    private static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}

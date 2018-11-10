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

import java.util.ArrayList;
import java.util.List;

public class F {

    private static GemsEconomy plugin = GemsEconomy.getInstance();
    private static FileConfiguration cfg = plugin.getConfig();

    private static String get(String path){
        return colorize(cfg.getString(path));
    }

    private static List<String> getList(String path){
        List<String> str = new ArrayList<>();
        for(String s : cfg.getStringList(path)){
            str.add(colorize(s));
        }
        return str;
    }

    private static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String getPrefix() {
        return colorize(cfg.getString("Messages.prefix"));
    }

    public static String getNoPerms() {
        return getPrefix() + colorize(cfg.getString("Messages.nopermission"));
    }

    public static String getNoConsole() {
        return getPrefix() + colorize(cfg.getString("Messages.noconsole"));
    }

    public static String getInsufficientFunds() { return getPrefix() + colorize(cfg.getString("Messages.insufficientFunds")); }

    public static String getTargetInsufficientFunds() { return getPrefix() + colorize(cfg.getString("Messages.targetInsufficientFunds")); }

    public static String getPayerMessage() {
        return getPrefix() + colorize(cfg.getString("Messages.payer"));
    }

    public static String getPaidMessage() {
        return getPrefix() + colorize(cfg.getString("Messages.paid"));
    }

    public static String getPayUsage() {
        return colorize(cfg.getString("Messages.usage.pay_command"));
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

    public static String getPlayerDoesNotExist() { return getPrefix() + colorize(cfg.getString("Messages.player_is_null")); }

    public static String getPayYourself() {
        return getPrefix() + colorize(cfg.getString("Messages.pay_yourself"));
    }

    public static String getUnknownCurrency() { return getPrefix() + colorize(cfg.getString("Messages.unknownCurrency")); }

    public static void getManageHelp(CommandSender sender) {
        for (String s : cfg.getStringList("Messages.help.eco_command")) {
            sender.sendMessage(colorize(s.replace("{prefix}", getPrefix())));
        }
    }

    public static void getExchangeHelp(CommandSender sender) {
        for (String s : cfg.getStringList("Messages.help.exchange_command")) {
            sender.sendMessage(colorize(s.replace("{prefix}", getPrefix())));
        }
    }

    public static String getBalance() {
        return getPrefix() + colorize(cfg.getString("Messages.balance.current"));
    }
    public static String getBalanceMultiple() { return getPrefix() + colorize(cfg.getString("Messages.balance.multiple")); }

    public static String getUnvalidAmount() {
        return getPrefix() + colorize(cfg.getString("Messages.invalidamount"));
    }
    public static String getUnvalidPage() {
        return getPrefix() + colorize(cfg.getString("Messages.invalidpage"));
    }

    public static void getChequeHelp(CommandSender sender) {
        for (String s : cfg.getStringList("Messages.help.cheque_command")) {
            sender.sendMessage(colorize(s.replace("{prefix}", getPrefix())));
        }
    }

    public static String getChequeSucess() {
        return getPrefix() + colorize(cfg.getString("Messages.cheque.success"));
    }

    public static String getChequeRedeemed() { return getPrefix() + colorize(cfg.getString("Messages.cheque.redeemed")); }

    public static String getChequeInvalid() {
        return getPrefix() + colorize(cfg.getString("Messages.cheque.invalid"));
    }

    public static String getGiveUsage(){
        return colorize(cfg.getString("Messages.usage.give_command"));
    }

    public static String getTakeUsage(){
        return colorize(cfg.getString("Messages.usage.take_command"));
    }

    public static String getSetUsage(){
        return colorize(cfg.getString("Messages.usage.set_command"));
    }

    public static String getBalanceTopHeader(){
        return colorize(cfg.getString("Messages.balance_top.header"));
    }

    public static String getBalanceTopEmpty(){
        return colorize(cfg.getString("Messages.balance_top.empty"));
    }

    public static String getBalanceTopNext(){
        return colorize(cfg.getString("Messages.balance_top.next"));
    }

    public static String getBalanceTop(){
        return colorize(cfg.getString("Messages.balance_top.balance"));
    }

    public static String getNoDefaultCurrency(){
        return getPrefix() + colorize(cfg.getString("Messages.noDefaultCurrency"));
    }

    public static String getBalanceNone(){
        return getPrefix() + colorize(cfg.getString("Messages.balance.none"));
    }

    public static String getBalanceTopNoSupport(){
        return getPrefix() + colorize(cfg.getString("Messages.balance_top.nosupport"));
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


    public static String getCurrencyUsage_Create() { return get("Messages.usage.currency_create"); }
    public static String getCurrencyUsage_Delete() { return get("Messages.usage.currency_delete"); }
    public static String getCurrencyUsage_View() { return get("Messages.usage.currency_view"); }
    public static String getCurrencyUsage_Default() { return get("Messages.usage.currency_default"); }
    public static String getCurrencyUsage_List() { return get("Messages.usage.currency_list"); }
    public static String getCurrencyUsage_Color() { return get("Messages.usage.currency_color"); }
    public static String getCurrencyUsage_Colorlist() { return get("Messages.usage.currency_colorlist"); }
    public static String getCurrencyUsage_Payable() { return get("Messages.usage.currency_payable"); }
    public static String getCurrencyUsage_Startbal() { return get("Messages.usage.currency_startbal"); }
    public static String getCurrencyUsage_Decimals() { return get("Messages.usage.currency_decimals"); }
    public static String getCurrencyUsage_Symbol() { return get("Messages.usage.currency_symbol"); }
    public static String getCurrencyUsage_Rate() { return get("Messages.usage.currency_setrate"); }

    public static void sendCurrencyUsage(CommandSender sender){
        for(String s : getList("Messages.help.currency_command")){
            sender.sendMessage(s.replace("{prefix}", getPrefix()));
        }
    }

    public static String getExchangeSuccess(){
        return getPrefix() + colorize(cfg.getString("Messages.exchange_success"));
    }

    public static String getExchangeRateSet(){
        return getPrefix() + colorize(cfg.getString("Messages.exchange_rate_set"));
    }
}

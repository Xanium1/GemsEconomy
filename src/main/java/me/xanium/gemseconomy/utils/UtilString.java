/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.utils;

import com.google.common.collect.Lists;
import me.xanium.gemseconomy.file.F;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class UtilString {

    public static boolean validateInput(CommandSender sender, String input) {
        double amount;
        try {
            amount = Double.parseDouble(input);
            if (amount < 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException ex) {
            sender.sendMessage(F.getPrefix() + F.getUnvalidAmount());
            return false;
        }
        return true;
    }

    public static String format(double value) {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);

        int max = 2;
        int min = 0;

        nf.setMaximumFractionDigits(max);
        nf.setMinimumFractionDigits(min);
        return nf.format(value);
    }

    public static String colorize(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> colorize(List<String> message){
        List<String> colorizedList = Lists.newArrayList();
        for(String str : message){
            colorizedList.add(colorize(str));
        }
        return colorizedList;
    }

}

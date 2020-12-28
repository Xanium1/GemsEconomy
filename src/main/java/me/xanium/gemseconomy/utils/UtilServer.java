/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.utils;

import me.xanium.gemseconomy.GemsEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;

public class UtilServer {

    private static Server getServer(){
        return Bukkit.getServer();
    }
    private static final String Console_Prefix = "§2[GemsEconomy] §f";
    private static final String Error_Prefix = "§c[G-Eco-Error] §f";

    public static void consoleLog(String message){
        if(GemsEconomy.getInstance().isDebug()) getServer().getConsoleSender().sendMessage(Console_Prefix + colorize(message));
    }

    public static void consoleLog(Throwable message){
        getServer().getConsoleSender().sendMessage(Error_Prefix + message);
    }

    private static String colorize(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}

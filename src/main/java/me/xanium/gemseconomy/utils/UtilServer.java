/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;

public class UtilServer {

    private static Server getServer(){
        return Bukkit.getServer();
    }
    private static final long MB = 1048576L;
    private static final String Console_Prefix = "§2[GemsEco] §f";

    public static void consoleLog(String message){
        getServer().getConsoleSender().sendMessage(Console_Prefix + colorize(message));
    }

    private static String colorize(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}

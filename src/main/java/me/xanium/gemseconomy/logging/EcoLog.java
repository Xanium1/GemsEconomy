/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.logging;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.utils.UtilString;
import org.bukkit.Location;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EcoLog {

    private static FileWriter fw = null;

    // Date = dato, Action = Eco Handling
    public static void log(String action, String sender, String amount, String receiver, Location location, GemsEconomy eco){
        if(location == null){
            return;
        }

        if(fw == null){
            try{
                fw = new FileWriter(new File(eco.getDataFolder(), "transactions.log"), true);
            }catch(IOException ex){
                Logger.getLogger("GemsEconomy").log(Level.SEVERE, null, ex);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(action).append(",\"");
        sb.append(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date()));
        sb.append("\", \"");
        if(sender != null){
            sb.append(sender);
        }
        sb.append("\",");
        if (amount != null) {
            sb.append(UtilString.format(Double.valueOf(amount)));
        }
        sb.append(",\"");
        if (receiver != null) {
            sb.append(receiver);
        }
        if (location == null) {
            sb.append(",\"\",\"\",\"\",\"\"");
        } else {
            sb.append(",\"");
            sb.append(location.getWorld().getName()).append("\",");
            sb.append(location.getBlockX()).append(",");
            sb.append(location.getBlockY()).append(",");
            sb.append(location.getBlockZ()).append(",");
        }
        sb.append("\n");
        try {
            fw.write(sb.toString());
            fw.flush();
        } catch (IOException ex) {
            Logger.getLogger("GemsEconomy").log(Level.SEVERE, null, ex);
        }
    }

    public static void closeLog() {
        if (fw != null) {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger("GemsEconomy").log(Level.SEVERE, null, ex);
            }
            fw = null;
        }
    }

}

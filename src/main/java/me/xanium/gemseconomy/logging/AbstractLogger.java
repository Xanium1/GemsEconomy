/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.logging;

import com.google.common.collect.Sets;
import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.utils.UtilTime;

import java.io.*;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class AbstractLogger {

    private final GemsEconomy plugin;
    private final File folder;
    private final File latest;
    private final Set<String> toAdd;
    private volatile boolean zipping;

    public AbstractLogger(GemsEconomy plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder() + File.separator + "logs");
        this.latest = new File(folder, "LATEST.log");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (!latest.exists()) {
            try {
                latest.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        this.toAdd = Sets.newHashSet();
        this.zipping = false;
    }

    public void save() {
        zipAndReplace();
    }

    public File getLatest() {
        return latest;
    }

    public File getFolder() {
        return folder;
    }

    public void zipAndReplace() {
        zipping = true;

       plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String date = UtilTime.date();
                date = date.replace("/", "-");
                File zFile = new File(folder, date + ".zip");
                int link = 1;
                while (zFile.exists()) {
                    zFile = new File(folder, (date + '[') + link + (']' + ".zip"));
                    link++;
                }
                FileOutputStream fos = new FileOutputStream(zFile);
                ZipOutputStream zipOut = new ZipOutputStream(fos);
                File fileToZip = latest;
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(date + ".log");
                zipOut.putNextEntry(zipEntry);
                final byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                zipOut.close();
                fis.close();
                fos.close();
                latest.delete();
                if (!plugin.isDisabling()) {
                    latest.createNewFile();
                    PrintWriter writer = new PrintWriter(new FileWriter(latest, true));
                    toAdd.forEach(writer::println);
                    toAdd.clear();
                    writer.close();
                }
                zipping = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void log(String message) {
        try {
            StringBuilder builder = new StringBuilder();
            appendDate(builder);
            builder.append('[').append("ECONOMY-LOG").append(']').append(' ');
            builder.append(message);
            writeToFile(builder.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void warn(String message) {
        try {
            StringBuilder builder = new StringBuilder();
            appendDate(builder);
            builder.append('[').append("WARNING").append(']').append(' ');
            builder.append(message);
            writeToFile(builder.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void error(String message, Exception ex) {
        try {
            StringBuilder builder = new StringBuilder();
            appendDate(builder);
            StackTraceElement element = ex.getStackTrace()[0];
            builder.append('[').append(ex.toString()).append(']').append(' ');
            builder.append('[').append("ERROR - ").append(ex.getMessage()).append(" -- ").append(element.getFileName())
                    .append(" where ").append(element.getMethodName()).append(" at ").append(element.getLineNumber())
                    .append(']').append(' ');
            builder.append(message);
            writeToFile(builder.toString());
        } catch (IOException e) {
            ex.printStackTrace();
        }
    }

    private final void appendDate(StringBuilder builder) {
        builder.append('[').append(getDateAndTime()).append(']').append(' ');
    }

    private final void writeToFile(String string) throws IOException {
        if (zipping) {
            toAdd.add(string);
            return;
        }
        PrintWriter writer = new PrintWriter(new FileWriter(latest, true));
        writer.println(string);
        writer.close();
    }

    public String getDateAndTime() {
        return UtilTime.now();
    }
}

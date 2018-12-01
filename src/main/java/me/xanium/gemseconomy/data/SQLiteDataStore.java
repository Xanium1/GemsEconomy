/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.data;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.utils.UtilServer;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLiteDataStore extends SQLDataStore {

    private File file;

    public SQLiteDataStore(File file) {
        super("SQLite", true);
        this.file = file;
    }

    @Override
    protected Connection openConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception ignored) { // getConnection will provide the error we actually want.
        }
        return DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
    }

    @Override
    protected void setupTables() throws SQLException {
        try (PreparedStatement stmt = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + this.getTablePrefix() + "_currencies(    id INTEGER PRIMARY KEY AUTOINCREMENT,    uuid VARCHAR(255),    name_singular VARCHAR(255),    name_plural VARCHAR(255),    default_balance DECIMAL,    symbol VARCHAR(10),    decimals_supported INT,    is_default INT,    payable INT,    color VARCHAR(255),    exchange_rate DECIMAL);")) {
            stmt.execute();
        }

        try (PreparedStatement stmt = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + this.getTablePrefix() + "_accounts(    id INTEGER PRIMARY KEY AUTOINCREMENT,    nickname VARCHAR(255),    uuid VARCHAR(255),    payable INT);")) {
            stmt.execute();
        }

        try (PreparedStatement stmt = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + this.getTablePrefix() + "_balances(    account_id VARCHAR(255),    currency_id VARCHAR(255),    balance DECIMAL);")) {
            stmt.execute();
        }
    }

    @Override
    public void initialize() {
        super.initialize();

        int mins = GemsEconomy.getInstance().getConfig().getInt("storage.sqlite.sync-minutes", 0);
        if (mins > 0) {
            long secs = mins * 60;
            secs = secs * 20;
            new BukkitRunnable() {
                @Override
                public void run() {
                    UtilServer.consoleLog("Resyncing data...");

                }
            }.runTaskTimerAsynchronously(GemsEconomy.getInstance(), secs, secs);
        }

    }
}

/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy;

import me.xanium.gemseconomy.commands.*;
import me.xanium.gemseconomy.data.DataStore;
import me.xanium.gemseconomy.data.MySQLStorage;
import me.xanium.gemseconomy.data.YamlStorage;
import me.xanium.gemseconomy.economy.Cheque;
import me.xanium.gemseconomy.file.MainConfiguration;
import me.xanium.gemseconomy.listeners.EconomyListener;
import me.xanium.gemseconomy.logging.EconomyLogger;
import me.xanium.gemseconomy.migration.MigrationListener;
import me.xanium.gemseconomy.nbt.NMSVersion;
import me.xanium.gemseconomy.utils.Metrics;
import me.xanium.gemseconomy.utils.Updater;
import me.xanium.gemseconomy.utils.UtilServer;
import me.xanium.gemseconomy.vault.VaultHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class GemsEconomy extends JavaPlugin {

    private static DataStore dataStore = null;
    private static GemsEconomy instance;
    private VaultHandler vaultHandler;
    private NMSVersion nmsVersion;
    private Metrics metrics;

    private boolean debug = false;
    private boolean vault = false;
    private boolean logging = true;


    /**
     * Todo Liste:
     * The exchange commands and maths
     *
     */

    //BUG: Not transferring balances while converting.

    @Override
    public void onLoad(){
        MainConfiguration mainConfiguration = new MainConfiguration(this);
        mainConfiguration.loadDefaultConfig();

        setDebug(getConfig().getBoolean("debug"));
        setVault(getConfig().getBoolean("vault"));
        setLogging(getConfig().getBoolean("transaction_log"));
    }

    @Override
    public void onEnable(){
        instance = this;

        nmsVersion = new NMSVersion();
        metrics = new Metrics(this);

        initializeDataStore(getConfig().getString("storage"), true);

        Cheque.setChequeBase();

        getServer().getPluginManager().registerEvents(new EconomyListener(), this);
        getServer().getPluginManager().registerEvents(new MigrationListener(), this);
        getCommand("gbalance").setExecutor(new BalanceCommand());
        getCommand("gbaltop").setExecutor(new BalanceTopCommand());
        getCommand("geco").setExecutor(new EconomyCommand());
        getCommand("gpay").setExecutor(new PayCommand());
        getCommand("gcurrencies").setExecutor(new CurrencyCommand());
        getCommand("cheque").setExecutor(new ChequeCommand());
        //getCommand("gexchange").setExecutor(new ExchangeCommand());

        if(isVault()){
            vaultHandler = new VaultHandler(this);
            vaultHandler.hook();
            UtilServer.consoleLog("Vault compatibility enabled.");
        }else{
            UtilServer.consoleLog("Vault compatibility is disabled.");
        }

        doAsync(() -> checkForUpdate());
    }

    @Override
    public void onDisable() {

        if(isVault()) getVaultHandler().unhook();
        if(isLogging()) EconomyLogger.closeLog();

        if (GemsEconomy.getDataStore() != null) {
            GemsEconomy.getDataStore().close();
        }
    }

    public void initializeDataStore(String strategy, boolean load) {

        if (strategy.equalsIgnoreCase("yaml")) {
            dataStore = new YamlStorage("YAML", false, new File(getDataFolder(), "data.yml"));
            UtilServer.consoleLog("YAML Storage selected.");
        }
        else if (strategy.equalsIgnoreCase("mysql")) {
            String host = getConfig().getString("mysql.host");
            int port = getConfig().getInt("mysql.port");
            String user = getConfig().getString("mysql.username");
            String pass = getConfig().getString("mysql.password");
            String prefix = getConfig().getString("mysql.tableprefix");
            String db = getConfig().getString("mysql.database");
            dataStore = new MySQLStorage("MySQL", true, host, port, user, pass, db, prefix);
            UtilServer.consoleLog("MySQL Storage selected.");
        }
        else {
            UtilServer.consoleLog("You have not specified the correct data storing method. Check your config.yml!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getDataStore().initialize();

        if (dataStore instanceof MySQLStorage) {
            if (((MySQLStorage) dataStore).getConnection() == null) {
                UtilServer.consoleLog("MySQL Database login credentials are wrong. Please check them.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }
        if(load)getDataStore().loadCurrencies();
    }

    private void checkForUpdate() {
        Updater updater = new Updater(this);
        try {
            if (updater.checkForUpdates()) {
                UtilServer.consoleLog("--------------------------------");
                UtilServer.consoleLog("New Version: " + updater.getNewVersion());
                UtilServer.consoleLog("Current Version: " + updater.getCurrentVersion());
                UtilServer.consoleLog("Download link: " + updater.getResourceURL());
                UtilServer.consoleLog("--------------------------------");
            }
        } catch (IOException e) {
            UtilServer.consoleLog("Could not check for updates! Error log will follow if debug is enabled.");
            if(isDebug()) {
                UtilServer.consoleLog(e.getCause());
            }
        }
    }

    public static void doAsync(Runnable runnable){
        getInstance().getServer().getScheduler().runTaskAsynchronously(getInstance(), runnable);
    }

    public static DataStore getDataStore() {
        return dataStore;
    }

    public static GemsEconomy getInstance() {
        return instance;
    }

    public boolean isDebug() {
        return debug;
    }

    private void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isVault() {
        return vault;
    }

    private void setVault(boolean vault) {
        this.vault = vault;
    }

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public VaultHandler getVaultHandler() {
        return vaultHandler;
    }

    public NMSVersion getNmsVersion() {
        return nmsVersion;
    }

    public Metrics getMetrics() {
        return metrics;
    }
}

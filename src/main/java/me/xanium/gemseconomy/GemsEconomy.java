/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy;

import me.xanium.gemseconomy.account.AccountManager;
import me.xanium.gemseconomy.bungee.UpdateForwarder;
import me.xanium.gemseconomy.cheque.ChequeManager;
import me.xanium.gemseconomy.commands.*;
import me.xanium.gemseconomy.currency.CurrencyManager;
import me.xanium.gemseconomy.data.DataStorage;
import me.xanium.gemseconomy.data.MySQLStorage;
import me.xanium.gemseconomy.data.YamlStorage;
import me.xanium.gemseconomy.file.Configuration;
import me.xanium.gemseconomy.listeners.EconomyListener;
import me.xanium.gemseconomy.logging.EconomyLogger;
import me.xanium.gemseconomy.nbt.NMSVersion;
import me.xanium.gemseconomy.utils.Metrics;
import me.xanium.gemseconomy.utils.SchedulerUtils;
import me.xanium.gemseconomy.utils.Updater;
import me.xanium.gemseconomy.utils.UtilServer;
import me.xanium.gemseconomy.vault.VaultHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class GemsEconomy extends JavaPlugin {

    private static GemsEconomy instance;

    private DataStorage dataStorage = null;
    private AccountManager accountManager;
    private ChequeManager chequeManager;
    private CurrencyManager currencyManager;
    private VaultHandler vaultHandler;
    private NMSVersion nmsVersion;
    private Metrics metrics;
    private EconomyLogger economyLogger;
    private UpdateForwarder updateForwarder;

    private boolean debug = false;
    private boolean vault = false;
    private boolean logging = false;
    private boolean cheques = true;

    private boolean disabling = false;

    /**
     * Bug fix Update
     *
     * MySQL would not write or read any data from database - Fixed (Some help from @FurryKitten @ github)
     * Rewritten many parts of loading / saving account data in MySQL.
     * YAML Storage would cache offline users when adding currency to them - Fixed
     * Balance Top command has been rewritten to support more efficient SQL queries.
     * Balance Top cache expiry lowered to 3 minutes from 5.
     * Added an option to enable/disable cheques in config.
     * There has also been many internal changes here and there.
     *
     * SQLITE Support has been dropped! IF this is relevant for you!
     * Please change your backend to YAML with the command /currency convert yaml
     *
     * THIS UPDATE MODIFIES HOW A PLAYERS BALANCE IS SAVED, ONLY RELEVANT FOR MYSQL USERS!
     * Please take a backup of your balances & accounts table before you start your server
     * with this new version of GemsEconomy!
     * The plugin will automatically alter the old table and add the new column.
     * When players log in their data will be converted to the new format.
     * IF you are using mysql, and utilize /baltop command a lot, the baltop might become
     * inaccurate due to the players need to log on your server before it can read their balances.
     *
     * Please let me know if you find bugs!
     * PM me here @ SpigotMC
     *
     */

    @Override
    public void onLoad() {
        Configuration configuration = new Configuration(this);
        configuration.loadDefaultConfig();

        setDebug(getConfig().getBoolean("debug"));
        setVault(getConfig().getBoolean("vault"));
        setLogging(getConfig().getBoolean("transaction_log"));
        setCheques(getConfig().getBoolean("cheque.enabled"));
    }

    @Override
    public void onEnable() {
        instance = this;

        nmsVersion = new NMSVersion();
        accountManager = new AccountManager(this);
        currencyManager = new CurrencyManager(this);
        economyLogger = new EconomyLogger(this);
        metrics = new Metrics(this);
        updateForwarder = new UpdateForwarder(this);

        initializeDataStore(getConfig().getString("storage"), true);

        getServer().getPluginManager().registerEvents(new EconomyListener(), this);
        getCommand("balance").setExecutor(new BalanceCommand());
        getCommand("baltop").setExecutor(new BalanceTopCommand());
        getCommand("economy").setExecutor(new EconomyCommand());
        getCommand("pay").setExecutor(new PayCommand());
        getCommand("currency").setExecutor(new CurrencyCommand());
        getCommand("cheque").setExecutor(new ChequeCommand());
        getCommand("exchange").setExecutor(new ExchangeCommand());

        if (isVault()) {
            vaultHandler = new VaultHandler(this);
            vaultHandler.hook();
        } else {
            UtilServer.consoleLog("Vault link is disabled.");
        }

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", updateForwarder);

        if (isLogging()) {
            getEconomyLogger().save();
        }

        if(isChequesEnabled()){
            chequeManager = new ChequeManager(this);
        }

        SchedulerUtils.runAsync(this::checkForUpdate);
    }

    @Override
    public void onDisable() {
        disabling = true;

        if (isVault()) getVaultHandler().unhook();

        if (getDataStore() != null) {
            getDataStore().close();
        }
    }

    public void initializeDataStore(String strategy, boolean load) {

        DataStorage.getMethods().add(new YamlStorage(new File(getDataFolder(), "data.yml")));
        DataStorage.getMethods().add(new MySQLStorage(getConfig().getString("mysql.host"), getConfig().getInt("mysql.port"), getConfig().getString("mysql.database"), getConfig().getString("mysql.username"), getConfig().getString("mysql.password")));

        // Disabled. Not many are using SQLite anyway. And MySQL has much better performance!
        //DataStorage.getMethods().add(new SQLiteStorage(new File(getDataFolder(), getConfig().getString("sqlite.file"))));

        if (strategy != null) {
            dataStorage = DataStorage.getMethod(strategy);
        } else {
            UtilServer.consoleLog("§cNo valid storage method provided.");
            UtilServer.consoleLog("§cCheck your files, then try again.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            UtilServer.consoleLog("Initializing data store \"" + getDataStore().getName() + "\"...");
            getDataStore().initialize();

            if (load) {
                UtilServer.consoleLog("Loading currencies...");
                getDataStore().loadCurrencies();
                UtilServer.consoleLog("Loaded " + getCurrencyManager().getCurrencies().size() + " currencies!");
            }
        } catch (Throwable e) {
            UtilServer.consoleLog("§cCannot load initial data from DataStore.");
            UtilServer.consoleLog("§cCheck your files, then try again.");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void checkForUpdate() {
        Updater updater = new Updater(this);
        try {
            if (updater.checkForUpdates()) {
                UtilServer.consoleLog("-------------------------------------------");
                UtilServer.consoleLog("New Version: " + updater.getNewVersion());
                UtilServer.consoleLog("Current Version: " + updater.getCurrentVersion());
                UtilServer.consoleLog("Download link: " + updater.getResourceURL());
                UtilServer.consoleLog("--------------------------------------------");
            }
        } catch (IOException e) {
            UtilServer.consoleLog("Could not check for updates! Error log will follow if debug is enabled.");
            if (isDebug()) {
                UtilServer.consoleLog(e.getCause());
            }
        }
    }

    public DataStorage getDataStore() {
        return dataStorage;
    }

    public static GemsEconomy getInstance() {
        return instance;
    }

    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public VaultHandler getVaultHandler() {
        return vaultHandler;
    }

    public EconomyLogger getEconomyLogger() {
        return economyLogger;
    }

    public NMSVersion getNmsVersion() {
        return nmsVersion;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public ChequeManager getChequeManager() {
        return chequeManager;
    }

    public UpdateForwarder getUpdateForwarder() {
        return updateForwarder;
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

    public boolean isDisabling() {
        return disabling;
    }

    public boolean isChequesEnabled() {
        return cheques;
    }

    public void setCheques(boolean cheques) {
        this.cheques = cheques;
    }
}

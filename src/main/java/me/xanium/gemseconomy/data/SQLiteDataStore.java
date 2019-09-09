/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.data;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.account.Account;
import me.xanium.gemseconomy.currency.Currency;
import me.xanium.gemseconomy.utils.UtilServer;
import org.bukkit.ChatColor;

import java.io.File;
import java.sql.*;
import java.util.*;

public class SQLiteDataStore extends DataStore {

    private Connection connection;
    private Map<UUID, CachedTopList> cachedTopList;
    private File file;

    public SQLiteDataStore(File file) {
        super("SQLite", true);
        this.file = file;
    }

    protected Connection openConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception ignored) { // getConnection will provide the error we actually want.
        }
        return DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
    }

    private void setupTables() throws SQLException {
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

    private String getTablePrefix() {
        return GemsEconomy.getInstance().getConfig().getString("mysql.tableprefix");
    }

    private Connection getConnection() {
        return this.connection;
    }

    private final String SAVE_CURRENCY = "INSERT INTO `" + getTablePrefix() + "_currencies` (`uuid`, `name_singular`, `name_plural`, `default_balance`, `symbol`, `decimals_supported`, `is_default`, `payable`, `color`, `exchange_rate`) VALUES(?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE `uuid` = ?, `name_singular` = ?, `name_plural` = ?, `default_balance` = ?, `symbol` = ?, `decimals_supported` = ?, `ìs_default` = ?, ``payable` = ?, `color` = ?, `èxchange_rate` = ?";
    private final String SAVE_ACCOUNT = "INSERT INTO `" + getTablePrefix() + "_accounts` (`nickname`, `uuid`, `payable`) VALUES(?,?,?) ON DUPLICATE KEY UPDATE `nickname` = ?, `uuid` = ?, `payable` = ?";
    private final String SAVE_BALANCE = "INSERT INTO `" + getTablePrefix() + "_balances` (`account_id`, `currency_id`, `balance`) VALUES(?,?,?) ON DUPLICATE KEY UPDATE `account_id` = ?, `currency_id` = ?, `balance` = ?";

    private boolean isConnected() {
        if (connection == null) {
            return false;
        }
        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    private void checkConnection() {
        if (!isConnected()) {
            UtilServer.consoleLog("Connection lost to database. Trying to reconnect...");
            reviveConnection();
        }
    }

    @Override
    public void initialize() {
        UtilServer.consoleLog("Establishing " + this.getName() + " database connection...");
        this.cachedTopList = new HashMap<>();
        try {
            this.connection = this.openConnection();

            UtilServer.consoleLog("Connection successful! Checking tables...");
            this.setupTables();

            UtilServer.consoleLog(getName() + " startup complete.");

        } catch (SQLException e) {
            this.close();
            throw new RuntimeException(e);
        }
    }

    public void reviveConnection() {
        try {
            if (this.getConnection().isClosed() || !this.getConnection().isValid(3)) {
                this.initialize();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            if (getConnection() != null && !getConnection().isClosed()) {
                getConnection().close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to properly close SQL connection", e);
        } finally {
            this.connection = null;
        }
    }

    @Override
    public void loadCurrencies() {
        if (this.getConnection() == null) {
            return;
        }
        this.reviveConnection();
        try {
            PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_currencies");
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                UUID uuid = UUID.fromString(set.getString("uuid"));
                String singular = set.getString("name_singular");
                String plural = set.getString("name_plural");
                double defaultBalance = set.getDouble("default_balance");
                String symbol = set.getString("symbol");
                boolean decimals = set.getInt("decimals_supported") == 1;
                boolean isDefault = set.getInt("is_default") == 1;
                boolean payable = set.getInt("payable") == 1;
                ChatColor color = ChatColor.valueOf(set.getString("color"));
                double exchangeRate = set.getDouble("exchange_rate");
                Currency currency = new Currency(uuid, singular, plural);
                currency.setDefaultBalance(defaultBalance);
                currency.setSymbol(symbol);
                currency.setDecimalSupported(decimals);
                currency.setDefaultCurrency(isDefault);
                currency.setPayable(payable);
                currency.setColor(color);
                currency.setExchangeRate(exchangeRate);
                plugin.getCurrencyManager().add(currency);
                UtilServer.consoleLog("Loaded currency: " + currency.getSingular());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCurrencyLocally(Currency currency) {
        throw new UnsupportedOperationException("This method is not supported in SQLite.");
    }

    @Override
    public void saveCurrency(Currency currency) {
        checkConnection();
        try {
            PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_currencies WHERE uuid = ? LIMIT 1;");
            stmt.setString(1, currency.getUuid().toString());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                stmt = this.getConnection().prepareStatement("INSERT INTO " + this.getTablePrefix() + "_currencies (uuid, name_singular, name_plural, default_balance, symbol, decimals_supported, is_default, payable, color, exchange_rate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                stmt.setString(1, currency.getUuid().toString());
                stmt.setString(2, currency.getSingular());
                stmt.setString(3, currency.getPlural());
                stmt.setDouble(4, currency.getDefaultBalance());
                stmt.setString(5, currency.getSymbol());
                stmt.setInt(6, currency.isDecimalSupported() ? 1 : 0);
                stmt.setInt(7, currency.isDefaultCurrency() ? 1 : 0);
                stmt.setInt(8, currency.isPayable() ? 1 : 0);
                stmt.setString(9, currency.getColor().name());
                stmt.setDouble(10, currency.getExchangeRate());
                stmt.execute();
            } else {
                stmt = this.getConnection().prepareStatement("UPDATE " + this.getTablePrefix() + "_currencies SET default_balance = ?, symbol = ?, decimals_supported = ?, is_default = ?, payable = ?, color = ?, exchange_rate = ? WHERE uuid = ?");
                stmt.setDouble(1, currency.getDefaultBalance());
                stmt.setString(2, currency.getSymbol());
                stmt.setInt(3, currency.isDecimalSupported() ? 1 : 0);
                stmt.setInt(4, currency.isDefaultCurrency() ? 1 : 0);
                stmt.setInt(5, currency.isPayable() ? 1 : 0);
                stmt.setString(6, currency.getColor().name());
                stmt.setDouble(7, currency.getExchangeRate());
                stmt.setString(8, currency.getUuid().toString());
                stmt.execute();
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        plugin.getUpdateForwarder().sendUpdateMessage("currency", currency.getUuid().toString());
    }

    @Override
    public void deleteCurrency(Currency currency) {
        checkConnection();

        try {
            PreparedStatement stmt = this.getConnection().prepareStatement("DELETE FROM " + this.getTablePrefix() + "_currencies WHERE uuid = ?");
            stmt.setString(1, currency.getUuid().toString());
            stmt.execute();
            stmt = this.getConnection().prepareStatement("DELETE FROM " + this.getTablePrefix() + "_balances WHERE currency_id = ?");
            stmt.setString(1, currency.getUuid().toString());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LinkedHashMap<String, Double> getTopList(Currency currency, int offset, int amount) {
        if (this.cachedTopList.containsKey(currency.getUuid())) {
            CachedTopList ctl = this.cachedTopList.get(currency.getUuid());
            if (ctl.matches(currency, offset, amount) && !ctl.isExpired()) {
                return ctl.getResults();
            }
        }
        checkConnection();

        LinkedHashMap<String, Double> resultPair = new LinkedHashMap<>();
        try {
            LinkedHashMap<String, Double> idBalancePair = new LinkedHashMap<>();
            PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_balances WHERE currency_id = ? ORDER BY balance DESC LIMIT " + offset + ", " + amount);
            stmt.setString(1, currency.getUuid().toString());
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                idBalancePair.put(set.getString("account_id"), set.getDouble("balance"));
            }
            set.close();
            if (idBalancePair.size() > 0) {
                for (String id : idBalancePair.keySet()) {
                    stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_accounts WHERE uuid = ? LIMIT 1");
                    stmt.setString(1, id);
                    set = stmt.executeQuery();
                    if (set.next()) {
                        resultPair.put(set.getString("nickname"), idBalancePair.get(id));
                    }
                    set.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        CachedTopList ctl2 = new CachedTopList(currency, amount, offset, System.currentTimeMillis());
        ctl2.setResults(resultPair);
        this.cachedTopList.put(currency.getUuid(), ctl2);
        return resultPair;
    }

    private Account returnAccountWithBalances(Account account) {
        if (account == null) {
            return null;
        }
        try {
            PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_balances WHERE account_id = ?");
            stmt.setString(1, account.getUuid().toString());
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                Currency currency = plugin.getCurrencyManager().getCurrency(UUID.fromString(set.getString("currency_id")));
                if (currency == null) {
                    continue;
                }
                account.modifyBalance(currency, set.getDouble("balance"), false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }

    @Override
    public Account loadAccount(String name) {
        Account account = null;
        checkConnection();

        if (this.getConnection() != null) {
            try {
                PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_accounts WHERE nickname = ? LIMIT 1");
                stmt.setString(1, name);
                ResultSet set = stmt.executeQuery();
                if (set.next()) {
                    account = new Account(UUID.fromString(set.getString("uuid")), set.getString("nickname"));
                    account.setCanReceiveCurrency(set.getInt("payable") == 1);
                }
                set.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this.returnAccountWithBalances(account);
    }

    @Override
    public Account loadAccount(UUID uuid) {
        Account account = null;
        checkConnection();

        if (this.getConnection() != null) {
            try {
                PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_accounts WHERE uuid = ? LIMIT 1");
                stmt.setString(1, uuid.toString());
                ResultSet set = stmt.executeQuery();
                if (set.next()) {
                    account = new Account(uuid, set.getString("nickname"));
                    account.setCanReceiveCurrency(set.getInt("payable") == 1);
                }
                set.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this.returnAccountWithBalances(account);
    }

    @Override
    public ArrayList<Account> getOfflineAccounts() {
        ArrayList<Account> accounts = new ArrayList<>();
        checkConnection();
        if (this.getConnection() != null) {
            try {
                PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_accounts;");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    accounts.add(returnAccountWithBalances(loadAccount(UUID.fromString(rs.getString("uuid")))));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return accounts;
    }

    @Override
    public void createAccount(Account account) {
        checkConnection();
        try {
            PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_accounts WHERE uuid = ? LIMIT 1");
            stmt.setString(1, account.getUuid().toString());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                stmt = this.getConnection().prepareStatement("INSERT INTO " + this.getTablePrefix() + "_accounts (nickname, uuid, payable) VALUES (?, ?, ?)");
                stmt.setString(1, account.getDisplayName());
                stmt.setString(2, account.getUuid().toString());
                stmt.setInt(3, account.canReceiveCurrency() ? 1 : 0);
                stmt.execute();
            }
            rs.close();
            for (Currency currency : plugin.getCurrencyManager().getCurrencies()) {
                double balance = currency.getDefaultBalance();
                stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_balances WHERE account_id = ? AND currency_id = ? LIMIT 1");
                stmt.setString(1, account.getUuid().toString());
                stmt.setString(2, currency.getUuid().toString());
                rs = stmt.executeQuery();
                if (!rs.next()) {
                    stmt = this.getConnection().prepareStatement("INSERT INTO " + this.getTablePrefix() + "_balances (account_id, currency_id, balance) VALUES (?, ?, ?)");
                    stmt.setString(1, account.getUuid().toString());
                    stmt.setString(2, currency.getUuid().toString());
                    stmt.setDouble(3, balance);
                    stmt.execute();
                }
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        plugin.getUpdateForwarder().sendUpdateMessage("account", account.getUuid().toString());
    }

    @Override
    public void saveAccount(Account account) {
        checkConnection();
        try {
            PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_accounts WHERE uuid = ? LIMIT 1");
            stmt.setString(1, account.getUuid().toString());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                stmt = this.getConnection().prepareStatement("INSERT INTO " + this.getTablePrefix() + "_accounts (nickname, uuid, payable) VALUES (?, ?, ?)");
                stmt.setString(1, account.getDisplayName());
                stmt.setString(2, account.getUuid().toString());
                stmt.setInt(3, account.canReceiveCurrency() ? 1 : 0);
                stmt.execute();
            } else {
                stmt = this.getConnection().prepareStatement("UPDATE " + this.getTablePrefix() + "_accounts SET nickname = ?, payable = ? WHERE uuid = ?");
                stmt.setString(1, account.getDisplayName());
                stmt.setInt(2, account.canReceiveCurrency() ? 1 : 0);
                stmt.setString(3, account.getUuid().toString());
                stmt.execute();
            }
            rs.close();
            for (Currency currency : plugin.getCurrencyManager().getCurrencies()) {
                double balance = account.getBalance(currency.getPlural());
                if (balance == -100) {
                    balance = currency.getDefaultBalance();
                }

                stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_balances WHERE account_id = ? AND currency_id = ? LIMIT 1");
                stmt.setString(1, account.getUuid().toString());
                stmt.setString(2, currency.getUuid().toString());
                rs = stmt.executeQuery();

                if (!rs.next()) {
                    stmt = this.getConnection().prepareStatement("INSERT INTO " + this.getTablePrefix() + "_balances (account_id, currency_id, balance) VALUES (?, ?, ?)");
                    stmt.setString(1, account.getUuid().toString());
                    stmt.setString(2, currency.getUuid().toString());
                    stmt.setDouble(3, balance);
                    stmt.execute();
                } else {
                    stmt = this.getConnection().prepareStatement("UPDATE " + this.getTablePrefix() + "_balances SET balance = ? WHERE account_id = ? AND currency_id = ?");
                    stmt.setDouble(1, balance);
                    stmt.setString(2, account.getUuid().toString());
                    stmt.setString(3, currency.getUuid().toString());
                    stmt.execute();
                }
                rs.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        plugin.getUpdateForwarder().sendUpdateMessage("account", account.getUuid().toString());
    }

    @Override
    public void deleteAccount(Account account) {
        checkConnection();
        try {
            PreparedStatement stmt = this.getConnection().prepareStatement("DELETE FROM " + this.getTablePrefix() + "_accounts WHERE uuid = ? LIMIT 1");
            stmt.setString(1, account.getUuid().toString());
            stmt.execute();
            stmt = this.getConnection().prepareStatement("DELETE FROM " + this.getTablePrefix() + "_balances WHERE account_id = ?");
            stmt.setString(1, account.getUuid().toString());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

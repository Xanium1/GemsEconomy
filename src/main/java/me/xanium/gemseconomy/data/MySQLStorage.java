/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.xanium.gemseconomy.utils.UtilServer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLStorage extends SQLDataStore {

    private HikariConfig hikariConfig;
    private HikariDataSource hikari;

    public MySQLStorage(String host, int port, String database, String username, String password) {
        super("MySQL", true);
        hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://"+ host + ":" + port + "/" + database + "?allowPublicKeyRetrieval=true&useSSL=false");
        hikariConfig.setPassword(password);
        hikariConfig.setUsername(username);
        hikariConfig.setMaxLifetime(1500000);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("userServerPrepStmts", "true");
    }

    @Override
    protected Connection openConnection() throws SQLException {
        if(hikari == null)hikari = new HikariDataSource(hikariConfig);
        return hikari.getConnection();
    }

    @Override
    protected void setupTables() throws SQLException {
        try(PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + this.getTablePrefix() + "_currencies(    id INT PRIMARY KEY AUTO_INCREMENT,    uuid VARCHAR(255),    name_singular VARCHAR(255),    name_plural VARCHAR(255),    default_balance DECIMAL,    symbol VARCHAR(10),    decimals_supported INT,    is_default INT,    payable INT,    color VARCHAR(255),    exchange_rate DECIMAL);")){
            ps.execute();
        }
        try(PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + this.getTablePrefix() + "_accounts(    id INT PRIMARY KEY AUTO_INCREMENT,    nickname VARCHAR(255),    uuid VARCHAR(255),    payable INT);")){
            ps.execute();
        }
        try(PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + this.getTablePrefix() + "_balances(    account_id VARCHAR(255),    currency_id VARCHAR(255),    balance DECIMAL);")){
            ps.execute();
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        if (this.getConnection() != null) {
            try {
                PreparedStatement stmt;
                List<String> columns = new ArrayList<>();
                DatabaseMetaData metaData = getConnection().getMetaData();
                ResultSet tableResultSet = metaData.getTables(null, "public", null, new String[]{"TABLE"});
                try {
                    while (tableResultSet.next()) {
                        String tableName = tableResultSet.getString("TABLE_NAME");
                        ResultSet columnResultSet = metaData.getColumns(null, "public", tableName, null);
                        try {
                            while (columnResultSet.next()) {
                                String columnName = columnResultSet.getString("COLUMN_NAME");
                                columns.add(columnName);
                            }
                        } finally {
                            columnResultSet.close();
                        }
                    }
                } finally {
                    tableResultSet.close();
                }

                if(!columns.contains("exchange_rate")) {
                    stmt = this.getConnection().prepareStatement("ALTER TABLE " + this.getTablePrefix() + "_currencies ADD exchange_rate DECIMAL NULL DEFAULT NULL AFTER `color`;");
                    stmt.execute();

                    UtilServer.consoleLog("Altered Table gemsconomy_currencies to support the new variable.");
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        if (this.getConnection() != null) {
            try {
                this.getConnection().close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

   /* @Override
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
               // double exchangeRate = set.getDouble("exchange_rate");
                Currency currency = new Currency(uuid, singular, plural);
                currency.setDefaultBalance(defaultBalance);
                currency.setSymbol(symbol);
                currency.setDecimalSupported(decimals);
                currency.setDefaultCurrency(isDefault);
                currency.setPayable(payable);
                currency.setColor(color);
                //currency.setExchangeRate(exchangeRate);
                AccountManager.getCurrencies().add(currency);
                UtilServer.consoleLog("Loaded currency: " + currency.getSingular());
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveCurrency(Currency currency) {
        if (this.getConnection() == null) {
            return;
        }
        this.reviveConnection();
        try {
            PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_currencies WHERE uuid = ? LIMIT 1;");
            stmt.setString(1, currency.getUuid().toString());
            ResultSet rs = stmt.executeQuery();
            int resultCount = rs.last() ? rs.getRow() : 0;
            rs.close();
            if (resultCount == 0) {
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
            }
            else {
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
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCurrency(Currency currency) {
        if (this.getConnection() == null) {
            return;
        }
        this.reviveConnection();
        try {
            PreparedStatement stmt = this.getConnection().prepareStatement("DELETE FROM " + this.getTablePrefix() + "_currencies WHERE uuid = ?");
            stmt.setString(1, currency.getUuid().toString());
            stmt.execute();
            stmt = this.getConnection().prepareStatement("DELETE FROM " + this.getTablePrefix() + "_balances WHERE currency_id = ?");
            stmt.setString(1, currency.getUuid().toString());
            stmt.execute();
        }
        catch (SQLException e) {
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
        if (this.getConnection() == null) {
            return null;
        }
        this.reviveConnection();
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
        }
        catch (SQLException e) {
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
                Currency currency = AccountManager.getCurrency(UUID.fromString(set.getString("currency_id")));
                if (currency == null) {
                    continue;
                }
                account.setBalance(currency, set.getDouble("balance"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }

    @Override
    public Account loadAccount(String name) {
        Account account = null;
        if (this.getConnection() != null) {
            this.reviveConnection();
            try {
                PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_accounts WHERE nickname = ? LIMIT 1");
                stmt.setString(1, name);
                ResultSet set = stmt.executeQuery();
                if (set.next()) {
                    account = new Account(UUID.fromString(set.getString("uuid")), set.getString("nickname"));
                    account.setCanReceiveCurrency(set.getInt("payable") == 1);
                }
                set.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this.returnAccountWithBalances(account);
    }

    @Override
    public Account loadAccount(UUID uuid) {
        Account account = null;
        if (this.getConnection() != null) {
            this.reviveConnection();
            try {
                PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_accounts WHERE uuid = ? LIMIT 1");
                stmt.setString(1, uuid.toString());
                ResultSet set = stmt.executeQuery();
                if (set.next()) {
                    account = new Account(uuid, set.getString("nickname"));
                    account.setCanReceiveCurrency(set.getInt("payable") == 1);
                }
                set.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this.returnAccountWithBalances(account);
    }

    @Override
    public ArrayList<Account> getOfflineAccounts() {

        ArrayList<Account> accounts = new ArrayList<>();

        if(this.getConnection() != null){
            this.reviveConnection();
            try{
                PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_accounts;");
                ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    Account acc = returnAccountWithBalances(loadAccount(UUID.fromString(rs.getString("uuid"))));
                    accounts.add(acc);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }


        return accounts;
    }

    @Override
    public void createAccount(Account account) {
        if (this.getConnection() == null) {
            return;
        }
        this.reviveConnection();
        try {
            PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_accounts WHERE uuid = ? LIMIT 1");
            stmt.setString(1, account.getUuid().toString());
            ResultSet rs = stmt.executeQuery();
            int resultCount = rs.last() ? rs.getRow() : 0;
            rs.close();
            if (resultCount == 0) {
                stmt = this.getConnection().prepareStatement("INSERT INTO " + this.getTablePrefix() + "_accounts (nickname, uuid, payable) VALUES (?, ?, ?)");
                stmt.setString(1, account.getDisplayName());
                stmt.setString(2, account.getUuid().toString());
                stmt.setInt(3, account.isCanReceiveCurrency() ? 1 : 0);
                stmt.execute();
            }
            for (Currency currency : AccountManager.getCurrencies()) {
                double balance = currency.getDefaultBalance();
                stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_balances WHERE account_id = ? AND currency_id = ? LIMIT 1");
                stmt.setString(1, account.getUuid().toString());
                stmt.setString(2, currency.getUuid().toString());
                rs = stmt.executeQuery();
                resultCount = (rs.last() ? rs.getRow() : 0);
                rs.close();
                if (resultCount == 0) {
                    stmt = this.getConnection().prepareStatement("INSERT INTO " + this.getTablePrefix() + "_balances (account_id, currency_id, balance) VALUES (?, ?, ?)");
                    stmt.setString(1, account.getUuid().toString());
                    stmt.setString(2, currency.getUuid().toString());
                    stmt.setDouble(3, balance);
                    stmt.execute();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveAccount(Account account) {
        if (this.getConnection() == null) {
            return;
        }
        this.reviveConnection();
        try {
            PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_accounts WHERE uuid = ? LIMIT 1");
            stmt.setString(1, account.getUuid().toString());
            ResultSet rs = stmt.executeQuery();
            int resultCount = rs.last() ? rs.getRow() : 0;
            rs.close();
            if (resultCount == 0) {
                stmt = this.getConnection().prepareStatement("INSERT INTO " + this.getTablePrefix() + "_accounts (nickname, uuid, payable) VALUES (?, ?, ?)");
                stmt.setString(1, account.getDisplayName());
                stmt.setString(2, account.getUuid().toString());
                stmt.setInt(3, account.isCanReceiveCurrency() ? 1 : 0);
                stmt.execute();
            }
            else {
                stmt = this.getConnection().prepareStatement("UPDATE " + this.getTablePrefix() + "_accounts SET nickname = ?, payable = ? WHERE uuid = ?");
                stmt.setString(1, account.getDisplayName());
                stmt.setInt(2, account.isCanReceiveCurrency() ? 1 : 0);
                stmt.setString(3, account.getUuid().toString());
                stmt.execute();
            }
            for (Currency currency : AccountManager.getCurrencies()) {
                double balance = account.getBalance(currency.getSingular());
                if (balance != currency.getDefaultBalance()) {
                    stmt = this.getConnection().prepareStatement("SELECT * FROM " + this.getTablePrefix() + "_balances WHERE account_id = ? AND currency_id = ? LIMIT 1");
                    stmt.setString(1, account.getUuid().toString());
                    stmt.setString(2, currency.getUuid().toString());
                    rs = stmt.executeQuery();
                    resultCount = (rs.last() ? rs.getRow() : 0);
                    rs.close();
                    if (resultCount == 0) {
                        stmt = this.getConnection().prepareStatement("INSERT INTO " + this.getTablePrefix() + "_balances (account_id, currency_id, balance) VALUES (?, ?, ?)");
                        stmt.setString(1, account.getUuid().toString());
                        stmt.setString(2, currency.getUuid().toString());
                        stmt.setDouble(3, balance);
                        stmt.execute();
                    }
                    else {
                        stmt = this.getConnection().prepareStatement("UPDATE " + this.getTablePrefix() + "_balances SET balance = ? WHERE account_id = ? AND currency_id = ?");
                        stmt.setDouble(1, balance);
                        stmt.setString(2, account.getUuid().toString());
                        stmt.setString(3, currency.getUuid().toString());
                        stmt.execute();
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAccount(Account account) {
        if (this.getConnection() == null) {
            return;
        }
        this.reviveConnection();
        try {
            PreparedStatement stmt = this.getConnection().prepareStatement("DELETE FROM " + this.getTablePrefix() + "_accounts WHERE uuid = ? LIMIT 1");
            stmt.setString(1, account.getUuid().toString());
            stmt.execute();
            stmt = this.getConnection().prepareStatement("DELETE FROM " + this.getTablePrefix() + "_balances WHERE account_id = ?");
            stmt.setString(1, account.getUuid().toString());
            stmt.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
}

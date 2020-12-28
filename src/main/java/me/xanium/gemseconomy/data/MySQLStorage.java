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
import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.account.Account;
import me.xanium.gemseconomy.currency.CachedTopList;
import me.xanium.gemseconomy.currency.CachedTopListEntry;
import me.xanium.gemseconomy.currency.Currency;
import me.xanium.gemseconomy.utils.SchedulerUtils;
import me.xanium.gemseconomy.utils.UtilServer;
import org.bukkit.ChatColor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class MySQLStorage extends DataStorage {

    private String currencyTable = getTablePrefix() + "_currencies";
    private String accountsTable = getTablePrefix() + "_accounts";

    private final String SAVE_ACCOUNT = "INSERT INTO `" + getTablePrefix() + "_accounts` (`nickname`, `uuid`, `payable`, `balance_data`) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE `nickname` = VALUES(`nickname`), `uuid` = VALUES(`uuid`), `payable` = VALUES(`payable`), `balance_data` = VALUES(`balance_data`)";
    private final String SAVE_CURRENCY = "INSERT INTO `" + getTablePrefix() + "_currencies` (`uuid`, `name_singular`, `name_plural`, `default_balance`, `symbol`, `decimals_supported`, `is_default`, `payable`, `color`, `exchange_rate`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `uuid` = VALUES(`uuid`), `name_singular` = VALUES(`name_singular`), `name_plural` = VALUES(`name_plural`), `default_balance` = VALUES(`default_balance`), `symbol` = VALUES(`symbol`), `decimals_supported` = VALUES(`decimals_supported`), `is_default` = VALUES(`is_default`), `payable` = VALUES(`payable`), `color` = VALUES(`color`), `exchange_rate` = VALUES(`exchange_rate`)";

    /**
     * Do not use.
     */
    @Deprecated
    private String balancesTable = getTablePrefix() + "_balances";

    private HikariConfig hikariConfig;
    private HikariDataSource hikari;
    private final LinkedHashMap<UUID, CachedTopList> topList = new LinkedHashMap<>();

    public MySQLStorage(String host, int port, String database, String username, String password) {
        super("MySQL", true);
        hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?allowPublicKeyRetrieval=true&useSSL=false");
        hikariConfig.setPassword(password);
        hikariConfig.setUsername(username);
        hikariConfig.setMaxLifetime(1500000);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("userServerPrepStmts", "true");
    }

    private String getTablePrefix() {
        return GemsEconomy.getInstance().getConfig().getString("mysql.tableprefix");
    }

    private void setupTables() throws SQLException {
        try (PreparedStatement ps = getHikari().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + this.currencyTable + " (uuid VARCHAR(255) NOT NULL PRIMARY KEY, name_singular VARCHAR(255), name_plural VARCHAR(255),    default_balance DECIMAL,    symbol VARCHAR(10),    decimals_supported INT,    is_default INT,    payable INT,    color VARCHAR(255),    exchange_rate DECIMAL);")) {
            ps.execute();
        }
        try (PreparedStatement ps = getHikari().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + this.accountsTable + " (nickname VARCHAR(255), uuid VARCHAR(255) NOT NULL PRIMARY KEY, payable INT, balance_data LONGTEXT NULL);")) {
            ps.execute();
        }
        try (PreparedStatement ps = getHikari().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + this.balancesTable + " (account_id VARCHAR(255), currency_id VARCHAR(255), balance DECIMAL);")) {
            ps.execute();
        }
    }

    @Override
    public void initialize() {
        this.hikari = new HikariDataSource(this.hikariConfig);

        try (Connection connection = hikari.getConnection()) {
            setupTables();

            PreparedStatement stmt;
            Map<String, List<String>> structure = new HashMap<>();
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet tableResultSet = metaData.getTables(null, "public", null, new String[]{"TABLE"})) {
                while (tableResultSet.next()) {
                    String tableName = tableResultSet.getString("TABLE_NAME");
                    UtilServer.consoleLog("Table: " + tableName);

                    if(tableName.startsWith(getTablePrefix())) {
                        structure.put(tableName, new ArrayList<>());
                        UtilServer.consoleLog("Added table");
                    }
                }
            }

            for(String table : structure.keySet()){
                try (ResultSet columnResultSet = metaData.getColumns(null, "public", table, null)) {
                    while (columnResultSet.next()) {
                        String columnName = columnResultSet.getString("COLUMN_NAME");
                        structure.get(table).add(columnName);
                        UtilServer.consoleLog("Column: " + columnName + " (" + table + ")");
                    }
                }
            }

            List<String> currencyTableColumns = structure.get(this.currencyTable);
            if(currencyTableColumns != null && !currencyTableColumns.isEmpty()){
                if (!currencyTableColumns.contains("exchange_rate")) {
                    stmt = getHikari().getConnection().prepareStatement("ALTER TABLE " + this.currencyTable + " ADD exchange_rate DECIMAL NULL DEFAULT NULL AFTER `color`;");
                    stmt.execute();

                    UtilServer.consoleLog("Altered Table " + this.currencyTable + " to support the new exchange_rate variable.");
                }
            }

            List<String> accountTableColumns = structure.get(this.accountsTable);
            if(accountTableColumns != null && !accountTableColumns.isEmpty()){
                if(!accountTableColumns.contains("balance_data")){
                    stmt = getHikari().getConnection().prepareStatement("ALTER TABLE " + this.accountsTable + " ADD balance_data LONGTEXT NULL DEFAULT NULL AFTER `payable`;");
                    stmt.execute();

                    stmt = getHikari().getConnection().prepareStatement("ALTER TABLE " + this.accountsTable + " DROP COLUMN `id`");
                    stmt.execute();

                    stmt = getHikari().getConnection().prepareStatement("ALTER TABLE " + this.accountsTable + " ADD PRIMARY KEY (uuid)");
                    stmt.execute();

                    stmt = getHikari().getConnection().prepareStatement("ALTER TABLE " + this.currencyTable + " DROP COLUMN `id`");
                    stmt.execute();

                    stmt = getHikari().getConnection().prepareStatement("ALTER TABLE " + this.currencyTable + " ADD PRIMARY KEY (uuid)");
                    stmt.execute();

                    UtilServer.consoleLog("Altered Tables " + this.accountsTable + " to support the new balance data saving");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (hikari != null) {
            hikari.close();
        }
    }

    @Override
    public void loadCurrencies() {
        if (hikari == null) {
            return;
        }
        try (Connection connection = getHikari().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + this.currencyTable);
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
                UtilServer.consoleLog("Loaded currency: " + currency.getSingular() + " (" + currency.getPlural() + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCurrencyLocally(Currency currency) {
        try (Connection connection = getHikari().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + this.currencyTable + " WHERE uuid = ? LIMIT 1;");
            stmt.setString(1, currency.getUuid().toString());
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                double defaultBalance = set.getDouble("default_balance");
                String symbol = set.getString("symbol");
                boolean decimals = set.getInt("decimals_supported") == 1;
                boolean isDefault = set.getInt("is_default") == 1;
                boolean payable = set.getInt("payable") == 1;
                ChatColor color = ChatColor.valueOf(set.getString("color"));
                double exchangeRate = set.getDouble("exchange_rate");

                currency.setDefaultBalance(defaultBalance);
                currency.setSymbol(symbol);
                currency.setDecimalSupported(decimals);
                currency.setDefaultCurrency(isDefault);
                currency.setPayable(payable);
                currency.setColor(color);
                currency.setExchangeRate(exchangeRate);
                UtilServer.consoleLog("Updated currency: " + currency.getPlural());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveCurrency(Currency currency) {
        try (Connection connection = getHikari().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(SAVE_CURRENCY);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        plugin.getUpdateForwarder().sendUpdateMessage("currency", currency.getUuid().toString());
    }

    @Override
    public void deleteCurrency(Currency currency) {
        try (Connection connection = getHikari().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM " + this.currencyTable + " WHERE uuid = ?");
            stmt.setString(1, currency.getUuid().toString());
            stmt.execute();

            //TODO Scheduled for removal
            stmt = connection.prepareStatement("DELETE FROM " + this.balancesTable + " WHERE currency_id = ?");
            stmt.setString(1, currency.getUuid().toString());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getTopList(Currency currency, int offset, int amount, Callback<LinkedList<CachedTopListEntry>> callback) {
        if (this.topList.containsKey(currency.getUuid())) {
            CachedTopList cache = this.topList.get(currency.getUuid());
            if (!cache.isExpired()) {
                LinkedList<CachedTopListEntry> searchResults = new LinkedList<>();
                int collected = 0;
                for(int i = offset; i < cache.getResults().size(); i++){
                    if(collected == amount)break;
                    searchResults.add(cache.getResults().get(i));
                    collected++;
                }
                SchedulerUtils.run(() -> callback.call(searchResults));
                return;
            }
        }
        JSONParser parser = new JSONParser();

        SchedulerUtils.runAsync(() -> {
            LinkedHashMap<String, Double> cache = new LinkedHashMap<>();
            try (Connection connection = getHikari().getConnection()) {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.accountsTable);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    String json = rs.getString("balance_data");

                    Object obj = parser.parse(json);
                    JSONObject data = (JSONObject) obj;
                    Number bal = (Number) data.get(currency.getUuid().toString());
                    if(bal == null || bal.doubleValue() < 1)continue;

                    cache.put(rs.getString("nickname"), bal.doubleValue());
                }

                LinkedHashMap<String, Double> sorted = cache.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                CachedTopList topList = new CachedTopList(currency, amount, offset, System.currentTimeMillis());
                LinkedList<CachedTopListEntry> list = new LinkedList<>();
                for(String name : sorted.keySet()){
                    list.add(new CachedTopListEntry(name, sorted.get(name)));
                    UtilServer.consoleLog(name + ": " + sorted.get(name));
                }
                topList.setResults(list);
                this.topList.put(currency.getUuid(), topList);

                LinkedList<CachedTopListEntry> searchResults = new LinkedList<>();
                int collected = 0;
                for(int i = offset; i < sorted.size(); i++){
                    if(collected == amount)break;
                    searchResults.add(list.get(i));
                    collected++;
                }
                SchedulerUtils.run(() -> callback.call(searchResults));
            }catch(SQLException | ParseException ex){
                ex.printStackTrace();
            }
        });
    }

    private Account returnAccountWithBalances(Account account) {
        if (account == null) {
            return null;
        }

        try (Connection connection = getHikari().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + this.balancesTable + " WHERE account_id = ?");
            stmt.setString(1, account.getUuid().toString());
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                Currency currency = plugin.getCurrencyManager().getCurrency(UUID.fromString(set.getString("currency_id")));
                if (currency == null) {
                    continue;
                }
                account.modifyBalance(currency, set.getDouble("balance"), false);

                if(set.isLast()){
                    saveAccount(account);
                    deleteOldBalances(account);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }

    @Override
    public Account loadAccount(String name) {
        Account account = null;

        try (Connection connection = getHikari().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + this.accountsTable + " WHERE nickname = ? LIMIT 1");
            stmt.setString(1, name);
            ResultSet set = stmt.executeQuery();
            if (set.next()) {
                account = new Account(UUID.fromString(set.getString("uuid")), set.getString("nickname"));
                account.setCanReceiveCurrency(set.getInt("payable") == 1);

                String json = set.getString("balance_data");

                // This is temporary support for older versions to allow converting to new storage
                if(json == null){
                    return this.returnAccountWithBalances(account);
                }

                JSONParser parser = new JSONParser();
                Object obj = parser.parse(json);
                JSONObject data = (JSONObject) obj;

                for(Currency currency : plugin.getCurrencyManager().getCurrencies()){
                    Number amount = (Number) data.get(currency.getUuid().toString());
                    if(amount != null) {
                        account.getBalances().put(currency, amount.doubleValue());
                    }else{
                        account.getBalances().put(currency, currency.getDefaultBalance());
                    }
                }
            }
            set.close();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }

        return account;
    }

    @Override
    public Account loadAccount(UUID uuid) {
        Account account = null;

        try (Connection connection = getHikari().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + this.accountsTable + " WHERE uuid = ? LIMIT 1");
            stmt.setString(1, uuid.toString());
            ResultSet set = stmt.executeQuery();
            if (set.next()) {
                account = new Account(uuid, set.getString("nickname"));
                account.setCanReceiveCurrency(set.getInt("payable") == 1);

                String json = set.getString("balance_data");

                // This is temporary support for older versions to allow converting to new storage
                if(json == null){
                    return this.returnAccountWithBalances(account);
                }

                JSONParser parser = new JSONParser();
                Object obj = parser.parse(json);
                JSONObject data = (JSONObject) obj;

                for(Currency currency : plugin.getCurrencyManager().getCurrencies()){
                    Number amount = (Number) data.get(currency.getUuid().toString());
                    if(amount != null) {
                        account.getBalances().put(currency, amount.doubleValue());
                    }else{
                        account.getBalances().put(currency, currency.getDefaultBalance());
                    }
                }
            }
            set.close();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return account;
    }

    @Override
    public void loadAccount(UUID uuid, Callback<Account> callback) {
        SchedulerUtils.runAsync(() -> {
            Account account = this.loadAccount(uuid);
            SchedulerUtils.run(() -> callback.call(account));
        });
    }

    @Override
    public void loadAccount(String name, Callback<Account> callback) {
        SchedulerUtils.runAsync(() -> {
            Account account = this.loadAccount(name);
            SchedulerUtils.run(() -> callback.call(account));
        });
    }

    @Override
    public ArrayList<Account> getOfflineAccounts() {
        ArrayList<Account> accounts = new ArrayList<>();

        try (Connection connection = getHikari().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + this.accountsTable);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                accounts.add(returnAccountWithBalances(loadAccount(UUID.fromString(rs.getString("uuid")))));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return accounts;
    }

    @Override
    public void createAccount(Account account) {
        try (Connection connection = getHikari().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(SAVE_ACCOUNT);
            stmt.setString(1, account.getDisplayName());
            stmt.setString(2, account.getUuid().toString());
            stmt.setInt(3, account.canReceiveCurrency() ? 1 : 0);

            JSONObject obj = new JSONObject();
            for(Currency currency : plugin.getCurrencyManager().getCurrencies()){
                obj.put(currency.getUuid().toString(), currency.getDefaultBalance());
            }
            String json = obj.toJSONString();
            stmt.setString(4, json);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        plugin.getUpdateForwarder().sendUpdateMessage("account", account.getUuid().toString());
    }

    @Override
    public void saveAccount(Account account) {
        try (Connection connection = getHikari().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(SAVE_ACCOUNT);
            stmt.setString(1, account.getDisplayName());
            stmt.setString(2, account.getUuid().toString());
            stmt.setInt(3, account.canReceiveCurrency() ? 1 : 0);

            JSONObject obj = new JSONObject();
            for(Currency currency : plugin.getCurrencyManager().getCurrencies()){
                obj.put(currency.getUuid().toString(), account.getBalance(currency.getSingular()));
            }
            String json = obj.toJSONString();
            stmt.setString(4, json);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        plugin.getUpdateForwarder().sendUpdateMessage("account", account.getUuid().toString());
    }

    @Override
    public void deleteAccount(Account account) {
        try (Connection connection = getHikari().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM " + this.accountsTable + " WHERE uuid = ? LIMIT 1");
            stmt.setString(1, account.getUuid().toString());
            stmt.execute();
            stmt = connection.prepareStatement("DELETE FROM " + this.balancesTable + " WHERE account_id = ?");
            stmt.setString(1, account.getUuid().toString());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteOldBalances(Account account) {
        try (Connection connection = getHikari().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM " + this.balancesTable + " WHERE account_id = ?");
            stmt.setString(1, account.getUuid().toString());
            stmt.execute();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public HikariDataSource getHikari() {
        return hikari;
    }
}

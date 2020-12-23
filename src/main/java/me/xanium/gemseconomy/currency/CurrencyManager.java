package me.xanium.gemseconomy.currency;

import com.google.common.collect.Lists;
import me.xanium.gemseconomy.GemsEconomy;

import java.util.List;
import java.util.UUID;

public class CurrencyManager {

    private final GemsEconomy plugin;

    public CurrencyManager(GemsEconomy plugin) {
        this.plugin = plugin;
    }

    private final List<Currency> currencies = Lists.newArrayList();

    public boolean currencyExist(String name) {
        for(Currency currency : currencies) {
            if(currency.getSingular().equalsIgnoreCase(name) || currency.getPlural().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    public Currency getCurrency(String name) {
        for(Currency currency : currencies) {
            if(currency.getSingular().equalsIgnoreCase(name) || currency.getPlural().equalsIgnoreCase(name)){
                return currency;
            }
        }
        return null;
    }

    public Currency getCurrency(UUID uuid) {
        for (Currency currency : getCurrencies()) {
            if (!currency.getUuid().equals(uuid)) continue;
            return currency;
        }
        return null;
    }

    public Currency getDefaultCurrency() {
        for (Currency currency : currencies) {
            if (!currency.isDefaultCurrency()) continue;
            return currency;
        }
        return null;
    }

    public void createNewCurrency(String singular, String plural){
        if(currencyExist(singular) || currencyExist(plural)) {
            return;
        }

        Currency currency = new Currency(UUID.randomUUID(), singular, plural);
        currency.setExchangeRate(1.0);
        if(currencies.size() == 0) {
            currency.setDefaultCurrency(true);
        }

        add(currency);

        plugin.getDataStore().saveCurrency(currency);
    }

    public void deleteCurrency(Currency currency) {
        plugin.getDataStore().deleteCurrency(currency);
    }

    public void add(Currency currency) {
        if(currencies.contains(currency))return;

        currencies.add(currency);
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }
}

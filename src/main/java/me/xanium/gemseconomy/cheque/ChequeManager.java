/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.cheque;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.currency.Currency;
import me.xanium.gemseconomy.nbt.NBTItem;
import me.xanium.gemseconomy.utils.UtilString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChequeManager {

    private final GemsEconomy plugin;
    private final ItemStack chequeBaseItem;
    private String nbt_issuer = "issuer";
    private String nbt_value = "value";
    private String nbt_currency = "currency";

    public ChequeManager(GemsEconomy plugin) {
        this.plugin = plugin;

        ItemStack item = new ItemStack(Material.valueOf(plugin.getConfig().getString("cheque.material")), 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(UtilString.colorize(plugin.getConfig().getString("cheque.name")));
        meta.setLore(UtilString.colorize(plugin.getConfig().getStringList("cheque.lore")));
        item.setItemMeta(meta);
        chequeBaseItem = item;
    }

    public ItemStack write(String creatorName, Currency currency, double amount) {
        if(!currency.isPayable())return null;


        if (creatorName.equals("CONSOLE")) {
            creatorName = UtilString.colorize(plugin.getConfig().getString("cheque.console_name"));
        }
        List<String> formatLore = new ArrayList<>();

        for (String baseLore2 : Objects.requireNonNull(chequeBaseItem.getItemMeta().getLore())) {
            formatLore.add(baseLore2.replace("{value}", currency.format(amount)).replace("{player}", creatorName));
        }
        ItemStack ret = chequeBaseItem.clone();
        NBTItem nbt = new NBTItem(ret);
        ItemMeta meta = nbt.getItem().getItemMeta();
        meta.setLore(formatLore);
        nbt.getItem().setItemMeta(meta);
        nbt.setString(nbt_issuer, creatorName);
        nbt.setString(nbt_currency, currency.getPlural());
        nbt.setString(nbt_value, String.valueOf(amount));
        return nbt.getItem();
    }

    public boolean isValid(NBTItem itemstack) {
        if(itemstack.getItem().getType() != chequeBaseItem.getType())return false;
        if (itemstack.getString(nbt_value) != null && itemstack.getString(nbt_currency) != null && itemstack.getString(nbt_issuer) != null) {

            String display = chequeBaseItem.getItemMeta().getDisplayName();
            ItemMeta meta = itemstack.getItem().getItemMeta();

            if(meta == null) return false;

            if(meta.hasDisplayName() && meta.getDisplayName().equals(display)){
                if(meta.hasLore() && meta.getLore().size() == chequeBaseItem.getItemMeta().getLore().size()){
                    return true;
                }
            }
        }
        return false;
    }

    public double getValue(NBTItem itemstack) {
        if (itemstack.getString(nbt_currency) != null && itemstack.getString(nbt_value) != null) {
            return Double.parseDouble(itemstack.getString(nbt_value));
        }
        return 0;
    }

    /**
     *
     * @param item - The Cheque.
     * @return - Currency it represents.
     */
    public Currency getCurrency(NBTItem item) {
        if (item.getString(nbt_currency) != null && item.getString(nbt_value) != null) {
            return plugin.getCurrencyManager().getCurrency(item.getString(nbt_currency));
        }
        return plugin.getCurrencyManager().getDefaultCurrency();
    }
}

/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.nbt;

import org.bukkit.inventory.ItemStack;

public class NBTItem {

    private ItemStack bukkititem;

    public NBTItem(ItemStack Item) {
        bukkititem = Item.clone();
    }

    public ItemStack getItem() {
        return bukkititem;
    }

    public void setString(String Key, String Text) {
        bukkititem = NBTReflection.setString(bukkititem, Key, Text);
    }

    public String getString(String Key) {
        return NBTReflection.getString(bukkititem, Key);
    }

    public void setInteger(String key, Integer Int) {
        bukkititem = NBTReflection.setInt(bukkititem, key, Int);
    }

    public Integer getInteger(String key) {
        return NBTReflection.getInt(bukkititem, key);
    }

    public void setDouble(String key, Double d) {
        bukkititem = NBTReflection.setDouble(bukkititem, key, d);
    }

    public Double getDouble(String key) {
        return NBTReflection.getDouble(bukkititem, key);
    }

    public void setBoolean(String key, Boolean b) {
        bukkititem = NBTReflection.setBoolean(bukkititem, key, b);
    }

    public Boolean getBoolean(String key) {
        return NBTReflection.getBoolean(bukkititem, key);
    }

    public Boolean hasKey(String key) {
        return NBTReflection.hasKey(bukkititem, key);
    }
}

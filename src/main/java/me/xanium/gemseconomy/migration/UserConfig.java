/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.migration;

import me.xanium.gemseconomy.GemsEconomy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class UserConfig {

    private UserConfig(){}
    private static UserConfig instance = null;

    public static UserConfig getInstance() {
        if(instance == null) {
            instance = new UserConfig();
        }
        return instance;
    }

    private HashMap<JavaPlugin, HashMap<String, FileConfiguration>> configs = new HashMap<>();

    public FileConfiguration reloadConfig(JavaPlugin plugin, String id) {
        if(!configs.containsKey(plugin)) configs.put(plugin, new HashMap<>());
        File customConfigFile = new File(plugin.getDataFolder() + "/userdata/", id + ".yml");
        FileConfiguration customConfig  = YamlConfiguration.loadConfiguration(customConfigFile);
        configs.get(plugin).put(id, customConfig);
        return customConfig;
    }

    public FileConfiguration getConfig(JavaPlugin plugin, String id) {
        if(configs.containsKey(plugin) && configs.get(plugin).containsKey(id)) {
            return configs.get(plugin).get(id);
        }
        return reloadConfig(plugin, id);
    }

    public void saveConfig(JavaPlugin plugin, String id) {
        try {
            getConfig(plugin, id).save(new File(plugin.getDataFolder() + "/userdata/", id + ".yml"));
        }
        catch (Exception ex) {
            plugin.getLogger().severe("Could not save userdata for: " + id);
        }
    }

    public FileConfiguration getConfig(UUID player){
        if(configs.containsKey(GemsEconomy.getInstance()) && configs.get(GemsEconomy.getInstance()).containsKey(player.toString())) {
            return configs.get(GemsEconomy.getInstance()).get(player.toString());
        }
        return reloadConfig(GemsEconomy.getInstance(), player.toString());
    }

    public void saveUser(UUID player){
        saveConfig(GemsEconomy.getInstance(), player.toString());
    }

}

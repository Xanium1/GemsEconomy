/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerPreLoginEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package me.xanium.gemseconomy.listeners;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.economy.Account;
import me.xanium.gemseconomy.economy.AccountManager;
import me.xanium.gemseconomy.utils.UtilServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EconomyListener implements Listener {

    private final GemsEconomy plugin = GemsEconomy.getInstance();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        Account account = AccountManager.getAccount(event.getUniqueId());
        if (account == null) {
            account = new Account(event.getUniqueId(), event.getName());
            GemsEconomy.getDataStore().saveAccount(account);
            UtilServer.consoleLog("New Account created for: " + account.getDisplayName());
        } else if ((account.getNickname() != null && !account.getNickname().equals(event.getName())) || account.getNickname() == null) {
            account.setNickname(event.getName());
            GemsEconomy.getDataStore().saveAccount(account);
            UtilServer.consoleLog("Name change found! Updating user " + account.getDisplayName() + "...");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Account account = AccountManager.getAccount(player);
            if (account != null) {
                AccountManager.getAccounts().remove(account);
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Account account = AccountManager.getAccount(player);
            if (account != null && player.isOnline() && !AccountManager.getAccounts().contains(account)) {
                AccountManager.getAccounts().add(account);
            }
        });
    }

}


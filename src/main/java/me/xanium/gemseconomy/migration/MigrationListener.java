/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.migration;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.economy.Account;
import me.xanium.gemseconomy.economy.AccountManager;
import me.xanium.gemseconomy.economy.Currency;
import me.xanium.gemseconomy.file.F;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MigrationListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if(GemsEconomy.getInstance().getConfig().getBoolean("migrate_old_accounts")) {

            UserConfig user = UserConfig.getInstance();

            if (user.getConfig(player.getUniqueId()).getString("UniqueID") != null) {

                Account account = AccountManager.getAccount(player.getUniqueId());
                Currency def = AccountManager.getDefaultCurrency();

                account.deposit(def, user.getConfig(player.getUniqueId()).getDouble("Balance"));
                player.sendMessage(F.getPrefix() + "Your old account were migrated to the new system!");
            }

        }
    }
}

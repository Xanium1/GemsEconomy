package me.xanium.gemseconomy.bungee;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.economy.AccountManager;
import me.xanium.gemseconomy.economy.Currency;
import me.xanium.gemseconomy.utils.UtilServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class UpdateForwarder implements PluginMessageListener {

    /**
     * GemsEconomy Bungee-Spigot Messaging Listener
     *
     * This listener is used to update currencies and balance for players
     * on different servers. This is important to sustain synced balances
     * and currencies on all of the servers.
     */

    private final String channelName = "GemsEconomy Data Channel";

    @Override
    public void onPluginMessageReceived(String channel, Player notInUse, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals(channelName)) {
            String[] info = in.readUTF().split(",");
            String type = info[0];
            String name = info[1];
            if(GemsEconomy.getInstance().isDebug()){
                UtilServer.consoleLog(channelName + " - Received: " + type + " = " + name);
            }

            if(type.equals("currency")){
                UUID uuid = UUID.fromString(name);
                Currency currency = AccountManager.getCurrency(uuid);
                if(currency != null) {
                    GemsEconomy.getDataStore().updateCurrency(currency);
                    if(GemsEconomy.getInstance().isDebug()){
                        UtilServer.consoleLog(channelName + " - Currency " + name + " updated.");
                    }
                }
            }
            else if(type.equals("account")){
                UUID uuid = UUID.fromString(name);
                AccountManager.removeAccount(uuid);
                GemsEconomy.doAsync(() -> GemsEconomy.getDataStore().loadAccount(uuid));
                if(GemsEconomy.getInstance().isDebug()){
                    UtilServer.consoleLog(channelName + " - Account " + name + " updated.");
                }
            }
        }
    }


    public void sendUpdateMessage(String type, String name){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ONLINE");
        out.writeUTF(channelName);

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(type + "," + name);
        } catch (IOException exception){
            exception.printStackTrace();
        }

        out.write(msgbytes.toByteArray());

        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if(player == null){
            if(GemsEconomy.getInstance().isDebug()){
                UtilServer.consoleLog(channelName + " - Player is Null. Cancelled.");
            }
            return;
        }
        player.sendPluginMessage(GemsEconomy.getInstance(), "BungeeCord", out.toByteArray());
    }


}

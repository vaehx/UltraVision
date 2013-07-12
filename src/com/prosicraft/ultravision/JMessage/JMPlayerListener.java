/*
 *      =====================================================================
 *      T H E    J M E S S A G E     L I S T E N E R
 *      =====================================================================
 *
 */
package com.prosicraft.ultravision.JMessage;

import com.prosicraft.ultravision.base.UVClickAuth;
import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MLog;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author prosicraft
 */
public class JMPlayerListener implements Listener {

    private JavaPlugin parent   = null;
    private JMessage messager   = null;
    private MAuthorizer auth    = null;
    private UVClickAuth cauth   = null;
    private Chat chat           = null;
    private Economy econ        = null;

    public JMPlayerListener (JavaPlugin prnt, JMessage msg, MAuthorizer mauth, UVClickAuth cauth) {
        this.parent = prnt;
        this.messager = msg;
        this.auth = mauth;
        this.cauth = cauth;
    }

    public void init () {
        parent.getServer().getPluginManager().registerEvents(this, parent);
    }

    public void onPluginDisable (PluginDisableEvent e) {
            if ( e.getPlugin().getName().equals(econ.getName()) ) {
                    econ = null;
                    MLog.d("Unhooked from Economy plugin");
            }
            if ( e.getPlugin().getName().equals(chat.getName()) ) {
                    chat = null;
                    MLog.d("Unhooked from Chat plugin");
            }
            if ( e.getPlugin().getName().equals("Vault") ) {
                    MLog.d("Unhooked from Vault");
            }
    }

    public Chat getChat () {
            return chat;
    }

    public Economy getEcon () {
            return econ;
    }

    public void checkVault () {
            if ( chat == null ) {
                    try {
                        RegisteredServiceProvider<Chat> chatProvider = parent.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
                        if (chatProvider != null) {
                                chat = chatProvider.getProvider();
                        }
                    }
                    catch (Exception ex) {
                            MLog.e("Vault seems not to be enabled! No Features relating to perms will function.");
                    }
                    if (chat == null)
                            MLog.d( "Can't hook into Chat Plugin" );
                    else
                            MLog.i( "Hooked into Chat Plugin" );
            }
            if ( econ == null ) {
                    try {
                            RegisteredServiceProvider<Economy> economyProvider = parent.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
                            if (economyProvider != null) {
                                    econ = economyProvider.getProvider();
                            }
                    } catch (Exception ex) {
                            MLog.e("Vault seems not to be enabled! No Features relating to Economy will function.");
                    }
                    if (econ == null)
                            MLog.d("Can't hook into Economy plugin!");
                    else
                            MLog.i("Hooked into Economy plugin: " + econ.getName());
            }
    }

    public String untag2 (String src, Player p)
    {
            checkVault();
            String res = "";

            try {
                res = src.replaceAll("%prefix", chat.getPlayerPrefix(p))
                        .replaceAll("%suffix", chat.getPlayerSuffix(p));
            } catch (Exception ex) {
                MLog.w("Groups were not supported by your permissions system or Vault isn't enabled!");
            }

            try {
                    res = res.replaceAll("%bal", Double.toString(econ.getBalance(p.getName())));
            } catch (Exception ex) {
                    MLog.w("No Economy plugin found, or Vault isn't enabled!");
            }

            return res;
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if ( messager == null ) return;

        messager.doJoin(event.getPlayer());

        if ( messager.isClearingStandard() )
            event.setJoinMessage("");
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if ( messager == null ) return;

        messager.doLeave(event.getPlayer());

        if ( messager.isClearingStandard() )
            event.setQuitMessage("");
    }

    public boolean loggedIn (Player p) {
            boolean res = true;
            if ( auth != null ) res = auth.loggedIn(p);
            if ( cauth != null ) { if ( res && !cauth.isLoggedIn(p.getName()) ) res = false; } else MLog.d("cauth = null");
            return res;
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        if (messager.getIngameLogger().isEmpty())
            return;
        for ( Player p : messager.getIngameLogger() )
        {
                if ( !loggedIn(p) ) continue;
                if ( !p.getName().equalsIgnoreCase(event.getPlayer().getName()) )
                        p.sendMessage(ChatColor.DARK_GRAY + " " + event.getPlayer().getName() + ": " + ChatColor.GRAY+ event.getMessage());
        }
    }

}

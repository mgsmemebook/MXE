package me.mgsmemebook.mxe;
import me.mgsmemebook.mxe.func;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;
import java.util.Objects;

import static me.mgsmemebook.mxe.db.DB.addDBPlayer;
import static me.mgsmemebook.mxe.db.DB.getDBPlayer;

public class PlayerEvents implements Listener {
    LuckPerms lp = LuckPermsProvider.get();

    //Player Join/Leave
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        lp.getUserManager().loadUser(p.getUniqueId());
        User u = lp.getUserManager().getUser(p.getUniqueId());
        if(u == null) { return; }
        if(u.getPrimaryGroup().equals("default")) {
            func.switchGroup(u, "user", "default");
        }
        lp.getUserManager().saveUser(u);
        Group g = lp.getGroupManager().getGroup(u.getPrimaryGroup());
        if(g == null) { return; }
        func.updateUser(p, g);

        e.setJoinMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[join/leave]: " + ChatColor.RESET + p.getDisplayName() + ChatColor.AQUA + " ist uns beigetreten!");

        String res = getDBPlayer(String.valueOf(p.getUniqueId()));
        func.cMSG(ChatColor.AQUA + "SQL: Gefundene Datenbankeinträge: " + res);
        if(res == null) {
            func.cMSG(ChatColor.AQUA + "SQL: Spieler nicht gefunden - Füge Datenbankeintrag hinzu");
            addDBPlayer(String.valueOf(p.getUniqueId()), p.getName());
        }
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.setQuitMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[join/leave] " + ChatColor.RESET + p.getDisplayName() + ChatColor.AQUA + " ist Milch holen gegangen!");
        lp.getUserManager().saveUser(Objects.requireNonNull(lp.getUserManager().getUser(p.getUniqueId())));
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();

        msg = func.colCodes(msg);
        msg = p.getDisplayName() + ChatColor.GOLD + ChatColor.BOLD + " sagt: " + ChatColor.RESET + ChatColor.GRAY + msg;
        Bukkit.broadcastMessage(msg);
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity().getPlayer();
        Player k = e.getEntity().getKiller();
        String msg = e.getDeathMessage();
        msg = msg.replaceAll(p.getName(), p.getDisplayName()+ChatColor.RESET+ChatColor.GRAY);
        if(k != null) {
            msg = msg.replaceAll(k.getName(), k.getDisplayName()+ChatColor.RESET+ChatColor.GRAY);
        }
        msg = ChatColor.GRAY + msg;
        e.setDeathMessage(msg);
    }
}

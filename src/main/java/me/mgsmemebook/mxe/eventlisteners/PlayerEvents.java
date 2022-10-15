package me.mgsmemebook.mxe.eventlisteners;
import me.mgsmemebook.mxe.MXE;
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

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
            func.addNode(u, "group.user");
            u.setPrimaryGroup("user");
        }
        if(func.isPlayerInGroup(p,"default")) {
            func.remNode(u, "group.default");
        }
        Group g = lp.getGroupManager().getGroup(u.getPrimaryGroup());
        if(g == null) { return; }
        String prefix = g.getCachedData().getMetaData().getPrefix();
        prefix = func.colCodes(prefix);
        p.setDisplayName(prefix + " " + p.getName() + ChatColor.RESET);
        p.setPlayerListName(p.getDisplayName());
        for(Player otherp : Bukkit.getOnlinePlayers()){
            if(p != otherp){
                p.hidePlayer(MXE.getPlugin(MXE.class), otherp);
            }
        }
        for(Player otherp : Bukkit.getOnlinePlayers()){
            if(p != otherp){
                p.showPlayer(MXE.getPlugin(MXE.class), otherp);
            }
        }
        e.setJoinMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[join/leave]: " + ChatColor.RESET + p.getDisplayName() + ChatColor.AQUA + " ist uns beigetreten!");

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
}

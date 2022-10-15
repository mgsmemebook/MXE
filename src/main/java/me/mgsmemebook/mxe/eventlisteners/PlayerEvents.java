package me.mgsmemebook.mxe.eventlisteners;
import me.mgsmemebook.mxe.func;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        if(func.isPlayerInGroup(p,"default")) {
            func.remNode(u, "group.default");
        }
        if(u.getPrimaryGroup().equals("default")) {
            func.addNode(u, "group.user");
            u.setPrimaryGroup("user");
        }
        Group g = lp.getGroupManager().getGroup(u.getPrimaryGroup());
        assert g != null;
        String prefix = g.getCachedData().getMetaData().getPrefix();
        p.setDisplayName(prefix + " " + p.getName());

        e.setJoinMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[join/leave] " + ChatColor.RESET + "" + ChatColor.AQUA + p.getDisplayName() + " ist uns beigetreten!");

    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.setQuitMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[join/leave] " + ChatColor.RESET + "" + ChatColor.AQUA + p.getDisplayName() + " ist Milch holen gegangen!");
        lp.getUserManager().saveUser(Objects.requireNonNull(lp.getUserManager().getUser(p.getUniqueId())));
    }

}

package me.mgsmemebook.mxe.eventlisteners;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {
    //Player Join/Leave
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.setJoinMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[join/leave] " + ChatColor.RESET + "" + ChatColor.AQUA + p.getDisplayName() + " ist uns beigetreten!");
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.setQuitMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[join/leave] " + ChatColor.RESET + "" + ChatColor.AQUA + p.getDisplayName() + " ist Milch holen gegangen!");
    }

}

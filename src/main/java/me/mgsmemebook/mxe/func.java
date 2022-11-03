package me.mgsmemebook.mxe;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

import static org.bukkit.Bukkit.getServer;

public class func {
    public static void cMSG(String message, Integer logLevel) {
        if(logLevel <= MXE.getCustomConfig().getInt("mxe.log-level")) {
            getServer().getConsoleSender().sendMessage(message);
        }
    }

    public static String colCodes(String s) {
        if(s != null) s = ChatColor.translateAlternateColorCodes('&', s);
        return s;
    }
    public static void updateUser(Player p, Group g) {
        Nametag.resetName(p);
        if(MXE.lpLoaded) {
            if(MXE.getPlayerSB().getEntryTeam(p.getName()) != null) MXE.getPlayerSB().getEntryTeam(p.getName()).removeEntry(p.getName());
            if(MXE.getPlayerSB().getEntryTeam(p.getDisplayName()) != null) MXE.getPlayerSB().getEntryTeam(p.getDisplayName()).removeEntry(p.getDisplayName());
            p.setDisplayName(p.getName());
            MXE.getPlayerSB().getTeam(g.getName()).addEntry(p.getDisplayName());
        } else {
            p.setDisplayName(p.getName());
        }
        MXE.updatePlayerSB();

        p.setPlayerListName(MXE.getPlayerPrefix(p)+p.getName());
    }
    public static void switchGroup(User u, String group, String oldgroup) {
        if(MXE.lpLoaded) {
            LuckPerms lp = LuckPermsProvider.get();
            u.data().add(InheritanceNode.builder(group).build());
            u.setPrimaryGroup(group);
            u.data().remove(InheritanceNode.builder(oldgroup).build());
            lp.getUserManager().saveUser(u);
        }
    }

    public static boolean isNumeric(String string) {
        if (string == null || string.equals("")) {
            System.out.println("[MXE] String cannot be parsed, it is null or empty.");
            return false;
        }
        try {
            Double.parseDouble(string);
        } catch (NumberFormatException e) {
            System.out.println("[MXE] "+string+" cannot be parsed to double.");
            return false;
        }
        return true;
    }

    public static void teleportDelay(Player p, Long delay, Location loc) {
        if(delay == null || delay == 0.0) {
            p.teleport(loc);
        } else {
            Location oldloc = p.getLocation();
            String msg = MXE.getCustomConfig().getString("messages.custom.timedtp.wait");
            if(msg == null) {
                func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.timedtp.wait)", 2);
            } else {
                msg = colCodes(msg);
                msg = msg.replaceAll("%t", delay.toString());
                p.sendMessage(msg);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    Location newloc = p.getLocation();
                    if(oldloc.getX() == newloc.getX() && oldloc.getY() == newloc.getY() && oldloc.getZ() == newloc.getZ() && oldloc.getWorld() == newloc.getWorld()) {
                        p.teleport(loc);
                    } else {
                        String failmsg = MXE.getCustomConfig().getString("messages.custom.timedtp.failed");
                        if(failmsg == null) {
                            func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.timedtp.failed)", 2);
                        } else {
                            failmsg = colCodes(failmsg);
                            p.sendMessage(failmsg);
                        }
                    }
                }
            }.runTaskLater(MXE.getPlugin(), delay*20);
        }
    }
    public static void checkAllSections() {
        Set<String> keys = MXE.getCustomConfig().getDefaults().getKeys(true);
        for(String key:keys) {
            if(!MXE.getCustomConfig().contains(key, true) && MXE.getCustomConfig().getDefaults().contains(key, false)) {
                if(MXE.getCustomConfig().getDefaults().isSet(key)) {
                    MXE.getCustomConfig().set(key, MXE.getCustomConfig().get(key));
                    cMSG(ChatColor.DARK_GRAY + "[MXE] Debug: Setting " + key + " to " + MXE.getCustomConfig().get(key) + ".", 3);
                } else {
                    MXE.getCustomConfig().createSection(key);
                    cMSG(ChatColor.DARK_GRAY + "[MXE] Debug: Creating section " + key + ".", 3);
                }
            }
            MXE.getCustomConfig().setComments(key, MXE.getCustomConfig().getComments(key));
        }
        MXE.getPlugin().saveConfig();
    }

    public static Player getRealPlayer(String name) {
        if(Bukkit.getPlayer(name) == null) {
            if(Nametag.isFakeName(name)) {
                return Nametag.getRealPlayer(name);
            } else {
                return null;
            }
        } else {
            return Bukkit.getPlayer(name);
        }
    }
}

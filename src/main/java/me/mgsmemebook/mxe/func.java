package me.mgsmemebook.mxe;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class func {
    public static void cMSG(String message) {
        getServer().getConsoleSender().sendMessage(message);
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

}

package me.mgsmemebook.mxe;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class func {
    static LuckPerms lp = LuckPermsProvider.get();

    public static void cMSG(String message) {
        getServer().getConsoleSender().sendMessage(message);
    }

    public static String colCodes(String s) {
        if(s != null) s = ChatColor.translateAlternateColorCodes('&', s);
        return s;
    }
    public static void updateUser(Player p, Group g) {
        Nametag.resetName(p);

        String prefix = g.getCachedData().getMetaData().getPrefix();
        if(MXE.getPlayerSB().getEntryTeam(p.getName()) != null) MXE.getPlayerSB().getEntryTeam(p.getName()).removeEntry(p.getName());
        MXE.getPlayerSB().getTeam(g.getName()).addEntry(p.getName());
        MXE.updatePlayerSB();

        p.setPlayerListName(MXE.getPlayerPrefix(p)+p.getName());
        p.setDisplayName(p.getName());
    }
    public static void switchGroup(User u, String group, String oldgroup) {
        DataMutateResult result = u.data().add(InheritanceNode.builder(group).build());
        System.out.println("[MXE] NodeBuilder (add " + group + "): " + result);
        u.setPrimaryGroup(group);
        result = u.data().remove(InheritanceNode.builder(oldgroup).build());
        System.out.println("[MXE] NodeBuilder (remove " + oldgroup + "): " + result);
        lp.getUserManager().saveUser(u);
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

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
        s = ChatColor.translateAlternateColorCodes('&', s);
        return s;
    }
    public static void updateUser(Player p, Group g) {
        String prefix = g.getCachedData().getMetaData().getPrefix();
        if (prefix != null) {
            prefix = func.colCodes(prefix);
            p.setDisplayName(prefix + " " + p.getName() + ChatColor.RESET);
            p.setPlayerListName(p.getDisplayName());
        }
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
            double d = Double.parseDouble(string);
        } catch (NumberFormatException e) {
            System.out.println("[MXE] "+string+" cannot be parsed to double.");
            return false;
        }
        return true;
    }
}

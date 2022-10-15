package me.mgsmemebook.mxe;

import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;

import static org.bukkit.Bukkit.getServer;

public class func {
    public static void cMSG(String message) {
        getServer().getConsoleSender().sendMessage(message);
    }
    public static boolean isPlayerInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
    }
    public static String getPlayerGroup(Player player, Collection<String> possibleGroups) {
        for (String group : possibleGroups) {
            if (player.hasPermission("group." + group)) {
                return group;
            }
        }
        return null;
    }
    public static void addNode(User user, String node) {
        user.data().add(Node.builder(node).build());
    }
    public static void remNode(User user, String node) {
        user.data().remove(Node.builder(node).build());
    }

    public static String colCodes(String s) {
        s = ChatColor.translateAlternateColorCodes('&', s);
        return s;
    }
}

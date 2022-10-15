package me.mgsmemebook.mxe;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
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

    public static void sendMessageToAll(String msg) {
        for(Player p : Bukkit.getOnlinePlayers()){
            p.sendMessage(msg);
        }
    }
    public static void updateUser(Player p, Group g) {

        String prefix = g.getCachedData().getMetaData().getPrefix();
        prefix = func.colCodes(prefix);
        p.setDisplayName(prefix + " " + p.getName() + ChatColor.RESET);
        p.setPlayerListName(p.getDisplayName());
    }
    public static void switchGroup(User u, String group, String oldgroup) {
        DataMutateResult result = u.data().add(InheritanceNode.builder(group).build());
        System.out.println("NodeBuilder (add " + group + "): " + result);
        u.setPrimaryGroup(group);
        result = u.data().remove(InheritanceNode.builder(oldgroup).build());
        System.out.println("NodeBuilder (remove " + oldgroup + "): " + result);
        lp.getUserManager().saveUser(u);
    }

}

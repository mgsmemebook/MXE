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
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Set;

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
        Scoreboard sb = MXE.getPlayerSB();
        Team team = sb.getTeam(g.getName());
        if(team != null) {
            team.addEntry(p.getName());
            p.setPlayerListName(team.getPrefix() + p.getName());
            Nametag.resetName(p);
            Nametag.setName(p, p.getName());
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

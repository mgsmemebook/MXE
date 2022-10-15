package me.mgsmemebook.mxe;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TabCompletion implements TabCompleter {
    LuckPerms lp = LuckPermsProvider.get();
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> playerNames = new ArrayList<>();
        Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
        Bukkit.getServer().getOnlinePlayers().toArray(players);
        for (Player player : players) {
            playerNames.add(player.getName());
        }

        switch (command.getName()) {
            case "tphere":
            case "kill":
                if(args.length == 1) {
                    return playerNames;
                } else {
                    return null;
                }
            case "gm":
                if(args.length == 1) {
                    List<String> gameModes = new ArrayList<>();
                    gameModes.add("Survival");
                    gameModes.add("Creative");
                    gameModes.add("Adventure");
                    gameModes.add("Spectator");
                    return gameModes;
                } else if(args.length == 2) {
                    return playerNames;
                } else {
                    return null;
                }
            case "setrank":
                if(args.length == 1) {
                    return playerNames;
                } else if(args.length == 2) {
                    List<String> ranks = new ArrayList<>();
                    Set<Group> groups = lp.getGroupManager().getLoadedGroups();
                    for(Group group : groups) {
                        ranks.add(group.getName());
                    }
                    return ranks;
                } else {
                    return null;
                }
            default:
                return null;
        }
    }
}

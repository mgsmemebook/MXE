package me.mgsmemebook.mxe;

import me.mgsmemebook.mxe.db.DB;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> playerNames = new ArrayList<>();
        Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
        Bukkit.getServer().getOnlinePlayers().toArray(players);
        for (Player player : players) {
            playerNames.add(player.getDisplayName());
        }

        List<String> options;
        String lang = MXE.getCustomConfig().getString("messages.language");
        if(lang == null) {
            func.cMSG(ChatColor.RED + "[MXE]: Error: Config misconfigured! Commands won't work!", 1);
            return new ArrayList<>();
        }
        switch (command.getName()) {
            case "ban":
                if(args.length == 1) {
                    return playerNames;
                } else if(args.length == 2) {
                    options = new ArrayList<>();
                    switch (lang) {
                        case "de":
                            options.add("Grund");
                            options.add("Zeit (in s/m/h/D/M/Y)");
                            break;
                        default:
                            options.add("Reason");
                            options.add("Time (in s/m/h/D/M/Y)");
                    }
                    return options;
                } else if(args.length == 3) {
                    options = new ArrayList<>();
                    switch (lang) {
                        case "de":
                            options.add("Grund");
                            break;
                        default:
                            options.add("Reason");
                    }
                    return options;
                }

            case "fly":
                if(args.length == 1) {
                    options = new ArrayList<>();
                    switch (lang) {
                        case "de":
                            options.add("Geschwindigkeit");
                            break;
                        default:
                            options.add("Speed");
                    }
                    return options;
                }

            case "gm":
                if(args.length == 1) {
                    options = new ArrayList<>();
                    options.add("Survival");
                    options.add("Creative");
                    options.add("Adventure");
                    options.add("Spectator");
                    return options;
                } else if(args.length == 2) {
                    return playerNames;
                }

            case "god":
                if(args.length == 1) {
                    options = new ArrayList<>();
                    switch (lang) {
                        case "de":
                            options.add("an");
                            options.add("aus");
                            options.add("1");
                            options.add("0");
                            break;
                        default:
                            options.add("on");
                            options.add("off");
                            options.add("1");
                            options.add("0");
                    }
                    return options;
                }

            case "home":
                options = new ArrayList<>();
                if(args.length == 1) {
                    options.add("name");
                    options.add("set");
                    options.add("list");
                    options.add("remove");
                    options.add("rename");
                    return options;
                } else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("rename") || args[0].equalsIgnoreCase("remove") && sender instanceof Player) {
                        Player p = Bukkit.getPlayerExact(sender.getName());
                        if(p == null) {
                            String error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE TabCompletion]: " + ChatColor.RESET + ChatColor.DARK_RED + "Warn: Player is null (" + sender.getName() + ")";
                            func.cMSG(error, 2);
                            return null;
                        }
                        return DB.getPlayerHomes(p.getUniqueId());
                    } else if(args[0].equalsIgnoreCase("set")) {
                        options.add("name");
                    }
                }
                return options;

            case "kick":
                if(args.length == 1) {
                    return playerNames;
                }

            case "kill":
                if(args.length == 1) {
                    return playerNames;
                }

            case "mute":
                if(args.length == 1) {
                    return playerNames;
                } else if(args.length == 2) {
                    options = new ArrayList<>();
                    switch (lang) {
                        case "de":
                            options.add("Zeit (in s/m/h/D/M/Y)");
                            break;
                        default:
                            options.add("Time (in s/m/h/D/M/Y)");
                    }
                    return options;
                }

            case "pm":
                if(args.length == 1) {
                    return playerNames;
                } else if(args.length == 2) {
                    options = new ArrayList<>();
                    switch (lang) {
                        case "de":
                            options.add("Nachricht");
                            break;
                        default:
                            options.add("Message");
                    }
                    return options;
                }

            case "reply":
                if(args.length == 1) {
                    options = new ArrayList<>();
                    switch (lang) {
                        case "de":
                            options.add("Nachricht");
                            break;
                        default:
                            options.add("Message");
                    }
                    return options;
                }

            case "setrank":
                if(args.length == 1) {
                    return playerNames;
                } else if(args.length == 2 && MXE.lpLoaded) {
                    LuckPerms lp = LuckPermsProvider.get();
                    List<String> ranks = new ArrayList<>();
                    Set<Group> groups = lp.getGroupManager().getLoadedGroups();
                    for(Group group : groups) {
                        ranks.add(group.getName());
                    }
                    return ranks;
                }

            case "tpa":
                if(args.length == 1) {
                    return playerNames;
                } break;

            case "tpahere":
                if(args.length == 1) {
                    return playerNames;
                }

            case "tphere":
                if(args.length == 1) {
                    return playerNames;
                }

            case "unban":
                if(args.length == 1) {
                    return playerNames;
                }

            case "unmute":
                if(args.length == 1) {
                    return playerNames;
                }

            default:
                return new ArrayList<>();
        }
        return new ArrayList<>();
    }
}

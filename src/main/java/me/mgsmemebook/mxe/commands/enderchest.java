package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.MXE;
import me.mgsmemebook.mxe.func;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class enderchest implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error;
        String othererror = MXE.getCustomConfig().getString("messages.custom.error.other");
        othererror = func.colCodes(othererror);
        String permerror = MXE.getCustomConfig().getString("messages.custom.error.unsufficient-permissions");
        permerror = func.colCodes(permerror);
        String lang = MXE.getCustomConfig().getString("messages.language");
        if(othererror == null || lang == null || permerror == null) {
            func.cMSG(ChatColor.RED + "[MXE]: Error: Config misconfigured! Commands won't work!", 1);
            return true;

        }
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE enderchest]: " + ChatColor.RESET + ChatColor.DARK_RED + "Warn: Player is null (" + sender.getName() + ")";
                func.cMSG(error, 2);
                return true;

            }
            if(!p.isOp()) {
                if(MXE.lpLoaded) {
                    LuckPerms lp = LuckPermsProvider.get();
                    User user = lp.getUserManager().getUser(p.getUniqueId());
                    if(!user.getCachedData().getPermissionData().checkPermission("mxe.enderchest").asBoolean()) {
                        p.sendMessage(permerror);
                        return true;
                    }
                } else {
                    if(!p.hasPermission("mxe.enderchest")) {
                        p.sendMessage(permerror);
                        return true;

                    }

                }
            }
            p.openInventory(p.getEnderChest());
            switch (lang) {
                case "de":
                    p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"[Server]: "+ChatColor.RESET+ChatColor.AQUA+"Enderchest geöffnet");
                    break;
                default:
                    p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"[Server]: "+ChatColor.RESET+ChatColor.AQUA+"Enderchest opened");
            }
        } else {
            error = ChatColor.DARK_RED + "[MXE] You can't perform this command while in console!";
            sender.sendMessage(error);

        }

        return true;
    }

}

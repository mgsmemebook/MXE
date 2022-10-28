package me.mgsmemebook.mxe;

import me.mgsmemebook.mxe.db.DB;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static me.mgsmemebook.mxe.db.DB.*;

public class PlayerEvents implements Listener {
    LuckPerms lp = LuckPermsProvider.get();

    //Player Join/Leave
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        ArrayList<String> baninf = DB.getPlayerBanInfo(p.getUniqueId());
        if(baninf != null) {
            //Banned
            String reason = null;
            if(baninf.size() == 3) {
                reason = baninf.get(2);
            }
            if(Boolean.parseBoolean(baninf.get(0))) {
                //Tempban
                Calendar cl = Calendar.getInstance();
                Calendar now = Calendar.getInstance();

                long longtime = Long.parseLong(baninf.get(1));

                now.setTime(new Date());
                cl.setTimeInMillis(longtime);

                long timeleft = longtime-now.getTimeInMillis();

                if(timeleft > 0) {
                    LocalDateTime ldt = LocalDateTime.ofInstant(cl.toInstant(), cl.getTimeZone().toZoneId());
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                    String unbandate = ldt.format(dtf);

                    String msg = ChatColor.RED + "Du wurdest bis zum " + ChatColor.BOLD + unbandate + ChatColor.RESET + ChatColor.RED + " von diesem Server gebannt! Grund: " + reason;
                    p.kickPlayer(msg);
                    e.setJoinMessage("");
                } else {
                    DB.unbanDBPlayer(p.getUniqueId());
                    playerJoin(p, e);
                }
            } else {
                //Permban
                String msg = ChatColor.RED + "Du wurdest " + ChatColor.BOLD + "permanent" + ChatColor.RESET + ChatColor.RED + " von diesem Server gebannt! Grund: " + reason;
                p.kickPlayer(msg);
                e.setJoinMessage("");
            }
        } else {
            //Not Banned
            playerJoin(p, e);
        }
    }
    public void playerJoin(Player p, PlayerJoinEvent e) {
        User u = lp.getUserManager().getUser(p.getUniqueId());
        if(u != null) {
            String group = u.getPrimaryGroup();
            Group g = lp.getGroupManager().getGroup(group);
            if(g != null) {
                func.updateUser(p, g);
            }
        }

        e.setJoinMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[join/leave]: " + ChatColor.RESET + MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.AQUA + " ist uns beigetreten!");

        //Check DB
        String res = getDBPlayer(p.getUniqueId());
        func.cMSG(ChatColor.DARK_AQUA + "[MXE] SQL: Found database entries: " + res);
        if(res == null) {
            func.cMSG(ChatColor.DARK_AQUA + "[MXE] SQL: Player not found - Adding to database");
            addDBPlayer(p.getUniqueId(), p.getName());
        }

        //Get vanished Players
        ArrayList<String> van = DB.getAllVanished();
        if(!van.isEmpty()) {
            for(String user:van) {
                Player t = Bukkit.getPlayerExact(user);
                Group tg = lp.getGroupManager().getGroup(Objects.requireNonNull(lp.getUserManager().getUser(t.getUniqueId())).getPrimaryGroup());
                Group pg = lp.getGroupManager().getGroup(u.getPrimaryGroup());
                if(pg != null && tg != null) {
                    if(pg.getWeight().isPresent() && tg.getWeight().isPresent() && tg.getWeight().getAsInt() > pg.getWeight().getAsInt()) {
                        t.hidePlayer(MXE.getPlugin(), p);
                    }
                }
            }
        }

        p.setPlayerListHeader(ChatColor.GOLD+""+ChatColor.BOLD+"     -- Wilkommen "+p.getName()+" --     ");
        p.setPlayerListFooter(ChatColor.YELLOW + "MXE V."+MXE.getPlugin().getDescription().getVersion());
    }
    /*TO DO:
    * Player NameTags
    */

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        ArrayList<String> baninf = DB.getPlayerBanInfo(p.getUniqueId());
        if(baninf == null) {
            e.setQuitMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[join/leave] " + ChatColor.RESET + MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.AQUA + " ist Milch holen gegangen!");
            lp.getUserManager().saveUser(Objects.requireNonNull(lp.getUserManager().getUser(p.getUniqueId())));
        } else {
            e.setQuitMessage("");
        }
    }
    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        Player p = e.getPlayer();
        lp.getUserManager().saveUser(Objects.requireNonNull(lp.getUserManager().getUser(p.getUniqueId())));
    }
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        ArrayList<String> muteinf = DB.getPlayerMuteInfo(p.getUniqueId());
        long mutetime;

        if(muteinf != null) {
            boolean tempmute = Boolean.parseBoolean(muteinf.get(0));
            if(tempmute) {
                String timestring = muteinf.get(1);
                if(timestring == null) {
                    mutetime = 0;
                } else {
                    mutetime = Long.parseLong(timestring);
                }
                Calendar cl = Calendar.getInstance();
                Calendar now = Calendar.getInstance();

                now.setTime(new Date());
                cl.setTimeInMillis(mutetime);

                long timeleft = mutetime-now.getTimeInMillis();

                if(timeleft > 0) {
                    LocalDateTime ldt = LocalDateTime.ofInstant(cl.toInstant(), cl.getTimeZone().toZoneId());
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                    String unmutedate = ldt.format(dtf);

                    String mutemsg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dir wurde für " + ChatColor.BOLD + unmutedate + ChatColor.RESET + ChatColor.DARK_RED + " das Rederecht genommen!";
                    p.sendMessage(mutemsg);
                } else {
                    DB.setDBPlayerMute(false, false, null, p.getUniqueId());
                }
            } else {
                String mutemsg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Du hast kein Recht zu reden!";
                p.sendMessage(mutemsg);
            }
        } else {
            String msg = e.getMessage();
            msg = func.colCodes(msg);
            msg = MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.GOLD + ChatColor.BOLD + " sagt: " + ChatColor.RESET + ChatColor.GRAY + msg;
            Bukkit.broadcastMessage(msg);
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity().getPlayer();
        Player k = e.getEntity().getKiller();
        String msg = e.getDeathMessage();
        msg = Objects.requireNonNull(msg).replaceAll(Objects.requireNonNull(p).getName(), MXE.getPlayerPrefix(p) + p.getDisplayName()+ChatColor.RESET+ChatColor.GRAY);
        if(k != null) {
            msg = msg.replaceAll(k.getName(), MXE.getPlayerPrefix(k)+k.getDisplayName()+ChatColor.RESET+ChatColor.GRAY);
        }
        msg = ChatColor.GRAY + msg;
        e.setDeathMessage(msg);

        if(DB.getBackCoords(p.getUniqueId()) == null) {
            DB.addBackCoords(p.getUniqueId());
        } else {
            DB.setBackCoords(p.getUniqueId());
        }
        msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Info: Kehre mit " + ChatColor.YELLOW + "/back" + ChatColor.GOLD + " wieder an deinen Totespunkt zurück!";
        p.sendMessage(msg);
    }

}

package me.mgsmemebook.mxe.db;

import me.mgsmemebook.mxe.func;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.lang.String.valueOf;
import static me.mgsmemebook.mxe.db.SQLite.getSQLConnection;

public class DB {
    public static String getDBVar(String table, String wanted, String wherevar, String whereval) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection(table);
            String sql = "SELECT * FROM "+table+" WHERE `"+wherevar+"` = ?";
            ps = Objects.requireNonNull(conn).prepareStatement(sql);
            ps.setString(1, whereval);
            rs = ps.executeQuery();
            return rs.getString(wanted);
        } catch (SQLException ex) {
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't retrieve table data");
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            }
        }
        return null;
    }
    public static void setDBPlayerMute(boolean muted, boolean tempmute, String mutetime, UUID uuid) {
        if(Bukkit.getPlayer(uuid) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB addPlayer] Player not found.");
        } else {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = getSQLConnection("users");
                String sql = "UPDATE users SET muted = ?, tempmute = ?, mutetime = ? WHERE UUID = ?";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setBoolean(1, muted);
                ps.setBoolean(2, tempmute);
                ps.setString(3, mutetime);
                ps.setString(4, uuid.toString());
                ps.execute();
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't retrieve table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
    }

    public static void addDBPlayer(UUID uuid, String username) {
        if(Bukkit.getPlayer(uuid) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB addPlayer] Player " + username + " not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            try {
                conn = getSQLConnection("users");
                String sql = "INSERT INTO users (UUID, username, muted, banned) VALUES (?, ?, ?, ?)";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setString(1, uuid.toString());
                ps.setString(2, username);
                ps.setBoolean(3, false);
                ps.setBoolean(4, false);
                ps.execute();
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't insert into database");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
    }
    public static String getDBPlayer(UUID uuid) {
        if(Bukkit.getPlayer(uuid) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB getPlayer] Player not found.");
        } else {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = getSQLConnection("users");
                String sql = "SELECT * FROM users WHERE UUID = ?";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setString(1, uuid.toString());
                rs = ps.executeQuery();
                return rs.getString("username");
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't retrieve table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
        return null;
    }
    public static String getPlayerUUID(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection("users");
            String sql = "SELECT UUID FROM users WHERE username = ?";
            ps = Objects.requireNonNull(conn).prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            return rs.getString("UUID");
        } catch (SQLException ex) {
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't retrieve table data");
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            }
        }
        return null;
    }
    public static void banDBPlayer(UUID uuid, boolean tempban, String bantime, String reason) {
        Player p = Bukkit.getPlayer(uuid);
        if(p == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB addPlayer] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            try {
                conn = getSQLConnection("users");
                String sql = "UPDATE users SET banned = ?, tempban = ?, bantime = ?, reason = ? WHERE UUID = ?";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setBoolean(1, true);
                ps.setBoolean(2, tempban);
                ps.setString(3, bantime);
                ps.setString(4, reason);
                ps.setString(5, uuid.toString());
                ps.executeUpdate();
                func.cMSG(ChatColor.RED + "[MXE] Banned player: " + p.getName());
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't update database");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
    }

    public static void unbanDBPlayer(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if(p == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB unbanDBPlayer] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            try {
                conn = getSQLConnection("users");
                String sql = "UPDATE users SET banned = ?, tempban = ?, bantime = ?, reason = ? WHERE UUID = ?";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setBoolean(1, false);
                ps.setBoolean(2, false);
                ps.setString(3, "");
                ps.setString(4, "");
                ps.setString(5, uuid.toString());
                ps.executeUpdate();
                func.cMSG(ChatColor.RED + "[MXE] Unbanned player: " + p.getName());
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't update database");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
    }

    public static ArrayList<String> getPlayerBanInfo(UUID uuid) {
        if(Bukkit.getPlayer(uuid) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB getPlayerBanInfo] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            ResultSet rs = null;
            try {
                conn = getSQLConnection("users");
                String sql = "SELECT * FROM users WHERE UUID = ?";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setString(1, uuid.toString());
                rs = ps.executeQuery();
                ArrayList<String> baninf = new ArrayList<>();

                if(rs.getBoolean("banned")) {
                    baninf.add(valueOf(rs.getBoolean("tempban")));
                    baninf.add(rs.getString("bantime"));
                    if(rs.getString("reason") != null) {
                        baninf.add(rs.getString("reason"));
                    }
                    return baninf;
                } else {
                    return null;
                }
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't retrieve table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
        return null;
    }

    public static ArrayList<String> getPlayerMuteInfo(UUID uuid) {
        if(Bukkit.getPlayer(uuid) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB getPlayerMuteInfo] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            ResultSet rs = null;
            try {
                conn = getSQLConnection("users");
                String sql = "SELECT * FROM users WHERE UUID = ?";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setString(1, uuid.toString());
                rs = ps.executeQuery();
                ArrayList<String> baninf = new ArrayList<>();

                if(rs.getBoolean("muted")) {
                    baninf.add(valueOf(rs.getBoolean("tempmute")));
                    baninf.add(rs.getString("mutetime"));
                    return baninf;
                } else {
                    return null;
                }
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't retrieve table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
        return null;
    }
    public static ArrayList<Integer> getPlayerTpas(String username) {
        if(Bukkit.getPlayer(username) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB getPlayerTpa] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            ResultSet rs = null;
            try {
                conn = getSQLConnection("users");
                String sql = "SELECT * FROM tpa WHERE `to` = ?";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setString(1, username);
                rs = ps.executeQuery();
                ArrayList<Integer> query = new ArrayList<>();

                while(rs.next()) {
                    query.add(rs.getInt("id"));
                }
                return query;
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't retrieve table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
        return null;
    }
    public static ArrayList<String> getPlayerTpa(int id) {
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection("users");
            String sql = "SELECT * FROM tpa WHERE `id` = ?";
            ps = Objects.requireNonNull(conn).prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            ArrayList<String> query = new ArrayList<>();

            query.add(rs.getString("from"));
            query.add(rs.getString("to"));
            query.add(rs.getString("time"));
            query.add(valueOf(rs.getBoolean("tpahere")));
            query.add(valueOf(rs.getInt("id")));
            return query;
        } catch (SQLException ex) {
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't retrieve table data");
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            }
        }
        return null;
    }
    public static void remPlayerTpa(Integer id) {
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getSQLConnection("users");
            String sql;
            sql = "DELETE FROM tpa WHERE `id` = ?";
            ps = Objects.requireNonNull(conn).prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        } catch (SQLException ex) {
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't remove table data");
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            }
        }
    }
    public static void setPlayerTpa(String from, String to, boolean tpahere) {
        if(Bukkit.getPlayer(from) == null || Bukkit.getPlayer(to) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB setPlayerTpa] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            Calendar cl = Calendar.getInstance();
            cl.setTime(new Date());
            try {
                conn = getSQLConnection("users");
                String sql = "INSERT INTO tpa (`from`, `to`, `time`, `tpahere`) VALUES (?, ?, ?, ?)";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setString(1, from);
                ps.setString(2, to);
                ps.setString(3, valueOf(cl.getTimeInMillis()));
                ps.setBoolean(4, tpahere);
                ps.execute();
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't insert table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
    }
    public static Location getPlayerHome(UUID uuid, String home) {
        if(Bukkit.getPlayer(uuid) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB getPlayerHome] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            ResultSet rs = null;
            try {
                conn = getSQLConnection("users");
                String sql = "SELECT * FROM homes WHERE UUID = ? AND `home` = '"+home+"'";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setString(1, uuid.toString());
                rs = ps.executeQuery();

                return new Location(Bukkit.getWorld(rs.getString("world")), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"));
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't retrieve table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
        return null;
    }
    public static ArrayList<String> getPlayerHomes(UUID uuid) {
        if(Bukkit.getPlayer(uuid) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB getPlayerMuteInfo] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            ResultSet rs = null;
            try {
                conn = getSQLConnection("users");
                String sql = "SELECT home FROM homes WHERE UUID = ?";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setString(1, uuid.toString());
                rs = ps.executeQuery();
                ArrayList<String> homes = new ArrayList<>();
                while(rs.next()) {
                    homes.add(rs.getString("home"));
                }
                return homes;
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't retrieve table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
        return null;
    }
    public static boolean addPlayerHome(UUID uuid, String home, Location loc) {
        if(Bukkit.getPlayer(uuid) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB addPlayerHome] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            try {
                conn = getSQLConnection("users");
                String sql = "INSERT INTO homes (UUID, home, x, y, z, yaw, world) VALUES (?, ?, ?, ?, ?, ?, ?)";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setString(1, uuid.toString());
                ps.setString(2, home);
                ps.setDouble(3, loc.getBlockX());
                ps.setDouble(4, loc.getBlockY());
                ps.setDouble(5, loc.getBlockZ());
                ps.setDouble(6, loc.getYaw());
                ps.setString(7, Objects.requireNonNull(loc.getWorld()).getName());
                ps.execute();
                return true;
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't insert table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
        return true;
    }
    public static boolean renamePlayerHome(UUID uuid, String oldhome, String newhome) {
        if(Bukkit.getPlayer(uuid) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB renamePlayerHome] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            try {
                conn = getSQLConnection("users");
                String sql = "UPDATE homes SET home = ? WHERE UUID = ? AND home = ?";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setString(1, newhome);
                ps.setString(2, uuid.toString());
                ps.setString(3, oldhome);
                ps.executeUpdate();
                return true;
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't insert table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
        return false;
    }
    public static boolean remPlayerHome(UUID uuid, String home) {
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getSQLConnection("users");
            String sql;
            sql = "DELETE FROM homes WHERE UUID = ? AND home = ?";
            ps = Objects.requireNonNull(conn).prepareStatement(sql);
            ps.setString(1, uuid.toString());
            ps.setString(2, home);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't remove table data");
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            }
        }
        return false;
    }
    public static boolean setBackCoords(UUID uuid) {
        if(Bukkit.getPlayer(uuid) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB renamePlayerHome] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            try {
                Player p = Bukkit.getPlayer(uuid);
                Location loc = Objects.requireNonNull(p).getLocation();
                conn = getSQLConnection("users");
                String sql = "UPDATE back SET x = ?, y = ?, z = ?, yaw = ?, world = ? WHERE UUID = ?";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setDouble(1, loc.getBlockX());
                ps.setDouble(2, loc.getBlockY());
                ps.setDouble(3, loc.getBlockZ());
                ps.setDouble(4, loc.getYaw());
                ps.setString(5, Objects.requireNonNull(loc.getWorld()).getName());
                ps.setString(6, uuid.toString());
                ps.executeUpdate();
                return true;
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't insert table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
        return false;
    }
    public static boolean delBackCoords(UUID uuid) {
        if(Bukkit.getPlayer(uuid) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB renamePlayerHome] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            try {
                conn = getSQLConnection("users");
                String sql = "DELETE FROM back WHERE UUID = ?";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setString(1, uuid.toString());
                ps.execute();
                return true;
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't delete table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
        return false;
    }
    public static Location getBackCoords(UUID uuid) {
        if(Bukkit.getPlayer(uuid) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB renamePlayerHome] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            ResultSet rs = null;
            try {
                conn = getSQLConnection("users");
                String sql = "SELECT * FROM back WHERE UUID = ?";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setString(1, uuid.toString());
                rs = ps.executeQuery();

                return new Location(Bukkit.getWorld(rs.getString("world")), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"));
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't recieve table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
        return null;
    }
    public static void addBackCoords(UUID uuid) {
        if(Bukkit.getPlayer(uuid) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB renamePlayerHome] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            try {
                Location loc = Objects.requireNonNull(Bukkit.getPlayer(uuid)).getLocation();
                conn = getSQLConnection("users");
                String sql = "INSERT INTO back (UUID, x, y, z, yaw, world) VALUES (?, ?, ?, ?, ?, ?)";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setString(1, uuid.toString());
                ps.setDouble(2, loc.getBlockX());
                ps.setDouble(3, loc.getBlockY());
                ps.setDouble(4, loc.getBlockZ());
                ps.setDouble(5, loc.getYaw());
                ps.setString(6, Objects.requireNonNull(loc.getWorld()).getName());
                ps.execute();

            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't recieve table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
    }
}

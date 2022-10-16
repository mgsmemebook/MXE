package me.mgsmemebook.mxe.db;
import me.mgsmemebook.mxe.MXE;
import me.mgsmemebook.mxe.func;
import org.bukkit.ChatColor;
import java.sql.*;

public abstract class DB {
    private static String connurl = "jdbc:h2:" + MXE.getPlDir() + "/data/database";

    MXE plugin;
    Connection conn;
    public String table = "users";
    public int tokens = 0;
    public DB(MXE instance) {
        plugin = instance;
    }

    public abstract Connection getSQLConnection();
    public abstract void load();

    public void initialize() {
        conn = getSQLConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE UUID = ?");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
        } catch (SQLException ex) {
            func.cMSG(ChatColor.DARK_RED + "SQL error: Feler beim Herstellen der Verbindung");
            func.cMSG(ChatColor.DARK_RED + "SQL error: " + ex.getMessage());
        }
    }
    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if(ps != null) {
                ps.close();
            } if(rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            func.cMSG(ChatColor.DARK_RED + "SQL error: Fehler beim Schließen der Datenbank-Verbindung");
            func.cMSG(ChatColor.DARK_RED + "SQL error: " + ex.getMessage());
        }
    }
}

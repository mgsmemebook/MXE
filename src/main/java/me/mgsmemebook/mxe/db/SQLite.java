package me.mgsmemebook.mxe.db;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;

import me.mgsmemebook.mxe.MXE;
import me.mgsmemebook.mxe.func;
import org.bukkit.ChatColor;

public class SQLite {

    public static Connection conn = null;
    public static String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS users (" + // make sure to put your table name in here too.
            "`UUID` varchar(32) NOT NULL," +
            "`username` varchar(32) NOT NULL," +
            "`banned` BOOL NOT NULL," +
            "`tempban` BOOL," +
            "`bantime` varchar(512)," +
            "`reason` varchar(512)," +
            "PRIMARY KEY (`UUID`)" +  // This is creating 3 colums Player, Kills, Total. Primary key is what you are going to use as your indexer. Here we want to use player so
            ");"; // we can search by player, and get kills and total. If you some how were searching kills it would provide total and player.

    public static Connection getSQLConnection() {
        File dataFolder = new File(MXE.getPlDir(), "users.db");
        if(!dataFolder.exists()) {
            try {
                if(!dataFolder.createNewFile()) {
                    func.cMSG(ChatColor.RED + "[MXE] Couldn't create database file");
                }
            } catch (IOException ex) {
                func.cMSG(ChatColor.RED + "File write error " + ex.getMessage());
            }
        }
        try {
            if(conn != null && !conn.isClosed()) {
                return conn;
            }
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return conn;
        } catch (SQLException ex) {
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't initialize SQLite");
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: SQLite JBDC Library not found. (insert into /plugins/MXE/lib/)");
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
        }
        return null;
    }

    public static void load() {
        try {
            conn = getSQLConnection();
            Statement s = Objects.requireNonNull(conn).createStatement();
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //initialize();
    }
}

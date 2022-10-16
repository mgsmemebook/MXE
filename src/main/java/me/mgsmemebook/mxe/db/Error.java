package me.mgsmemebook.mxe.db;

import me.mgsmemebook.mxe.MXE;
import me.mgsmemebook.mxe.func;
import org.bukkit.ChatColor;

public class Error {
    public static void execute(MXE plugin, Exception ex) {
        func.cMSG(ChatColor.DARK_RED + "SQL error: Fehler beim Ausführen des MySQL Statements");
        func.cMSG(ChatColor.DARK_RED + "SQL error: " + ex.getMessage());
    }
    public static void close(MXE plugin, Exception ex) {
        func.cMSG(ChatColor.DARK_RED + "SQL error: Fehler beim Schließen der Datenbank-Verbindung");
        func.cMSG(ChatColor.DARK_RED + "SQL error: " + ex.getMessage());
    }
}

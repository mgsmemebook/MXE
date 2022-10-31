package me.mgsmemebook.mxe;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Nametag {
    private static Plugin plugin = MXE.getPlugin();
    private static final Map<Player, String> fakeNames = new WeakHashMap<>();

    public static void NameChanger() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if(event.getPacket().getPlayerInfoAction().read(0) != EnumWrappers.PlayerInfoAction.ADD_PLAYER) return;
                List<PlayerInfoData> newPlayerInfoDataList = new ArrayList<>();
                List<PlayerInfoData> playerInfoDataList = event.getPacket().getPlayerInfoDataLists().read(0);
                for(PlayerInfoData playerInfoData:playerInfoDataList) {
                    if(playerInfoData == null || playerInfoData.getProfile() == null || Bukkit.getPlayer(playerInfoData.getProfile().getUUID()) == null) {
                        newPlayerInfoDataList.add(playerInfoData);
                        continue;
                    }
                    WrappedGameProfile profile = playerInfoData.getProfile();
                    profile = profile.withName(getNameToSend(profile.getUUID()));
                    PlayerInfoData newPlayerInfoData = new PlayerInfoData(profile, playerInfoData.getLatency(), playerInfoData.getGameMode(), playerInfoData.getDisplayName());
                    newPlayerInfoDataList.add(newPlayerInfoData);
                }
                event.getPacket().getPlayerInfoDataLists().write(0, newPlayerInfoDataList);
            }
        });
    }

    private static String getNameToSend(UUID id) {
        Player player = Bukkit.getPlayer(id);
        if(!fakeNames.containsKey(player)) return player.getName();
        return fakeNames.get(player);
    }

    /**
     * Change the player's name to the provided string
     * <br>
     * The player may disappear for approximately 2 ticks after you change it
     * </br>
     * @param player player whos name to change
     * @param fakeName the player's new name
     */
    public static void setName(final Player player, String fakeName) {
        fakeNames.put(player, fakeName);
        refresh(player);
        func.cMSG("fakeNames test: " + fakeNames);
    }

    /**
     * Reset the player's name to it's original value
     * <br>
     * The player may disappear for approximately 2 ticks after you change it
     * </br>
     * @param player player whos name to change back to the original value
     */
    public static void resetName(Player player) {
        if(fakeNames.containsKey(player)) {
            fakeNames.remove(player);
        }
        refresh(player);
    }

    private static void refresh(final Player player) {
        for (final Player forWhom : player.getWorld().getPlayers()) {
            forWhom.hidePlayer(plugin, player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    forWhom.showPlayer(plugin, player);
                }
            }.runTaskLater(plugin, 2);
        }
    }
}

package me.faintcloudy.bedwars.utils;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionBarUtil {

    public static void sendActionBar(Player player, String message)
    {
        if (!player.isOnline())
            return;
        message = ChatColor.translateAlternateColorCodes('&', message);
        PlayerConnection connection = ((CraftPlayer) player)
                .getHandle().playerConnection;
        IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer
                .a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(icbc, (byte) 2);
        connection.sendPacket(ppoc);
    }
}

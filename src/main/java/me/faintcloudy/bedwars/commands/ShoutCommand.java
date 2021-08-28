package me.faintcloudy.bedwars.commands;

import com.sun.scenario.effect.impl.prism.ps.PPSBlend_ADDPeer;
import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.GameState;
import me.faintcloudy.bedwars.utils.command.BukkitCommand;
import me.faintcloudy.bedwars.utils.command.SenderType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class ShoutCommand extends BukkitCommand {
    public ShoutCommand() {
        super("shout", ShoutCommand.class, false);
        this.senderType = SenderType.PLAYER;
        new BukkitRunnable()
        {
            public void run()
            {
                colddown.forEach((c, v) -> colddown.put(c, v-1));
            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 20);
    }
    HashMap<GamePlayer, Integer> colddown = new HashMap<>();

    @Override
    public void onMain(CommandSender sender, String[] args) {
        if (Bedwars.getInstance().game.state != GameState.GAMING)
        {
            sender.sendMessage("§c你不能在这个时候使用该指令");
            return;
        }
        GamePlayer player = GamePlayer.get((Player) sender);
        if (player.state != GamePlayer.PlayerState.ALIVE)
        {
            player.player.sendMessage("§c你不能在这个时候使用该指令");
            return;
        }

        if (args.length < 1)
        {
            player.player.sendMessage("§c正确用法: /shout <信息>");
            return;
        }



        if (colddown.get(player) != null && colddown.get(player) > 0)
        {
            if (player.player.isOp() || player.player.hasPermission("bw.admin"))
            {
                player.player.sendMessage("§c已自动为您跳过喊话冷却, 喵~");
            }
            else {
                player.player.sendMessage("§c还有 " + colddown.get(player) + " 秒你才能使用该指令");
                return;
            }
        }


        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg);
        }

        colddown.put(player, 30);

        Bukkit.broadcastMessage("§6[喊话] " + player.getTeam().color.chatColor + "[" + player.getTeam().color.cn + "] " + player.getPrefixedName() + "§f: "  + message);
    }
}

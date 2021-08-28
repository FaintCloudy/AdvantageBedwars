package me.faintcloudy.bedwars.listener;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.database.SQLManager;
import me.faintcloudy.bedwars.events.GameEndedEvent;
import me.faintcloudy.bedwars.events.GameStartedEvent;
import me.faintcloudy.bedwars.events.player.PlayerGotKilledEvent;
import me.faintcloudy.bedwars.events.player.PlayerSpawnEvent;
import me.faintcloudy.bedwars.events.team.TeamBedBrokeEvent;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.stats.RewardManager;
import me.faintcloudy.bedwars.stats.RewardReason;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RewardListener implements Listener {

    @EventHandler
    public void onKill(PlayerGotKilledEvent event)
    {
        reward(event.killer, event.isFinal ? RewardReason.FINAL_KILL : RewardReason.KILL);
    }


    @EventHandler
    public void gameEnd(GameEndedEvent event)
    {
        if (event.winner != null)
        {
            for (GamePlayer player : event.winner.players) {
                reward(player, RewardReason.WIN_A_GAME);
            }
            for (GamePlayer player : GamePlayer.getOnlineGamePlayers()) {
                reward(player, RewardReason.PLAY_A_GAME);
            }
        }
    }

    public List<GamePlayer> pickedUpDiamondPlayers = new ArrayList<>();
    public List<GamePlayer> pickedUpEmeraldPlayers = new ArrayList<>();
    @EventHandler
    public void onPickup(PlayerPickupItemEvent event)
    {
        if (event.isCancelled())
            return;
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (event.getItem().getItemStack().getType() == Material.DIAMOND)
        {
            if (!pickedUpDiamondPlayers.contains(player))
            {
                reward(player, RewardReason.PICKUP_DIAMOND);
                pickedUpDiamondPlayers.add(player);
            }
        } else if (event.getItem().getItemStack().getType() == Material.EMERALD)
        {
            if (!pickedUpEmeraldPlayers.contains(player))
            {
                reward(player, RewardReason.PICKUP_EMERALD);
                pickedUpEmeraldPlayers.add(player);
            }
        }
    }

    @EventHandler
    public void onBreakBed(TeamBedBrokeEvent event)
    {
        if (event.broker != null)
        {
            reward(event.broker, RewardReason.BREAK_BED);
        }
    }

    public void reward(GamePlayer player, RewardReason reason)
    {
        RewardManager manager = Bedwars.getInstance().rewardManager;
        if (manager.getRewardCoins(reason) != 0)
            rewardCoins(player, manager.getRewardCoins(reason), reason);
        if (manager.getRewardExperience(reason) != 0)
            rewardExp(player, manager.getRewardExperience(reason), reason);
    }


    public void rewardCoins(GamePlayer player, int coins, RewardReason reason)
    {
        new BukkitRunnable()
        {
            public void run()
            {
                player.data.add(SQLManager.FIELD.COINS, coins);
                player.player.sendMessage("§6+" + coins + "硬币 (" + reason.display + ")");
                player.player.sendMessage("§6当前硬币: " + Integer.parseInt(player.data.get(SQLManager.FIELD.COINS)));
            }
        }.runTask(Bedwars.getInstance());

    }

    public void rewardExp(GamePlayer player, int exp, RewardReason reason)
    {
        new BukkitRunnable()
        {
            public void run()
            {
                player.data.add(SQLManager.FIELD.EXP, exp);
                player.player.sendMessage(ChatColor.DARK_AQUA + "+" + exp + "经验 (" + reason.display + ")");
                player.player.sendMessage(ChatColor.DARK_AQUA + "当前经验: " + Integer.parseInt(player.data.get(SQLManager.FIELD.EXP)));
            }
        }.runTask(Bedwars.getInstance());

    }
}

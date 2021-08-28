package me.faintcloudy.bedwars.game.team.upgrade;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.GameState;
import me.faintcloudy.bedwars.game.team.Team;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.Bed;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HealPoolUpgrade implements TeamUpgrade {

    public HealPoolUpgrade()
    {
        new BukkitRunnable()
        {
            final PotionEffect effect = new PotionEffect(PotionEffectType.REGENERATION, 60, 0);
            public void run()
            {
                if (Bedwars.getInstance().game == null)
                    return;
                for (Team team : Bedwars.getInstance().game.teams)
                {
                    if (team.upgradeLevels.get(HealPoolUpgrade.this) < 1)
                        continue;
                    for (GamePlayer player : team.players)
                    {
                        if (player.getTeam().isNearBase(player))
                            continue;
                        if (player.player.hasPotionEffect(PotionEffectType.REGENERATION))
                            continue;
                        player.player.addPotionEffect(effect, false);
                    }
                }
            }


        }.runTaskTimer(Bedwars.getInstance(), 0, 20L);
    }
    @Override
    public int maxLevel() {
        return 1;
    }

    @Override
    public void onUpgrade(Team team) {

    }

}

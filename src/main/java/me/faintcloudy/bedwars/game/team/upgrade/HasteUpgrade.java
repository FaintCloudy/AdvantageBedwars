package me.faintcloudy.bedwars.game.team.upgrade;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.team.Team;
import org.bukkit.event.EventHandler;
import org.bukkit.material.Bed;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HasteUpgrade implements TeamUpgrade {
    public HasteUpgrade()
    {
        new BukkitRunnable()
        {
            public void run()
            {
                if (Bedwars.getInstance().game == null)
                    return;
                for (Team team : Bedwars.getInstance().game.teams)
                {
                    if (team.upgradeLevels.get(HasteUpgrade.this) < 1)
                        continue;
                    for (GamePlayer player : team.players)
                    {
                        if (player.player.hasPotionEffect(PotionEffectType.FAST_DIGGING))
                            continue;
                        player.player.addPotionEffect(HasteUpgrade.hasteEffect(team.upgradeLevels.get(HasteUpgrade.this)), false);
                    }
                }
            }


        }.runTaskTimer(Bedwars.getInstance(), 0, 20L);
    }
    @Override
    public int maxLevel() {
        return 2;
    }

    static PotionEffect hasteEffect(int level)
    {
        if (level == 0)
            return new PotionEffect(PotionEffectType.NIGHT_VISION, 1, 0);
        return new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, level-1);
    }

    @Override
    public void onUpgrade(Team team) {

    }
}

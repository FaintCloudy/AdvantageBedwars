package me.faintcloudy.bedwars.game.team.upgrade;

import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.team.Team;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItsTrapUpgrade extends TrapUpgrade {
    @Override
    public void onIntrude(GamePlayer intruder, Team team) {
        PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 60, 0, false, false);
        PotionEffect blind = new PotionEffect(PotionEffectType.BLINDNESS, 120, 0, false, false);
        intruder.player.addPotionEffect(slow);
        intruder.player.addPotionEffect(blind);
        for (GamePlayer member : team.getAlive())
        {
            member.sendTitle("§c§l陷阱触发!", intruder.getTeam().color.chatColor + intruder.getTeam().color.cn + " §f这是个陷阱触发了!", 0, 40, 0);
            member.sendMessage("§c§l这是个陷阱触发了!");

        }
    }
}

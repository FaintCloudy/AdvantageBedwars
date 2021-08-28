package me.faintcloudy.bedwars.game.team.upgrade;

import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.team.Team;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CounterAttackTrapUpgrade extends TrapUpgrade {
    @Override
    public void onIntrude(GamePlayer intruder, Team team) {
        PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 200, 0, false, false);
        PotionEffect jump = new PotionEffect(PotionEffectType.JUMP, 200, 1, false, false);
        for (GamePlayer member : team.getAlive())
        {
            member.player.addPotionEffect(speed);
            member.player.addPotionEffect(jump);
            member.sendTitle("§c§l陷阱触发!", intruder.getTeam().color.chatColor + intruder.getTeam().color.cn + " §f这是个陷阱触发了!", 0, 40, 0);
            member.sendMessage("§c§l这是个陷阱触发了!");

        }
    }
}


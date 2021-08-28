package me.faintcloudy.bedwars.game.team.upgrade;

import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.team.Team;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AlarmTrapUpgrade extends TrapUpgrade {
    @Override
    public void onIntrude(GamePlayer intruder, Team team) {
        intruder.player.removePotionEffect(PotionEffectType.INVISIBILITY);
        for (GamePlayer member : team.getAlive())
        {
            member.sendTitle("§c§l警报!!!", intruder.getTeam().color.chatColor + intruder.getTeam().color.cn + " §f触发了警报陷阱!", 0, 40, 0);
            member.sendMessage("§c§l这是个陷阱触发了!");
            member.player.sendMessage(intruder.getTeam().color.chatColor + intruder.getTeam().color.cn + "§c§l玩家" + intruder.getColoredName() + "§c§l触发了报警陷阱!");

        }
    }
}

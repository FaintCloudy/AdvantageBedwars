package me.faintcloudy.bedwars.game.team.upgrade;

import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.team.Team;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MinerFatigueTrapUpgrade extends TrapUpgrade {
    @Override
    public void onIntrude(GamePlayer intruder, Team team) {
        for (GamePlayer member : team.getAlive())
        {
            member.sendTitle("§c§l陷阱触发!", intruder.getTeam().color.chatColor + intruder.getTeam().color.cn + "§f挖掘疲劳陷阱 触发了!", 0, 40, 0);
            member.player.sendMessage("§c§l挖掘疲劳陷阱触发了!");
        }
        intruder.player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 0, false, false));
    }
}

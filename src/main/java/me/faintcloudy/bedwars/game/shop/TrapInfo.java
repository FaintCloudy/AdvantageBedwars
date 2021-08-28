package me.faintcloudy.bedwars.game.shop;

import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.team.Team;
import me.faintcloudy.bedwars.game.team.upgrade.TeamUpgrade;
import me.faintcloudy.bedwars.game.team.upgrade.TrapUpgrade;
import org.bukkit.entity.Player;

public class TrapInfo {
    private TrapInfo()
    {

    }

    public static TrapInfo newInstance(GamePlayer player, TrapUpgrade upgrade)
    {
        TrapInfo info = new TrapInfo();
        info.player = player;
        info.upgrade = upgrade;
        return info;
    }

    public GamePlayer player;
    public TrapUpgrade upgrade;
}

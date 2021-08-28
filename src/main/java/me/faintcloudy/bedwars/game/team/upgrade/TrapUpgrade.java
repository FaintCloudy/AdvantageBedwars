package me.faintcloudy.bedwars.game.team.upgrade;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.GameState;
import me.faintcloudy.bedwars.game.team.Team;
import net.minecraft.server.v1_8_R3.PacketPlayInKeepAlive;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public abstract class TrapUpgrade implements TeamUpgrade {
    @Override
    public int maxLevel() {
        return 1;
    }

    @Override
    public void onUpgrade(Team team) {

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        if (Bedwars.getInstance().game.state != GameState.GAMING)
            return;

        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.milkTime)
            return;
        for (Team team : Bedwars.getInstance().game.teams)
        {
            if (team.upgradeLevels.get(this) < 1)
                continue;

            if (this.intruded(player, team))
            {
                this.onIntrude(player, team);
                team.upgradeLevels.put(this, 0);
            }
        }
    }

    private boolean intruded(GamePlayer intruder, Team team)
    {
        return team.isNearBase(intruder) && intruder.getTeam() != team;
    }

    public abstract void onIntrude(GamePlayer intruder, Team teamBeen);
}

package me.faintcloudy.bedwars.game.team.upgrade;

import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.team.Team;

public class ProtectionUpgrade implements TeamUpgrade {
    @Override
    public void onUpgrade(Team team) {
        for (GamePlayer player : team.players)
        {
            if (player.state == GamePlayer.PlayerState.ALIVE)
            {

                player.armor.set(player);
            }
        }
    }

    @Override
    public int maxLevel() {
        return 4;
    }
}

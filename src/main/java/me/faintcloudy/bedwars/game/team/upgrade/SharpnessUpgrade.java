package me.faintcloudy.bedwars.game.team.upgrade;

import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.team.Team;

public class SharpnessUpgrade implements TeamUpgrade {
    @Override
    public int maxLevel() {
        return 1;
    }

    @Override
    public void onUpgrade(Team team) {
        for (GamePlayer player : team.players)
        {
            if (player.state == GamePlayer.PlayerState.ALIVE)
            {
                player.checkSharpness();
            }
        }
    }
}

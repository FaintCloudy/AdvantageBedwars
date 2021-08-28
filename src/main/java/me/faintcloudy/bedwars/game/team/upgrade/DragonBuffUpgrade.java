package me.faintcloudy.bedwars.game.team.upgrade;

import me.faintcloudy.bedwars.game.team.Team;

public class DragonBuffUpgrade implements TeamUpgrade {
    @Override
    public int maxLevel() {
        return 1;
    }

    @Override
    public void onUpgrade(Team team) {

    }
}

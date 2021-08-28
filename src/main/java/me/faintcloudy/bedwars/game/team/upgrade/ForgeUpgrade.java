package me.faintcloudy.bedwars.game.team.upgrade;

import me.faintcloudy.bedwars.game.team.Team;

public class ForgeUpgrade implements TeamUpgrade {
    @Override
    public int maxLevel() {
        return 4;
    }

    @Override
    public void onUpgrade(Team team) {

    }
}

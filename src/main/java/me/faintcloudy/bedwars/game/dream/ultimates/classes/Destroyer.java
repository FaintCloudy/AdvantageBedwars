package me.faintcloudy.bedwars.game.dream.ultimates.classes;

import me.faintcloudy.bedwars.game.GamePlayer;

public class Destroyer implements UltimateClass {
    @Override
    public String displayName() {
        return "破坏者";
    }

    @Override
    public void init(GamePlayer player) {

    }

    @Override
    public int colddown() {
        return 15;
    }
}

package me.faintcloudy.bedwars.game;

import me.faintcloudy.bedwars.game.dream.DreamManager;
import me.faintcloudy.bedwars.game.dream.gun.GunModeManager;
import me.faintcloudy.bedwars.game.dream.luckyblock.LuckyBlockModeManager;
import me.faintcloudy.bedwars.game.dream.megawalls.MegaWallsModeManager;
import me.faintcloudy.bedwars.game.dream.rush.RushModeManager;
import me.faintcloudy.bedwars.game.dream.ultimates.UltimatesModeManager;

public enum BedwarsMode {
    NORMAL(new DreamManager() {
        @Override
        public void init() {

        }

        @Override
        public String[] startMessage() {
            return new String[]
                    {
                            "保护你的床并摧毁敌人的床。收集铁锭，金锭，绿宝石和钻石",
                            "来升级，使自身和队伍变得更强。"
                    };
        }
    }, "普通模式"), RUSH(new RushModeManager(), "极速模式"), LUCKY_BLOCK(new LuckyBlockModeManager(), "幸运方块模式"),
    MEGA_WALLS(new MegaWallsModeManager(), "超级战墙模式"), ULTIMATES(new UltimatesModeManager(), "超能力模式"), GUN(new GunModeManager(), "枪械模式");

    public DreamManager manager;
    public String cn;
    BedwarsMode(DreamManager dreamManager, String cn)
    {
        this.manager = dreamManager;
        this.cn = cn;
    }

    public static BedwarsMode of(String modeName)
    {
        for (BedwarsMode mode : values())
        {
            if (mode.name().equalsIgnoreCase(modeName))
                return mode;
        }

        return NORMAL;
    }
}

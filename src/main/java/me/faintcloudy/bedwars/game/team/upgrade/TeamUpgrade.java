package me.faintcloudy.bedwars.game.team.upgrade;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public interface TeamUpgrade extends Listener {
    int maxLevel();
    default boolean unlocked(Team team)
    {
        return team.upgradeLevels.get(this) >= maxLevel();
    }

    default void upgrade(Team team)
    {
        if (unlocked(team))
            return;
        int origin = team.upgradeLevels.get(this);
        team.upgradeLevels.put(this, origin + 1);
        this.onUpgrade(team);
    }

    void onUpgrade(Team team);


    static void registerUpgrades()
    {
        SHARPNESS.register();
        PROTECTION.register();
        HASTE.register();
        FORGE.register();
        HEAL_POOL.register();
        DRAGON_BUFF.register();
        ITS_TRAP.register();
        COUNTER_ATTACK_TRAP.register();
        ALARM_TRAP.register();
        MINER_FATIGUE_TRAP.register();
    }

    default void register()
    {
        Bukkit.getPluginManager().registerEvents(this, Bedwars.getInstance());
    }

    TeamUpgrade SHARPNESS = new SharpnessUpgrade();
    TeamUpgrade PROTECTION = new ProtectionUpgrade();
    TeamUpgrade HASTE = new HasteUpgrade();
    TeamUpgrade FORGE = new ForgeUpgrade();
    TeamUpgrade HEAL_POOL = new HealPoolUpgrade();
    TeamUpgrade DRAGON_BUFF = new DragonBuffUpgrade();
    TrapUpgrade ITS_TRAP = new ItsTrapUpgrade();
    TrapUpgrade COUNTER_ATTACK_TRAP = new CounterAttackTrapUpgrade();
    TrapUpgrade ALARM_TRAP = new AlarmTrapUpgrade();
    TrapUpgrade MINER_FATIGUE_TRAP = new MinerFatigueTrapUpgrade();


}

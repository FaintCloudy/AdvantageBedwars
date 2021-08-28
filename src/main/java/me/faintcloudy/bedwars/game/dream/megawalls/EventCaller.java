package me.faintcloudy.bedwars.game.dream.megawalls;

import me.faintcloudy.bedwars.events.player.PlayerAttackEvent;
import me.faintcloudy.bedwars.game.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EventCaller implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAttackEvent(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player)
        {
            if (event.isCancelled())
                return;
            GamePlayer solver = GamePlayer.get((Player) event.getEntity());
            GamePlayer damager = GamePlayer.get((Player) event.getDamager());

            if (damager.state != GamePlayer.PlayerState.ALIVE || solver.state != GamePlayer.PlayerState.ALIVE)
                return;
            Bukkit.getPluginManager().callEvent(new PlayerAttackEvent(solver, damager, PlayerAttackEvent.AttackType.HIT));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAttackEvent2(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player)
        {
            if (event.getDamager().hasMetadata("skill"))
            {
                event.setCancelled(true);
                return;
                //TODO
            }

            if (event.isCancelled())
                return;
            GamePlayer solver = GamePlayer.get((Player) event.getEntity());
            GamePlayer damager = GamePlayer.get((Player) ((Arrow) event.getDamager()).getShooter());

            if (damager.state != GamePlayer.PlayerState.ALIVE || solver.state != GamePlayer.PlayerState.ALIVE)
                return;
            Bukkit.getPluginManager().callEvent(new PlayerAttackEvent(solver, damager, PlayerAttackEvent.AttackType.BOW));
        }
    }
}

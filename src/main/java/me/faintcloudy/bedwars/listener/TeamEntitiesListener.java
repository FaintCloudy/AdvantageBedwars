package me.faintcloudy.bedwars.listener;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.team.Team;
import me.faintcloudy.bedwars.game.team.TeamColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

import java.util.StringJoiner;

public class TeamEntitiesListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTarget(EntityTargetEvent event)
    {
        if (event.getEntity() instanceof Player && event.getTarget() instanceof Player)
            return;

        if (event.getEntity().getMetadata("Team") == null)
        {
            event.setCancelled(true);
            event.setTarget(null);

            return;
        }

        if (!(event.getTarget() instanceof Player))
        {
            event.setCancelled(true);
            event.setTarget(null);

            return;
        }
        GamePlayer player = GamePlayer.get((Player) event.getTarget());

        if (player.state != GamePlayer.PlayerState.ALIVE)
        {
            event.setCancelled(true);
            event.setTarget(null);

        }

        if (event.getEntity().getMetadata("Team") == null || event.getEntity().getMetadata("Team").isEmpty())
            return;
        MetadataValue meta = event.getEntity().getMetadata("Team").get(0);
        if (meta.asString().isEmpty())
            return;

        TeamColor color = TeamColor.valueOf(meta.asString());
        if (player.getTeam().color == color)
        {
            event.setCancelled(true);
            event.setTarget(null);

        }


        
    }

    @EventHandler
    public void onEnderDragonDamage(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player)
            return;
        if (event.getEntity().getMetadata("Team") == null || event.getEntity().getMetadata("Team").isEmpty())
            return;
        if (!(event.getEntity() instanceof Player))
            return;

        GamePlayer player = GamePlayer.get((Player) event.getEntity());

        if (player.state != GamePlayer.PlayerState.ALIVE)
        {
            event.setCancelled(true);
            if (event.getEntity() instanceof Creature)
            {
                ((Creature) event.getEntity()).setTarget(null);
            }
            return;
        }

        if (event.getEntity().getMetadata("Team").isEmpty())
        {
            event.setCancelled(true);
            return;
        }

        MetadataValue meta = event.getEntity().getMetadata("Team").get(0);
        if (meta.asString().isEmpty())
            return;
        TeamColor color = TeamColor.valueOf(meta.asString());
        if (player.getTeam().color == color)
        {
            event.setCancelled(true);
            if (event.getEntity() instanceof Creature)
            {
                ((Creature) event.getEntity()).setTarget(null);
            }
        }
        else
        {
            if (event.getDamager() instanceof EnderDragon)
                player.player.setVelocity(event.getDamager().getVelocity().normalize().multiply(2));
        }

    }

    @EventHandler
    public void onPlayerDamageEntity(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player)
            return;
        if (event.getEntity().getMetadata("Team") == null)
            return;
        if (!(event.getDamager() instanceof Player))
            return;

        GamePlayer player = GamePlayer.get((Player) event.getDamager());

        if (player.state != GamePlayer.PlayerState.ALIVE)
        {
            event.setCancelled(true);
            return;
        }

        if (event.getEntity().getMetadata("Team").isEmpty())
        {
            event.setCancelled(true);
            return;
        }
        MetadataValue meta = event.getEntity().getMetadata("Team").get(0);
        if (meta.asString().isEmpty())
            return;
        TeamColor color = TeamColor.valueOf(meta.asString());
        if (player.getTeam().color == color)
        {
            event.setCancelled(true);
        }
        else
        {
            if (event.getEntity() instanceof EnderDragon)
                event.setDamage(1.0D);
        }

    }


}

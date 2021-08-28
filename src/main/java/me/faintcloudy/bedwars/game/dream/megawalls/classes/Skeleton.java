package me.faintcloudy.bedwars.game.dream.megawalls.classes;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.events.player.PlayerAttackEvent;
import me.faintcloudy.bedwars.game.GamePlayer;

import me.faintcloudy.bedwars.game.dream.megawalls.MegaWallsModeManager;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import me.faintcloudy.bedwars.utils.LocationUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class Skeleton extends MegaWallsClass implements Archer {
    SpeedColddownTask speedColddownTask;
    public Skeleton() {
        super(Material.BONE, "骷髅", ChatColor.AQUA, new ArmorSet() {
            @Override
            public ItemStack getWeapon() {
                return new ItemBuilder(Material.IRON_SWORD).setDisplayName(ChatColor.AQUA + "骷髅 剑")
                        .addEnchantment(Enchantment.DURABILITY, 10).setUnbreakable(true).build();
            }

            @Override
            public ItemStack getHelmet() {
                return new ItemBuilder(Material.DIAMOND_HELMET).setDisplayName(ChatColor.AQUA + "骷髅 头盔")
                        .setUnbreakable(true).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        .addEnchantment(Enchantment.DURABILITY, 3)
                        .addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 1).build();
            }
        });

        speedColddownTask = new SpeedColddownTask();
        speedColddownTask.runTaskTimerAsynchronously(Bedwars.getInstance(), 0, 20);
    }

    public class SpeedColddownTask extends BukkitRunnable
    {
        @Override
        public void run() {
            speedColddown.keySet().forEach(p ->
            {
                speedColddown.put(p, speedColddown.get(p)-1);

                if (p.state != GamePlayer.PlayerState.ALIVE)
                    return;

                if (MegaWallsModeManager.getInstance().getSelectedClass(p) != ClassManager.SKELETON)
                    return;

                p.actionBarText = getActionBarText(p);
            });
        }
    }



    @Override
    public boolean onSkill(GamePlayer gamePlayer) {

        Arrow arrow = gamePlayer.player.launchProjectile(Arrow.class, gamePlayer.player.getLocation().getDirection().multiply(2.35D));
        arrow.spigot().setDamage(0);
        arrow.setMetadata("skill", new FixedMetadataValue(Bedwars.getInstance(), "爆炸箭矢:" + gamePlayer.player.getName()));

        new BukkitRunnable()
        {
            @Override
            public void run() {
                if (arrow.isOnGround() || arrow.isDead())
                {
                    TNTPrimed tnt = gamePlayer.player.getWorld().spawn(arrow.getLocation().add(0.0D, 1.0D, 0.0D), TNTPrimed.class);
                    tnt.setFuseTicks(1);
                    tnt.setMetadata("skill", new FixedMetadataValue(Bedwars.getInstance(), "爆炸箭矢:" + gamePlayer.player.getName()));
                    arrow.getLocation().getWorld().playEffect(arrow.getLocation(), Effect.EXPLOSION_HUGE, 10);
                    arrow.getLocation().getWorld().playSound(arrow.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);

                    for (Player player : LocationUtils.getNearbyPlayers(arrow.getLocation(), 8))
                    {
                        GamePlayer gp = GamePlayer.get(player);
                        if (gp == gamePlayer)
                            continue;
                        if (gp.state == GamePlayer.PlayerState.ALIVE)
                            gp.smartDamage(gp.player.getHealth() * 0.18D, gamePlayer, EntityDamageEvent.DamageCause.PROJECTILE);
                    }

                    this.cancel();
                    if (!arrow.isDead())
                        arrow.remove();
                }
            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 1L);

        return true;
    }

    @EventHandler
    public void onSkillDamage(EntityDamageByEntityEvent event)
    {
        if (event.getEntity().getType() != EntityType.PLAYER)
            return;
        if (!event.getDamager().hasMetadata("skill"))
            return;
        if (event.getDamager().getType() != EntityType.PRIMED_TNT)
            return;
        String[] meta = event.getDamager().getMetadata("skill").get(0).asString().split(":");
        if (!meta[0].equalsIgnoreCase("爆炸箭矢"))
            return;
        GamePlayer shooter = GamePlayer.get(Bukkit.getPlayer(meta[1]));
        GamePlayer player = GamePlayer.get((Player) event.getEntity());
        if (player.getTeam() == shooter.getTeam())
            event.setCancelled(true);
        else
        {
            event.setDamage(1);
        }
    }

    @Override
    public String getSkillName() {
        return "爆炸箭矢";
    }

    Map<GamePlayer, Integer> speedColddown = new HashMap<>();
    @EventHandler
    public void agile(PlayerAttackEvent event)
    {
        if (MegaWallsModeManager.getInstance().getSelectedClass(event.attacker) != this)
            return;

        if (event.type != PlayerAttackEvent.AttackType.BOW)
            return;

        if (event.attacker.player.hasPotionEffect(PotionEffectType.REGENERATION))
            event.attacker.player.removePotionEffect(PotionEffectType.REGENERATION);

        event.attacker.player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));

        if (speedColddown.getOrDefault(event.attacker, 0) > 0)
            return;

        if (event.attacker.player.hasPotionEffect(PotionEffectType.SPEED))
            event.attacker.player.removePotionEffect(PotionEffectType.SPEED);

        event.attacker.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1));
        speedColddown.put(event.attacker, 14);
        event.attacker.actionBarText = this.getActionBarText(event.attacker);
    }

    @EventHandler
    public void arrowRecovery(PlayerAttackEvent event)
    {
        if (MegaWallsModeManager.getInstance().getSelectedClass(event.attacker) != this)
            return;

        if (event.type != PlayerAttackEvent.AttackType.BOW)
            return;

        ItemStack arrows = new ItemStack(Material.ARROW, 2);
        event.attacker.player.getInventory().addItem(arrows);
        event.attacker.sendMessage("§e你的箭矢回收技能返还了你两支箭！");
        event.attacker.player.setFoodLevel(20);
    }

    @Override
    public ItemStack getBow() {
        return new ItemBuilder(super.getBow())
                .addEnchantment(Enchantment.ARROW_DAMAGE, 3)
                .addEnchantment(Enchantment.DURABILITY, 2).build();
    }

    @Override
    public String getActionBarText(GamePlayer gamePlayer) {
        String explosiveArrow = color + "" + ChatColor.BOLD + "爆炸箭矢 " + this.getSkillReadyChar(gamePlayer);
        String agile = color + "" + ChatColor.BOLD + " 敏捷 ";

        if (speedColddown.getOrDefault(gamePlayer, 0) > 0)
        {
            agile += ChatColor.RED + "" + speedColddown.get(gamePlayer) + " 秒";
        }
        else
        {
            agile += ChatColor.GREEN + "" + ChatColor.BOLD + "✓";
        }

        return explosiveArrow + agile;
    }

    @Override
    public int everyHitEnergy() {
        return 0;
    }

    @Override
    public int arrowEnergy() {
        return 20;
    }
}

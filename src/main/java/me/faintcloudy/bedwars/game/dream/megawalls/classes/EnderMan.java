package me.faintcloudy.bedwars.game.dream.megawalls.classes;

import me.faintcloudy.bedwars.events.player.PlayerEnergyChangeEvent;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.dream.megawalls.MegaWallsModeManager;
import me.faintcloudy.bedwars.utils.ItemBuilder;

import me.faintcloudy.bedwars.utils.LocationUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashSet;

public class EnderMan extends MegaWallsClass {
    public EnderMan() {
        super(Material.ENDER_PEARL, "末影人", ChatColor.DARK_PURPLE, new ArmorSet() {
            @Override
            public ItemStack getBoots() {
                return new ItemBuilder(Material.DIAMOND_BOOTS).setDisplayName(ChatColor.DARK_PURPLE + "末影人 靴子")
                        .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        .addEnchantment(Enchantment.DURABILITY, 10)
                        .setUnbreakable(true).setLore(
                                "", ChatColor.BLUE + "免疫 75% 的摔落伤害").build();
            }

            @Override
            public ItemStack getWeapon() {
                return new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName(ChatColor.DARK_PURPLE + "末影人 剑")
                        .addEnchantment(Enchantment.DURABILITY, 10).setUnbreakable(true).build();
            }
        });

    }

    @EventHandler
    public void onBoots(EntityDamageEvent event)
    {

        if (event.getEntity() instanceof Player)
        {
            if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
                return;

            Player player = (Player) event.getEntity();
            if (player.getInventory().getBoots() == this.armorSet.getBoots())
                event.setDamage(event.getDamage() * 0.25);
        }
    }

    @Override
    public boolean onSkill(GamePlayer gamePlayer) {
        if (gamePlayer.state != GamePlayer.PlayerState.ALIVE)
            return false;
        Player target = null;
        for (Block block : gamePlayer.player.getLineOfSight(new HashSet<>(Arrays.asList(Material.values())), 25))
        {
            for (Player player : LocationUtils.getNearbyPlayers(block.getLocation(), 2))
            {
                if (GamePlayer.get(player).getTeam() == gamePlayer.getTeam())
                    continue;
                if (target == null && player.getLocation().distance(gamePlayer.player.getLocation()) <= 25 && player != gamePlayer.player)
                {
                    target = player;
                    continue;
                }

                if (target == null)
                    continue;

                if (player.getLocation().distance(gamePlayer.player.getLocation()) < target.getLocation().distance(gamePlayer.player.getLocation())
                        && player != gamePlayer.player)
                {
                    target = player;
                }
            }
        }

        if (target != null)
        {
            gamePlayer.safetyTeleport(target.getLocation());
            gamePlayer.playEffectAround(Effect.WITCH_MAGIC, 30);
            gamePlayer.playSound(Sound.ENDERMAN_TELEPORT, true);
            gamePlayer.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5*20, 2));
        }

        return target != null;
    }

    @EventHandler
    public void onCharge(PlayerEnergyChangeEvent event)
    {
        if (MegaWallsModeManager.getInstance().getSelectedClass(event.gamePlayer) != this)
            return;
        if (event.gamePlayer.state != GamePlayer.PlayerState.ALIVE)
            return;
        if (event.news >= 100 && event.original < 100)
        {
            event.gamePlayer.player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 0));
        }
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event)
    {
        if (event.getEntity().getKiller() == null)
            return;

        GamePlayer gamePlayer = GamePlayer.get(event.getEntity().getKiller());
        if (gamePlayer.state != GamePlayer.PlayerState.ALIVE)
            return;
        if (MegaWallsModeManager.getInstance().getSelectedClass(gamePlayer) != this)
            return;

        gamePlayer.heal(6);
    }

    @Override
    public String getSkillName() {
        return "末影传送";
    }

    @Override
    public String getActionBarText(GamePlayer gamePlayer) {
        return color + "" + ChatColor.BOLD + "末影传送 " + this.getSkillReadyChar(gamePlayer);
    }

    @Override
    public int everyHitEnergy() {
        return 20;
    }
}

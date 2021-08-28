package me.faintcloudy.bedwars.game.dream.megawalls.classes;

import me.faintcloudy.bedwars.events.player.PlayerAttackEvent;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.dream.megawalls.MegaWallsModeManager;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class Herobrine extends MegaWallsClass {

    public Herobrine() {
        super(Material.DIAMOND_SWORD, "HIM", ChatColor.YELLOW, new ArmorSet()
        {
            @Override
            public ItemStack getHelmet() {
                return new ItemBuilder(Material.IRON_HELMET).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                        .setDisplayName(ChatColor.YELLOW + "HIM 头盔").setUnbreakable(true).build();
            }

            @Override
            public ItemStack getWeapon() {
                return new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName(ChatColor.YELLOW + "HIM 剑")
                        .addEnchantment(Enchantment.DURABILITY, 10).setUnbreakable(true).build();
            }
        });
    }

    @Override
    public boolean onSkill(GamePlayer gamePlayer) {
        boolean success = false;
        if (gamePlayer.state != GamePlayer.PlayerState.ALIVE)
            return false;

        for (GamePlayer player : gamePlayer.getNearestPlayers(4.5)) {
            if (player.state != GamePlayer.PlayerState.ALIVE)
                continue;
            success = true;
            player.player.getWorld().spigot().strikeLightningEffect(player.player.getLocation(), true);
            player.smartDamage(4, gamePlayer, EntityDamageEvent.DamageCause.MAGIC);
            player.sendMessage(ChatColor.YELLOW + "你被 " + ChatColor.GREEN + gamePlayer.getColoredName() +
                    ChatColor.YELLOW + " 的雷神之怒击中了！");
            player.player.getWorld().playSound(player.player.getLocation(), Sound.ENDERMAN_DEATH, 1, 1);
            player.playEffectAround(Effect.LAVA_POP, 30);
        }

        return success;
    }

    Map<GamePlayer, Integer> attacks = new HashMap<>();

    @EventHandler
    public void onFlurry(PlayerAttackEvent event)
    {
        if (MegaWallsModeManager.getInstance().getSelectedClass(event.attacker) != this)
            return;
        if (event.attacker.state != GamePlayer.PlayerState.ALIVE)
            return;
        if (!attacks.containsKey(event.attacker))
            attacks.put(event.attacker, 0);

        attacks.put(event.attacker, attacks.get(event.attacker) + 1);

        if (attacks.get(event.attacker) >= 4)
        {
            attacks.put(event.attacker, 0);
            event.attacker.sendMessage(ChatColor.YELLOW + "你的飓风技能给予了你 3 秒的 " + ChatColor.GREEN + "速度 II");
            event.attacker.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3 * 20, 1));
        }

        event.attacker.actionBarText = this.getActionBarText(event.attacker);
    }

    @Override
    public String getSkillName() {
        return "雷神之怒";
    }

    @Override
    public String getActionBarText(GamePlayer gamePlayer) {

        String mainSkill = color + "" + ChatColor.BOLD + "雷神之怒 " + this.getSkillReadyChar(gamePlayer);
        if (!attacks.containsKey(gamePlayer))
            attacks.put(gamePlayer, 0);
        String flurry = color + "" + ChatColor.BOLD + " 飓风 ";
        switch (attacks.get(gamePlayer))
        {
            case 0:
                flurry += ChatColor.RED + "" + ChatColor.BOLD + "x";
                break;
            case 3:
                flurry += ChatColor.GREEN + "" + ChatColor.BOLD + "√";
                break;
            default:
                flurry += ChatColor.GREEN + "" + ChatColor.BOLD + attacks.get(gamePlayer);
                break;
        }

        return mainSkill + flurry;
    }

    @Override
    public int everyHitEnergy() {
        return 25;
    }
}

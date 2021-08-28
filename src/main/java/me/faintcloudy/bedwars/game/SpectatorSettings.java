package me.faintcloudy.bedwars.game;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.shop.shopitem.ShopItem;
import me.faintcloudy.bedwars.utils.ActionBarUtil;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class SpectatorSettings {
    public GamePlayer player;
    public int speed;
    public boolean nightVision = false;
    public boolean autoTeleport = false;
    public boolean autoFirstPerson = false;
    public boolean firstPersonTargeting = false;
    public boolean hideSpectators = false;
    public GamePlayer target = null;
    public List<BukkitTask> tasks = new ArrayList<>();
    public SpectatorSettings(GamePlayer player)
    {
        this.player = player;
    }

    public void speed(int i)
    {
        this.speed = i;
        if (player.player.hasPotionEffect(PotionEffectType.SPEED))
            player.player.removePotionEffect(PotionEffectType.SPEED);
        if (speed <= 0)
        {
            player.player.sendMessage("§c你不再拥有任何的速度效果");
            return;
        }
        player.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, this.speed-1));
        player.player.setFlySpeed(speed*0.1f);
        player.player.sendMessage("§a你现在拥有速度 " + ShopItem.toRome(this.speed));
    }

    public void firstPersonTargeting(boolean b)
    {
        this.firstPersonTargeting = b;
        if (this.firstPersonTargeting) {
            ((CraftPlayer) player.player).getHandle().setSpectatorTarget(((CraftPlayer) target.player).getHandle());
            player.sendTitle("§a正在旁观 " + target.getColoredName(), "§a点击左键打开菜单 §c按Shift键退出", 0, 40, 20);
        }
        else
        {
            ((CraftPlayer) player.player).getHandle().setSpectatorTarget(null);
            player.sendTitle("§e退出旁观模式", "", 0, 40, 20);
        }

    }

    public void autoFirstPerson(boolean b)
    {
        this.autoFirstPerson = b;
        player.player.sendMessage(autoFirstPerson ? "§a你将默认使用第一人称旁观模式！" : "§c你将默认使用第三人称旁观模式！");
    }

    public void autoTeleport(boolean b)
    {
        this.autoTeleport = b;
        player.player.sendMessage(autoTeleport ? "§a当你用指南针选择一个玩家后，你会被自动传送到他那里！" : "§c你不会再被自动传送到目标位置！");
    }

    public void target(GamePlayer target)
    {
        this.target = target;
        player.safetyTeleport(target.player.getLocation());
        player.player.sendMessage("§a你被传送到了 " + target.getPrefixedName() + " §a处");
        if (autoFirstPerson)
        {
            this.firstPersonTargeting(true);
        }
    }

    public void hideSpectators(boolean b)
    {
        this.hideSpectators = b;
        player.player.sendMessage(hideSpectators ? "§c你不会再看到其他旁观者" : "§a你现在可以看到其他旁观者了！");
    }

    public void nightVision(boolean b)
    {
        this.nightVision = b;
        if (player.player.hasPotionEffect(PotionEffectType.NIGHT_VISION))
            player.player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        if (nightVision)
            player.player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
        player.player.sendMessage(nightVision ? "§a你现在拥有了夜视！" : "§c你不再拥有了夜视！");
    }

    public void startTask()
    {
        if (!tasks.isEmpty())
            tasks.forEach(BukkitTask::cancel);
        tasks.add(new BukkitRunnable()
        {
            public void run()
            {
                if (target == null)
                    return;
                if (target.state != GamePlayer.PlayerState.ALIVE)
                {
                    ActionBarUtil.sendActionBar(player.player, "§c目标丢失！");
                    target = null;
                    return;
                }
                double health = target.player.getHealth() / target.player.getMaxHealth();
                double distance = target.player.getLocation().distance(player.player.getLocation());
                String message = "§f目标: §a§l" + target.player.getName() + " §f血量: §a§l" + (int)(100*health) + "%" + " §f距离: §a§l" + (int)distance + "m";
                if (firstPersonTargeting)
                {
                    message = "§f目标: §a§l" + target.player.getName() + " §f血量: §a§l" + (int)(100*health) + "% §a点击左键打开菜单 §c按Shift键退出";
                }
                ActionBarUtil.sendActionBar(player.player, message);
            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 1));
        tasks.add(new BukkitRunnable()
        {
            public void run()
            {
                if (autoTeleport && !firstPersonTargeting)
                {
                    if (target == null)
                        return;
                    player.safetyTeleport(target.player.getLocation());
                }
            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 20));
        tasks.add(new BukkitRunnable()
        {
            public void run()
            {
                for (GamePlayer p : GamePlayer.getOnlineGamePlayers())
                {
                    if (hideSpectators)
                        player.player.hidePlayer(p.player);
                    else
                        player.player.showPlayer(p.player);
                }
            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 20));
    }
}

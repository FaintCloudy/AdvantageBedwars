package me.faintcloudy.bedwars.listener;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.events.InventoryChangeEvent;
import me.faintcloudy.bedwars.events.player.PlayerGotKilledEvent;
import me.faintcloudy.bedwars.game.Game;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.GameState;
import me.faintcloudy.bedwars.game.resource.DeclaredResourceSpawner;
import me.faintcloudy.bedwars.game.team.TeamState;
import me.faintcloudy.bedwars.inventory.TargetSelectMenu;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.*;

public class PlayerListener implements Listener {
    Game game = Bedwars.getInstance().game;
    public static List<Block> blocksPut = new ArrayList<>();
    Map<Player, BukkitTask> leaveRequests = new HashMap<>();
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        event.setJoinMessage(null);
        if (Bedwars.getInstance().getConfig().getBoolean("setup") && !event.getPlayer().isOp())
        {
            event.getPlayer().kickPlayer("§c当前服务器未开启");
            return;
        }

        if (Bukkit.getOnlinePlayers().size() >= game.scale.maxPlayers())
        {
            event.getPlayer().kickPlayer("§c服务器已满");
            return;
        }
        GamePlayer player = GamePlayer.get(event.getPlayer());
        Bedwars.getInstance().scoreBoardManager.unloadGameBoard(event.getPlayer());
        Bedwars.getInstance().scoreBoardManager.loadGameBoard(event.getPlayer());
        player.player = event.getPlayer();
        if (game.state != GameState.WAITING)
        {
            player.death();
            return;
        }

        player.resetEntityState();
        for (GamePlayer p : GamePlayer.getOnlineGamePlayers())
        {
            p.player.showPlayer(player.player);
            player.player.showPlayer(p.player);
        }
        player.game.spectatorTeam.joinTeam(player);
        player.giveWaitingItems();
        player.safetyTeleport(game.map.lobby);
        int online = Bukkit.getOnlinePlayers().size();
        game.registerScoreboardTeams();
        event.setJoinMessage(player.getColoredName() + " §e加入了游戏 (§b" + online + "§e/§b" + game.scale.maxPlayers() + "§e)！");
    }

    @EventHandler
    public void onJoin2(PlayerJoinEvent event)
    {
        GamePlayer.get(event.getPlayer()).player = event.getPlayer();
    }

    @EventHandler
    public void onFireBurn(BlockBurnEvent event)
    {
        event.setCancelled(true);
    }


    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent event)
    {
        event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
    }

    @EventHandler
    public void onBucket2(PlayerBucketFillEvent event)
    {
        event.setCancelled(true);
    }
    @EventHandler
    public void onExplode1(EntityExplodeEvent event)
    {
        if (event.getEntityType() == EntityType.ENDER_DRAGON)
            return;
        List<Block> ingore = new ArrayList<>();
        for (Block block : event.blockList())
        {
            if (!blocksPut.contains(block) || block.getType() == Material.BED_BLOCK)
                ingore.add(block);
        }

        for (Block block : ingore)
        {
            event.blockList().remove(block);
        }
    }

    @EventHandler
    public void betterPotion(PlayerItemConsumeEvent event)
    {
        if (event.getItem().getType() == Material.POTION)
        {
            if (event.isCancelled())
                return;

            event.setCancelled(true);

            PotionMeta potionMeta = (PotionMeta) event.getItem().getItemMeta();

            ItemStack item = event.getItem();
            item.setAmount(item.getAmount()-1);
            event.getPlayer().setItemInHand(item);

            potionMeta.getCustomEffects().forEach(effect ->
            {
                if (event.getPlayer().hasPotionEffect(effect.getType()))
                    event.getPlayer().removePotionEffect(effect.getType());

                event.getPlayer().addPotionEffect(effect);
            });




        }
    }

    @EventHandler
    public void onSpectatingChat(AsyncPlayerChatEvent event)
    {
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.state == GamePlayer.PlayerState.SPECTATING)
        {
            event.setCancelled(true);
            String message = "§7[旁观者] " + player.getPrefixedName() + "§f: " + event.getMessage();
            for (GamePlayer spectator : GamePlayer.getOnlineGamePlayers())
            {
                if (spectator.state == GamePlayer.PlayerState.SPECTATING)
                    spectator.sendMessage(message);
            }
        }
    }

    @EventHandler
    public void onRespawningChat(AsyncPlayerChatEvent event)
    {
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.state == GamePlayer.PlayerState.RESPAWNING)
        {
            player.player.sendMessage("§c你不可以在此时进行聊天");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLobbyChat(AsyncPlayerChatEvent event)
    {
        if (game.state != GameState.WAITING)
            return;
        event.setCancelled(true);
        GamePlayer player = GamePlayer.get(event.getPlayer());
        Bukkit.broadcastMessage(player.getPrefixedName() + "§f: " + event.getMessage());
    }

    @EventHandler
    public void onGameChat(AsyncPlayerChatEvent event)
    {
        if (game.state == GameState.WAITING)
            return;
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.state != GamePlayer.PlayerState.ALIVE)
            return;
        event.setCancelled(true);

        player.getTeam().chat(player, event.getMessage());
    }

    @EventHandler
    public void onMoveToolIntoChest(InventoryMoveItemEvent event)
    {
        if (event.getItem().getType().name().contains("AXE") || event.getItem().getType() == Material.SHEARS)
        {
            if (event.getSource() != event.getDestination())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        if (event.isCancelled())
            return;
        if (Bedwars.getInstance().game.map.outOfBorder(event.getTo()))
        {
            event.getPlayer().sendMessage("§c§l你不能朝着这个方向前进");
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onArmorStand(PlayerArmorStandManipulateEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPI(PlayerInteractAtEntityEvent event)
    {
        if (!(event.getRightClicked() instanceof ArmorStand))
            return;
        if (event.getPlayer().getItemInHand() == null || event.getPlayer().getItemInHand().getType() == Material.AIR || !event.getPlayer().getItemInHand().getType().isBlock())
            return;
        ArmorStand block = (ArmorStand) event.getRightClicked();
        for (DeclaredResourceSpawner spawner : game.declaredResourceSpawners)
        {
            if (spawner.block.armorStand == block)
            {
                if (block.getHelmet().getType() != spawner.type.block)
                    return;
                event.getPlayer().sendMessage("§c盲生，你发现了华点");
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ORB_PICKUP, 0.6F, 0.6F);
                block.setHelmet(event.getPlayer().getItemInHand().clone());
                new BukkitRunnable()
                {
                    public void run()
                    {
                        spawner.block.armorStand.setHelmet(new ItemStack(spawner.type.block));
                    }
                }.runTaskLater(Bedwars.getInstance(), 70L);
                return;
            }
        }

    }

    @EventHandler
    public void onGamingQuit(PlayerQuitEvent event)
    {
        if (game.state != GameState.GAMING)
            return;

        GamePlayer player = GamePlayer.get(event.getPlayer());
        event.setQuitMessage(player.getColoredName() + " §7掉线了");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        if (game.state != GameState.WAITING)
            return;

        GamePlayer player = GamePlayer.get(event.getPlayer());
        event.setQuitMessage(player.getPrefixedName() + " §e已退出！");
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event)
    {
        if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        if (game.state != GameState.GAMING)
        {
            event.setCancelled(true);
            return;
        }

        if (!blocksPut.contains(event.getBlock()))
        {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onInteractBed(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if (event.getClickedBlock().getType() == Material.BED_BLOCK)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBed(BlockBreakEvent event)
    {
        if (game.state != GameState.GAMING)
            return;
        if (event.getBlock().getType() != Material.BED_BLOCK)
            return;
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.getTeam().isTeamBed(event.getBlock()))
        {
            event.setCancelled(true);
            return;
        }
        event.getBlock().breakNaturally(new ItemStack(Material.AIR));
        game.getTeamByBed(event.getBlock()).brokeBed(player);
    }

    @EventHandler
    public void onBedItemSpawn(EntitySpawnEvent event)
    {
        if (event.getEntity() instanceof Item)
        {
            Item item = (Item) event.getEntity();
            if (item.getItemStack().getType() == Material.BED)
                item.remove();
        }
    }

    @EventHandler
    public void onQuitKill(PlayerQuitEvent event)
    {
        if (game.state != GameState.GAMING)
            return;

        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.state == GamePlayer.PlayerState.RESPAWNING || player.state == GamePlayer.PlayerState.SPECTATING)
        {
            return;
        }

        String message = player.getColoredName() + " §7吓得拔下了网线";
        if (player.getLastDamageByEntity() == null)
            return;
        switch (player.getLastDamageByEntity().getDamager().getType())
        {
            case PLAYER:
                GamePlayer damager = null;
                if (player.getLastDamageByEntity().getDamager() instanceof Player)
                    damager = GamePlayer.get((Player) player.getLastDamageByEntity().getDamager());
                else if (player.getLastDamageByEntity().getDamager() instanceof Projectile)
                    damager = GamePlayer.get((Player) ((Projectile) player.getLastDamageByEntity().getDamager()).getShooter());
                if (damager == null)
                    break;
                message = player.getColoredName() + " §7被 " + damager.getColoredName() + " §7杀死了";
                if (player.getTeam().state == TeamState.BED_LESS)
                {
                    damager.data.currentGameData.finalKills++;
                    Bukkit.getPluginManager().callEvent(new PlayerGotKilledEvent(player, damager, true));
                }
                else
                {
                    damager.data.currentGameData.kills++;
                    Bukkit.getPluginManager().callEvent(new PlayerGotKilledEvent(player, damager, false));
                }
                break;
            case IRON_GOLEM:
                message = player.getColoredName() + " §7被铁傀儡推倒了";
                break;
            case SILVERFISH:
                message = player.getColoredName() + " §7被蠹虫咬死了";
                break;
        }

        player.death();
        Bukkit.broadcastMessage(message);
    }

    @EventHandler
    public void onShoot(EntityDamageByEntityEvent event)
    {
        if (!(event.getEntity() instanceof Player))
            return;
        if (game.state != GameState.GAMING)
            return;
        if (event.isCancelled())
            return;
        if (!(event.getDamager() instanceof Projectile))
            return;

        Projectile projectile = (Projectile) event.getDamager();
        if (projectile.getShooter() instanceof Player)
        {
            GamePlayer player = GamePlayer.get((Player) event.getEntity());
            GamePlayer shooter = GamePlayer.get((Player) ((Projectile) event.getDamager()).getShooter());
            if (player.state == GamePlayer.PlayerState.RESPAWNING || player.state == GamePlayer.PlayerState.SPECTATING)
            {
                event.setCancelled(true);
                return;
            }
            if (player.getTeam() == shooter.getTeam())
            {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(false);
            player.setLastDamageEvent(event);
        }

    }

    @EventHandler
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent event)
    {

        if (!(event.getEntity() instanceof Player))
        {

            return;
        }
        if (game.state != GameState.GAMING)
        {

            return;
        }
        if (event.isCancelled())
        {
            return;
        }

        GamePlayer player = GamePlayer.get((Player) event.getEntity());
        if (player.state == GamePlayer.PlayerState.RESPAWNING || player.state == GamePlayer.PlayerState.SPECTATING)
        {
            event.setCancelled(true);
            return;
        }
        if (event.getDamager() instanceof Player)
        {
            GamePlayer damager = GamePlayer.get((Player) event.getDamager());
            if (player.getTeam() == damager.getTeam())
            {
                event.setCancelled(true);
                return;
            }
        }

        event.setCancelled(false);

        player.setLastDamageEvent(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
        {
            return;
        }
        if (event.getEntity() == null)
            return;
        if (game.state != GameState.GAMING)
            return;


        GamePlayer player = GamePlayer.get(event.getEntity());
        if (player.state == GamePlayer.PlayerState.RESPAWNING || player.state == GamePlayer.PlayerState.SPECTATING)
        {
            return;
        }

        boolean finalKill = player.getTeam().state != TeamState.ALIVE;
        String message = player.getColoredName() + " §7死了";
        if (player.getLastAttacker() == null)
        {
            switch (event.getEntity().getLastDamageCause().getCause())
            {
                case LAVA:
                case FIRE:
                case FIRE_TICK:
                    message = player.getColoredName() + " §7被火焰焚烧至死";
                    break;
                case VOID:
                case FALL:
                    message = player.getColoredName() + " §7跌下了悬崖";
                    break;
                case MAGIC:
                case POISON:
                    message = player.getColoredName() + " §7被魔法杀死";
                    break;
                case BLOCK_EXPLOSION:
                case ENTITY_EXPLOSION:
                    message = player.getColoredName() + " §7爆炸了";

            }
        }
        else
        {

            message = player.getColoredName() + " §7被 " + player.getLastAttacker().getColoredName() + " §7击杀了";
            if (finalKill) {
                player.getLastAttacker().data.currentGameData.finalKills++;
                player.data.currentGameData.finalDeaths++;
            }
            else {
                player.getLastAttacker().data.currentGameData.kills++;
                player.data.currentGameData.deaths++;
            }
            switch (event.getEntity().getLastDamageCause().getCause())
            {

                case LAVA:
                case FIRE:
                case FIRE_TICK:
                    message = player.getColoredName() + " §7被" + player.getLastAttacker().getColoredName() + " §7用火焰焚烧至死";
                    break;
                case VOID:
                case FALL:
                    message = player.getColoredName() + " §7被 " + player.getLastAttacker().getColoredName() + " §7丢下了悬崖";
                    break;
                case MAGIC:
                case POISON:
                    message = player.getColoredName() + " §7被 " + player.getLastAttacker().getColoredName() + " §7用魔法杀死";
                    break;
                case BLOCK_EXPLOSION:
                case ENTITY_EXPLOSION:
                    message = player.getColoredName() + " §7被" + player.getLastAttacker().getColoredName() + " §7引爆了";

            }
        }

        if (finalKill)
            message += " §b§l最终击杀";
        event.setDeathMessage(null);
        event.setKeepInventory(true);
        player.death();
        Bukkit.broadcastMessage(message);


    }

    @EventHandler
    public void onSpawn(PlayerSpawnLocationEvent event)
    {

        event.setSpawnLocation(Bedwars.getInstance().game.map.middle);
    }

    @EventHandler
    public void onBed(PlayerBedEnterEvent event)
    {
        event.setCancelled(true);
    }


    @EventHandler
    public void onSpecDamage(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player))
            return;
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        Player p = (Player) event.getEntity();
        GamePlayer player = GamePlayer.get(p);
        if (player.state != GamePlayer.PlayerState.ALIVE)
            event.setCancelled(true);
    }

    @EventHandler
    public void blockFromToEvent(BlockFromToEvent event)
    {
        if (event.getBlock().getType() == Material.SNOW_BLOCK || event.getBlock().getType() == Material.ICE)
            event.setCancelled(true);
    }

    @EventHandler
    public void onSleep1(PlayerBedEnterEvent event)
    {

        event.setCancelled(true);
        event.getPlayer().sendMessage("§c太阳晒屁股了~");

    }

    @EventHandler
    public void onWake(PlayerBedLeaveEvent event)
    {
        event.getPlayer().sendMessage("§c请及时向服务器管理员举报BUG");
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event)
    {
        if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;


        if (game.state != GameState.GAMING)
        {
            event.setCancelled(true);
            return;
        }

        if (game.region(event.getBlock().getLocation()))
        {
            event.setCancelled(true);
            return;
        }

        blocksPut.add(event.getBlock());

        //TODO no place in team region
    }

    @EventHandler
    public void onThrow(PlayerDropItemEvent event)
    {
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.state != GamePlayer.PlayerState.ALIVE)
            event.setCancelled(true);
        if (game.state != GameState.GAMING)
            event.setCancelled(true);
    }

    @EventHandler
    public void onThrowTool(PlayerDropItemEvent event)
    {
        if (game.state != GameState.GAMING)
            return;

        Material type = event.getItemDrop().getItemStack().getType();
        if (type.name().contains("AXE") || type.name().contains("SHEARS"))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event)
    {
        if (game.state != GameState.GAMING)
            event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event)
    {
        event.setFoodLevel(20);
        if (event.getEntity() instanceof Player)
            ((Player) event.getEntity()).setSaturation(20F);
    }

    @EventHandler
    public void onLobbyDamage(EntityDamageEvent event)
    {
        if (game.state != GameState.GAMING)
            event.setCancelled(true);
    }


    @EventHandler
    public void onVoid(PlayerMoveEvent event)
    {
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (game.state == GameState.WAITING && event.getTo().getY() <= 2)
        {
            player.safetyTeleport(game.map.lobby);
            return;
        }

        if (player.state == GamePlayer.PlayerState.SPECTATING || player.state == GamePlayer.PlayerState.RESPAWNING)
        {
            if (event.getTo().getY() > 2)
                return;
            player.safetyTeleport(game.map.middle);
            return;
        }

        if (event.getTo().getY() <= 2)
        {
            player.death();
            Bukkit.broadcastMessage(player.getColoredName() + " §7跌入了虚空");
        }
    }

    @EventHandler
    public void onLobbyInventoryChange(InventoryClickEvent event)
    {
        if (game.state != GameState.GAMING)
        {
            if (event.getClickedInventory() == null)
                return;
            if (event.getClickedInventory() != event.getWhoClicked().getInventory())
                return;
            if (event.getWhoClicked() instanceof Player)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortal(PortalCreateEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPortalTeleport(PlayerPortalEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        switch (event.getSpawnReason())
        {
            case NATURAL:
                event.setCancelled(true);
            case EGG:
                event.setCancelled(true);
            case BUILD_WITHER:
                event.setCancelled(true);
            case BUILD_SNOWMAN:
                event.setCancelled(true);
            case NETHER_PORTAL:
                event.setCancelled(true);
            case INFECTION:
                event.setCancelled(true);
            case VILLAGE_INVASION:
                event.setCancelled(true);
            case VILLAGE_DEFENSE:
                event.setCancelled(true);
            case SILVERFISH_BLOCK:
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMoveArmor(InventoryClickEvent event)
    {
        if (event.getClickedInventory() instanceof PlayerInventory)
        {
            if (event.getSlotType() == InventoryType.SlotType.ARMOR)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBukkitEmpty(PlayerBucketEmptyEvent event)
    {
        event.setItemStack(null);
    }

    @EventHandler
    public void onFill(PlayerBucketFillEvent event)
    {
        event.setItemStack(null);
    }


    public static List<Player> noDamages = new ArrayList<>();
    @EventHandler
    public void onDamage(EntityDamageEvent event)
    {
        if (event.getEntityType() != EntityType.PLAYER)
            return;

        if (noDamages.contains((Player) event.getEntity()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onAchievement(PlayerAchievementAwardedEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event)
    {
        GamePlayer player = GamePlayer.get(event.getPlayer());
        switch (event.getMaterial())
        {
            case DIAMOND:
                if (game.state == GameState.WAITING)
                if (player.player.isOp() || player.player.hasPermission("bw.admin"))
                    game.onStart();
                break;
            case BED:
                if (game.state == GameState.WAITING || player.state == GamePlayer.PlayerState.SPECTATING)
                    this.addLeaveRequest(player.player);
                break;
            case WOOL:
                if (game.state == GameState.WAITING)
                    Bedwars.getInstance().SELECT_GAME_MENU.open(player.player);
                break;
            case COMPASS:
                if (player.state == GamePlayer.PlayerState.SPECTATING)
                    TargetSelectMenu.menu().open(player.player);
                break;
            case PAPER:
                if (player.state == GamePlayer.PlayerState.SPECTATING)
                    player.player.sendMessage("§c正在开发中...");
                break;
            case REDSTONE_COMPARATOR:
                if (player.state == GamePlayer.PlayerState.SPECTATING)
                Bedwars.getInstance().SPECTATOR_SETTINGS_MENU.open(player.player);
                break;
        }
    }

    public void addLeaveRequest(Player player)
    {
        if (leaveRequests.containsKey(player))
        {
            player.sendMessage("§c§l传送取消了!");
            leaveRequests.get(player).cancel();
            leaveRequests.remove(player);
            return;
        }

        player.sendMessage("§a§l3秒后将你传送到大厅...再次右键以取消传送!");

        leaveRequests.put(player, new BukkitRunnable()
        {
            public void run()
            {
                Bedwars.getInstance().sendToLobby(player);
                leaveRequests.remove(player);
            }
        }.runTaskLater(Bedwars.getInstance(), 60));
    }

    @EventHandler
    public void onOpenOtherTeamChest(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null)
        {
            if (event.getClickedBlock().getType() != Material.CHEST)
                return;
            GamePlayer player = GamePlayer.get(event.getPlayer());
            if (!player.getTeam().isTeamChest(event.getClickedBlock()))
            {
                event.setCancelled(true);
                player.sendMessage("§c该队伍尚未被消灭 无法打开其队伍箱子");
            }
        }
    }

    @EventHandler
    public void onWoodenSwordSpawn(ItemSpawnEvent event)
    {
        if (event.getEntity().getItemStack().getType() == Material.WOOD_SWORD)
            event.setCancelled(true);
    }

    @EventHandler
    public void onDrinkInvPotion(PlayerItemConsumeEvent event)
    {
        if (event.isCancelled())
            return;
        if (event.getItem().getType() != Material.POTION)
            return;
        PotionMeta meta = (PotionMeta) event.getItem().getItemMeta().clone();
        new BukkitRunnable()
        {
            public void run()
            {
                event.getItem().setType(Material.AIR);
                event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
            }
        }.runTaskLater(Bedwars.getInstance(), 2);
        if (!meta.hasCustomEffect(PotionEffectType.INVISIBILITY))
            return;

        if (game.state != GameState.GAMING)
            return;
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.state != GamePlayer.PlayerState.ALIVE)
            return;
        player.hideArmor();

    }

    @EventHandler
    public void onThrowWoodSword(PlayerDropItemEvent event)
    {
        if (game.state != GameState.GAMING)
            return;

        if (event.getItemDrop().getItemStack().getType() == Material.WOOD_SWORD)
        {
            new BukkitRunnable()
            {
                public void run()
                {
                    event.setCancelled(true);
                }
            }.runTaskLater(Bedwars.getInstance(), 10);

        }
    }

    @EventHandler
    public void onInventoryChangeEvent(InventoryChangeEvent event)
    {



    }







}



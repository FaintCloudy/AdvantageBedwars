package me.faintcloudy.bedwars.game;

import fr.minuskube.inv.content.SlotPos;
import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.database.SQLManager;
import me.faintcloudy.bedwars.events.player.PlayerDeadEvent;
import me.faintcloudy.bedwars.events.player.PlayerGotKilledEvent;
import me.faintcloudy.bedwars.events.player.PlayerSpawnEvent;
import me.faintcloudy.bedwars.game.equipment.ArmorEquipment;
import me.faintcloudy.bedwars.game.equipment.ToolEquipment;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.shop.shopitem.*;
import me.faintcloudy.bedwars.game.team.Team;
import me.faintcloudy.bedwars.game.team.TeamState;
import me.faintcloudy.bedwars.game.team.upgrade.TeamUpgrade;
import me.faintcloudy.bedwars.listener.PlayerListener;
import me.faintcloudy.bedwars.listener.SpecialItemListener;
import me.faintcloudy.bedwars.stats.PlayerData;
import me.faintcloudy.bedwars.utils.*;
import net.citizensnpcs.api.CitizensAPI;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.*;

public class GamePlayer {
    public static ItemStack WOOD_SWORD = new ItemBuilder(Material.WOOD_SWORD).setUnbreakable(true)
            .addFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    public static HashMap<SlotPos, ShopItem> defaultFastBuySettings;

    static {
        defaultFastBuySettings = new MapBuilder<SlotPos, ShopItem>()
                .put(SlotPos.of(2, 1), BlockItem.WOOL)
                .put(SlotPos.of(3, 1), BlockItem.WOOD)
                .put(SlotPos.of(2, 2), MeleeItem.STONE_SWORD)
                .put(SlotPos.of(3, 2), MeleeItem.IRON_SWORD)
                .put(SlotPos.of(4, 2), MeleeItem.DIAMOND_SWORD)
                .put(SlotPos.of(2, 3), ArmorItem.CHAINMAIL_ARMOR)
                .put(SlotPos.of(3, 3), ArmorItem.IRON_ARMOR)
                .put(SlotPos.of(4, 3), ArmorItem.DIAMOND_ARMOR)
                .put(SlotPos.of(2, 4), ToolItem.PICKAXE)
                .put(SlotPos.of(3, 4), ToolItem.AXE)
                .put(SlotPos.of(4, 4), ToolItem.SHEARS)
                .put(SlotPos.of(2, 5), RangedItem.BOW)
                .put(SlotPos.of(3, 5), RangedItem.ARROW)
                .put(SlotPos.of(4, 5), SpecialItem.FIRE_BALL)
                .put(SlotPos.of(2, 6), PotionItem.JUMP_POTION)
                .put(SlotPos.of(3, 6), PotionItem.INVISIBILITY_POTION)
                .put(SlotPos.of(4, 6), PotionItem.SPEED_POTION)
                .put(SlotPos.of(2, 7), SpecialItem.TNT)
                .put(SlotPos.of(3, 7), SpecialItem.GOLDEN_APPLE).build();
    }

    public Player player;
    public boolean milkTime = false;
    public PlayerData data;
    public Map<ToolEquipment, Integer> toolLevels = new HashMap<>();
    public ShopItem settingItem = null;
    public ArmorEquipment armor;
    public boolean quickBuying = false;
    public PlayerState state;
    private EntityDamageByEntityEvent lastDamageByEntity = null;
    public int lastDamageClearTime = 15;
    public Game game;
    public SpectatorSettings spectatorSettings;
    public String actionBarText = "";

    public GamePlayer(Player player) {
        this.player = player;
        player.getActivePotionEffects().forEach(p -> player.removePotionEffect(p.getType()));
        this.data = new PlayerData(this);
        this.state = PlayerState.ALIVE;
        this.toolLevels.put(ToolEquipment.AXE, 0);
        this.toolLevels.put(ToolEquipment.PICKAXE, 0);
        this.toolLevels.put(ToolEquipment.SHEARS, 0);
        this.player.setPlayerTime(0, false);
        this.player.setPlayerWeather(WeatherType.CLEAR);
        this.game = Bedwars.getInstance().game;
        this.armor = ArmorEquipment.LEATHER_ARMOR;
        this.spectatorSettings = new SpectatorSettings(this);
        new BukkitRunnable()
        {
            public void run()
            {
                if (lastDamageClearTime <= 0 && lastDamageByEntity != null)
                {
                    lastDamageByEntity = null;
                }
                lastDamageClearTime--;
            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 20);
        new BukkitRunnable()
        {
            public void run()
            {
                if (actionBarText.isEmpty())
                    return;
                ActionBarUtil.sendActionBar(player, actionBarText);
            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 10);
    }

    public Team getTeam()
    {
        for (Team team : game.teams)
        {
            for (GamePlayer member : team.players)
            {
                if (member.player.getName().equals(player.getName()))
                    return team;
            }
        }
        return game.spectatorTeam;
    }

    public void setLastDamageEvent(EntityDamageByEntityEvent event)
    {
        this.lastDamageByEntity = event;
        this.lastDamageClearTime = 15;
    }
    public enum PlayerState {
        ALIVE, RESPAWNING, SPECTATING
    }

    @Override
    public String toString() {
        return "GamePlayer{" +
                "player=" + player +
                ", milkTime=" + milkTime +
                ", data=" + data +
                ", team=" + getTeam() +
                ", toolLevels=" + toolLevels +
                ", settingItem=" + settingItem +
                ", armor=" + armor +
                ", quickBuying=" + quickBuying +
                ", state=" + state +
                ", lastDamageByEntity=" + lastDamageByEntity +
                ", lastDamageClearTime=" + lastDamageClearTime +
                ", game=" + game +
                ", spectatorSettings=" + spectatorSettings +
                ", actionBarText='" + actionBarText + '\'' +
                ", luckPermsEnabled=" + luckPermsEnabled +
                '}';
    }

    private final boolean luckPermsEnabled = Bukkit.getPluginManager().isPluginEnabled("LuckPerms");

    public void clearInventory() {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }

    public void checkSharpness() {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null)
                continue;

            if (!item.getType().name().contains("SWORD"))
                continue;

            new ItemBuilder(item).removeEnchantment(Enchantment.DAMAGE_ALL);
            if (this.getTeam().upgradeLevels.get(TeamUpgrade.SHARPNESS) > 0)
                new ItemBuilder(item).addEnchantment(Enchantment.DAMAGE_ALL, getTeam().upgradeLevels.get(TeamUpgrade.SHARPNESS));
        }
    }

    public static List<Material> higherThanWoodSwords = Arrays.asList(Material.STONE_SWORD, Material.IRON_SWORD, Material.DIAMOND_SWORD);

    public void checkWoodSword() {
        if (state != PlayerState.ALIVE)
            return;
        PlayerInventory inv = player.getInventory();
        int woodSwords = 0;
        int higherSwords = 0;
        if (player.getItemOnCursor() != null && player.getItemOnCursor().getType() == Material.WOOD_SWORD)
            woodSwords++;
        else if (player.getItemOnCursor() != null && higherThanWoodSwords.contains(player.getItemOnCursor().getType()))
            higherSwords++;
        for (ItemStack item : inv.getContents()) {
            if (item != null) {
                if (item.getType() == Material.WOOD_SWORD) {
                    woodSwords++;
                } else if (higherThanWoodSwords.contains(item.getType())) {
                    higherSwords++;
                }
            }
        }
        if (woodSwords > 1 && higherSwords <= 0) {
            ItemUtils.take(inv, Material.WOOD_SWORD, woodSwords - 1);
        } else if (woodSwords > 0 && higherSwords > 0) {
            ItemUtils.take(inv, Material.WOOD_SWORD, woodSwords);
        } else if (woodSwords <= 0 && higherSwords <= 0) {
            inv.addItem(WOOD_SWORD);
            checkSharpness();
        }
    }

    public void setIfWoodSwordIn(ItemStack is)
    {

        PlayerInventory inv = player.getInventory();
        int woodSwords = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null) {
                if (item.getType() == Material.WOOD_SWORD) {
                    if (woodSwords <= 0)
                    {
                        item.setType(is.getType());
                        item.setItemMeta(is.getItemMeta());
                        item.setAmount(is.getAmount());
                        item.setDurability(is.getDurability());
                        item.setData(is.getData());
                    }
                    woodSwords++;


                }
            }
        }
        if (woodSwords < 1)
        {
            inv.addItem(is);
        }

    }



    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    public void setVisible(boolean visible) {
        for (GamePlayer player : GamePlayer.getOnlineGamePlayers()) {
            if (player.player == this.player)
                continue;
            if (player.state != PlayerState.ALIVE)
                continue;
            if (visible)
                player.player.showPlayer(this.player);
            else
                player.player.hidePlayer(this.player);
        }
    }

    public void smartDamage(double damage)
    {
        if (player.getHealth() - damage > 0) {
            player.setHealth(player.getHealth() - damage);
            Bukkit.getPluginManager().callEvent(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.MAGIC, damage));
            player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_HIT, 1, 1);
        }
        else
            player.damage(damage);


        for (Player player : Bukkit.getOnlinePlayers())
            GamePlayer.get(player).playDamageAnimation();

    }

    public void playEffectAround(Effect effect, int points)
    {
        Location origin = player.getEyeLocation();
        for (int i = 0;i<points;i++)
        {
            Location location = new Location(origin.getWorld(), origin.getX() + Math.random(), origin.getY() + Math.random(), origin.getZ() + Math.random());
            player.getWorld().playEffect(location, effect, 3);
        }
    }

    public void smartDamage(double damage, GamePlayer damager, EntityDamageEvent.DamageCause cause)
    {
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager.player, player, cause, damage);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        smartDamage(damage);
    }

    private void playDamageAnimation()
    {
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation(this.toNMSEntity(), 50);
        this.sendPacket(packet);
    }

    private EntityPlayer toNMSEntity()
    {
        return ((CraftPlayer) player).getHandle();
    }


    public void sendTitle(String bigTitle, String subTitle, int fadeIn, int stay, int fadeOut)
    {
        TitleUtils.sendTitle(player, fadeIn, stay, fadeOut, bigTitle, subTitle);
    }

    public void playSound(Sound sound, boolean visible)
    {
        player.playSound(player.getLocation(), sound, 1.0f, visible ? 1.0f : 0.0f);
    }

    public List<GamePlayer> getNearestPlayers(double radios)
    {
        List<GamePlayer> players = new ArrayList<>();
        for (Entity entity : player.getNearbyEntities(radios, radios, radios))
        {
            if (entity instanceof Player && !CitizensAPI.getNPCRegistry().isNPC(entity))
            {
                players.add(GamePlayer.get((Player) entity));
            }
        }

        return players;
    }

    public void heal(int health)
    {
        player.setHealth(Math.min(health + player.getHealth(), player.getMaxHealth()));
        PacketPlayOutAnimation healAnimation = new PacketPlayOutAnimation(this.toNMSEntity(), 0);
        this.sendPacket(healAnimation);
    }


    public void death() {
        resetEntityState();
        PlayerDeadEvent event;
        if (this.getLastAttacker() != null) {
            event = new PlayerGotKilledEvent(this, this.getLastAttacker(), getTeam().state != TeamState.ALIVE);
        }
        else if (player.getLastDamageCause() != null)
        {
            event = new PlayerDeadEvent(this, player.getLastDamageCause().getCause(), getTeam().state != TeamState.ALIVE);
        }
        else
            event = new PlayerDeadEvent(this, EntityDamageEvent.DamageCause.VOID, getTeam().state != TeamState.ALIVE);

        Bukkit.getPluginManager().callEvent(event);
        List<ItemStack> resources = getResourceFromInventory();

        boolean drop = event.drop;
        if (drop)
            giveResourcesTo();

        player.setBedSpawnLocation(game.map.middle, true);
        player.spigot().respawn();
        if (getTeam().state == TeamState.ALIVE) {
            for (PotionEffect effect : player.getActivePotionEffects())
                player.removePotionEffect(effect.getType());
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0), false);
            this.setVisible(false);
            player.getInventory().clear();
            for (ToolEquipment equipment : toolLevels.keySet())
            {
                if (toolLevels.get(equipment) <= 1)
                    continue;
                toolLevels.put(equipment, toolLevels.get(equipment)-1);
            }
            player.getInventory().setArmorContents(null);
            player.setAllowFlight(true);
            player.setFlying(true);
            safetyTeleport(game.map.middle);

            heal();

            player.spigot().setCollidesWithEntities(false);
            state = PlayerState.RESPAWNING;
            game.registerScoreboardTeams();

            int i = 0;
            for (float f = 1.7F; f < 2.0F; f = (float) (f + 0.1D)) {
                float Nf = f;
                Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(), () -> player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, Nf), (i * 2));
                i++;
            }
            int Ni = i;
            for (; i < Ni + 7; i++) {
                Bukkit.getScheduler().runTaskLater(Bedwars.getInstance(), () -> player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 2.0F), (i * 2));
            }


            new BukkitRunnable() {

                int time = 5;

                public void run() {

                    GamePlayer.this.sendTitle("§c你失败了!", "§e你将在 §c" + time + " §e秒后重生!", 0, 25, 0);
                    if (time > 0)
                        player.sendMessage("§e你将在§c" + time + "§e秒后重生!");


                    if (time <= 0)
                    {
                        GamePlayer.this.sendTitle("§a已重生!", "", 5, 10 , 5);
                        player.sendMessage("§e你已经重生!");
                        spawn();
                        if (!drop)
                        {
                            for (ItemStack item : resources)
                                player.getInventory().addItem(item);
                        }
                        cancel();
                        return;
                    }
                    time--;
                }
            }.runTaskTimer(Bedwars.getInstance(), 0, 20L);
        } else {
            this.toSpectator();
        }
        //TODO DEATH ACTIONS
    }

    public void safetyTeleport(Location location)
    {
        PlayerListener.noDamages.add(player);
        player.teleport(location);
        new BukkitRunnable()
        {
            @Override
            public void run() {
                PlayerListener.noDamages.remove(player);
            }
        }.runTaskLater(Bedwars.getInstance(), 30);
    }

    public GamePlayer getLastAttacker()
    {
        if (lastDamageByEntity == null)
            return null;
        if (lastDamageByEntity.getDamager() instanceof Player)
            return GamePlayer.get((Player) lastDamageByEntity.getDamager());
        else if (lastDamageByEntity.getDamager() instanceof Fireball)
            return GamePlayer.get((Player) ((Fireball) lastDamageByEntity.getDamager()).getShooter());
        return null;
    }

    public void toSpectator() {
        resetEntityState();
        GamePlayer.this.sendTitle("§c你失败了!", "", 0, 25, 0);
        state = PlayerState.SPECTATING;
        giveSpectatingItems();
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.setGameMode(GameMode.ADVENTURE);
        setVisible(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1), false);
        safetyTeleport(game.map.middle);
        spectatorSettings.startTask();
        game.registerScoreboardTeams();
    }

    public void resetEntityState()
    {
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setLevel(0);
        player.setExp(0);
        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(activePotionEffect.getType());
        }

        player.setGameMode(GameMode.ADVENTURE);
        player.closeInventory();
        player.setVelocity(new Vector().zero());
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setFoodLevel(20);
        player.resetTitle();
        toNMSEntity().setSpectatorTarget(null);
    }

    public void giveSpectatingItems()
    {
        this.clearInventory();
        ItemStack compass = new ItemBuilder(Material.COMPASS).setDisplayName("§a§l传送器 §7(右键点击)")
                .setLore("§7右键来观察玩家！").build();
        ItemStack spectatorSettings = new ItemBuilder(Material.REDSTONE_COMPARATOR).setDisplayName("§b§l旁观者设置 §7(右键点击)")
                .setLore("§7右键点击更改你的旁观者设置！").build();
        ItemStack playAgain = new ItemBuilder(Material.PAPER).setDisplayName("§b§l再玩一局 §7(右键点击)")
                .setLore("§7右键点击来玩另一件游戏！").build();
        ItemStack leave = new ItemBuilder(Material.BED).setDisplayName("§c§l返回大厅 §7(右键点击)")
                .setLore("§7右键离开并返回大厅！").build();
        player.getInventory().setItem(0, compass);
        player.getInventory().setItem(4, spectatorSettings);
        if (Bukkit.getPluginManager().isPluginEnabled("ServerJoiner"))
            player.getInventory().setItem(7, playAgain);
        player.getInventory().setItem(8, leave);
    }

    class GamePlayerCompass
    {
        public GamePlayer currentTarget = null;
        public Team targetTeam;
        public BukkitTask delectTask = null;

        public void start(Team targetTeam)
        {
            this.targetTeam = targetTeam;
            if (delectTask != null)
                delectTask.cancel();
            new BukkitRunnable()
            {
                public void run()
                {
                    if (targetTeam == null)
                    {
                        this.cancel();
                        return;
                    }

                    delect();
                }
            }.runTaskTimer(Bedwars.getInstance(), 0, 8);
        }
        public void delect()
        {
            if (targetTeam == null)
                return;
            if (targetTeam.getAlive().isEmpty())
            {
                ActionBarUtil.sendActionBar(player, "§c没有任何存活玩家！");
                return;
            }
            GamePlayer closest = null;
            for (GamePlayer p : targetTeam.getAlive())
            {
                if (closest == null)
                {
                    closest = p;
                    continue;
                }
                if (player.getLocation().distance(p.player.getLocation()) < player.getLocation().distance(closest.player.getLocation()))
                    closest = p;
            }
            if (closest == null)
            {
                ActionBarUtil.sendActionBar(player, "§c没有任何存活玩家！");
                return;
            }
            currentTarget = closest;
            ActionBarUtil.sendActionBar(player, "§f追踪: " + toBold(closest.getColoredName())
                    + " §f距离: §a§l" + player.getLocation().distance(closest.player.getLocation()));

        }
    }

    public static String toBold(String string)
    {
        String regex = "§([0-9]|[a-z])";
        if (!string.matches(regex))
            return string;
        char c = string.charAt(string.indexOf("§") + 1);
        string = string.replaceFirst(regex, "§" + c + "§l");
        return string;
    }

    public String getColoredName() {
        if (game.state == GameState.WAITING) {
            if (!luckPermsEnabled) {
                return "§7" + player.getName();
            }
            String prefix = LuckPermsUtils.getPrefix(player);
            int colorCharIndex = prefix.indexOf("§");
            if (colorCharIndex == -1)
                colorCharIndex = prefix.indexOf("&");
            String color = "§7";
            if (colorCharIndex != -1)
                color = "§" + prefix.charAt(colorCharIndex + 1);
            return color + player.getName();
        }
        return this.
                getTeam().
                color.
                chatColor
                + player
                .getName();
    }

    public String getPrefixedName() {
        if (!luckPermsEnabled)
            return "§7" + player.getName();
        String prefix = "§7";
        String suffix = "";
        User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
        if (user == null)
            return prefix + player.getName();
        CachedMetaData metaData = user.getCachedData().getMetaData(QueryOptions.defaultContextualOptions());
        if (metaData.getPrefix() != null)
            prefix = ChatColor.translateAlternateColorCodes('&', metaData.getPrefix()) + " ";
        if (metaData.getSuffix() != null)
            suffix = " " + ChatColor.translateAlternateColorCodes('&', metaData.getSuffix());
        return prefix + player.getName() + suffix;
    }

    public void hideArmor() {
        net.minecraft.server.v1_8_R3.ItemStack air = CraftItemStack.asNMSCopy(new ItemStack(Material.AIR));
        PacketPlayOutEntityEquipment equipment1 = new PacketPlayOutEntityEquipment(player.getEntityId(), 1, air);
        PacketPlayOutEntityEquipment equipment2 = new PacketPlayOutEntityEquipment(player.getEntityId(), 2, air);
        PacketPlayOutEntityEquipment equipment3 = new PacketPlayOutEntityEquipment(player.getEntityId(), 3, air);
        PacketPlayOutEntityEquipment equipment4 = new PacketPlayOutEntityEquipment(player.getEntityId(), 4, air);
        List<Player> notTeammates = new ArrayList<>();
        for (GamePlayer gamePlayer : game.getAlive()) {
            if (gamePlayer.getTeam() != getTeam())
                notTeammates.add(gamePlayer.player);
        }
        game.removeEntry(player.getName(), notTeammates);
        new BukkitRunnable() {
            Location moveLocation = player.getLocation().clone();

            @Override
            public void run() {
                if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    showArmor();
                    cancel();
                    game.registerScoreboardTeams();
                    return;
                }



                if (player.isOnline() && (player.getLocation().getX() != moveLocation.getX() || player.getLocation().getY() != moveLocation.getY() || player.getLocation().getZ() != moveLocation.getZ())) {
                    moveLocation = player.getLocation().clone();
                    player.getLocation().getWorld().playEffect(player.getLocation().clone()
                            .add((Math.random() - Math.random()) * 0.15D, 0.03D, (Math.random() - Math.random()) * 0.15D), Effect.FOOTSTEP, 0);
                }

                for (GamePlayer p : GamePlayer.getOnlineGamePlayers()) {
                    if (p.getTeam() != GamePlayer.this.getTeam()) {
                        p.sendPacket(equipment1);
                        p.sendPacket(equipment2);
                        p.sendPacket(equipment3);
                        p.sendPacket(equipment4);
                    }
                }

            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 10);
    }

    public void sendPacket(Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void showArmor() {
        net.minecraft.server.v1_8_R3.ItemStack equipments1 = CraftItemStack.asNMSCopy(player.getInventory().getBoots());
        net.minecraft.server.v1_8_R3.ItemStack equipments2 = CraftItemStack.asNMSCopy(player.getInventory().getLeggings());
        net.minecraft.server.v1_8_R3.ItemStack equipments3 = CraftItemStack.asNMSCopy(player.getInventory().getChestplate());
        net.minecraft.server.v1_8_R3.ItemStack equipments4 = CraftItemStack.asNMSCopy(player.getInventory().getHelmet());
        PacketPlayOutEntityEquipment packet1 = new PacketPlayOutEntityEquipment(player.getEntityId(), 1, equipments1);
        PacketPlayOutEntityEquipment packet2 = new PacketPlayOutEntityEquipment(player.getEntityId(), 2, equipments2);
        PacketPlayOutEntityEquipment packet3 = new PacketPlayOutEntityEquipment(player.getEntityId(), 3, equipments3);
        PacketPlayOutEntityEquipment packet4 = new PacketPlayOutEntityEquipment(player.getEntityId(), 4, equipments4);

        for (GamePlayer p : GamePlayer.getOnlineGamePlayers()) {
            if (p.getTeam() != GamePlayer.this.getTeam()) {
                p.sendPacket(packet1);
                p.sendPacket(packet2);
                p.sendPacket(packet3);
                p.sendPacket(packet4);
            }
        }
    }

    public void spawn() {

        this.lastDamageByEntity = null;
        this.setVisible(true);
        player.setFireTicks(0);
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
        player.setAllowFlight(true);
        player.setFlying(true);
        safetyTeleport(getTeam().spawnLocation);
        player.spigot().setCollidesWithEntities(true);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.setMaxHealth(20);
        heal();
        game.registerScoreboardTeams();
        this.clearInventory();
        armor.set(this);
        state = PlayerState.ALIVE;
        game.registerScoreboardTeams();

        player.getInventory().addItem(WOOD_SWORD);
        checkWoodSword();

        for (GamePlayer player : GamePlayer.getOnlineGamePlayers())
        {
            if (player.state == PlayerState.ALIVE)
                player.player.showPlayer(this.player);
        }

        for (ToolEquipment equipment : toolLevels.keySet())
            equipment.set(this);

        checkSharpness();

        Bukkit.getPluginManager().callEvent(new PlayerSpawnEvent(this));



    }

    public void healthDisplay() {



    }


    public List<ItemStack> getResourceFromInventory()
    {
        Map<ResourceType, Integer> amounts = new HashMap<>();
        Inventory inventory = player.getInventory();
        for (ItemStack item : inventory.getContents())
        {
            if (item == null)
                continue;
            for (ResourceType type : ResourceType.values())
            {
                if (item.getType() == type.material)
                {
                    amounts.put(type, amounts.getOrDefault(type, 0) + item.getAmount());
                }
            }
        }
        Map<ResourceType, List<ItemStack>> itemStacks = new HashMap<>();
        for (ResourceType type : amounts.keySet())
        {
            int amount = amounts.get(type);
            if (amount <= 64)
            {
                List<ItemStack> items = itemStacks.getOrDefault(type, new ArrayList<>());
                items.add(new ItemStack(type.material, amount));
                itemStacks.put(type, items);
            }
            else
            {
                int surplus = amount;
                for (;surplus >= 64;surplus-=64)
                {
                    List<ItemStack> items = itemStacks.getOrDefault(type, new ArrayList<>());
                    items.add(new ItemStack(type.material, 64));
                    itemStacks.put(type, items);
                }
                if (surplus > 0)
                {
                    List<ItemStack> items = itemStacks.getOrDefault(type, new ArrayList<>());
                    items.add(new ItemStack(type.material, surplus));
                    itemStacks.put(type, items);
                }
            }
        }

        List<ItemStack> all = new ArrayList<>();
        for (List<ItemStack> itemStackList : itemStacks.values())
            all.addAll(itemStackList);
        return all;
    }

    public void giveResourcesTo()
    {

        Map<ResourceType, Integer> amounts = new HashMap<>();
        Inventory inventory = player.getInventory();
        for (ItemStack item : inventory.getContents())
        {
            if (item == null)
                continue;
            for (ResourceType type : ResourceType.values())
            {
                if (item.getType() == type.material)
                {
                    amounts.put(type, amounts.getOrDefault(type, 0) + item.getAmount());
                }
            }
        }
        List<String> messages = new ArrayList<>();
        Map<ResourceType, List<ItemStack>> itemStacks = new HashMap<>();
        for (ResourceType type : amounts.keySet())
        {
            int amount = amounts.get(type);
            messages.add("§a + " + type.color + amount + " " + type.cn);
            if (amount <= 64)
            {
                List<ItemStack> items = itemStacks.getOrDefault(type, new ArrayList<>());
                items.add(new ItemStack(type.material, amount));
                itemStacks.put(type, items);
            }
            else
            {
                int surplus = amount;
                for (;surplus >= 64;surplus-=64)
                {
                    List<ItemStack> items = itemStacks.getOrDefault(type, new ArrayList<>());
                    items.add(new ItemStack(type.material, 64));
                    itemStacks.put(type, items);
                }
                if (surplus > 0)
                {
                    List<ItemStack> items = itemStacks.getOrDefault(type, new ArrayList<>());
                    items.add(new ItemStack(type.material, surplus));
                    itemStacks.put(type, items);
                }
            }
        }
        if (lastDamageByEntity != null)
        {
            GamePlayer killer = null;
            if (lastDamageByEntity.getDamager() instanceof Player)
                killer = GamePlayer.get((Player) lastDamageByEntity.getDamager());
            else if (lastDamageByEntity.getDamager() instanceof Projectile)
                killer = GamePlayer.get((Player) ((Projectile) lastDamageByEntity.getDamager()).getShooter());
            if (killer == null || killer == this)
            {
                Location location = player.getLocation().clone();
                for (ResourceType type : itemStacks.keySet())
                {
                    for (ItemStack itemStack : itemStacks.get(type))
                    {
                        Item entity = location.getWorld().dropItem(location.clone().add(0, 0.5, 0), itemStack);
                        entity.setVelocity(new Vector());
                        entity.setPickupDelay(5);
                    }
                }
                return;
            }
            for (String msg : messages)
                killer.sendMessage(msg);
            for (ResourceType type : itemStacks.keySet())
            {
                for (ItemStack itemStack : itemStacks.get(type))
                {
                    killer.player.getInventory().addItem(itemStack);
                }
            }
        }
        else
        {
            Location location = player.getLocation().clone();
            for (ResourceType type : itemStacks.keySet())
            {
                for (ItemStack itemStack : itemStacks.get(type))
                {
                    Item entity = location.getWorld().dropItem(location.clone().add(0, 0.5, 0), itemStack);
                    entity.setVelocity(new Vector());
                    entity.setPickupDelay(5);
                }
            }
        }

    }

    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (o instanceof GamePlayer)
        {
            GamePlayer go = (GamePlayer) o;
            return go.player.getName().equals(player.getName());
        }
        return false;
    }



    public EntityDamageByEntityEvent getLastDamageByEntity() {
        return lastDamageByEntity;
    }

    public String getDisplayName() {
        return this.game.state == GameState.WAITING ? "§7" : getTeam().color.chatColor + player.getName();
        //TODO 替换 ‘§7’ 至 Vault 前缀
    }


    public void giveWaitingItems() {
        this.clearInventory();
        ItemStack selectTeam = new ItemBuilder(Material.WOOL).setDyeColor(getTeam().color.dyeColor)
                .setDisplayName("§a§l选择队伍 §7(右键点击)").build();
        ItemStack leave = new ItemBuilder(Material.BED).setDisplayName("§c§l返回大厅 §7(右键点击)").build();
        ItemStack forceStart = new ItemBuilder(Material.DIAMOND).setDisplayName("§b§l强制游戏 §7(右键点击)").build();
        if (Bedwars.getInstance().getConfig().getBoolean("select-team"))
            player.getInventory().setItem(0, selectTeam);
        if (player.isOp())
            player.getInventory().setItem(7, forceStart);
        player.getInventory().setItem(8, leave);
    }

    private static final Set<GamePlayer> gamePlayers = new HashSet<>();

    public static GamePlayer get(Player player) {
        if (CitizensAPI.getNPCRegistry().isNPC(player))
            throw new IllegalStateException("The player cannot be a NPC");
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.player.getName().equals(player.getName()))
                return gamePlayer;
        }

        GamePlayer gamePlayer = new GamePlayer(player);
        gamePlayers.add(gamePlayer);
        return gamePlayer;
    }

    public void heal()
    {
        player.setHealth(player.getMaxHealth());
    }

    public static Set<GamePlayer> getOnlineGamePlayers() {
        Set<GamePlayer> gamePlayers = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (CitizensAPI.getNPCRegistry().isNPC(player))
                continue;
            gamePlayers.add(GamePlayer.get(player));
        }

        return gamePlayers;
    }

    public static void remove(GamePlayer player)
    {
        gamePlayers.remove(player);
    }

    public static Set<GamePlayer> getSavedGamePlayers() {
        return gamePlayers;
    }
}

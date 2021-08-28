package me.faintcloudy.bedwars.game;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.events.BedwarsGameEvent;
import me.faintcloudy.bedwars.events.GameEndedEvent;
import me.faintcloudy.bedwars.events.GameStartedEvent;
import me.faintcloudy.bedwars.game.events.GameEvent;
import me.faintcloudy.bedwars.game.resource.DeclaredResourceSpawner;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.team.Team;
import me.faintcloudy.bedwars.game.team.TeamColor;
import me.faintcloudy.bedwars.game.team.TeamState;
import me.faintcloudy.bedwars.listener.NPCListener;
import me.faintcloudy.bedwars.listener.PlayerListener;
import me.faintcloudy.bedwars.tasks.GameEventTimer;
import me.faintcloudy.bedwars.tasks.GameStateCheckTimer;
import me.faintcloudy.bedwars.utils.LocationUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.Gravity;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import net.citizensnpcs.trait.waypoint.triggers.SpeedTrigger;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.omg.CORBA.CODESET_INCOMPATIBLE;

import java.util.*;

public class Game {
    public GameMap map;
    public List<GameEvent> gameEvents;
    public int nextEventTime;
    public Team spectatorTeam = new Team(TeamColor.NONE, this);
    public World gameWorld;
    public World lobbyWorld;
    public GameState state;
    public BedwarsMode mode;
    public int startTime;
    public GameEvent nextEvent;
    public long gameStartedTime;

    public boolean gameCheck = true;

    public Map<ResourceType, Integer> tiers = new HashMap<>();
    public List<DeclaredResourceSpawner> declaredResourceSpawners = new ArrayList<>();
    public GameScale scale;
    public List<Team> teams = new ArrayList<>();
    public Game(GameMap map, GameScale scale, BedwarsMode mode)
    {

        gameEvents = Arrays.asList(GameEvent.DIAMOND_RESOURCE_UPGRADE_II, GameEvent.EMERALD_RESOURCE_UPGRADE_II,
                        GameEvent.DIAMOND_RESOURCE_UPGRADE_III, GameEvent.EMERALD_RESOURCE_UPGRADE_III,
                        GameEvent.BED_GONE, GameEvent.SUDDEN_DEATH, GameEvent.GAME_END);

        nextEvent = gameEvents.get(0);
        nextEventTime = nextEvent.time();
        this.tiers.put(ResourceType.DIAMOND, 1);
        this.tiers.put(ResourceType.EMERALD, 1);
        this.scale = scale;

        this.map = map;
        this.startTime = Bedwars.getInstance().getConfig().getInt("start-time");
        for (TeamColor color : scale.getTeamColors())
        {
            if (color == TeamColor.NONE)
                continue;
            teams.add(new Team(color, this));
        }
        this.state = GameState.WAITING;
        this.mode = mode;
        mode.manager.init();

        this.gameWorld = this.map.middle.getWorld();
        this.gameWorld.setDifficulty(Difficulty.NORMAL);
        this.gameWorld.setStorm(false);
        this.gameWorld.setThundering(false);
        this.gameWorld.setAutoSave(false);
        this.gameWorld.setTicksPerAnimalSpawns(Integer.MAX_VALUE);
        this.gameWorld.setTicksPerMonsterSpawns(Integer.MAX_VALUE);
        this.gameWorld.setGameRuleValue("doMobLoot", "false");
        this.gameWorld.setGameRuleValue("doMobSpawning", "false");
        this.gameWorld.setGameRuleValue("keepInventory", "true");
        this.gameWorld.setGameRuleValue("mobGriefing", "true");
        this.gameWorld.setGameRuleValue("naturalRegeneration", "true");
        this.gameWorld.setGameRuleValue("showDeathMessages", "false");
        this.gameWorld.setAutoSave(false);
        this.gameWorld.getEntities().forEach(entity ->
        {
            if (entity instanceof Painting || entity instanceof ItemFrame)
                return;
            entity.remove();
        });

        this.lobbyWorld = this.map.lobby.getWorld();
        if (this.lobbyWorld == this.gameWorld)
            return;

        this.lobbyWorld.setDifficulty(Difficulty.PEACEFUL);
        this.lobbyWorld.setPVP(false);
        this.lobbyWorld.setStorm(false);
        this.lobbyWorld.setThundering(false);
        this.lobbyWorld.setTicksPerMonsterSpawns(Integer.MAX_VALUE);
        this.lobbyWorld.setTicksPerAnimalSpawns(Integer.MAX_VALUE);
        this.lobbyWorld.setGameRuleValue("doMobLoot", "false");
        this.lobbyWorld.setGameRuleValue("doMobSpawning", "false");
        this.lobbyWorld.setGameRuleValue("keepInventory", "true");
        this.lobbyWorld.setGameRuleValue("mobGriefing", "false");
        this.gameWorld.setGameRuleValue("naturalRegeneration", "true");
    }

    public int getEventOrder(GameEvent event)
    {
        for (int i = 0;i<gameEvents.size();i++)
        {
            if (gameEvents.get(i) == event)
                return i;
        }

        return -1;
    }

    public boolean region(Location location)
    {
        for (DeclaredResourceSpawner spawner : this.declaredResourceSpawners)
        {
            if (spawner.location.distance(location) <= 2)
                return true;
        }

        for (Team team : teams)
        {
            if (team.spawnLocation.distance(location) <= 5)
                return true;
            if (team.resourceLocation.distance(location) <= 2)
                return true;
        }

        return map.outOfBorder(location);
    }

    public void onEnd()
    {

        state = GameState.ENDED;
        List<Team> aliveTeams = new ArrayList<>();
        for (Team team : teams)
        {
            team.checkState();
            if (team.state != TeamState.DEAD)
                aliveTeams.add(team);
        }

        Team winner = null;
        if (aliveTeams.size() < 2)
        {
            if (aliveTeams.size() > 0)
                winner = aliveTeams.get(0);
            else
                winner = null;
        }
        else
        {
            for (Team team : aliveTeams)
            {
                if (winner == null)
                {
                    winner = team;
                    continue;
                }

                if (team.getTotalFinalKills() > winner.getTotalFinalKills())
                    winner = team;
            }
        }

        if (winner == null)
        {
            for (GamePlayer player : GamePlayer.getOnlineGamePlayers())
            {
                player.sendTitle("§7平局", "", 0, 200, 0);
            }
        }
        else
        {
            for (GamePlayer player : GamePlayer.getOnlineGamePlayers())
            {
                if (winner.players.contains(player))
                {
                    player.sendTitle("§6§l胜利", "", 0, 200, 0);
                    player.data.currentGameData.win++;
                }
                else
                {
                    player.sendTitle("§c§l游戏结束", "", 0, 200, 0);
                    player.data.currentGameData.lose++;
                }
            }
        }

        for (GamePlayer player : GamePlayer.getSavedGamePlayers())
            player.data.saveData();

        HashMap<GamePlayer, Integer> finalKills = new HashMap<>();
        GamePlayer.getSavedGamePlayers().forEach(player -> finalKills.put(player, player.data.currentGameData.finalKills));
        List<Map.Entry<GamePlayer, Integer>> list = new ArrayList<>(finalKills.entrySet());
        list.sort(Map.Entry.comparingByValue());
        List<Map.Entry<GamePlayer, Integer>> tops = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            tops.add(list.get(i));
        }


        StringBuilder winTeamMembers = new StringBuilder();
        if (winner != null)
        {
            for (GamePlayer player : winner.players) {
                winTeamMembers.append(player.getPrefixedName());
                winTeamMembers.append(", ");
            }
            if (!winTeamMembers.toString().isEmpty())
                winTeamMembers.deleteCharAt(winTeamMembers.toString().length()-2);
        }


        Map.Entry<GamePlayer, Integer> top1 = tops.size() < 1 ? null : tops.get(0);
        Map.Entry<GamePlayer, Integer> top2 = tops.size() < 2 ? null : tops.get(1);
        Map.Entry<GamePlayer, Integer> top3 = tops.size() < 3 ? null : tops.get(2);
        String[] winAndTop = new String[]{
                "§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "                                   §a起床战争",
                "",
                "                           " + (winner == null ? "§7无" : winner.color.chatColor + winner.color.cn) + " §7- " + winTeamMembers.toString(),
                "",
                "",
                "                   §a击杀数第一名 §7- " + ((top1 == null) ? "无" : top1.getKey().getPrefixedName() + " §7- §a" + top1.getKey().data.currentGameData.kills),
                "                   §6击杀数第二名 §7- " + ((top2 == null) ? "无" : top2.getKey().getPrefixedName() + " §7- §6" + top2.getValue()),
                "                   §c击杀数第三名 §7- " + ((top3 == null) ? "无" : top3.getKey().getPrefixedName() + " §7- §c" + top3.getValue()),
                "",
                "§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"};
        GamePlayer.getOnlineGamePlayers().forEach(player -> player.player.sendMessage(winAndTop));
        for (GamePlayer player : GamePlayer.getOnlineGamePlayers())
        {
            String[] rewards = new String[] {
                    "§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                    "                            §f§l奖励总览§f§l",
                    "",
                    "   §7你获得了",
                    "     • §6" + player.data.currentGameData.coins + " 起床战争硬币",
                    "     • §b" + player.data.currentGameData.exp + " 起床战争经验",
                    "",
                    "§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"};
            player.player.sendMessage(rewards);
        }

        new BukkitRunnable()
        {
            public void run()
            {
                for (GamePlayer player : GamePlayer.getOnlineGamePlayers())
                {
                    Bedwars.getInstance().sendToLobby(player.player);
                }
                for (Block block : PlayerListener.blocksPut)
                {
                    if (!block.getChunk().isLoaded())
                        block.getChunk().load(true);
                    block.setType(Material.AIR);
                    block.getDrops().clear();
                    block.breakNaturally();
                }
                new BukkitRunnable()
                {
                    public void run()
                    {
                        Bukkit.shutdown();
                    }
                }.runTaskLater(Bedwars.getInstance(), 60);
            }
        }.runTaskLater(Bedwars.getInstance(), 140);
        Bedwars.callEvent(new GameEndedEvent(this, winner));
    }

    public List<GamePlayer> getAlive()
    {
        List<GamePlayer> alive = new ArrayList<>();
        for (Team team : teams)
        {
            alive.addAll(team.getAlive());
        }

        return alive;
    }

    public Team getTeamByBed(Block block)
    {
        Team closest = null;
        for (Team team : this.teams)
        {
            if (closest == null)
            {
                closest = team;
                continue;
            }

            if (team.bedLocation.distance(block.getLocation()) < closest.bedLocation.distance(block.getLocation()))
            {
                closest = team;
            }
        }

        return closest;
    }
    public void onStart()
    {
        gameStartedTime = System.currentTimeMillis();
        new GameEventTimer().runTaskTimer(Bedwars.getInstance(), 0, 20);

        for (Entity entity : gameWorld.getEntities())
        {
            if (!(entity instanceof Player || entity instanceof Painting || entity instanceof ItemFrame))
                entity.remove();
        }
        this.state = GameState.GAMING;
        for (GamePlayer player : GamePlayer.getOnlineGamePlayers())
        {
            if (player.getTeam() != this.spectatorTeam)
                continue;
            this.getLowestTeam().joinTeam(player);
        }



        this.initSpanwers();
        this.initNPCs();

        //分配队伍

        List<String> startMessages = new ArrayList<>();
        startMessages.add("§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        startMessages.add("                                   §f§l起床战争");
        startMessages.add("");
        for (String message : mode.manager.startMessage())
            startMessages.add("§e§l" + message);
        startMessages.add("");
        startMessages.add("§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        for (Team team : teams)
        {
            for (GamePlayer player : team.players)
            {
                player.player.sendMessage(startMessages.toArray(new String[0]));
                player.player.setBedSpawnLocation(team.spawnLocation);
                player.spawn();
            }
        }

        Bedwars.callEvent(new GameStartedEvent(this));
        new GameStateCheckTimer(this).start();
    }

    public void registerScoreboardTeams()
    {
        for (GamePlayer player : GamePlayer.getOnlineGamePlayers())
            registerScoreboardTeams(player);
    }

    public void removeEntry(String entry, List<Player> players)
    {
        for (Player player : players) {
            Scoreboard scoreboard = player.getScoreboard();
            scoreboard.getEntries().remove(entry);
        }
    }

    public void registerScoreboardTeams(GamePlayer player)
    {
        player.healthDisplay();
        if (state == GameState.WAITING)
        {
            org.bukkit.scoreboard.Team team = player.player.getScoreboard().getTeam("WAITING");
            if (team == null)
                team = player.player.getScoreboard().registerNewTeam("WAITING");
            for (GamePlayer p : GamePlayer.getOnlineGamePlayers())
            {
                team.addEntry(p.getColoredName());
            }
            return;
        }
        if (player.player.getScoreboard().getTeam("WAITING") != null)
            player.player.getScoreboard().getTeam("WAITING").unregister();
        if (player.state == GamePlayer.PlayerState.ALIVE)
        {
            for (Team gt : teams)
            {
                if (gt.state == TeamState.DEAD)
                    return;
                org.bukkit.scoreboard.Team team = player.player.getScoreboard().getTeam(gt.color.name());
                if (team == null)
                {
                    team = player.player.getScoreboard().registerNewTeam(gt.color.name());
                    team.setPrefix(gt.color.chatColor + "§l" + gt.color.chatColor + gt.color.en + " ");
                }
                for (GamePlayer member : gt.getAlive())
                    team.addEntry(member.player.getName());
            }

        }
        if (player.state == GamePlayer.PlayerState.RESPAWNING)
        {
            org.bukkit.scoreboard.Team team = player.player.getScoreboard().getTeam("RESPAWNING");
            if (team == null)
            {
                team = player.player.getScoreboard().registerNewTeam("RESPAWNING");
                team.setPrefix("§7");
            }
            for (GamePlayer p : GamePlayer.getOnlineGamePlayers())
            {
                if (p.state == GamePlayer.PlayerState.RESPAWNING)
                    team.addEntry(p.player.getName());
            }
        }
        org.bukkit.scoreboard.Team team = player.player.getScoreboard().getTeam("SPECTATING");
        if (team == null)
        {
            team = player.player.getScoreboard().registerNewTeam("SPECTATING");
            team.setPrefix("§7");
        }
        for (GamePlayer p : GamePlayer.getOnlineGamePlayers())
        {
            if (p.state == GamePlayer.PlayerState.SPECTATING)
                team.addEntry(p.player.getName());
        }
        if (player.state == GamePlayer.PlayerState.ALIVE)
        {
            if (player.player.getScoreboard().getTeam("SPECTATING") != null)
                player.player.getScoreboard().getTeam("SPECTATING").unregister();
            if (player.player.getScoreboard().getTeam("RESPAWNING") != null)
                player.player.getScoreboard().getTeam("RESPAWNING").unregister();
        }
        if (player.state == GamePlayer.PlayerState.RESPAWNING)
        {
            if (player.player.getScoreboard().getTeam("SPECTATING") != null)
                player.player.getScoreboard().getTeam("SPECTATING").unregister();
        }
        if (player.state == GamePlayer.PlayerState.SPECTATING)
        {
            if (player.player.getScoreboard().getTeam("RESPAWNING") != null)
                player.player.getScoreboard().getTeam("RESPAWNING").unregister();
        }
    }

    public List<GamePlayer> getSpectators() {
        List<GamePlayer> spectators = new ArrayList<>();
        for (GamePlayer player : GamePlayer.getOnlineGamePlayers()) {
            if (player.state != GamePlayer.PlayerState.SPECTATING) {
                spectators.add(player);
            }
        }
        return spectators;
    }

    private String spaces(int amount)
    {
        StringBuilder ss = new StringBuilder();
        for (int i = 0;i<amount;i++)
        {
            ss.append(" ");
        }
        return ss.toString();
    }

    public void initSpanwers()
    {
        for (ResourceType type : map.declaredResourceLocations.keySet())
        {
            for (Location location : map.declaredResourceLocations.get(type))
            {
                DeclaredResourceSpawner spawner = new DeclaredResourceSpawner(type, location);
                spawner.start();
                declaredResourceSpawners.add(spawner);
            }
        }

        for (Team team : teams)
        {
            team.ironSpawner.start();
            team.goldSpawner.start();
        }
    }

    public void initNPCs()
    {
        CitizensAPI.getNPCRegistry().deregisterAll();
        CitizensAPI.getNPCRegistry().despawnNPCs(DespawnReason.PLUGIN);
        for (Team team : teams)
        {
            NPC itemShop = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "§e§l右键点击!");

            itemShop.getTrait(LookClose.class).lookClose(true);
            itemShop.getTrait(Gravity.class).toggle();
            itemShop.getTrait(SkinTrait.class).setSkinName("FaintCloudy", true);
            Location location = team.itemShopLocation.clone();
            location.setPitch(45F);
            itemShop.spawn(location);
            itemShop.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            itemShop.getEntity().setMetadata("Shop-Type-Item", new FixedMetadataValue(Bedwars.getInstance(), "shop-npc"));
            itemShop.getTrait(HologramTrait.class).addLine("§b道具商店");
            NPCListener.itemShops.add(itemShop);
        }

        for (Team team : teams)
        {
            NPC teamShop = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "§e§l右键点击!");
            teamShop.getTrait(LookClose.class).lookClose(true);
            teamShop.getTrait(Gravity.class).toggle();
            teamShop.getTrait(SkinTrait.class).setSkinName("Yeoc", true);
            Location location = team.teamShopLocation.clone();
            location.setPitch(45F);
            teamShop.spawn(location);
            teamShop.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            teamShop.getEntity().setMetadata("Shop-Type-Team", new FixedMetadataValue(Bedwars.getInstance(), "shop-npc"));
            teamShop.getTrait(HologramTrait.class).addLine("§b商店");
            teamShop.getTrait(HologramTrait.class).addLine("§b团队模式");
            NPCListener.teamShops.add(teamShop);
        }
    }

    public Team getLowestTeam()
    {
        Team lowest = null;
        for (Team team : teams)
        {
            if (lowest == null)
            {
                lowest = team;
                continue;
            }

            if (team.players.size() < lowest.players.size())
                lowest = team;
        }

        return lowest;
    }


}

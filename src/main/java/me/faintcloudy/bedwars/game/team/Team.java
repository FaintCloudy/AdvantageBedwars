package me.faintcloudy.bedwars.game.team;

import me.faintcloudy.bedwars.game.Game;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.resource.TeamResourceSpawner;
import me.faintcloudy.bedwars.game.shop.TrapInfo;
import me.faintcloudy.bedwars.game.team.upgrade.TeamUpgrade;
import me.faintcloudy.bedwars.game.team.upgrade.TrapUpgrade;
import me.faintcloudy.bedwars.holographic.SimpleHologram;
import me.faintcloudy.bedwars.utils.LocationUtils;
import me.faintcloudy.bedwars.utils.VarCheck;
import net.minecraft.server.v1_8_R3.ItemMonsterEgg;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffectType;

import javax.persistence.SecondaryTable;
import java.util.*;

public class Team {
    public TeamColor color;
    public Game game;
    public TeamState state;
    public TeamResourceSpawner ironSpawner, goldSpawner, emeraldSpawner;
    public Map<TeamUpgrade, Integer> upgradeLevels = new HashMap<>();
    public Map<Integer, TrapInfo> trapInfos = new HashMap<>();
    public Location spawnLocation, bedLocation, resourceLocation, itemShopLocation, teamShopLocation;
    public Set<GamePlayer> players = new HashSet<>();
    public Team(TeamColor color, Game game)
    {
        this.game = game;
        this.upgradeLevels.put(TeamUpgrade.PROTECTION, 0);
        this.upgradeLevels.put(TeamUpgrade.SHARPNESS, 0);
        this.upgradeLevels.put(TeamUpgrade.HASTE, 0);
        this.upgradeLevels.put(TeamUpgrade.HEAL_POOL, 0);
        this.upgradeLevels.put(TeamUpgrade.FORGE, 0);
        this.upgradeLevels.put(TeamUpgrade.DRAGON_BUFF, 0);
        this.upgradeLevels.put(TeamUpgrade.ITS_TRAP, 0);
        this.upgradeLevels.put(TeamUpgrade.ALARM_TRAP, 0);
        this.upgradeLevels.put(TeamUpgrade.COUNTER_ATTACK_TRAP, 0);
        this.upgradeLevels.put(TeamUpgrade.MINER_FATIGUE_TRAP, 0);
        if (color == TeamColor.NONE)
        {
            this.state = TeamState.DEAD;
            this.color = color;

            return;
        }
        this.state = TeamState.ALIVE;
        this.color = color;
        try {
            this.spawnLocation = game.map.spawnLocations.get(color);
            this.bedLocation = game.map.bedLocations.get(color);
            this.resourceLocation = game.map.baseResourceLocations.get(color);
            this.itemShopLocation = game.map.itemShopLocations.get(color);
            this.teamShopLocation = game.map.teamShopLocations.get(color);
        } catch (NullPointerException exception)
        {
            exception.printStackTrace();
            Bukkit.broadcastMessage("§c加载地图时错误: 不完整的地图");
            System.out.println("§c加载地图时错误: 不完整的地图");
        }
        ironSpawner = new TeamResourceSpawner(ResourceType.IRON, resourceLocation, this);
        goldSpawner = new TeamResourceSpawner(ResourceType.GOLD, resourceLocation, this);
        emeraldSpawner = new TeamResourceSpawner(ResourceType.EMERALD, resourceLocation, this);

    }

    @Override
    public String toString() {
        return "Team{" +
                "color=" + color +
                ", game=" + game +
                ", state=" + state +
                ", ironSpawner=" + ironSpawner +
                ", goldSpawner=" + goldSpawner +
                ", emeraldSpawner=" + emeraldSpawner +
                ", upgradeLevels=" + upgradeLevels +
                ", trapInfos=" + trapInfos +
                ", spawnLocation=" + spawnLocation +
                ", bedLocation=" + bedLocation +
                ", resourceLocation=" + resourceLocation +
                ", itemShopLocation=" + itemShopLocation +
                ", teamShopLocation=" + teamShopLocation +
                ", broke=" + broke +
                '}';
    }

    public int getTotalFinalKills()
    {
        int finalKills = 0;
        for (GamePlayer player : this.getAlive())
        {
            finalKills += player.data.currentGameData.finalKills;
        }

        return finalKills;
    }


    public void brokeBed()
    {
        state = TeamState.BED_LESS;
        breakBedPhysically();
        this.checkState();
    }

    private boolean broke = false;

    private void breakBedPhysically()
    {
        Block bed = this.bedLocation.clone().getBlock();
        Block nBed = LocationUtils.getBedNeighbor(bed);
        if (bed.getType() == Material.BED_BLOCK)
            bed.setType(Material.AIR);
        if (nBed.getType() == Material.BED_BLOCK)
            bed.setType(Material.AIR);
        broke = true;
    }

    public boolean isNearBase(GamePlayer player)
    {
        return player.player.getLocation().distance(spawnLocation.clone()) < 13;
    }

    public void chat(GamePlayer sender, String message)
    {
        String chat = color.chatColor + "[" + color.cn + "] " + sender.getPrefixedName() + "§f: " + message;
        players.forEach(player -> player.sendMessage(chat));
    }

    public void brokeBed(GamePlayer broker)
    {
        broker.data.currentGameData.bedBroken++;
        state = TeamState.BED_LESS;
        breakBedPhysically();
        new SimpleHologram(this.bedLocation.clone().add(0, -0.7, 0), "§7该床已被 " + broker.getColoredName() + " §7破坏了");
        for (GamePlayer player : GamePlayer.getOnlineGamePlayers())
        {
            if (player.state == GamePlayer.PlayerState.SPECTATING)
                continue;
            if (players.contains(player))
            {
                player.sendTitle("§c床已被破坏!", "§f死亡后无法重生!", 0, 20, 20);
                player.player.sendMessage("");
                player.player.sendMessage("§f§l床被破坏了 > §r你的床被拆了, 破坏者: " + broker.getColoredName());
                player.player.sendMessage("");
                player.player.playSound(player.player.getLocation(), Sound.WITHER_DEATH, 1.0F, 1.0F);
            }
            else
            {
                player.player.sendMessage("");
                player.player.sendMessage("§f§l床被破坏了 > " + this.color.chatColor + this.color.cn + "§r的床被拆了, 破坏者: " + broker.getColoredName());
                player.player.sendMessage("");
                player.player.playSound(player.player.getLocation(), Sound.ENDERDRAGON_GROWL, 1.0F, 1.0F);
            }

        }
        this.checkState();
    }

    public boolean isTeamChest(Block block)
    {
        Team closest = null;
        for (Team team : game.teams)
        {
            if (closest == null)
            {
                closest = team;
                continue;
            }

            if (team.spawnLocation.distance(block.getLocation()) < closest.spawnLocation.distance(block.getLocation()))
            {
                closest = team;
            }
        }
        if (closest == null)
        {
            return true;
        }
        if (closest.state == TeamState.DEAD)
        {
            return true;
        }

        return closest == this;
    }

    public List<GamePlayer> getAlive()
    {
        List<GamePlayer> alive = new ArrayList<>();
        for (GamePlayer player : players)
        {
            if (player.state == GamePlayer.PlayerState.SPECTATING)
                continue;
            alive.add(player);

        }

        return alive;
    }

    public void checkState()
    {
        if (!broke && state != TeamState.ALIVE)
            this.breakBedPhysically();
        if (this.state == TeamState.DEAD)
        {
            return;
        }
        if (this.getAlive().isEmpty())
        {
            this.state = TeamState.DEAD;
            for (GamePlayer player : GamePlayer.getOnlineGamePlayers())
            {
                player.player.sendMessage("");
                player.player.sendMessage(" §f§l团灭 > " + this.color.chatColor + this.color.cn + "§c已被淘汰!");
                player.player.sendMessage("");
            }
        }
    }

    public boolean isTeamBed(Block block)
    {
        Team closest = null;
        for (Team team : game.teams)
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

        return closest == this;
    }

    public int activeTraps()
    {
        int active = 0;
        for (TeamUpgrade upgrade : upgradeLevels.keySet())
        {
            if (upgrade instanceof TrapUpgrade)
            {
                if (upgradeLevels.get(upgrade) > 0)
                    active++;
            }
        }

        return active;
    }

    public void joinTeam(GamePlayer player)
    {
        for (Team team : game.teams)
        {
            team.players.remove(player);
        }

        if (players.contains(player))
            return;
        players.add(player);

    }

    public boolean isFull()
    {
        return this.players.size() >= game.scale.ppt;
    }

    public String getStateDisplay()
    {
        switch (state)
        {
            case ALIVE:
                return ChatColor.GREEN + "" + ChatColor.BOLD + "✔";
            case BED_LESS:
                return ChatColor.GREEN + "" + this.getAlive().size();
            default:
                return ChatColor.RED + "" + ChatColor.BOLD + "✘";
        }
    }

}

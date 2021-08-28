package me.faintcloudy.bedwars.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.BedwarsMode;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.team.TeamColor;
import me.faintcloudy.bedwars.listener.SetupListener;
import me.faintcloudy.bedwars.utils.LocationUtils;
import me.faintcloudy.bedwars.utils.command.BukkitCommand;
import me.faintcloudy.bedwars.utils.command.InheritedCommand;
import me.faintcloudy.bedwars.utils.command.SenderType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.minecraft.server.v1_8_R3.CommandSaveAll;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SetupCommand extends BukkitCommand implements TabCompleter {
    FileConfiguration config = Bedwars.getInstance().mapConfig;
    ConfigurationSection mapSection;
    {
        if (config.getConfigurationSection("map") == null)
            config.createSection("map");
        mapSection = config.getConfigurationSection("map");
    }
    public SetupCommand() {
        super("setup", SetupCommand.class);
        this.permission = "bw.admin";
        this.senderType = SenderType.PLAYER;
    }

    private boolean isSelected()
    {
        return true;
        //return SetupListener.selected != null;
    }
    @Override
    public void onMain(CommandSender sender, String[] args) {
        sender.sendMessage("§c当前模式非配置模式，请在 plugins\\bedwars\\config.yml 文件中将 setup 更改为 true");
    }

    @InheritedCommand(value = "setmapname", usage = "/setup setmapname <名字>", description = "设置地图名称")
    public void setMapName(CommandSender sender, String[] args)
    {
        Player player = (Player) sender;
        if (args.length < 2)
        {
            player.sendMessage(this.help());
            return;
        }

        String name = args[1];
        mapSection.set("map-name", name);
        player.sendMessage("§a你成功设置了地图名为 §6" + name);
    }

    @InheritedCommand(value = "addlobbyserver", usage = "/setup addlobbyserver <服务器名>", description = "添加大厅服务器")
    public void addLobbyServer(CommandSender sender, String[] args)
    {
        Player player = (Player) sender;
        if (args.length < 2)
        {
            player.sendMessage(this.help());
            return;
        }

        String serverName = args[0];

        List<String> servers = Bedwars.getInstance().getConfig().getStringList("lobbies");
        if (servers == null)
            servers = new ArrayList<>();

        if (servers.contains(serverName))
        {
            player.sendMessage("§c该大厅服务器已存在");
            return;
        }
        servers.add(serverName);
        Bedwars.getInstance().getConfig().set("lobbies", servers);

        player.sendMessage("成功添加了一个 §6大厅服务器");
    }

    @InheritedCommand(value = "removelobbyserver", usage = "/setup removelobbyserver <服务器名>", description = "删除大厅服务器")
    public void removeLobbyServer(CommandSender sender, String[] args)
    {
        Player player = (Player) sender;
        if (args.length < 2)
        {
            player.sendMessage(this.help());
            return;
        }

        String serverName = args[0];

        List<String> servers = Bedwars.getInstance().getConfig().getStringList("lobbies");
        if (servers == null)
            servers = new ArrayList<>();

        if (!servers.contains(serverName))
        {
            player.sendMessage("§c该大厅服务器不存在");
            return;
        }
        servers.remove(serverName);
        Bedwars.getInstance().getConfig().set("lobbies", servers);

        player.sendMessage("成功删除了一个 §6大厅服务器");
    }


    @InheritedCommand(value = "finish", usage = "/setup finish", description = "结束配置游戏 (将重启)")
    public void finishCommand(CommandSender sender)
    {
        Bukkit.broadcastMessage("§b§l结束配置游戏，服务器将在 §c2 §b§l秒后重启");
        Bukkit.broadcastMessage("§b正在保存游戏配置...");
        Bedwars.getInstance().getConfig().set("setup", false);
        Bedwars.getInstance().saveConfig();
        Bukkit.broadcastMessage("§b保存完毕！");
        new BukkitRunnable()
        {
            int count = 3;
            public void run()
            {
                count--;
                Bukkit.broadcastMessage("§b服务器将在 §c" + count + " §b秒后重启");
                if (count <= 0)
                {
                    Bukkit.broadcastMessage("§b服务器重启");
                    Bukkit.shutdown();
                }
            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 20);
    }

    @InheritedCommand(value = "setteams", usage = "/setup setteams <数量>", description = "设置队伍数量 (默认为 4)")
    public void setTeams(CommandSender sender, String[] args)
    {
        Player player = (Player) sender;
        if (args.length < 2)
        {
            player.sendMessage(this.help());
            return;
        }

        if (!args[0].matches("[1-9]"))
        {
            player.sendMessage("§c请输入数字");
            return;
        }

        int teams = Integer.parseInt(args[0]);

        if (teams > 4 || teams < 2)
        {
            player.sendMessage("§c请输入有效范围的数字 (2-4)");
            return;
        }
        Bedwars.getInstance().getConfig().set("game-teams", teams);
        player.sendMessage("§a你成功将队伍数量设为 §6" + teams);
    }

    @InheritedCommand(value = "sppt", usage = "/setup sppt <数量>", description = "设置每个队伍的玩家数量 (默认为 4)")
    public void setPlayersPerTeam(CommandSender sender, String[] args)
    {
        Player player = (Player) sender;
        if (args.length < 2)
        {
            player.sendMessage(this.help());
            return;
        }

        if (!args[0].matches("[1-9]"))
        {
            player.sendMessage("§c请输入有效范围的数字 (n > 1)");
            return;
        }

        int players = Integer.parseInt(args[0]);
        Bedwars.getInstance().getConfig().set("players-per-team", players);
        player.sendMessage("§a你成功将每个队伍的玩家数量设为 §6" + players);
    }


    @InheritedCommand(value = "Help", usage = "/setup help", description = "查看帮助")
    public void helpCommand(CommandSender sender)
    {
        sender.sendMessage(this.help());
    }

    @InheritedCommand(value = "sgm", usage = "/setup sgm <模式 (NORMAL——普通, RUSH——极速, LUCKY_BLOCK——幸运方块, MEGA_WALLS——超级战墙, ULTIMATES——超能力, GUN——枪械)>", description = "设置游戏模式")
    public void setGameMode(CommandSender sender, String[] args)
    {
        if (args.length < 2)
        {
            sender.sendMessage(this.help());
            return;
        }

        BedwarsMode mode = BedwarsMode.of(args[1]);
        Bedwars.getInstance().getConfig().set("game-mode", mode.name());
        Bedwars.getInstance().saveConfig();
        sender.sendMessage("§a你成功将游戏模式设为 §6" + mode.cn);
    }

    @InheritedCommand(value = "SetBed", usage = "/setup setbed <队伍名 (Red, Blue, Green, Yellow)>", description = "设置队伍的床")
    public void setBedCommand(CommandSender sender, String[] args)
    {
        Player player = (Player) sender;
        if (args.length < 2)
        {
            player.sendMessage(this.help());
            return;
        }

        TeamColor color = TeamColor.of(args[1]);
        if (color == TeamColor.NONE)
        {
            player.sendMessage("§c错误的队伍名称 (Blue, Red, Green, Yellow)");
            return;
        }

        if (!isSelected())
        {
            player.sendMessage("§c请先使用木稿选择一个方块进行操作！");
            return;
        }

        ConfigurationSection teamSection = mapSection.getConfigurationSection(color.name());
        if (teamSection == null)
            teamSection = mapSection.createSection(color.name());
        teamSection.set("bed", this.getFixedLocation(selectedLocation(player)));
        Bedwars.getInstance().saveConfig();

        player.sendMessage("§a成功设置了 " + color.chatColor + color.cn + " §a的 §6床");
    }

    private Location selectedLocation(Player player)
    {
        Location location = LocationUtils.getStandardLocation(player.getLocation());
        player.teleport(location);
        return location;
        //return SetupListener.selected.getLocation();
    }

    @InheritedCommand(value = "SetLobby", usage = "/setup setlobby", description = "设置等待大厅 (游戏内)")
    public void setLobby(CommandSender sender)
    {
        Player player = (Player) sender;
        if (!isSelected())
        {
            player.sendMessage("§c请先使用木稿选择一个方块进行操作！");
            return;
        }

        mapSection.set("lobby", this.getFixedLocation(selectedLocation(player)));
        Bedwars.getInstance().saveConfig();

        player.sendMessage("§a成功设置了 §6等待大厅");
    }

    @InheritedCommand(value = "SetMiddle", usage = "/setup setmiddle", description = "设置游戏中心点")
    public void setMiddle(CommandSender sender)
    {
        Player player = (Player) sender;

        if (!isSelected())
        {
            player.sendMessage("§c请先使用木稿选择一个方块进行操作！");
            return;
        }

        mapSection.set("middle", this.getFixedLocation(selectedLocation(player)));
        Bedwars.getInstance().saveConfig();

        player.sendMessage("§a成功设置了 §6游戏中心店");
    }

    @InheritedCommand(value = "adr", usage = "/setup adr <资源名称 (Iron, Gold, Diamond, Emerald)>", description = "添加公共资源点")
    public void addDeclaredResourceCommand(CommandSender sender, String[] args)
    {
        Player player = (Player) sender;
        if (args.length < 2)
        {
            player.sendMessage(this.help());
            return;
        }

        ResourceType type = ResourceType.of(args[1]);
        if (type == ResourceType.NONE)
        {
            player.sendMessage("§c错误的资源名称 (Iron, Gold, Diamond, Emerald)");
            return;
        }

        if (!isSelected())
        {
            player.sendMessage("§c请先使用木稿选择一个方块进行操作！");
            return;
        }

        ConfigurationSection declaredResourcesSection = mapSection.getConfigurationSection("declared-resources");
        if (declaredResourcesSection == null)
            declaredResourcesSection = mapSection.createSection("declared-resources");
        List<String> resourceLocations = declaredResourcesSection.getStringList(type.name());
        if(resourceLocations == null)
            resourceLocations = new ArrayList<>();
        resourceLocations.add(this.locationToString(this.getFixedLocation(selectedLocation(player))));
        sender.sendMessage(resourceLocations.toString());

        declaredResourcesSection.set(type.name(), resourceLocations);

        Bedwars.getInstance().saveConfig();

        player.sendMessage("§a你成功添加了一个 " + type.color + type.cn + "资源点");

    }

    public String locationToString(Location location)
    {
        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
    }

    public Location stringToLocation(String json)
    {
        String[] location = json.split(":");
        return new Location(Bukkit.getWorld(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3])
                , Float.parseFloat(location[4]), Float.parseFloat(location[5]));
    }

    @InheritedCommand(value = "rdr", usage = "/setup rdr <资源名称 (Iron, Gold, Diamond, Emerald)>", description = "删除距离你最近的公共资源点")
    public void removeDeclaredResourceCommand(CommandSender sender, String[] args)
    {
        Player player = (Player) sender;
        if (args.length < 2)
        {
            player.sendMessage(this.help());
            return;
        }

        ResourceType type = ResourceType.of(args[1]);
        if (type == ResourceType.NONE)
        {
            player.sendMessage("§c错误的资源名称 (Iron, Gold, Diamond, Emerald)");
            return;
        }

        ConfigurationSection declaredResourcesSection = mapSection.getConfigurationSection("declared-resources");
        if (declaredResourcesSection == null)
            declaredResourcesSection = mapSection.createSection("declared-resources");
        List<String> resourceLocations = declaredResourcesSection.getStringList(type.name());
        if(resourceLocations == null)
            resourceLocations = new ArrayList<>();
        if (resourceLocations.isEmpty())
        {
            player.sendMessage("§c当前没有任何 " + type.color + type.cn + "资源点");
            return;
        }
        List<Location> locations = new ArrayList<>();
        for (String resourceLocation : resourceLocations) {
            locations.add(this.stringToLocation(resourceLocation));
        }

        Location closest = LocationUtils.getClosest(player.getLocation(), locations);
        resourceLocations.remove(this.locationToString(closest));
        declaredResourcesSection.set(type.name(), resourceLocations);

        Bedwars.getInstance().saveConfig();

        player.sendMessage("§a你成功删除了一个距离你最近的 " + type.color + type.cn + "资源点 §7(x:" + closest.getX() + " y:" + closest.getY() + " z:" + closest
        .getZ() + ")");

    }

    @InheritedCommand(value = "str", usage = "/setup str <队伍名 (Red, Blue, Green, Yellow)>", description = "设置队伍基地资源点")
    public void setTeamResourceCommand(CommandSender sender, String[] args)
    {
        Player player = (Player) sender;
        if (args.length < 2)
        {
            player.sendMessage(this.help());
            return;
        }

        TeamColor color = TeamColor.of(args[1]);
        if (color == TeamColor.NONE)
        {
            player.sendMessage("§c错误的队伍名称 (Blue, Red, Green, Yellow)");
            return;
        }

        if (!isSelected())
        {
            player.sendMessage("§c请先使用木稿选择一个方块进行操作！");
            return;
        }

        ConfigurationSection teamSection = mapSection.getConfigurationSection(color.name());
        if (teamSection == null)
            teamSection = mapSection.createSection(color.name());
        teamSection.set("resource", this.getFixedLocation(selectedLocation(player)));
        Bedwars.getInstance().saveConfig();

        player.sendMessage("§a成功设置了 " + color.chatColor + color.cn + " §a的 §6基地资源点");
    }

    @InheritedCommand(value = "SetItemShop", usage = "/setup setitemshop <队伍名 (Blue, Red, Green, Yellow)>", description = "设置队伍物品商店")
    public void setItemShop(CommandSender sender, String[] args)
    {
        Player player = (Player) sender;
        if (args.length < 2)
        {
            player.sendMessage(this.help());
            return;
        }

        TeamColor color = TeamColor.of(args[1]);
        if (color == TeamColor.NONE)
        {
            player.sendMessage("§c错误的队伍名称 (Blue, Red, Green, Yellow)");
            return;
        }

        if (!isSelected())
        {
            player.sendMessage("§c请先使用木稿选择一个方块进行操作！");
            return;
        }

        ConfigurationSection teamSection = mapSection.getConfigurationSection(color.name());
        if (teamSection == null)
            teamSection = mapSection.createSection(color.name());
        teamSection.set("item-shop", this.getFixedLocation(selectedLocation(player)));
        Bedwars.getInstance().saveConfig();

        player.sendMessage("§a成功设置了 " + color.chatColor + color.cn + " §a的 §6物品商店");
    }

    @InheritedCommand(value = "SetTeamShop", usage = "/setup setteamshop <队伍名 (Blue, Red, Green, Yellow)>", description = "设置队伍物品商店")
    public void setTeamShop(CommandSender sender, String[] args)
    {
        Player player = (Player) sender;
        if (args.length < 2)
        {
            player.sendMessage(this.help());
            return;
        }

        TeamColor color = TeamColor.of(args[1]);
        if (color == TeamColor.NONE)
        {
            player.sendMessage("§c错误的队伍名称 (Blue, Red, Green, Yellow)");
            return;
        }

        if (!isSelected())
        {
            player.sendMessage("§c请先使用木稿选择一个方块进行操作！");
            return;
        }

        ConfigurationSection teamSection = mapSection.getConfigurationSection(color.name());
        if (teamSection == null)
            teamSection = mapSection.createSection(color.name());
        teamSection.set("team-shop", this.getFixedLocation(selectedLocation(player)));
        Bedwars.getInstance().saveConfig();

        player.sendMessage("§a成功设置了 " + color.chatColor + color.cn + " §a的 §6队伍商店");
    }


    @InheritedCommand(value = "SetSpawn", usage = "/setup setspawn <队伍名 (Blue, Red, Green, Yellow)>", description = "设置队伍重生点")
    public void setSpawnCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length < 2)
        {
            player.sendMessage(this.help());
            return;
        }

        TeamColor color = TeamColor.of(args[1]);
        if (color == TeamColor.NONE)
        {
            player.sendMessage("§c错误的队伍名称 (Blue, Red, Green, Yellow)");
            return;
        }

        if (!isSelected())
        {
            player.sendMessage("§c请先使用木稿选择一个方块进行操作！");
            return;
        }

        ConfigurationSection teamSection = mapSection.getConfigurationSection(color.name());
        if (teamSection == null)
            teamSection = mapSection.createSection(color.name());
        teamSection.set("spawn", this.getFixedLocation(selectedLocation(player)));

        Bedwars.getInstance().saveConfig();

        player.sendMessage("§a成功设置了 " + color.chatColor + color.cn + " §a的 §6队伍重生点");
    }

    public Location getFixedLocation(Location location)
    {
        return location;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (command == this)
        {
            List<String> results = new ArrayList<>();
            if (args.length == 1)
            {
                for (Method method : commandMethods)
                    results.add(method.getAnnotation(InheritedCommand.class).value());
                return results;
            }

        }

        return null;
    }

    public void declared(Player sender, ResourceType type)
    {
        int count = 0;
        ConfigurationSection declaredResourcesSection = mapSection.getConfigurationSection("declared-resources");
        if (declaredResourcesSection == null)
            declaredResourcesSection = mapSection.createSection("declared-resources");
        List<String> resourceLocations = declaredResourcesSection.getStringList(type.name());
        if(resourceLocations == null)
            resourceLocations = new ArrayList<>();
        if (resourceLocations.isEmpty())
        {
            sender.sendMessage(type.color + type.cn + "§f: 空！");
            return;
        }
        List<Location> locations = new ArrayList<>();
        for (String resourceLocation : resourceLocations) {
            locations.add(this.stringToLocation(resourceLocation));
        }

        for (Location location : locations)
        {
            count++;
            sender.sendMessage(type.color + type.cn + "资源点 §7" + count + " §3位于: x=" + (int) location.getX() + " y=" + (int) location.getY() + " z=" + (int) location.getZ());
            sender.spigot().sendMessage(new ComponentBuilder("§7[§e§l点击传送§7]").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/tp " + location.getX() + " " + location.getY() + " " + location.getZ()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("/tp " + location.getX() + " " + location.getY() + " " + location.getZ()).create()
                    )).create());
        }
    }

    @InheritedCommand(value = "listresources", usage = "/setup listresources", description = "查看资源点")
    public void listResources(CommandSender sn)
    {
        Player sender = (Player) sn;
        sender.sendMessage("§a公共资源点: ");

        declared(sender, ResourceType.IRON);
        declared(sender, ResourceType.GOLD);
        declared(sender, ResourceType.DIAMOND);
        declared(sender, ResourceType.EMERALD);

        sender.sendMessage("§a队伍资源点:");
        for (TeamColor color : TeamColor.values()) {
            if (color == TeamColor.NONE)
                continue;
            ConfigurationSection teamSection = mapSection.getConfigurationSection(color.name());
            if (teamSection == null)
                teamSection = mapSection.createSection(color.name());
            Location location = (Location) teamSection.get("resource");
            if (location == null)
            {
                sender.sendMessage(color.chatColor + color.cn + ": §f空！");
                continue;
            }

            sender.sendMessage(color.chatColor + color.cn + " §3位于: x=" + (int) location.getX() + " y=" + (int) location.getY() + " z=" + (int) location.getZ());
            sender.spigot().sendMessage(new ComponentBuilder("§7[§e§l点击传送§7]").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/tp " + location.getX() + " " + location.getY() + " " + location.getZ()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("/tp " + location.getX() + " " + location.getY() + " " + location.getZ()).create()
                    )).create());
        }
    }
}

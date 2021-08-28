package me.faintcloudy.bedwars.game;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.team.TeamColor;
import me.faintcloudy.bedwars.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class GameMap {
    public String name;
    public GameMap(FileConfiguration mapConfig) {
        try {
            this.name = mapConfig.getString("map.map-name");

            this.lobby = (Location) mapConfig.get("map.lobby");
            this.middle = (Location) mapConfig.get("map.middle");



            declaredResourceLocations.put(ResourceType.IRON, this.getResourceLocations(ResourceType.IRON));
            declaredResourceLocations.put(ResourceType.GOLD, this.getResourceLocations(ResourceType.GOLD));
            declaredResourceLocations.put(ResourceType.DIAMOND, this.getResourceLocations(ResourceType.DIAMOND));
            declaredResourceLocations.put(ResourceType.EMERALD, this.getResourceLocations(ResourceType.EMERALD));

            for (TeamColor color : TeamColor.values())
            {
                if (color == TeamColor.NONE)
                    continue;
                ConfigurationSection teamSection = mapConfig.getConfigurationSection("map." + color.name());
                if (teamSection == null)
                    teamSection = mapConfig.createSection("map." + color.name());
                Location spawn = (Location) teamSection.get("spawn");
                Location bed = (Location) teamSection.get("bed");
                Location resource = (Location) teamSection.get("resource");
                Location itemShop = (Location) teamSection.get("item-shop");
                Location teamShop = (Location) teamSection.get("team-shop");
                spawnLocations.put(color, spawn);
                bedLocations.put(color, bed);
                baseResourceLocations.put(color, resource);
                itemShopLocations.put(color, itemShop);
                teamShopLocations.put(color, teamShop);

            }

        } catch (NullPointerException exception)
        {
            Bukkit.getLogger().warning("在加载游戏地图时发生错误: 地图不完整");
            Bukkit.getLogger().warning("报错信息: ");
            exception.printStackTrace();
        }

    }

    public boolean outOfBorder(Location location)
    {
        Location middle = this.middle.clone();
        if (Math.abs(middle.getY() - location.getY()) > 150)
            return true;
        else if (Math.abs(middle.getX() - location.getX()) > 150)
            return true;
        else return Math.abs(middle.getZ() - location.getZ()) > 150;
    }

    public List<Location> getResourceLocations(ResourceType type)
    {
        ConfigurationSection declaredResourcesSection = Bedwars.getInstance().mapConfig.getConfigurationSection("map.declared-resources");
        if (declaredResourcesSection == null)
            declaredResourcesSection = Bedwars.getInstance().mapConfig.createSection("map.declared-resources");
        List<String> resourceLocations = declaredResourcesSection.getStringList(type.name());
        if(resourceLocations == null)
            resourceLocations = new ArrayList<>();
        if (resourceLocations.isEmpty())
        {
            return new ArrayList<>();
        }
        List<Location> locations = new ArrayList<>();
        for (String resourceLocation : resourceLocations) {
            locations.add(this.stringToLocation(resourceLocation));
        }

        return locations;

    }

    public void check()
    {
        try {
            //TODO
        } catch (NullPointerException exception)
        {
            Bukkit.getLogger().warning("在加载游戏地图时发生错误: 地图不完整");
            Bukkit.getLogger().warning("报错信息: ");
            exception.printStackTrace();
        }
    }

    public HashMap<TeamColor, Location> spawnLocations = new HashMap<>();
    public HashMap<TeamColor, Location> bedLocations = new HashMap<>();
    public HashMap<TeamColor, Location> baseResourceLocations = new HashMap<>();
    public HashMap<TeamColor, Location> itemShopLocations = new HashMap<>();
    public HashMap<TeamColor, Location> teamShopLocations = new HashMap<>();
    public Map<ResourceType, List<Location>> declaredResourceLocations = new HashMap<>();
    public Location lobby, middle;

    private Location stringToLocation(String json)
    {
        String[] location = json.split(":");
        return new Location(Bukkit.getWorld(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3])
                , Float.parseFloat(location[4]), Float.parseFloat(location[5]));
    }

}

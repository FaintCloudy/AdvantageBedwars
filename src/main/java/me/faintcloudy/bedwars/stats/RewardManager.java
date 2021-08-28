package me.faintcloudy.bedwars.stats;

import me.faintcloudy.bedwars.Bedwars;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

public class RewardManager {

    public int getRewardCoins(RewardReason reason)
    {
        ConfigurationSection section = Bedwars.getInstance().rewardsConfig.getConfigurationSection("elements");
        for (String key : section.getKeys(false)) {
            ConfigurationSection rewardSection = section.getConfigurationSection(key);
            if (rewardSection.getString("type").equalsIgnoreCase(reason.name()))
                return rewardSection.getInt("coins");
        }
        return 0;
    }

    public int getRewardExperience(RewardReason reason)
    {
        ConfigurationSection section = Bedwars.getInstance().rewardsConfig.getConfigurationSection("elements");
        for (String key : section.getKeys(false)) {
            ConfigurationSection rewardSection = section.getConfigurationSection(key);
            if (rewardSection.getString("type").equalsIgnoreCase(reason.name()))
                return rewardSection.getInt("experience");
        }
        return 0;
    }
}

package me.faintcloudy.bedwars.game.shop.shopitem;

import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.shop.ShopItemType;
import me.faintcloudy.bedwars.game.team.upgrade.TeamUpgrade;
import me.faintcloudy.bedwars.game.team.upgrade.TrapUpgrade;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import me.faintcloudy.bedwars.utils.MapBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum UpgradeItem implements ShopItem {
    SHARPNESS("锋利附魔", TeamUpgrade.SHARPNESS, new MapBuilder<>(1, Price.of(8, ResourceType.DIAMOND)).build(),
            "己方所有成员的剑和斧永久获得锋利 I 附魔！", Material.IRON_SWORD, null),
    PROTECTION("装备强化", TeamUpgrade.PROTECTION, new MapBuilder<Integer, Price>()
            .put(1, Price.of(5, ResourceType.DIAMOND))
            .put(2, Price.of(10, ResourceType.DIAMOND))
            .put(3, Price.of(20, ResourceType.DIAMOND))
            .put(4, Price.of(30, ResourceType.DIAMOND)).build(),
            "己方所有成员的剑和服永久获得保护附魔！", Material.IRON_CHESTPLATE, new MapBuilder<Integer, String>()
            .put(1, "保护 I")
            .put(2, "保护 II")
            .put(3, "保护 III")
            .put(4, "保护 IV").build()),
    HASTE("疯狂矿工", TeamUpgrade.HASTE, new MapBuilder<Integer, Price>()
            .put(1, Price.of(4, ResourceType.DIAMOND))
            .put(2, Price.of(6, ResourceType.DIAMOND)).build(),
            "己方所有成员永久获得急迫效果。", Material.GOLD_PICKAXE, new MapBuilder<Integer, String>()
            .put(1, "急迫 I")
            .put(2, "急迫 II").build()),
    FORGE("锻炉", TeamUpgrade.FORGE, new MapBuilder<Integer, Price>()
            .put(1, Price.of(4, ResourceType.DIAMOND))
            .put(2, Price.of(8, ResourceType.DIAMOND))
            .put(3, Price.of(12, ResourceType.DIAMOND))
            .put(4, Price.of(16, ResourceType.DIAMOND)).build(),
            "提升自己岛上资源生成的效率。", Material.FURNACE, new MapBuilder<Integer, String>()
            .put(1, "+50%资源")
            .put(2, "+100%资源")
            .put(3, "生成绿宝石")
            .put(4, "+200%资源").build()),
    HEAL_POOL("治愈池", TeamUpgrade.HEAL_POOL, new MapBuilder<>(1, Price.of(3, ResourceType.DIAMOND)).build(),
            "基地附近的队伍成员获得生命恢复效果。", Material.BEACON, null),
    DRAGON_BUFF("末影龙增益", TeamUpgrade.DRAGON_BUFF, new MapBuilder<>(1, Price.of(5, ResourceType.DIAMOND)).build(),
            "你的队伍在死亡竞赛中将会有两条龙而不是一条！", Material.DRAGON_EGG, null),
    ITS_TRAP("这是一个陷阱", TeamUpgrade.ITS_TRAP, null, "造成失明与缓慢效果，持续 8 秒。", Material.TRIPWIRE_HOOK, null),
    COUNTER_ATTACK_TRAP("反击陷阱", TeamUpgrade.COUNTER_ATTACK_TRAP, null,
            "赋予基地附近的队友速度 I 与跳跃提升 II 效果，持续 10 秒。", Material.FEATHER, null),
    ALARM_TRAP("报警陷阱", TeamUpgrade.ALARM_TRAP, null, Arrays
            .asList("显示隐身的玩家，", "及其名称与队伍名。"), Material.REDSTONE_TORCH_ON, null),
    MINER_FATIGUE_TRAP("挖掘疲劳陷阱", TeamUpgrade.MINER_FATIGUE_TRAP, null,
            "造成挖掘疲劳效果，持续 10 秒。", Material.IRON_PICKAXE, null);

    /* 铁锻炉、黄金锻炉、绿宝石锻炉、熔炉强化
    * */

    HashMap<Integer, String> forgeDisplays = new MapBuilder<Integer, String>()
            .put(1, "铁锻炉")
            .put(2, "黄金锻炉")
            .put(3, "绿宝石锻炉")
            .put(4, "熔炉强化").build();
    @Override
    public ItemStack showItem(GamePlayer player, ShopItemType type) {

        boolean maxed = this.unlocked(player);
        int level = maxed ? player.getTeam().upgradeLevels.get(upgrade) : player.getTeam().upgradeLevels.get(upgrade) + 1;
        Price price = prices.get(level);
        if (this.upgrade instanceof TrapUpgrade)
        {
            price = Price.of(player.getTeam().activeTraps() + 1, ResourceType.DIAMOND);
        }
        String displayName = this.displayName + " " + (this.isSinglePurchase() ? "" : ShopItem.toRome(level));
        if (this == FORGE)
        {
            displayName = forgeDisplays.get(level);
        }
        boolean enough = ShopItem.enoughPrice(player, price);
        ItemBuilder item = new ItemBuilder(icon, level)
                .setDisplayName((maxed ? "§a" : (enough ? "§e" : "§c")) + displayName)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .addFlag(ItemFlag.HIDE_UNBREAKABLE);

        List<String> lore = new ArrayList<>();
        for (String s : introduces)
        {
            lore.add("§7" + s);
        }

        lore.add("");

        if (this.isSinglePurchase() && !maxed)
        {
            lore.add(price.costDisplay());
        }
        else if (!this.isSinglePurchase())
        {
            for (int l : levelIntroduces.keySet())
            {
                Price p = prices.get(l);
                lore.add(((maxed ? level : level-1) >= l ? "§a" : "§7") + l + "等级:" + levelIntroduces.get(l) + "§7，" + p.resource.color + p.amount + " " + p.resource.cn);
            }
        }
        if (!(this.isSinglePurchase() && maxed))
            lore.add("");
        lore.add(maxed ? "§a已解锁" : (enough ? "§e点击购买！" : "§c你没有足够的" + price.resource.cn));
        item.setLore(lore);

        return item.build();
    }

    public static UpgradeItem getUpgradeItem(TeamUpgrade upgrade)
    {
        for (UpgradeItem item : values())
        {
            if (item.upgrade == upgrade)
                return item;
        }

        return null;
    }

    @Override
    public String insideName() {
        return this.name();
    }

    public TeamUpgrade upgrade;
    public HashMap<Integer, Price> prices;
    public HashMap<Integer, String> levelIntroduces = new HashMap<>();
    public List<String> introduces;
    public String displayName;
    public Material icon;
    UpgradeItem(String displayName, TeamUpgrade upgrade, HashMap<Integer, Price> prices, String introduce, Material icon,
                HashMap<Integer, String> levelIntroduces)
    {
        if (levelIntroduces != null)
        this.levelIntroduces = levelIntroduces;
        this.icon = icon;
        this.displayName = displayName;
        this.upgrade = upgrade;
        if (prices != null)
        {
            this.prices = prices;
        }
        else
        {
            this.prices = new MapBuilder<>(1, Price.of(114514, ResourceType.DIAMOND)).build();
        }
        introduces = new ArrayList<>();
        introduces.add(introduce);

    }

    UpgradeItem(String displayName, TeamUpgrade upgrade, HashMap<Integer, Price> prices, List<String> introduces, Material icon,
                HashMap<Integer, String> levelIntroduces)
    {
        if (levelIntroduces != null)
        this.levelIntroduces = levelIntroduces;
        this.icon = icon;
        this.displayName = displayName;
        this.upgrade = upgrade;
        if (prices != null)
        {
            this.prices = prices;
        }
        else
        {
            this.prices = new MapBuilder<>(1, Price.of(114514, ResourceType.DIAMOND)).build();
        }
        this.introduces = introduces;

    }

    public boolean isSinglePurchase()
    {
        return this.prices.keySet().size() <= 1;
    }

    @Override
    public ItemStack getItem(GamePlayer player) {

        upgrade.upgrade(player.getTeam());

        return new ItemStack(Material.AIR);
    }

    @Override
    public Price price(GamePlayer player) {
        if (this.upgrade instanceof TrapUpgrade)
        {
            return Price.of(player.getTeam().activeTraps() + 1, ResourceType.DIAMOND);
        }

        if (upgrade.unlocked(player.getTeam()))
            return Price.of(114514, ResourceType.DIAMOND);

        return prices.get(player.getTeam().upgradeLevels.get(upgrade) + 1);
    }

    @Override
    public boolean unlocked(GamePlayer player) {
        return upgrade.unlocked(player.getTeam());
    }
}

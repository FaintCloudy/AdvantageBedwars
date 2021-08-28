package me.faintcloudy.bedwars.game.shop.shopitem;

import me.faintcloudy.bedwars.game.resource.ResourceType;

public class Price {
    public int amount;
    public ResourceType resource;
    private Price(int amount, ResourceType type)
    {
        this.amount = amount;
        this.resource = type;
    }

    public String costDisplay()
    {
        return "§7花费: " + resource.color + amount + " " + resource.cn;
    }

    public static Price of(int amount, ResourceType resource)
    {
        return new Price(amount, resource);
    }
}

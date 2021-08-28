package me.faintcloudy.bedwars.utils.command;

public enum SenderType {
    ALL("全部"), PLAYER("玩家"), CONSOLE("控制台");
    public String cn;
    SenderType(String cn)
    {
        this.cn = cn;
    }
}

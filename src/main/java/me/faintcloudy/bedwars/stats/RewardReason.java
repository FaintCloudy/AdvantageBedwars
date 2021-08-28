package me.faintcloudy.bedwars.stats;

public enum RewardReason {
    KILL("击杀"), FINAL_KILL("最终击杀"), BREAK_BED("破坏床"), WIN_A_GAME("胜利"),
    PLAY_A_GAME("游玩"), PICKUP_DIAMOND("收集钻石"), PICKUP_EMERALD("收集绿宝石");
    public String display;
    RewardReason(String display)
    {

        this.display = display;
    }
}

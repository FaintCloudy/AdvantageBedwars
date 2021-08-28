package me.faintcloudy.bedwars.scoreboard;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.BedwarsMode;
import me.faintcloudy.bedwars.game.Game;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.team.Team;
import org.bukkit.Bukkit;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Scoreboards {


    static SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd");
    static Game game = Bedwars.getInstance().game;
    private static String getDate() {
        return "§7" + format.format(new Date()) + " §8";
    }

    private static final List<String> waiting = Arrays.asList(
            getDate() + Bukkit.getServerName(),
            "§1",
            "地图:§a %map_name%",
            "玩家: §a%online%/" + game.scale.maxPlayers(),
            "§2",
            "§f%state%",
            "§3",
            "模式: §a" + (game.mode == BedwarsMode.NORMAL ? "" : game.mode.cn) + game.scale.display(),
            "版本: §7v" + Bedwars.getInstance().getDescription().getVersion(),
            "§4",
            "§eAdvantage Bedwars"
    );

    public static List<String> getWaitingBoard()
    {
        List<String> board = new ArrayList<>();
        for (String line : waiting)
        {
            String newLine = line.replaceAll("%map_name%", game.map.name);
            newLine = newLine.replaceAll("%online%", "" + Bukkit.getOnlinePlayers().size());
            newLine = newLine.replaceAll("%state%", game.startTime < 20 ? "即将开始: §a" + game.startTime + "秒" : "等待中...");
            board.add(newLine);
        }

        return board;
    }

    private static <T> List<T> toRealArrayList(List<T> list)
    {
        return new ArrayList<>(list);
    }

    public static String secondsFormat(int seconds)
    {
        int min = 0;
        while (seconds > 60)
        {
            seconds -= 60;
            min++;
        }
        String ss = String.valueOf(seconds);
        if ((String.valueOf(seconds)).length() <= 1)
            ss = "0" + ss;
        return min + ":" + ss;
    }
    public static List<String> getGamingBoard(GamePlayer player)
    {
        List<String> gaming = toRealArrayList(Arrays.asList(
                getDate() + Bukkit.getServerName(),
                "§5",
                game.nextEvent.name() + " - §a" + secondsFormat(game.nextEventTime),
                "§6" //TODO event display
        ));

        for (Team team : Bedwars.getInstance().game.teams)
        {

            String teamDisplay = team.color.chatColor + "§l" + team.color.en + "§f " + team.color.cn + ": §a" + team.getStateDisplay()
                     + (team == player.getTeam() ? " §7你" : "");
            gaming.add(teamDisplay);
        }

        gaming.add("§8");
        gaming.add("§f击杀数: §a%kills%".replace("%kills%", "" + player.data.currentGameData.kills));
        gaming.add("§f最终击杀数: §a%final_kills%".replace("%final_kills%", "" + player.data.currentGameData.finalKills));
        gaming.add("§f破坏床数: §a%broke_beds%".replace("%broke_beds%", "" + player.data.currentGameData.bedBroken));

        gaming.add("§7");
        gaming.add("§eAdvantage Bedwars");

        return gaming;
    }
}

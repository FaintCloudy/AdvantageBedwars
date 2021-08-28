package me.faintcloudy.bedwars.game;

import me.faintcloudy.bedwars.game.team.TeamColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameScale {
    public int teams;
    public int ppt; // Players Per Team = P P T = PPT
    public GameScale(int teams, int playersPerTeam)
    {
        this.teams = teams;
        this.ppt = playersPerTeam;
    }

    public static GameScale of(int teams, int ppt)
    {
        return new GameScale(teams, ppt);
    }

    public int maxPlayers()
    {
        return teams * ppt;
    }

    public List<TeamColor> getTeamColors()
    {
        List<TeamColor> colors = new ArrayList<>();
        for (TeamColor color : TeamColor.values())
        {
            if (color == TeamColor.NONE)
                continue;
            colors.add(color);
            if (colors.size() >= teams)
                break;
        }

        return colors;
    }


    public String display()
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0;i<teams;i++)
        {
            builder.append(ppt).append("v");
        }

        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }
}

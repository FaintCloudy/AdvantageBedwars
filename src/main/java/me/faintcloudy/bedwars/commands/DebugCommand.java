package me.faintcloudy.bedwars.commands;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.team.Team;
import me.faintcloudy.bedwars.game.team.TeamColor;
import me.faintcloudy.bedwars.game.team.TeamState;
import me.faintcloudy.bedwars.utils.command.BukkitCommand;
import me.faintcloudy.bedwars.utils.command.InheritedCommand;
import me.faintcloudy.bedwars.utils.command.SenderType;
import org.apache.commons.lang3.reflect.InheritanceUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.ParseException;

public class DebugCommand extends BukkitCommand {
    public DebugCommand() {
        super("debug", DebugCommand.class);
        this.permission = "bw.admin";
    }

    @InheritedCommand(value = "gsc", usage = "/debug gsc <true / false>", description = "自动检查游戏进程")
    public void gameCheckCommand(CommandSender sender, String[] args)
    {
        if (args.length < 2)
        {
            sender.sendMessage(this.help());
            return;
        }
        try {
            Bedwars.getInstance().game.gameCheck = Boolean.parseBoolean(args[1]);
            sender.sendMessage("§e已 " + (Boolean.parseBoolean(args[1]) ? "§a开启" : "§c停用") + " §e自动检查游戏进程");
        } catch (Exception exception)
        {
            sender.sendMessage(this.help());
        }

    }

    @InheritedCommand(value = "info", usage = "/debug info", description = "查看你当前的数据", sender = SenderType.PLAYER)
    public void onInfo(CommandSender sender)
    {

        GamePlayer player = GamePlayer.get((Player) sender);


        player.player.sendMessage("§a队伍: " + player.getTeam().color.chatColor + player.getTeam().color.cn);
        player.player.sendMessage("§a状态: §6" + player.state.name());
        player.player.sendMessage(player.toString());

    }

    @InheritedCommand(value = "setstate", usage = "/debug setstate <队伍> <状态 (ALIVE, BED_LESS, DEAD)>")
    public void setState(CommandSender sender, String[] args)
    {
        if (args.length < 3)
        {
            sender.sendMessage(this.help());
            return;
        }

        try {
            TeamColor color = TeamColor.of(args[1]);
            Team team = null;
            for (Team t : Bedwars.getInstance().game.teams)
            {
                if (t.color == color)
                {
                    team = t;
                    break;
                }
            }
            if (team == null)
            {
                sender.sendMessage(this.help());
                return;
            }

            TeamState state = TeamState.valueOf(args[2].toUpperCase());
            team.state = state;
            sender.sendMessage("§e已将 " + color.chatColor + color.cn + " §e的队伍状态设置为 §6" + state.name());
        } catch (Exception exception)
        {
            sender.sendMessage(this.help());
        }
    }

    @InheritedCommand(value = "end", usage = "/debug end", description = "强制结束游戏")
    public void endCommand()
    {
        Bedwars.getInstance().game.onEnd();
    }

    @InheritedCommand(value = "start", usage = "/debug start", description = "强制开始游戏")
    public void startCommand()
    {
        Bedwars.getInstance().game.onStart();
    }

    @InheritedCommand(value = "ts", usage = "/debug ts <玩家>", description = "将玩家设置为旁观者身份")
    public void setSpectator(CommandSender sender, String[] args)
    {
        if (args.length < 2)
        {
            sender.sendMessage(this.help());
            return;
        }
        String name = args[1];
        GamePlayer player = null;
        for (GamePlayer p : GamePlayer.getOnlineGamePlayers())
        {
            if (p.player.getName().equalsIgnoreCase(name))
            {
                player = p;
            }
        }
        if (player == null)
        {
            sender.sendMessage("§c找不到该玩家");
            return;
        }

        player.toSpectator();
        sender.sendMessage("§e已将 §a" + player.player.getName() + " §e设置为旁观者");
    }

    @InheritedCommand(value = "reborn", usage = "/debug reborn <玩家>", description = "重生该玩家及其他的队伍")
    public void reborn(CommandSender sender, String[] args)
    {
        if (args.length < 2)
        {
            sender.sendMessage(this.help());
            return;
        }
        String name = args[1];
        GamePlayer player = null;
        for (GamePlayer p : GamePlayer.getOnlineGamePlayers())
        {
            if (p.player.getName().equalsIgnoreCase(name))
            {
                player = p;
            }
        }
        if (player == null)
        {
            sender.sendMessage("§c找不到该玩家");
            return;
        }

        player.getTeam().state = TeamState.BED_LESS;
        player.state = GamePlayer.PlayerState.ALIVE;
        player.spawn();
        sender.sendMessage("§e已重生 §a" + player.player.getName() + " §e以及他所在的队伍");
    }

}

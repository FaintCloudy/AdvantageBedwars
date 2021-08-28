package me.faintcloudy.bedwars.utils.command;

import me.faintcloudy.bedwars.Bedwars;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.beans.beancontext.BeanContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class BukkitCommand extends Command {
    private Class<? extends BukkitCommand> clazz;
    public final List<Method> commandMethods = new ArrayList<>();
    protected boolean inheritedCommands = true;
    protected String permission = "";
    protected SenderType senderType = SenderType.ALL;
    public BukkitCommand(String name, Class<? extends BukkitCommand> classSelf) {
        super(name);
        this.clazz = classSelf;
        Method[] allMethods = this.clazz.getDeclaredMethods();
        for (Method method : allMethods)
        {
            if (method.isAnnotationPresent(InheritedCommand.class))
            {
                commandMethods.add(method);
            }
        }
    }

    public BukkitCommand(String name, Class<? extends BukkitCommand> classSelf, boolean inheritedCommands) {
        super(name);
        this.clazz = classSelf;
        this.inheritedCommands = inheritedCommands;
        Method[] allMethods = this.clazz.getDeclaredMethods();
        for (Method method : allMethods)
        {
            if (method.isAnnotationPresent(InheritedCommand.class))
            {
                commandMethods.add(method);
            }
        }
    }

    public boolean checkSender(CommandSender sender)
    {
        if (!equalsSender(sender))
        {
            sender.sendMessage("§c你不是一个" + this.senderType.cn);
            return false;
        }

        if (permission.isEmpty())
            return true;

        if (!sender.hasPermission(permission))
        {
            sender.sendMessage("§c你没有足够的权限");
            return false;
        }

        return true;
    }

    public boolean checkSender(CommandSender sender, InheritedCommand command)
    {
        if (!equalsSender(sender, command))
        {
            sender.sendMessage("§c你不是一个" + command.sender().cn);
            return false;
        }

        if (command.permission().isEmpty())
            return true;

        if (!sender.hasPermission(command.permission()))
        {
            sender.sendMessage("§c你没有足够的权限");
            return false;
        }

        return true;
    }

    public String[] help()
    {
        List<String> helps = new ArrayList<>();
        helps.add("§b§l指令使用教程");
        for (Method method : commandMethods)
        {
            InheritedCommand info = method.getAnnotation(InheritedCommand.class);
            String help = "§b" + info.usage() + " §8- §e" + info.description();
            helps.add(help);
        }

        return helps.toArray(new String[0]);
    }

    public boolean equalsSender(CommandSender sender)
    {
        if (this.senderType == SenderType.ALL)
            return true;

        SenderType senderType;
        if (sender instanceof Player)
            senderType = SenderType.PLAYER;
        else
            senderType = SenderType.CONSOLE;


        return senderType == this.senderType;
    }

    public boolean equalsSender(CommandSender sender, InheritedCommand command)
    {
        if (command.sender() == SenderType.ALL)
            return true;

        SenderType senderType;
        if (sender instanceof Player)
            senderType = SenderType.PLAYER;
        else
            senderType = SenderType.CONSOLE;


        return senderType == command.sender();
    }


    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {

        if (!checkSender(sender))
            return false;

        if (!inheritedCommands)
        {
            this.onMain(sender, args);
            return true;
        }

        if (this.commandMethods.size() < 1)
        {
            this.onMain(sender, args);
            return true;
        }

        if (args.length < 1)
        {
            sender.sendMessage(help());
            return false;
        }

        String commandValue = args[0];
        for (Method method : commandMethods)
        {
            InheritedCommand commandSet = method.getAnnotation(InheritedCommand.class);
            if (!commandSet.value().equalsIgnoreCase(commandValue))
                continue;

            //start to execute sub command
            if (!checkSender(sender, commandSet))
                return false;

            Class<?>[] methodArgs = method.getParameterTypes();
            try {
                if (methodArgs.length == 0)
                {
                    method.invoke(this);
                }
                if (methodArgs.length == 1 && methodArgs[0] == CommandSender.class)
                {
                    method.invoke(this, sender);
                    return true;
                }
                else if (methodArgs.length == 2 && methodArgs[0] == CommandSender.class && methodArgs[1] == String[].class)
                {
                    method.invoke(this, sender, args);
                    return true;
                }
                else if (methodArgs.length == 3 && methodArgs[0] == CommandSender.class && methodArgs[1] == String.class && methodArgs[2] == String[].class)
                {
                    method.invoke(this, sender, s, args);
                    return true;
                }

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }

        sender.sendMessage(help());
        return false;
    }

    public boolean lastExtends(Class son, Class father)
    {
        if (son == father)
            return true;
        Class superNow = son.getSuperclass();
        while (true)
        {
            if (superNow == father)
                return true;
            else if (superNow == Object.class || superNow.getSuperclass() == superNow)
                return false;
            superNow = superNow.getSuperclass();
        }
    }

    private SimpleCommandMap getCommandMap() throws NoSuchFieldException, IllegalAccessException {
        Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        return (SimpleCommandMap) commandMapField.get(Bukkit.getServer());
    }

    public void register() {
        try
        {
            if (this.getCommandMap().getCommands().contains(this))
                return;
            this.getCommandMap().register(this.getName(), this);
        }
        catch (Exception exception)
        {
            minecraftLogger.warning("Error while register command \"" + this.getName() + "\":");
            exception.printStackTrace();
        }
    }

    public void onMain(CommandSender sender, String[] args)
    {

    }

    public void unregister() {
        try
        {
            if (!this.getCommandMap().getCommands().contains(this))
                return;
            this.unregister(this.getCommandMap());
        }
        catch (Exception exception)
        {
            minecraftLogger.warning("Error in registering command \"" + this.getName() + "\":");
            exception.printStackTrace();
        }
    }

    private static Logger minecraftLogger = Logger.getLogger("Minecraft");
}

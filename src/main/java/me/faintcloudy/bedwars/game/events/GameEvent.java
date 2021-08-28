package me.faintcloudy.bedwars.game.events;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.Game;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.team.Team;
import me.faintcloudy.bedwars.game.team.TeamState;
import me.faintcloudy.bedwars.game.team.upgrade.TeamUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

public interface GameEvent {
    int time();
    String name();
    void onEvent();
    //6 12 18 24 30 40 50
    //resource upgrades... bed gone 、 sudden death 、 game end
    GameEvent GAME_END = new GameEvent() {
        @Override
        public int time() {
            return 10 * 60;
        }

        @Override
        public String name() {
            return "游戏结束";
        }

        @Override
        public void onEvent() {
            Bedwars.getInstance().game.onEnd();
        }
    };
    GameEvent SUDDEN_DEATH = new GameEvent() {
        @Override
        public int time() {
            return 10 * 60;
        }

        @Override
        public String name() {
            return "绝杀模式";
        }

        @Override
        public void onEvent() {
            for (Team team : Bedwars.getInstance().game.teams)
            {
                for (int i = 0;i<team.upgradeLevels.get(TeamUpgrade.DRAGON_BUFF) + 1;i++)
                {
                    Location middle = Bedwars.getInstance().game.map.middle;
                    EnderDragon dragon = (EnderDragon) middle.getWorld().spawnEntity(middle, EntityType.ENDER_DRAGON);
                    dragon.setCustomName(team.color.chatColor + team.color.cn + " 龙");
                    dragon.setCustomNameVisible(true);
                    dragon.setMaxHealth(500.0);
                    dragon.setHealth(500.0);
                    dragon.setMetadata("Team", new FixedMetadataValue(Bedwars.getInstance(), team.color.name()));

                }
            }
            GamePlayer.getOnlineGamePlayers().forEach(player ->
            {
                player.player.sendMessage("§a绝杀模式开始，接受龙的洗礼吧！");
                player.sendTitle("§c绝杀模式", "§e接受龙的洗礼吧！", 20, 20, 20);
            });

        }
    };
    GameEvent BED_GONE = new GameEvent() {
        @Override
        public int time() {
            return 6 * 60;
        }

        @Override
        public String name() {
            return "床自毁";
        }

        @Override
        public void onEvent() {
            for (Team team : Bedwars.getInstance().game.teams)
            {
                if (team.state != TeamState.ALIVE)
                    continue;
                team.brokeBed();
            }
            for (GamePlayer p : GamePlayer.getOnlineGamePlayers()) {
                p.sendTitle("§c床已被破坏!", "§f所有的床已被破坏", 20, 20, 20);
                p.player.sendMessage("§c§l所有床已被破坏!");
            }
        }
    };
    GameEvent DIAMOND_RESOURCE_UPGRADE_II = new GameEvent() {
        @Override
        public int time() {
            return 6 * 60;
        }

        @Override
        public String name() {
            return "钻石生成点II级";
        }

        @Override
        public void onEvent() {
            Bedwars.getInstance().game.tiers.put(ResourceType.DIAMOND, 2);
            GamePlayer.getOnlineGamePlayers().forEach(player -> player.player.sendMessage("§b钻石生成点§e已升级至 §cII §e级!"));
        }
    };
    GameEvent EMERALD_RESOURCE_UPGRADE_II = new GameEvent() {
        @Override
        public int time() {
            return 6 * 60;
        }

        @Override
        public String name() {
            return "绿宝石生成点II级";
        }

        @Override
        public void onEvent() {
            Bedwars.getInstance().game.tiers.put(ResourceType.EMERALD, 2);
            GamePlayer.getOnlineGamePlayers().forEach(player -> player.player.sendMessage(ChatColor.DARK_GREEN + "绿宝石生成点§e已升级至 §cII §e级!"));
        }
    };

    GameEvent DIAMOND_RESOURCE_UPGRADE_III = new GameEvent() {
        @Override
        public int time() {
            return 6 * 60;
        }

        @Override
        public String name() {
            return "钻石生成点III级";
        }

        @Override
        public void onEvent() {
            Bedwars.getInstance().game.tiers.put(ResourceType.DIAMOND, 3);
            GamePlayer.getOnlineGamePlayers().forEach(player -> player.player.sendMessage("§b钻石生成点§e已升级至 §cIII §e级!"));
        }
    };

    GameEvent EMERALD_RESOURCE_UPGRADE_III = new GameEvent() {
        @Override
        public int time() {
            return 6 * 60;
        }

        @Override
        public String name() {
            return "绿宝石生成点III级";
        }

        @Override
        public void onEvent() {
            Bedwars.getInstance().game.tiers.put(ResourceType.EMERALD, 3);
            GamePlayer.getOnlineGamePlayers().forEach(player -> player.player.sendMessage(ChatColor.DARK_GREEN + "绿宝石生成点§e已升级至 §cIII §e级!"));
        }
    };
}

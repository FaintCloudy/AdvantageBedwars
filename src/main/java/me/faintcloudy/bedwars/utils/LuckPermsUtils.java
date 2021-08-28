/*    */
package me.faintcloudy.bedwars.utils;
/*    */ 
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.*;
/*    */ import net.luckperms.api.LuckPermsProvider;
/*    */ import net.luckperms.api.context.ContextSet;
/*    */ import net.luckperms.api.model.group.Group;
/*    */ import net.luckperms.api.node.NodeType;
/*    */ import net.luckperms.api.node.types.InheritanceNode;
/*    */ import net.luckperms.api.query.QueryOptions;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public class LuckPermsUtils {
/*    */   public static String getPrefix(Player p) {
/* 17 */     if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
/* 18 */       return "§7";
/*    */     }
if (!Bukkit.getPluginManager().getPlugin("LuckPerms").getDescription().getVersion().equals("5.3.16"))
{

    List<Integer> versionSplit = Arrays.asList(5, 3, 16);
    String[] split = Bukkit.getPluginManager().getPlugin("LuckPerms").getDescription().getVersion().split("\\.");
    for (int i = 0;i<split.length;i++)
    {
        if (Integer.parseInt(split[i]) < versionSplit.get(i))
            return "§7";
    }
}
/* 20 */     if (LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()) == null || LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()).getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getPrefix() == null) {
/* 21 */       return "§7";
/*    */     }
/* 23 */     return ChatColor.translateAlternateColorCodes('&', LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()).getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getPrefix() + " ");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getSuffix(Player p) {
    if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
/* 31 */       return "§7";
/*    */     }
/* 33 */     if (LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()).getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getSuffix() == null) {
/* 34 */       return "";
/*    */     }
/* 36 */     return ChatColor.translateAlternateColorCodes('&', " " + LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()).getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getSuffix());
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getRank(Player p) {
/* 42 */     return LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()).getNodes().stream()
/* 43 */       .filter(NodeType.INHERITANCE::matches)
/* 44 */       .map(NodeType.INHERITANCE::cast)
/* 45 */       .filter(n -> n.getContexts().isSatisfiedBy((ContextSet)QueryOptions.defaultContextualOptions().context()))
/* 46 */       .map(InheritanceNode::getGroupName)
/* 47 */       .map(n -> LuckPermsProvider.get().getGroupManager().getGroup(n))
/* 48 */       .filter(Objects::nonNull)
/* 49 */       .min((o1, o2) -> {
/*    */           int ret = Integer.compare(o1.getWeight().orElse(0), o2.getWeight().orElse(0));
/*    */           
/*    */           return (ret == 1) ? -1 : 1;
/* 53 */         }).map(Group::getName)
/* 54 */       .map(LuckPermsUtils::convertGroupDisplayName)
/* 55 */       .orElse("");
/*    */   }
/*    */   private static String convertGroupDisplayName(String groupName) {
/* 58 */     Group group = LuckPermsProvider.get().getGroupManager().getGroup(groupName);
/* 59 */     if (group != null) {
/* 60 */       groupName = group.getFriendlyName();
/*    */     }
/* 62 */     return groupName;
/*    */   }
/*    */   public static String getGroupPrefix(String group) {
/* 65 */     Group g = LuckPermsProvider.get().getGroupManager().getGroup(group);
/* 66 */     return ChatColor.translateAlternateColorCodes('&', g.getCachedData().getMetaData().getPrefix());
/*    */   }
/*    */   public static String getGroupSuffix(String group) {
/* 69 */     Group g = LuckPermsProvider.get().getGroupManager().getGroup(group);
/* 70 */     return ChatColor.translateAlternateColorCodes('&', g.getCachedData().getMetaData().getSuffix());
/*    */   }
/*    */   public static String getGroup(Player p) {
/* 73 */     return LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()).getPrimaryGroup();
/*    */   }
/*    */   public static String formatTime(int i) {
/* 76 */     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy/MM/dd hh:mm:ss");
/* 77 */     return simpleDateFormat.format(Long.valueOf(System.currentTimeMillis() + (i * 1000)));
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Desktop\IBedwars-1.0-SNAPSHOT.jar!\me\huanmeng\ibedwar\\utils\player\LuckPerms.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */
package me.faintcloudy.bedwars.utils;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Objects;
/*     */ import net.minecraft.server.v1_8_R3.Entity;
/*     */ import net.minecraft.server.v1_8_R3.EntityLightning;
/*     */ import net.minecraft.server.v1_8_R3.Packet;
/*     */ import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
/*     */ import net.minecraft.server.v1_8_R3.World;
/*     */ import net.minecraft.server.v1_8_R3.WorldServer;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Sound;
/*     */ import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
/*     */ import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TitleUtils
/*     */ {
/*     */   public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String message) {
/*  26 */     sendTitle(player, fadeIn, stay, fadeOut, message, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void sendSubtitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String message) {
/*  31 */     sendTitle(player, fadeIn, stay, fadeOut, null, message);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void sendFullTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
/*  36 */     sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void sendPacket(Player player, Object packet) {
/*     */     try {
/*  42 */       Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
/*     */       
/*  44 */       Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
/*  45 */       playerConnection.getClass()
/*  46 */         .getMethod("sendPacket", new Class[] { getNMSClass("Packet")
/*  47 */           }).invoke(playerConnection, packet);
/*  48 */     } catch (Exception e) {
/*  49 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static Class<?> getNMSClass(String name) {
/*  55 */     String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
/*     */     try {
/*  57 */       return Class.forName("net.minecraft.server." + version + "." + name);
/*  58 */     } catch (ClassNotFoundException e) {
/*  59 */       e.printStackTrace();
/*     */       
/*  61 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static <T> void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
/*     */     try {
/*  67 */       if (title != null) {
/*  68 */         title = ChatColor.translateAlternateColorCodes('&', title);
/*  69 */         title = title.replaceAll("%player%", player.getDisplayName());
/*     */         
/*  71 */         Object e = ((Class)Objects.requireNonNull((T)getNMSClass("PacketPlayOutTitle"))).getDeclaredClasses()[0].getField("TIMES").get(null);
/*     */ 
/*     */         
/*  74 */         Object chatTitle = ((Class)Objects.requireNonNull((T)getNMSClass("IChatBaseComponent"))).getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + title + "\"}");
/*     */ 
/*     */         
/*  77 */         Constructor subtitleConstructor = ((Class)Objects.<Class<?>>requireNonNull(getNMSClass("PacketPlayOutTitle"))).getConstructor(((Class)Objects.requireNonNull((T)getNMSClass("PacketPlayOutTitle"))).getDeclaredClasses()[0],
                    /*  78 */               getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
/*     */         
/*  80 */         Object titlePacket = subtitleConstructor.newInstance(e, chatTitle, fadeIn, stay, fadeOut);
/*     */         
/*  82 */         sendPacket(player, titlePacket);
/*     */         
/*  84 */         e = ((Class)Objects.requireNonNull((T)getNMSClass("PacketPlayOutTitle"))).getDeclaredClasses()[0].getField("TITLE").get(null);
/*     */ 
/*     */         
/*  87 */         chatTitle = ((Class)Objects.requireNonNull((T)getNMSClass("IChatBaseComponent"))).getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + title + "\"}");
/*     */         
/*  89 */         subtitleConstructor = ((Class)Objects.<Class<?>>requireNonNull(getNMSClass("PacketPlayOutTitle"))).getConstructor(((Class)Objects.requireNonNull((T)getNMSClass("PacketPlayOutTitle"))).getDeclaredClasses()[0],
                    /*  90 */               getNMSClass("IChatBaseComponent"));
/*     */         
/*  92 */         titlePacket = subtitleConstructor.newInstance(e, chatTitle);
/*  93 */         sendPacket(player, titlePacket);
/*     */       } 
/*  95 */       if (subtitle != null) {
/*  96 */         subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
/*  97 */         subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
/*     */         
/*  99 */         Object e = ((Class)Objects.requireNonNull((T)getNMSClass("PacketPlayOutTitle"))).getDeclaredClasses()[0].getField("TIMES").get(null);
/*     */ 
/*     */         
/* 102 */         Object chatSubtitle = ((Class)Objects.requireNonNull((T)getNMSClass("IChatBaseComponent"))).getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + title + "\"}");
/*     */ 
/*     */         
/* 105 */         Constructor subtitleConstructor = ((Class)Objects.<Class<?>>requireNonNull(getNMSClass("PacketPlayOutTitle"))).getConstructor(((Class)Objects.requireNonNull((T)getNMSClass("PacketPlayOutTitle"))).getDeclaredClasses()[0],
                    /* 106 */               getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
/*     */         
/* 108 */         Object subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
/*     */         
/* 110 */         sendPacket(player, subtitlePacket);
/*     */         
/* 112 */         e = ((Class)Objects.requireNonNull((T)getNMSClass("PacketPlayOutTitle"))).getDeclaredClasses()[0].getField("SUBTITLE").get(null);
/*     */ 
/*     */         
/* 115 */         chatSubtitle = ((Class)Objects.requireNonNull((T)getNMSClass("IChatBaseComponent"))).getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + subtitle + "\"}");
/*     */         
/* 117 */         subtitleConstructor = ((Class)Objects.<Class<?>>requireNonNull(getNMSClass("PacketPlayOutTitle"))).getConstructor(((Class)Objects.requireNonNull((T)getNMSClass("PacketPlayOutTitle"))).getDeclaredClasses()[0],
                    /* 118 */               getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
/*     */         
/* 120 */         subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
/*     */         
/* 122 */         sendPacket(player, subtitlePacket);
/*     */       } 
/* 124 */     } catch (Exception var11) {
/* 125 */       var11.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void clearTitle(Player player) {
/* 130 */     sendTitle(player, Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), "", "");
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T> void sendTabTitle(Player player, String header, String footer) {
/* 135 */     if (header == null) {
/* 136 */       header = "";
/*     */     }
/* 138 */     header = ChatColor.translateAlternateColorCodes('&', header);
/* 139 */     if (footer == null) {
/* 140 */       footer = "";
/*     */     }
/* 142 */     footer = ChatColor.translateAlternateColorCodes('&', footer);
/* 143 */     header = header.replaceAll("%player%", player.getDisplayName());
/* 144 */     footer = footer.replaceAll("%player%", player.getDisplayName());
/*     */ 
/*     */     
/*     */     try {
/* 148 */       Object tabHeader = ((Class)Objects.requireNonNull((T)getNMSClass("IChatBaseComponent"))).getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + header + "\"}");
/*     */ 
/*     */       
/* 151 */       Object tabFooter = ((Class)Objects.requireNonNull((T)getNMSClass("IChatBaseComponent"))).getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + footer + "\"}");
/*     */       
/* 153 */       Constructor<?> titleConstructor = ((Class)Objects.<Class<?>>requireNonNull(getNMSClass("PacketPlayOutPlayerListHeaderFooter"))).getConstructor();
/* 154 */       Object packet = titleConstructor.newInstance();
/* 155 */       Field aField = packet.getClass().getDeclaredField("a");
/* 156 */       aField.setAccessible(true);
/* 157 */       aField.set(packet, tabHeader);
/* 158 */       Field bField = packet.getClass().getDeclaredField("b");
/* 159 */       bField.setAccessible(true);
/* 160 */       bField.set(packet, tabFooter);
/* 161 */       sendPacket(player, packet);
/* 162 */     } catch (Exception ex) {
/* 163 */       ex.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static void playFakeLightning(Player sender, boolean silent) {
/* 169 */     WorldServer worldServer = ((CraftWorld)sender.getLocation().getWorld()).getHandle();
/* 170 */     PacketPlayOutSpawnEntityWeather packetPlayOutSpawnEntityWeather = new PacketPlayOutSpawnEntityWeather(new EntityLightning(worldServer, sender.getLocation().getX(), sender.getLocation().getY(), sender.getLocation().getZ()));
/*     */     
/* 172 */     (((CraftPlayer)sender).getHandle()).playerConnection.sendPacket(packetPlayOutSpawnEntityWeather);
/*     */     
/* 174 */     if (!silent)
/* 175 */       sender.getLocation().getWorld().playSound(sender.getLocation(), Sound.AMBIENCE_THUNDER, 100.0F, 0.0F); 
/*     */   }
/*     */   
/*     */   public static void playFakeLightning(Player sender) {
/* 179 */     playFakeLightning(sender, true);
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Desktop\IBedwars-1.0-SNAPSHOT.jar!\me\huanmeng\ibedwar\\utils\player\TitleUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package me.faintcloudy.bedwars.utils;
/*     */ 
/*     */

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */

/*     */
/*     */ public enum ParticleEffects
/*     */ {
/*  20 */   EXPLOSION_NORMAL("explode", 0, -1, ParticleProperty.DIRECTIONAL),
/*  21 */   EXPLOSION_LARGE("largeexplode", 1, -1),
/*  22 */   EXPLOSION_HUGE("hugeexplosion", 2, -1),
/*  23 */   FIREWORKS_SPARK("fireworksSpark", 3, -1, ParticleProperty.DIRECTIONAL),
/*  24 */   WATER_BUBBLE("bubble", 4, -1, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_WATER),
/*  25 */   WATER_SPLASH("splash", 5, -1, ParticleProperty.DIRECTIONAL),
/*  26 */   WATER_WAKE("wake", 6, 7, ParticleProperty.DIRECTIONAL),
/*  27 */   SUSPENDED("suspended", 7, -1, ParticleProperty.REQUIRES_WATER),
/*  28 */   SUSPENDED_DEPTH("depthSuspend", 8, -1, ParticleProperty.DIRECTIONAL),
/*  29 */   CRIT("crit", 9, -1, ParticleProperty.DIRECTIONAL),
/*  30 */   CRIT_MAGIC("magicCrit", 10, -1, ParticleProperty.DIRECTIONAL),
/*  31 */   SMOKE_NORMAL("smoke", 11, -1, ParticleProperty.DIRECTIONAL),
/*  32 */   SMOKE_LARGE("largesmoke", 12, -1, ParticleProperty.DIRECTIONAL),
/*  33 */   SPELL("spell", 13, -1),
/*  34 */   SPELL_INSTANT("instantSpell", 14, -1),
/*  35 */   SPELL_MOB("mobSpell", 15, -1, ParticleProperty.COLORABLE),
/*  36 */   SPELL_MOB_AMBIENT("mobSpellAmbient", 16, -1, ParticleProperty.COLORABLE),
/*  37 */   SPELL_WITCH("witchMagic", 17, -1),
/*  38 */   DRIP_WATER("dripWater", 18, -1),
/*  39 */   DRIP_LAVA("dripLava", 19, -1),
/*  40 */   VILLAGER_ANGRY("angryVillager", 20, -1),
/*  41 */   VILLAGER_HAPPY("happyVillager", 21, -1, ParticleProperty.DIRECTIONAL
        /*     */),
/*  43 */   TOWN_AURA("townaura", 22, -1, ParticleProperty.DIRECTIONAL
        /*     */),
/*  45 */   NOTE("note", 23, -1, ParticleProperty.COLORABLE
        /*     */),
/*  47 */   PORTAL("portal", 24, -1, ParticleProperty.DIRECTIONAL
        /*     */),
/*  49 */   ENCHANTMENT_TABLE("enchantmenttable", 25, -1, ParticleProperty.DIRECTIONAL
        /*     */),
/*  51 */   FLAME("flame", 26, -1, ParticleProperty.DIRECTIONAL
        /*     */),
/*  53 */   LAVA("lava", 27, -1),
/*  54 */   FOOTSTEP("footstep", 28, -1),
/*  55 */   CLOUD("cloud", 29, -1, ParticleProperty.DIRECTIONAL),
/*  56 */   REDSTONE("reddust", 30, -1, ParticleProperty.COLORABLE
        /*     */),
/*  58 */   SNOWBALL("snowballpoof", 31, -1),
/*  59 */   SNOW_SHOVEL("snowshovel", 32, -1, ParticleProperty.DIRECTIONAL
        /*     */),
/*  61 */   SLIME("slime", 33, -1),
/*  62 */   HEART("heart", 34, -1),
/*  63 */   BARRIER("barrier", 35, 8),
/*  64 */   ITEM_CRACK("iconcrack", 36, -1, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_DATA
        /*     */),
/*  66 */   BLOCK_CRACK("blockcrack", 37, -1, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_DATA
        /*     */
        /*     */),
/*  69 */   BLOCK_DUST("blockdust", 38, 7, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_DATA
        /*     */),
/*  71 */   WATER_DROP("droplet", 39, 8),
/*  72 */   ITEM_TAKE("take", 40, 8),
/*  73 */   MOB_APPEARANCE("mobappearance", 41, 8);
/*     */   
/*     */   private static final Map<String, ParticleEffects> NAME_MAP;
/*     */   
/*     */   private static final Map<Integer, ParticleEffects> ID_MAP;
/*     */   private final String name;
/*     */   private final int id;
/*     */   private final int requiredVersion;
/*     */   private final List<ParticleProperty> properties;
/*     */   
/*     */   static {
/*  84 */     NAME_MAP = new HashMap<>();
/*  85 */     ID_MAP = new HashMap<>();
/*  86 */     for (ParticleEffects effect : values()) {
/*  87 */       NAME_MAP.put(effect.name, effect);
/*  88 */       ID_MAP.put(Integer.valueOf(effect.id), effect);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   ParticleEffects(String name, int id, int requiredVersion, ParticleProperty... properties) {
/*  94 */     this.name = name;
/*  95 */     this.id = id;
/*  96 */     this.requiredVersion = requiredVersion;
/*  97 */     this.properties = Arrays.asList(properties);
/*     */   }
/*     */   
/*     */   public String getName() {
/* 101 */     return this.name;
/*     */   }
/*     */   
/*     */   public int getId() {
/* 105 */     return this.id;
/*     */   }
/*     */   
/*     */   public int getRequiredVersion() {
/* 109 */     return this.requiredVersion;
/*     */   }
/*     */   
/*     */   public boolean hasProperty(ParticleProperty property) {
/* 113 */     return this.properties.contains(property);
/*     */   }
/*     */   
/*     */   public boolean isSupported() {
/* 117 */     if (this.requiredVersion == -1)
/* 118 */       return true; 
/* 119 */     return (ParticlePacket.getVersion() >= this.requiredVersion);
/*     */   }
/*     */   
/*     */   public static ParticleEffects fromName(String name) {
/* 123 */     for (Map.Entry<String, ParticleEffects> entry : NAME_MAP.entrySet()) {
/* 124 */       if (entry.getKey().equalsIgnoreCase(name))
/* 125 */         return entry.getValue(); 
/*     */     } 
/* 127 */     return null;
/*     */   }
/*     */   
/*     */   public static ParticleEffects fromId(int id) {
/* 131 */     for (Map.Entry<Integer, ParticleEffects> entry : ID_MAP.entrySet()) {
/* 132 */       if (entry.getKey().intValue() == id)
/* 133 */         return entry.getValue(); 
/*     */     } 
/* 135 */     return null;
/*     */   }
/*     */   
/*     */   private static boolean isWater(Location location) {
/* 139 */     Material material = location.getBlock().getType();
/* 140 */     return (material == Material.WATER || material == Material.STATIONARY_WATER);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isLongDistance(Location location, List<Player> players) {
/* 146 */     for (Player player : players) {
/* 147 */       if (player.getLocation().distanceSquared(location) >= 65536.0D)
/* 148 */         return true; 
/*     */     } 
/* 150 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isDataCorrect(ParticleEffects effect, ParticleData data) {
/* 155 */     return ((effect != BLOCK_CRACK && effect != BLOCK_DUST) || data instanceof BlockData || (effect == ITEM_CRACK && data instanceof ItemData));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isColorCorrect(ParticleEffects effect, ParticleColor color) {
/* 161 */     return ((effect != SPELL_MOB && effect != SPELL_MOB_AMBIENT && effect != REDSTONE) || color instanceof OrdinaryColor || (effect == NOTE && color instanceof NoteColor));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
/* 169 */     if (!isSupported()) {
/* 170 */       throw new ParticleVersionException("This particle effect is not supported by your server version");
/*     */     }
/* 172 */     if (hasProperty(ParticleProperty.REQUIRES_DATA)) {
/* 173 */       throw new ParticleDataException("This particle effect requires additional data");
/*     */     }
/* 175 */     if (hasProperty(ParticleProperty.REQUIRES_WATER) && 
/* 176 */       !isWater(center)) {
/* 177 */       throw new IllegalArgumentException("There is no water at the center location");
/*     */     }
/* 179 */     (new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, (range > 256.0D), null))
/* 180 */       .sendTo(center, range);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, List<Player> players) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
/* 187 */     if (!isSupported()) {
/* 188 */       throw new ParticleVersionException("This particle effect is not supported by your server version");
/*     */     }
/* 190 */     if (hasProperty(ParticleProperty.REQUIRES_DATA)) {
/* 191 */       throw new ParticleDataException("This particle effect requires additional data");
/*     */     }
/* 193 */     if (hasProperty(ParticleProperty.REQUIRES_WATER) && 
/* 194 */       !isWater(center)) {
/* 195 */       throw new IllegalArgumentException("There is no water at the center location");
/*     */     }
/* 197 */     (new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, 
/* 198 */         isLongDistance(center, players), null)).sendTo(center, players);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Player... players) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
/* 205 */     display(offsetX, offsetY, offsetZ, speed, amount, center, 
/* 206 */         Arrays.asList(players));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(int amount, Location center, double range) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
/* 212 */     display(0.0F, 0.0F, 0.0F, 0.0F, amount, center, range);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(Location center, double range) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
/* 218 */     display(0.0F, 0.0F, 0.0F, 0.0F, 1, center, range);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(Vector direction, float speed, Location center, double range) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
/* 224 */     if (!isSupported()) {
/* 225 */       throw new ParticleVersionException("This particle effect is not supported by your server version");
/*     */     }
/* 227 */     if (hasProperty(ParticleProperty.REQUIRES_DATA)) {
/* 228 */       throw new ParticleDataException("This particle effect requires additional data");
/*     */     }
/* 230 */     if (!hasProperty(ParticleProperty.DIRECTIONAL)) {
/* 231 */       throw new IllegalArgumentException("This particle effect is not directional");
/*     */     }
/* 233 */     if (hasProperty(ParticleProperty.REQUIRES_WATER) && 
/* 234 */       !isWater(center)) {
/* 235 */       throw new IllegalArgumentException("There is no water at the center location");
/*     */     }
/* 237 */     (new ParticlePacket(this, direction, speed, (range > 256.0D), null))
/* 238 */       .sendTo(center, range);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(Vector direction, float speed, Location center, List<Player> players) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
/* 245 */     if (!isSupported()) {
/* 246 */       throw new ParticleVersionException("This particle effect is not supported by your server version");
/*     */     }
/* 248 */     if (hasProperty(ParticleProperty.REQUIRES_DATA)) {
/* 249 */       throw new ParticleDataException("This particle effect requires additional data");
/*     */     }
/* 251 */     if (!hasProperty(ParticleProperty.DIRECTIONAL)) {
/* 252 */       throw new IllegalArgumentException("This particle effect is not directional");
/*     */     }
/* 254 */     if (hasProperty(ParticleProperty.REQUIRES_WATER) && 
/* 255 */       !isWater(center)) {
/* 256 */       throw new IllegalArgumentException("There is no water at the center location");
/*     */     }
/* 258 */     (new ParticlePacket(this, direction, speed, isLongDistance(center, players), null))
/* 259 */       .sendTo(center, players);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(Vector direction, float speed, Location center, Player... players) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
/* 265 */     display(direction, speed, center, Arrays.asList(players));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(ParticleColor color, Location center, double range) throws ParticleVersionException, ParticleColorException {
/* 271 */     if (!isSupported()) {
/* 272 */       throw new ParticleVersionException("This particle effect is not supported by your server version");
/*     */     }
/* 274 */     if (!hasProperty(ParticleProperty.COLORABLE)) {
/* 275 */       throw new ParticleColorException("This particle effect is not colorable");
/*     */     }
/* 277 */     if (!isColorCorrect(this, color)) {
/* 278 */       throw new ParticleColorException("The particle color type is incorrect");
/*     */     }
/* 280 */     (new ParticlePacket(this, color, (range > 256.0D))).sendTo(center, range);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(ParticleColor color, Location center, List<Player> players) throws ParticleVersionException, ParticleColorException {
/* 287 */     if (!isSupported()) {
/* 288 */       throw new ParticleVersionException("This particle effect is not supported by your server version");
/*     */     }
/* 290 */     if (!hasProperty(ParticleProperty.COLORABLE)) {
/* 291 */       throw new ParticleColorException("This particle effect is not colorable");
/*     */     }
/* 293 */     if (!isColorCorrect(this, color)) {
/* 294 */       throw new ParticleColorException("The particle color type is incorrect");
/*     */     }
/* 296 */     (new ParticlePacket(this, color, isLongDistance(center, players)))
/* 297 */       .sendTo(center, players);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(ParticleColor color, Location center, Player... players) throws ParticleVersionException, ParticleColorException {
/* 303 */     display(color, center, Arrays.asList(players));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(ParticleData data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range) throws ParticleVersionException, ParticleDataException {
/* 310 */     if (!isSupported()) {
/* 311 */       throw new ParticleVersionException("This particle effect is not supported by your server version");
/*     */     }
/* 313 */     if (!hasProperty(ParticleProperty.REQUIRES_DATA)) {
/* 314 */       throw new ParticleDataException("This particle effect does not require additional data");
/*     */     }
/* 316 */     if (!isDataCorrect(this, data)) {
/* 317 */       throw new ParticleDataException("The particle data type is incorrect");
/*     */     }
/* 319 */     (new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, (range > 256.0D), data))
/* 320 */       .sendTo(center, range);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(ParticleData data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, List<Player> players) throws ParticleVersionException, ParticleDataException {
/* 328 */     if (!isSupported()) {
/* 329 */       throw new ParticleVersionException("This particle effect is not supported by your server version");
/*     */     }
/* 331 */     if (!hasProperty(ParticleProperty.REQUIRES_DATA)) {
/* 332 */       throw new ParticleDataException("This particle effect does not require additional data");
/*     */     }
/* 334 */     if (!isDataCorrect(this, data)) {
/* 335 */       throw new ParticleDataException("The particle data type is incorrect");
/*     */     }
/* 337 */     (new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, 
/* 338 */         isLongDistance(center, players), data)).sendTo(center, players);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(ParticleData data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Player... players) throws ParticleVersionException, ParticleDataException {
/* 345 */     display(data, offsetX, offsetY, offsetZ, speed, amount, center, 
/* 346 */         Arrays.asList(players));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(ParticleData data, Vector direction, float speed, Location center, double range) throws ParticleVersionException, ParticleDataException {
/* 353 */     if (!isSupported()) {
/* 354 */       throw new ParticleVersionException("This particle effect is not supported by your server version");
/*     */     }
/* 356 */     if (!hasProperty(ParticleProperty.REQUIRES_DATA)) {
/* 357 */       throw new ParticleDataException("This particle effect does not require additional data");
/*     */     }
/* 359 */     if (!isDataCorrect(this, data)) {
/* 360 */       throw new ParticleDataException("The particle data type is incorrect");
/*     */     }
/* 362 */     (new ParticlePacket(this, direction, speed, (range > 256.0D), data))
/* 363 */       .sendTo(center, range);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(ParticleData data, Vector direction, float speed, Location center, List<Player> players) throws ParticleVersionException, ParticleDataException {
/* 370 */     if (!isSupported()) {
/* 371 */       throw new ParticleVersionException("This particle effect is not supported by your server version");
/*     */     }
/* 373 */     if (!hasProperty(ParticleProperty.REQUIRES_DATA)) {
/* 374 */       throw new ParticleDataException("This particle effect does not require additional data");
/*     */     }
/* 376 */     if (!isDataCorrect(this, data)) {
/* 377 */       throw new ParticleDataException("The particle data type is incorrect");
/*     */     }
/* 379 */     (new ParticlePacket(this, direction, speed, isLongDistance(center, players), data))
/* 380 */       .sendTo(center, players);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(ParticleData data, Color color, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range) {
/* 386 */     if (color != null && (this == REDSTONE || this == SPELL_MOB || this == SPELL_MOB_AMBIENT)) {
/*     */ 
/*     */       
/* 389 */       amount = 0;
/* 390 */       if (speed == 0.0F) {
/* 391 */         speed = 1.0F;
/*     */       }
/* 393 */       offsetX = color.getRed() / 255.0F;
/* 394 */       offsetY = color.getGreen() / 255.0F;
/* 395 */       offsetZ = color.getBlue() / 255.0F;
/* 396 */       if (offsetX < 1.17549435E-38F) {
/* 397 */         offsetX = 1.17549435E-38F;
/*     */       }
/*     */     } 
/* 400 */     if (hasProperty(ParticleProperty.REQUIRES_DATA)) {
/* 401 */       display(data, offsetX, offsetY, offsetZ, speed, amount, center, range);
/*     */     } else {
/*     */       
/* 404 */       display(offsetX, offsetY, offsetZ, speed, amount, center, range);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(ParticleData data, Vector direction, float speed, Location center, Player... players) throws ParticleVersionException, ParticleDataException {
/* 413 */     display(data, direction, speed, center, Arrays.asList(players));
/*     */   }
/*     */   
/*     */   public ParticleData getData(Material material, Byte blockData) {
/* 417 */     ParticleData data = null;
/* 418 */     if (blockData == null) {
/* 419 */       blockData = Byte.valueOf((byte)0);
/*     */     }
/* 421 */     if ((this == BLOCK_CRACK || this == ITEM_CRACK || this == BLOCK_DUST) && material != null && material != Material.AIR)
/*     */     {
/*     */       
/* 424 */       if (this == ITEM_CRACK) {
/* 425 */         data = new ItemData(material, blockData.byteValue());
/*     */       } else {
/* 427 */         data = new BlockData(material, blockData.byteValue());
/*     */       } 
/*     */     }
/* 430 */     return data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum ParticleProperty
/*     */   {
/* 439 */     REQUIRES_WATER, REQUIRES_DATA, DIRECTIONAL, COLORABLE
    /*     */   }
/*     */ 
/*     */   
/*     */   public static abstract class ParticleData
/*     */   {
/*     */     private final Material material;
/*     */     
/*     */     private final byte data;
/*     */     private final int[] packetData;
/*     */     
/*     */     public ParticleData(Material material, byte data) {
/* 451 */       this.material = material;
/* 452 */       this.data = data;
/* 453 */       this.packetData = new int[] { material.getId(), data };
/*     */     }
/*     */     
/*     */     public Material getMaterial() {
/* 457 */       return this.material;
/*     */     }
/*     */     
/*     */     public byte getData() {
/* 461 */       return this.data;
/*     */     }
/*     */     
/*     */     public int[] getPacketData() {
/* 465 */       return this.packetData;
/*     */     }
/*     */     
/*     */     public String getPacketDataString() {
/* 469 */       return "_" + this.packetData[0] + "_" + this.packetData[1];
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class ItemData extends ParticleData {
/*     */     public ItemData(Material material, byte data) {
/* 475 */       super(material, data);
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class BlockData
/*     */     extends ParticleData {
/*     */     public BlockData(Material material, byte data) throws IllegalArgumentException {
/* 482 */       super(material, data);
/* 483 */       if (!material.isBlock())
/* 484 */         throw new IllegalArgumentException("The material is not a block"); 
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract class ParticleColor
/*     */   {
/*     */     public abstract float getValueX();
/*     */     
/*     */     public abstract float getValueY();
/*     */     
/*     */     public abstract float getValueZ();
/*     */   }
/*     */   
/*     */   public static final class OrdinaryColor
/*     */     extends ParticleColor
/*     */   {
/*     */     private final int red;
/*     */     private final int green;
/*     */     private final int blue;
/*     */     
/*     */     public OrdinaryColor(int red, int green, int blue) throws IllegalArgumentException {
/* 505 */       if (red < 0) {
/* 506 */         throw new IllegalArgumentException("The red value is lower than 0");
/*     */       }
/* 508 */       if (red > 255) {
/* 509 */         throw new IllegalArgumentException("The red value is higher than 255");
/*     */       }
/* 511 */       this.red = red;
/* 512 */       if (green < 0) {
/* 513 */         throw new IllegalArgumentException("The green value is lower than 0");
/*     */       }
/* 515 */       if (green > 255) {
/* 516 */         throw new IllegalArgumentException("The green value is higher than 255");
/*     */       }
/* 518 */       this.green = green;
/* 519 */       if (blue < 0) {
/* 520 */         throw new IllegalArgumentException("The blue value is lower than 0");
/*     */       }
/* 522 */       if (blue > 255) {
/* 523 */         throw new IllegalArgumentException("The blue value is higher than 255");
/*     */       }
/* 525 */       this.blue = blue;
/*     */     }
/*     */     
/*     */     public int getRed() {
/* 529 */       return this.red;
/*     */     }
/*     */     
/*     */     public int getGreen() {
/* 533 */       return this.green;
/*     */     }
/*     */     
/*     */     public int getBlue() {
/* 537 */       return this.blue;
/*     */     }
/*     */ 
/*     */     
/*     */     public float getValueX() {
/* 542 */       return this.red / 255.0F;
/*     */     }
/*     */ 
/*     */     
/*     */     public float getValueY() {
/* 547 */       return this.green / 255.0F;
/*     */     }
/*     */ 
/*     */     
/*     */     public float getValueZ() {
/* 552 */       return this.blue / 255.0F;
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class NoteColor extends ParticleColor {
/*     */     private final int note;
/*     */     
/*     */     public NoteColor(int note) throws IllegalArgumentException {
/* 560 */       if (note < 0) {
/* 561 */         throw new IllegalArgumentException("The note value is lower than 0");
/*     */       }
/* 563 */       if (note > 24) {
/* 564 */         throw new IllegalArgumentException("The note value is higher than 24");
/*     */       }
/* 566 */       this.note = note;
/*     */     }
/*     */ 
/*     */     
/*     */     public float getValueX() {
/* 571 */       return this.note / 24.0F;
/*     */     }
/*     */ 
/*     */     
/*     */     public float getValueY() {
/* 576 */       return 0.0F;
/*     */     }
/*     */ 
/*     */     
/*     */     public float getValueZ() {
/* 581 */       return 0.0F;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class ParticleDataException
/*     */     extends RuntimeException
/*     */   {
/*     */     private static final long serialVersionUID = 3203085387160737484L;
/*     */ 
/*     */     
/*     */     public ParticleDataException(String message) {}
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class ParticleColorException
/*     */     extends RuntimeException
/*     */   {
/*     */     private static final long serialVersionUID = 3203085387160737484L;
/*     */     
/*     */     public ParticleColorException(String message) {}
/*     */   }
/*     */   
/*     */   private static final class ParticleVersionException
/*     */     extends RuntimeException
/*     */   {
/*     */     private static final long serialVersionUID = 3203085387160737484L;
/*     */     
/*     */     public ParticleVersionException(String message) {}
/*     */   }
/*     */   
/*     */   public static final class ParticlePacket
/*     */   {
/*     */     private static int version;
/*     */     private static Class<?> enumParticle;
/*     */     private static Constructor<?> packetConstructor;
/*     */     private static Method getHandle;
/*     */     private static Field playerConnection;
/*     */     private static Method sendPacket;
/*     */     private static boolean initialized;
/*     */     private final ParticleEffects effect;
/*     */     private final float offsetX;
/*     */     private final float offsetY;
/*     */     private final float offsetZ;
/*     */     private final float speed;
/*     */     private final int amount;
/*     */     private final boolean longDistance;
/*     */     private final ParticleEffects.ParticleData data;
/*     */     private Object packet;
/*     */     
/*     */     public ParticlePacket(ParticleEffects effect, float offsetX, float offsetY, float offsetZ, float speed, int amount, boolean longDistance, ParticleEffects.ParticleData data) throws IllegalArgumentException {
/* 632 */       initialize();
/* 633 */       if (speed < 0.0F)
/* 634 */         throw new IllegalArgumentException("The speed is lower than 0"); 
/* 635 */       if (amount < 0)
/* 636 */         throw new IllegalArgumentException("The amount is lower than 0"); 
/* 637 */       this.effect = effect;
/* 638 */       this.offsetX = offsetX;
/* 639 */       this.offsetY = offsetY;
/* 640 */       this.offsetZ = offsetZ;
/* 641 */       this.speed = speed;
/* 642 */       this.amount = amount;
/* 643 */       this.longDistance = longDistance;
/* 644 */       this.data = data;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ParticlePacket(ParticleEffects effect, Vector direction, float speed, boolean longDistance, ParticleEffects.ParticleData data) throws IllegalArgumentException {
/* 651 */       this(effect, (float)direction.getX(), (float)direction.getY(), 
/* 652 */           (float)direction.getZ(), speed, 0, longDistance, data);
/*     */     }
/*     */ 
/*     */     
/*     */     public ParticlePacket(ParticleEffects effect, ParticleEffects.ParticleColor color, boolean longDistance) {
/* 657 */       this(effect, color.getValueX(), color.getValueY(), color
/* 658 */           .getValueZ(), 1.0F, 0, longDistance, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public static void initialize() throws VersionIncompatibleException {
/* 663 */       if (initialized)
/*     */         return; 
/*     */       try {
/* 666 */         version = Integer.parseInt(
/* 667 */             Character.toString(
/* 668 */               BukkitReflection.PackageType.getServerVersion().charAt(3)));
/* 669 */         if (version > 7)
/*     */         {
/* 671 */           enumParticle = BukkitReflection.PackageType.MINECRAFT_SERVER.getClass("EnumParticle");
/*     */         }
/*     */         
/* 674 */         Class<?> packetClass = BukkitReflection.PackageType.MINECRAFT_SERVER.getClass((version < 7) ? "Packet63WorldParticles" : "PacketPlayOutWorldParticles");
/*     */         
/* 676 */         packetConstructor = BukkitReflection.getConstructor(packetClass);
/*     */         
/* 678 */         getHandle = BukkitReflection.getMethod("CraftPlayer", BukkitReflection.PackageType.CRAFTBUKKIT_ENTITY, "getHandle");
/*     */ 
/*     */         
/* 681 */         playerConnection = BukkitReflection.getField("EntityPlayer", BukkitReflection.PackageType.MINECRAFT_SERVER, false, "playerConnection");
/*     */ 
/*     */ 
/*     */         
/* 685 */         sendPacket = BukkitReflection.getMethod(playerConnection
/* 686 */             .getType(), "sendPacket", BukkitReflection.PackageType.MINECRAFT_SERVER
/*     */
/*     */
/* 689 */               .getClass("Packet"));
/* 690 */       } catch (Exception exception) {
/* 691 */         throw new VersionIncompatibleException("Your current bukkit version seems to be incompatible with this library", exception);
/*     */       } 
/*     */ 
/*     */       
/* 695 */       initialized = true;
/*     */     }
/*     */     
/*     */     public static int getVersion() {
/* 699 */       return version;
/*     */     }
/*     */     
/*     */     public static boolean isInitialized() {
/* 703 */       return initialized;
/*     */     }
/*     */ 
/*     */     
/*     */     private void initializePacket(Location center) throws PacketInstantiationException {
/* 708 */       if (this.packet != null)
/*     */         return; 
/*     */       try {
/* 711 */         this.packet = packetConstructor.newInstance();
/* 712 */         if (version < 8) {
/* 713 */           String name = this.effect.getName();
/* 714 */           if (this.data != null) {
/* 715 */             name = name + this.data.getPacketDataString();
/*     */           }
/* 717 */           BukkitReflection.setValue(this.packet, true, "a", name);
/*     */         } else {
/*     */           
/* 720 */           BukkitReflection.setValue(this.packet, true, "a", enumParticle
/* 721 */               .getEnumConstants()[this.effect.getId()]);
/* 722 */           BukkitReflection.setValue(this.packet, true, "j", 
/* 723 */               Boolean.valueOf(this.longDistance));
/* 724 */           if (this.data != null) {
/* 725 */             BukkitReflection.setValue(this.packet, true, "k", this.data
/* 726 */                 .getPacketData());
/*     */           }
/*     */         } 
/* 729 */         BukkitReflection.setValue(this.packet, true, "b", 
/* 730 */             Float.valueOf((float)center.getX()));
/* 731 */         BukkitReflection.setValue(this.packet, true, "c", 
/* 732 */             Float.valueOf((float)center.getY()));
/* 733 */         BukkitReflection.setValue(this.packet, true, "d", 
/* 734 */             Float.valueOf((float)center.getZ()));
/* 735 */         BukkitReflection.setValue(this.packet, true, "e", 
/* 736 */             Float.valueOf(this.offsetX));
/* 737 */         BukkitReflection.setValue(this.packet, true, "f", 
/* 738 */             Float.valueOf(this.offsetY));
/* 739 */         BukkitReflection.setValue(this.packet, true, "g", 
/* 740 */             Float.valueOf(this.offsetZ));
/* 741 */         BukkitReflection.setValue(this.packet, true, "h", 
/* 742 */             Float.valueOf(this.speed));
/* 743 */         BukkitReflection.setValue(this.packet, true, "i", 
/* 744 */             Integer.valueOf(this.amount));
/* 745 */       } catch (Exception exception) {
/* 746 */         throw new PacketInstantiationException("Packet instantiation failed", exception);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void sendTo(Location center, Player player) throws PacketInstantiationException, PacketSendingException {
/* 754 */       initializePacket(center);
/*     */       try {
/* 756 */         sendPacket.invoke(playerConnection.get(getHandle.invoke(player)), this.packet);
/*     */       }
/* 758 */       catch (Exception exception) {
/* 759 */         throw new PacketSendingException("Failed to send the packet to player '" + player
/*     */             
/* 761 */             .getName() + "'", exception);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void sendTo(Location center, List<Player> players) throws IllegalArgumentException {
/* 767 */       if (players.isEmpty())
/* 768 */         throw new IllegalArgumentException("The player list is empty"); 
/* 769 */       for (Player player : players) {
/* 770 */         sendTo(center, player);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void sendTo(Location center, double range) throws IllegalArgumentException {
/* 776 */       if (range < 1.0D)
/* 777 */         throw new IllegalArgumentException("The range is lower than 1"); 
/* 778 */       String worldName = center.getWorld().getName();
/* 779 */       double squared = range * range;
/* 780 */       for (Player player : Bukkit.getOnlinePlayers()) {
/* 781 */         if (player.getWorld().getName().equals(worldName) && player
/* 782 */           .getLocation().distanceSquared(center) <= squared)
/* 783 */           sendTo(center, player); 
/*     */       } 
/*     */     }
/*     */     
/*     */     private static final class VersionIncompatibleException
/*     */       extends RuntimeException
/*     */     {
/*     */       private static final long serialVersionUID = 3203085387160737484L;
/*     */       
/*     */       public VersionIncompatibleException(String message, Throwable cause) {
/* 793 */         super(cause);
/*     */       }
/*     */     }
/*     */     
/*     */     private static final class PacketInstantiationException
/*     */       extends RuntimeException {
/*     */       private static final long serialVersionUID = 3203085387160737484L;
/*     */       
/*     */       public PacketInstantiationException(String message, Throwable cause) {
/* 802 */         super(cause);
/*     */       }
/*     */     }
/*     */     
/*     */     private static final class PacketSendingException
/*     */       extends RuntimeException {
/*     */       private static final long serialVersionUID = 3203085387160737484L;
/*     */       
/*     */       public PacketSendingException(String message, Throwable cause) {
/* 811 */         super(cause);
/*     */       }
/*     */     }
/*     */   }
/*     */ }



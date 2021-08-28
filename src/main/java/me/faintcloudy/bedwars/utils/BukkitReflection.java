/*     */ package me.faintcloudy.bedwars.utils;
/*     */ 
/*     */

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
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
/*     */ public class BukkitReflection
/*     */ {
/*     */   private static final String CRAFT_BUKKIT_PACKAGE;
/*     */   private static final String NET_MINECRAFT_SERVER_PACKAGE;
/*     */   private static final Class CRAFT_SERVER_CLASS;
/*     */   private static final Method CRAFT_SERVER_GET_HANDLE_METHOD;
/*     */   private static final Class PLAYER_LIST_CLASS;
/*     */   private static final Field PLAYER_LIST_MAX_PLAYERS_FIELD;
/*     */   private static final Class CRAFT_PLAYER_CLASS;
/*     */   private static final Method CRAFT_PLAYER_GET_HANDLE_METHOD;
/*     */   private static final Class ENTITY_PLAYER_CLASS;
/*     */   private static final Field ENTITY_PLAYER_PING_FIELD;
/*     */   private static final Class CRAFT_ITEM_STACK_CLASS;
/*     */   private static final Method CRAFT_ITEM_STACK_AS_NMS_COPY_METHOD;
/*     */   
/*     */   public static void sendLightning(Player p, Location l) {
/*  32 */     Class<?> light = getNMSClass("EntityLightning");
/*     */     
/*     */     try {
/*  35 */       assert light != null;
/*  36 */       Constructor<?> constu = light.getConstructor(new Class[] { getNMSClass("World"), double.class, double.class, double.class, boolean.class, boolean.class });
/*  37 */       Object wh = p.getWorld().getClass().getMethod("getHandle", new Class[0]).invoke(p.getWorld(), new Object[0]);
/*  38 */       Object lighobj = constu.newInstance(new Object[] { wh, Double.valueOf(l.getX()), Double.valueOf(l.getY()), Double.valueOf(l.getZ()), Boolean.valueOf(true), Boolean.valueOf(true) });
/*  39 */       Object obj = getNMSClass("PacketPlayOutSpawnEntityWeather").getConstructor(new Class[] { getNMSClass("Entity") }).newInstance(new Object[] { lighobj });
/*  40 */       sendPacket(p, obj);
/*  41 */       p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 100.0F, 1.0F);
/*  42 */     } catch (SecurityException|IllegalAccessException|IllegalArgumentException|InvocationTargetException|InstantiationException|NoSuchMethodException var7) {
/*  43 */       var7.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static Class<?> getNMSClass(String name) {
/*  49 */     String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
/*     */     
/*     */     try {
/*  52 */       return Class.forName("net.minecraft.server." + version + "." + name);
/*  53 */     } catch (ClassNotFoundException var3) {
/*  54 */       var3.printStackTrace();
/*  55 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void sendPacket(Player player, Object packet) {
/*     */     try {
/*  61 */       Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
/*  62 */       Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
/*  63 */       playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packet });
/*  64 */     } catch (Exception var4) {
/*  65 */       var4.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static int getPing(Player player) {
/*     */     try {
/*  72 */       int ping = ENTITY_PLAYER_PING_FIELD.getInt(CRAFT_PLAYER_GET_HANDLE_METHOD.invoke(player, new Object[0]));
/*  73 */       return Math.max(ping, 0);
/*  74 */     } catch (Exception var2) {
/*  75 */       return 1;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void setMaxPlayers(Server server, int slots) {
/*     */     try {
/*  81 */       PLAYER_LIST_MAX_PLAYERS_FIELD.set(CRAFT_SERVER_GET_HANDLE_METHOD.invoke(server, new Object[0]), Integer.valueOf(slots));
/*  82 */     } catch (Exception var3) {
/*  83 */       var3.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static String getItemStackName(ItemStack itemStack) {
/*     */     try {
/*  90 */       return (String)CRAFT_ITEM_STACK_AS_NMS_COPY_METHOD.invoke(itemStack, new Object[] { itemStack });
/*  91 */     } catch (Exception var2) {
/*  92 */       var2.printStackTrace();
/*  93 */       return "";
/*     */     } 
/*     */   }
/*     */   public static Class<?> getClass(String name) {
/*  97 */     String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
/*     */     try {
/*  99 */       return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
/* 100 */     } catch (Exception e) {
/* 101 */       e.printStackTrace();
/* 102 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
/* 107 */     Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
/* 108 */     for (Constructor<?> constructor : clazz.getConstructors()) {
/* 109 */       if (DataType.compare(DataType.getPrimitive(constructor.getParameterTypes()), primitiveTypes))
/* 110 */         return constructor; 
/*     */     } 
/* 112 */     throw new NoSuchMethodException("There is no such constructor in this class with the specified parameter types");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Constructor<?> getConstructor(String className, PackageType packageType, Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
/* 118 */     return getConstructor(packageType.getClass(className), parameterTypes);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Object instantiateObject(Class<?> clazz, Object... arguments) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
/* 123 */     return getConstructor(clazz, DataType.getPrimitive(arguments)).newInstance(arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object instantiateObject(String className, PackageType packageType, Object... arguments) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
/* 129 */     return instantiateObject(packageType.getClass(className), arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
/* 134 */     Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
/* 135 */     for (Method method : clazz.getMethods()) {
/* 136 */       if (method.getName().equals(methodName) && 
/* 137 */         DataType.compare(DataType.getPrimitive(method.getParameterTypes()), primitiveTypes))
/* 138 */         return method; 
/*     */     } 
/* 140 */     throw new NoSuchMethodException("There is no such method in this class with the specified name and parameter types");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Method getMethod(String className, PackageType packageType, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
/* 146 */     return getMethod(packageType.getClass(className), methodName, parameterTypes);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Object invokeMethod(Object instance, String methodName, Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
/* 151 */     return getMethod(instance.getClass(), methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Object invokeMethod(Object instance, Class<?> clazz, String methodName, Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
/* 156 */     return getMethod(clazz, methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object invokeMethod(Object instance, String className, PackageType packageType, String methodName, Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
/* 162 */     return invokeMethod(instance, packageType.getClass(className), methodName, arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Field getField(Class<?> clazz, boolean declared, String fieldName) throws NoSuchFieldException, SecurityException {
/* 167 */     Field field = declared ? clazz.getDeclaredField(fieldName) : clazz.getField(fieldName);
/* 168 */     field.setAccessible(true);
/* 169 */     return field;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Field getField(String className, PackageType packageType, boolean declared, String fieldName) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
/* 174 */     return getField(packageType.getClass(className), declared, fieldName);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Object getValue(Object instance, Class<?> clazz, boolean declared, String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
/* 179 */     return getField(clazz, declared, fieldName).get(instance);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object getValue(Object instance, String className, PackageType packageType, boolean declared, String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException {
/* 185 */     return getValue(instance, packageType.getClass(className), declared, fieldName);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Object getValue(Object instance, boolean declared, String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
/* 190 */     return getValue(instance, instance.getClass(), declared, fieldName);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void setValue(Object instance, Class<?> clazz, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
/* 195 */     getField(clazz, declared, fieldName).set(instance, value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setValue(Object instance, String className, PackageType packageType, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException {
/* 201 */     setValue(instance, packageType.getClass(className), declared, fieldName, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void setValue(Object instance, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
/* 206 */     setValue(instance, instance.getClass(), declared, fieldName, value);
/*     */   }
/*     */   
/*     */   public enum PackageType {
/* 210 */     MINECRAFT_SERVER("net.minecraft.server." + getServerVersion()), CRAFTBUKKIT("org.bukkit.craftbukkit." + 
/* 211 */       getServerVersion() + "."), CRAFTBUKKIT_BLOCK(CRAFTBUKKIT.getPath() +  "block"), CRAFTBUKKIT_CHUNKIO(CRAFTBUKKIT.getPath() +  "chunkio"),
/* 212 */     CRAFTBUKKIT_COMMAND(CRAFTBUKKIT.getPath() +  "command"), CRAFTBUKKIT_CONVERSATIONS(CRAFTBUKKIT.getPath() +  "conversations"),
/* 213 */     CRAFTBUKKIT_ENCHANTMENS(CRAFTBUKKIT.getPath() +  "enchantments"),
/* 214 */     CRAFTBUKKIT_ENTITY(CRAFTBUKKIT.getPath() +  "entity"), CRAFTBUKKIT_EVENT(CRAFTBUKKIT.getPath() +  "event"),
/* 215 */     CRAFTBUKKIT_GENERATOR(CRAFTBUKKIT.getPath() +  "generator"),
/* 216 */     CRAFTBUKKIT_HELP(CRAFTBUKKIT.getPath() +  "help"),
/* 217 */     CRAFTBUKKIT_INVENTORY(CRAFTBUKKIT.getPath() +  "inventory"),
/* 218 */     CRAFTBUKKIT_MAP(CRAFTBUKKIT.getPath() +  "map"),
/* 219 */     CRAFTBUKKIT_METADATA(CRAFTBUKKIT.getPath() +  "metadata"),
/*     */     
/* 221 */     CRAFTBUKKIT_POTION(CRAFTBUKKIT.getPath() +  "potion"),
/*     */     
/* 223 */     CRAFTBUKKIT_PROJECTILES(CRAFTBUKKIT.getPath() +  "projectiles"),
/*     */     
/* 225 */     CRAFTBUKKIT_SCHEDULER(CRAFTBUKKIT.getPath() +  "scheduler"),
/*     */     
/* 227 */     CRAFTBUKKIT_SCOREBOARD(CRAFTBUKKIT.getPath() +  "scoreboard"),
/*     */     
/* 229 */     CRAFTBUKKIT_UPDATER(CRAFTBUKKIT.getPath() +  "updater"),
/*     */     
/* 231 */     CRAFTBUKKIT_UTIL(CRAFTBUKKIT.getPath() +  "util");
/*     */ 
/*     */     
/*     */     private final String path;
/*     */ 
/*     */     
/*     */     PackageType(String path) {
/* 238 */       this.path = path;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getPath() {
/* 246 */       return this.path;
/*     */     }
/*     */     
/*     */     public Class<?> getClass(String className) throws ClassNotFoundException {
/* 250 */       return Class.forName(this + "." + className);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 255 */       return this.path;
/*     */     }
/*     */     
/*     */     public static String getServerVersion() {
/* 259 */       return Bukkit.getServer().getClass().getPackage().getName().substring(23);
/*     */     }
/*     */   }
/*     */   
/*     */   public enum DataType {
/* 264 */     BYTE(byte.class, Byte.class), SHORT(short.class, Short.class), INTEGER(int.class, Integer.class), LONG(long.class, Long.class),
/* 265 */     CHARACTER(char.class, Character.class), FLOAT(float.class, Float.class),
/* 266 */     DOUBLE(double.class, Double.class), BOOLEAN(boolean.class, Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 273 */     private static final Map<Class<?>, DataType> CLASS_MAP = new HashMap<>(); private final Class<?> primitive; static {
/* 274 */       for (DataType type : values()) {
/* 275 */         CLASS_MAP.put(type.primitive, type);
/* 276 */         CLASS_MAP.put(type.reference, type);
/*     */       } 
/*     */     }
/*     */     private final Class<?> reference;
/*     */     DataType(Class<?> primitive, Class<?> reference) {
/* 281 */       this.primitive = primitive;
/* 282 */       this.reference = reference;
/*     */     }
/*     */     
/*     */     public Class<?> getPrimitive() {
/* 286 */       return this.primitive;
/*     */     }
/*     */     
/*     */     public Class<?> getReference() {
/* 290 */       return this.reference;
/*     */     }
/*     */     
/*     */     public static DataType fromClass(Class<?> clazz) {
/* 294 */       return CLASS_MAP.get(clazz);
/*     */     }
/*     */     
/*     */     public static Class<?> getPrimitive(Class<?> clazz) {
/* 298 */       DataType type = fromClass(clazz);
/* 299 */       return (type == null) ? clazz : type.getPrimitive();
/*     */     }
/*     */     
/*     */     public static Class<?> getReference(Class<?> clazz) {
/* 303 */       DataType type = fromClass(clazz);
/* 304 */       return (type == null) ? clazz : type.getReference();
/*     */     }
/*     */     
/*     */     public static Class<?>[] getPrimitive(Class<?>[] classes) {
/* 308 */       int length = (classes == null) ? 0 : classes.length;
/* 309 */       Class<?>[] types = new Class[length];
/* 310 */       for (int index = 0; index < length; index++) {
/* 311 */         types[index] = getPrimitive(classes[index]);
/*     */       }
/* 313 */       return types;
/*     */     }
/*     */     
/*     */     public static Class<?>[] getReference(Class<?>[] classes) {
/* 317 */       int length = (classes == null) ? 0 : classes.length;
/* 318 */       Class<?>[] types = new Class[length];
/* 319 */       for (int index = 0; index < length; index++) {
/* 320 */         types[index] = getReference(classes[index]);
/*     */       }
/* 322 */       return types;
/*     */     }
/*     */     
/*     */     public static Class<?>[] getPrimitive(Object[] objects) {
/* 326 */       int length = (objects == null) ? 0 : objects.length;
/* 327 */       Class<?>[] types = new Class[length];
/* 328 */       for (int index = 0; index < length; index++) {
/* 329 */         types[index] = getPrimitive(objects[index].getClass());
/*     */       }
/* 331 */       return types;
/*     */     }
/*     */     
/*     */     public static Class<?>[] getReference(Object[] objects) {
/* 335 */       int length = (objects == null) ? 0 : objects.length;
/* 336 */       Class<?>[] types = new Class[length];
/* 337 */       for (int index = 0; index < length; index++) {
/* 338 */         types[index] = getReference(objects[index].getClass());
/*     */       }
/* 340 */       return types;
/*     */     }
/*     */     
/*     */     public static boolean compare(Class<?>[] primary, Class<?>[] secondary) {
/* 344 */       if (primary == null || secondary == null || primary.length != secondary.length)
/* 345 */         return false; 
/* 346 */       for (int index = 0; index < primary.length; index++) {
/* 347 */         Class<?> primaryClass = primary[index];
/* 348 */         Class<?> secondaryClass = secondary[index];
/* 349 */         if (!primaryClass.equals(secondaryClass) && !primaryClass.isAssignableFrom(secondaryClass))
/* 350 */           return false; 
/*     */       } 
/* 352 */       return true;
/*     */     } }
/*     */   
/*     */   static {
/*     */     try {
/* 357 */       String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
/* 358 */       CRAFT_BUKKIT_PACKAGE = "org.bukkit.craftbukkit." + version + ".";
/* 359 */       NET_MINECRAFT_SERVER_PACKAGE = "net.minecraft.server." + version + ".";
/* 360 */       CRAFT_SERVER_CLASS = Class.forName(CRAFT_BUKKIT_PACKAGE + "CraftServer");
/* 361 */       CRAFT_SERVER_GET_HANDLE_METHOD = CRAFT_SERVER_CLASS.getDeclaredMethod("getHandle", new Class[0]);
/* 362 */       CRAFT_SERVER_GET_HANDLE_METHOD.setAccessible(true);
/* 363 */       PLAYER_LIST_CLASS = Class.forName(NET_MINECRAFT_SERVER_PACKAGE + "PlayerList");
/* 364 */       PLAYER_LIST_MAX_PLAYERS_FIELD = PLAYER_LIST_CLASS.getDeclaredField("maxPlayers");
/* 365 */       PLAYER_LIST_MAX_PLAYERS_FIELD.setAccessible(true);
/* 366 */       CRAFT_PLAYER_CLASS = Class.forName(CRAFT_BUKKIT_PACKAGE + "entity.CraftPlayer");
/* 367 */       CRAFT_PLAYER_GET_HANDLE_METHOD = CRAFT_PLAYER_CLASS.getDeclaredMethod("getHandle", new Class[0]);
/* 368 */       CRAFT_PLAYER_GET_HANDLE_METHOD.setAccessible(true);
/* 369 */       ENTITY_PLAYER_CLASS = Class.forName(NET_MINECRAFT_SERVER_PACKAGE + "EntityPlayer");
/* 370 */       ENTITY_PLAYER_PING_FIELD = ENTITY_PLAYER_CLASS.getDeclaredField("ping");
/* 371 */       ENTITY_PLAYER_PING_FIELD.setAccessible(true);
/* 372 */       CRAFT_ITEM_STACK_CLASS = Class.forName(CRAFT_BUKKIT_PACKAGE + "inventory.CraftItemStack");
/* 373 */       CRAFT_ITEM_STACK_AS_NMS_COPY_METHOD = CRAFT_ITEM_STACK_CLASS.getDeclaredMethod("asNMSCopy", new Class[] { ItemStack.class });
/* 374 */       CRAFT_ITEM_STACK_AS_NMS_COPY_METHOD.setAccessible(true);
/* 375 */     } catch (Exception var1) {
/* 376 */       var1.printStackTrace();
/* 377 */       throw new RuntimeException("Failed to initialize Bukkit/NMS Reflection");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Desktop\IBedwars-1.0-SNAPSHOT.jar!\me\huanmeng\ibedwar\\utils\BukkitReflection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
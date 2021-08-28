/*     */ package me.faintcloudy.bedwars.utils;
/*     */
import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FileUtil
/*     */ {
/*     */   public static void copyFolder(String resource, String target) throws Exception {
/*  16 */     File resourceFile = new File(resource);
/*  17 */     if (!resourceFile.exists()) {
/*  18 */       throw new Exception("源目标路径：[" + resource + "] 不存在...");
/*     */     }
/*  20 */     File targetFile = new File(target);
/*  21 */     if (!targetFile.exists()) {
/*  22 */       targetFile.mkdir();
/*     */     }
/*     */ 
/*     */     
/*  26 */     File[] resourceFiles = resourceFile.listFiles();
/*     */     
/*  28 */     for (File file : resourceFiles) {
/*     */       
/*  30 */       File file1 = new File(targetFile.getAbsolutePath() + File.separator + resourceFile.getName());
/*     */       
/*  32 */       if (file.isFile()) {
/*  33 */         if (!file1.exists()) {
/*  34 */           file1.mkdirs();
/*     */         }
/*  36 */         File targetFile1 = new File(file1.getAbsolutePath() + File.separator + file.getName());
/*  37 */         copyFile(file, targetFile1);
/*     */       } 
/*     */       
/*  40 */       if (file.isDirectory()) {
/*  41 */         String dir1 = file.getAbsolutePath();
/*     */         
/*  43 */         String dir2 = file1.getAbsolutePath();
/*  44 */         copyFolder(dir1, dir2);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void copyFile(File resource, File target) throws Exception {
/*  58 */     FileInputStream inputStream = new FileInputStream(resource);
/*  59 */     BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
/*     */     
/*  61 */     FileOutputStream outputStream = new FileOutputStream(target);
/*  62 */     BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
/*     */
    /*     */
/*  66 */     byte[] bytes = new byte[2048];
/*  67 */     int len = 0;
/*  68 */     while ((len = inputStream.read(bytes)) != -1) {
/*  69 */       bufferedOutputStream.write(bytes, 0, len);
/*     */     }
/*     */     
/*  72 */     bufferedOutputStream.flush();
/*     */     
/*  74 */     bufferedInputStream.close();
/*  75 */     bufferedOutputStream.close();
/*  76 */     inputStream.close();
/*  77 */     outputStream.close();
/*     */   }
/*     */   
/*     */   public static void deleteDir(String dirPath) {
/*  81 */     File file = new File(dirPath);
/*  82 */     if (file.isFile()) {
/*  83 */       file.delete();
/*     */     } else {
/*  85 */       File[] files = file.listFiles();
/*  86 */       if (files == null) {
/*  87 */         file.delete();
/*     */       } else {
/*  89 */         for (int i = 0; i < files.length; i++)
/*     */         {
/*  91 */           deleteDir(files[i].getAbsolutePath());
/*     */         }
/*  93 */         file.delete();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   public static boolean delete(String path) {
/*  98 */     File file = new File(path);
/*  99 */     if (!file.exists()) {
/* 100 */       return false;
/*     */     }
/* 102 */     if (file.isFile()) {
/* 103 */       return file.delete();
/*     */     }
/* 105 */     File[] files = file.listFiles();
/* 106 */     for (File f : files) {
/* 107 */       if (f.isFile()) {
/* 108 */         if (!f.delete()) {
/* 109 */           System.out.println(f.getAbsolutePath() + " delete error!");
/* 110 */           return false;
/*     */         }
/*     */       
/* 113 */       } else if (!delete(f.getAbsolutePath())) {
/* 114 */         return false;
/*     */       } 
/*     */     } 
/*     */     
/* 118 */     return file.delete();
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Desktop\IBedwars-1.0-SNAPSHOT.jar!\me\huanmeng\ibedwar\\utils\FileUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package com.example.multidextest;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class MultidexInstaller {
    private static final String TAG = "MultidexInstaller";

    public static void installPlugins(Context context) throws Exception {
        String pluginPath = extractMultiDex(context);
        String dexOutput = context.getCacheDir() + File.separator + "DEX";
        File file = new File(dexOutput);
        if (!file.exists()) file.mkdirs();

        DexClassLoader dexClassLoader = new DexClassLoader(pluginPath, dexOutput, null, context.getClassLoader());
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();

        try {
            // 反射获取pathList成员变量Field
            Field dexPathList = BaseDexClassLoader.class.getDeclaredField("pathList");
            dexPathList.setAccessible(true);
            // 现获取两个类加载器内部的pathList成员变量
            Object pathList = dexPathList.get(pathClassLoader);
            Object fixPathList = dexPathList.get(dexClassLoader);

            // 反射获取DexPathList类的dexElements成员变量Field
            Field dexElements = pathList.getClass().getDeclaredField("dexElements");
            dexElements.setAccessible(true);
            // 反射获取pathList对象内部的dexElements成员变量
            Object originDexElements = dexElements.get(pathList);
            Object fixDexElements = dexElements.get(fixPathList);

            // 使用反射获取两个dexElements的长度
            int originLength = Array.getLength(originDexElements);
            int fixLength = Array.getLength(fixDexElements);
            int totalLength = originLength + fixLength;
            // 获取dexElements数组的元素类型
            Class<?> componentClass = originDexElements.getClass().getComponentType();
            // 将修复dexElements的元素放在前面，原始dexElements放到后面，这样就保证加载类的时候优先查找修复类
            Object[] elements = (Object[]) Array.newInstance(componentClass, totalLength);
            for (int i = 0; i < totalLength; i++) {
                if (i < fixLength) {
                    elements[i] = Array.get(fixDexElements, i);
                } else {
                    elements[i] = Array.get(originDexElements, i - fixLength);
                }
            }
            // 将新生成的dexElements数组注入到PathClassLoader内部，
            // 这样App查找类就会先从fixdex查找，在从App安装的dex里查找
            dexElements.set(pathList, elements);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static String extractMultiDex(Context context) throws Exception {
        String pluginPath = context.getApplicationContext().getApplicationInfo().sourceDir;
        ZipFile apk = new ZipFile(pluginPath);
        File path = new File(context.getCacheDir(), "multidex");
        if (!path.exists()) {
            path.mkdir();
        }
        int index = 2;
        byte[] buffer = new byte[1024];
        int length = -1;
        List<String> dexPathList = new ArrayList<>();
        for (ZipEntry zipEntry = apk.getEntry("classes" + index + ".dex");
             zipEntry != null;
             zipEntry = apk.getEntry("classes" + index + ".dex")) {
            InputStream inputStream = apk.getInputStream(zipEntry);
            File dex = new File(path, zipEntry.getName());
            if (!dex.exists()) {
                dex.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(dex);
            while ((length = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }

            fos.close();
            dexPathList.add(dex.getAbsolutePath());
            index++;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < dexPathList.size(); i++) {
            if (i == dexPathList.size() - 1) {
                stringBuilder.append(dexPathList.get(i));
            } else {
                stringBuilder.append(dexPathList.get(i)).append(":");
            }
        }
        return stringBuilder.toString();
    }
}

package com.example.multidextest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;

public class MainDexScript {
    public static final List<String> MAIN_CLASSES = Arrays.asList(
            "com.example.multidextest.MyApplication",
            "com.example.multidextest.MainActivity",
            "com.example.multidextest.MultidexInstaller");

    public static void main(String[] args) throws Exception {
        Set<String> set = new TreeSet<>();
        for (String clazz : MAIN_CLASSES) {
            set.add(clazz);
            bfs(clazz, set);
        }

        File file = new File("app/maindexlist.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        PrintWriter pw = new PrintWriter(fos);
        for (String clazz : set) {
            clazz = clazz.replace(".", "/") + ".class";
            pw.println(clazz);
        }

        List<String> loginR = Arrays.asList("com/example/login/R.class",
                "com/example/login/R$anim.class",
                "com/example/login/R$animator.class",
                "com/example/login/R$array.class",
                "com/example/login/R$attr.class",
                "com/example/login/R$bool.class",
                "com/example/login/R$color.class",
                "com/example/login/R$dimen.class",
                "com/example/login/R$drawable.class",
                "com/example/login/R$id.class",
                "com/example/login/R$integer.class",
                "com/example/login/R$layout.class",
                "com/example/login/R$mipmap.class",
                "com/example/login/R$string.class",
                "com/example/login/R$style.class",
                "com/example/login/R$styleable.class");

        List<String> orderR = Arrays.asList("com/example/order/R.class",
                "com/example/order/R$anim.class",
                "com/example/order/R$animator.class",
                "com/example/order/R$array.class",
                "com/example/order/R$attr.class",
                "com/example/order/R$bool.class",
                "com/example/order/R$color.class",
                "com/example/order/R$dimen.class",
                "com/example/order/R$drawable.class",
                "com/example/order/R$id.class",
                "com/example/order/R$integer.class",
                "com/example/order/R$layout.class",
                "com/example/order/R$mipmap.class",
                "com/example/order/R$string.class",
                "com/example/order/R$style.class",
                "com/example/order/R$styleable.class");

        for (String rPath : orderR) {
            pw.println(rPath);
        }

        for (String rPath : loginR) {
            pw.println(rPath);
        }

        pw.close();

        System.out.println(file.getAbsolutePath());
    }

    /**
     * 查看某个类引用的所有类
     * @param clazz
     * @param set
     * @throws NotFoundException
     */
    private static void bfs(String clazz, Set<String> set) throws NotFoundException {
        CtClass ctClass = ClassPool.getDefault().get(clazz);
        Set<String> levelClasses = new TreeSet<>();
        ClassFile classFile = ctClass.getClassFile();

        /**
         * 遍历代码内使用的类
         */
        for (String className : classFile.getConstPool().getClassNames()) {
            if (className.startsWith("[L")) {
                className = className.substring(2, className.length() - 1);
            } else if (className.startsWith("[")) {
                continue;
            }
            className = getClassName(className);
            addClassName(set, levelClasses, className);
        }

        /**
         * 获取父类
         */
        String superClass = classFile.getSuperclass();
        if (!"".equals(superClass) && superClass != null && !set.contains(superClass)) {
            levelClasses.add(superClass);
            set.add(superClass);
        }

        /**
         * 获取所有接口
         */
        String[] interfaces = classFile.getInterfaces();
        if (interfaces != null) {
            for (String face : interfaces) {
                String className = getClassName(face);
                addClassName(set, levelClasses, className);
            }
        }

        /**
         * 获取字段的类型
         */
        List<FieldInfo> fieldInfoList = classFile.getFields();
        if (fieldInfoList != null) {
            for (FieldInfo fieldInfo : fieldInfoList) {
                String descriptor = fieldInfo.getDescriptor();
                if (descriptor.startsWith("L") && descriptor.endsWith(";")) {
                    String className = descriptor.substring(1, descriptor.length() - 1);
                    className = getClassName(className);
                    addClassName(set, levelClasses, className);
                }

                if (descriptor.startsWith("[L") && descriptor.endsWith(";")) {
                    String className = descriptor.substring(2, descriptor.length() - 1);
                    className = getClassName(className);
                    addClassName(set, levelClasses, className);
                }
            }
        }

        /**
         * 获取方法声明的参数和返回值包含的所有类
         */
        List<MethodInfo> methodInfoList = classFile.getMethods();
        if (methodInfoList != null) {
            for (MethodInfo methodInfo : methodInfoList) {
                String descriptor = methodInfo.getDescriptor();
                extractClassNames(descriptor, set, levelClasses);
            }
        }

        /**
         * 对当前类直接依赖的类，继续查寻它们依赖的其他类
         */
        if (!levelClasses.isEmpty()) {
            for (String className : levelClasses) {
                bfs(className, set);
            }
        }
    }

    private static void addClassName(Set<String> set, Set<String> levelClasses, String className) {
        if (!set.contains(className)) {
            levelClasses.add(className);
            set.add(className);
        }
    }

    private static String getClassName(String className) {
        return className.replaceAll("/", ".");
    }

    private static void extractClassNames(String descriptor, Set<String> set, Set<String> levelClasses) {
        String reg = "(L.+?;)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(descriptor);
        while (matcher.find()) {
            String className = matcher.group();
            className = className.substring(1, className.length() - 1);
            className = getClassName(className);
            addClassName(set, levelClasses, className);
        }
    }

}

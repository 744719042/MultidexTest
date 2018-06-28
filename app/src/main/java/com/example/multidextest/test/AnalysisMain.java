package com.example.multidextest.test;

import java.util.Arrays;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;

public class AnalysisMain {
    public static void main(String[] args) throws NotFoundException {
        CtClass ctClass = ClassPool.getDefault().get("com.example.multidextest.test.User");
        ClassFile classFile = ctClass.getClassFile();
        ConstPool constPool = classFile.getConstPool();

        System.out.println("=======Version========");
        System.out.println(classFile.getMajorVersion());
        System.out.println(classFile.getMinorVersion());
        System.out.println("=======Constant Pool Size========");
        System.out.println(classFile.getConstPool().getSize());
        System.out.println("=======Access Flag========");
        int flag = classFile.getAccessFlags();
        System.out.println("package = " + AccessFlag.isPackage(flag));
        System.out.println("private = " + AccessFlag.isPrivate(flag));
        System.out.println("protected = " + AccessFlag.isProtected(flag));
        System.out.println("public = " + AccessFlag.isPublic(flag));
        System.out.println("=======Super Class=========");
        System.out.println(classFile.getSuperclass());
        System.out.println("=========This class========");
        System.out.println(classFile.getConstPool().getClassName());

        System.out.println("=======Interface Info========");
        System.out.println(classFile.getInterfaces().length);
        for (int i = 0; i < classFile.getInterfaces().length; i++) {
            String face = classFile.getInterfaces()[i];
            System.out.println(face);
        }

        System.out.println("=======Method Info========");
        List<MethodInfo> methodInfoList = classFile.getMethods();
        for (MethodInfo methodInfo : methodInfoList) {
            System.out.println(methodInfo.getName());
            List<AttributeInfo> attributeInfoList = methodInfo.getAttributes();
            for (AttributeInfo attributeInfo : attributeInfoList) {
                System.out.println(attributeInfo.getName());
            }
            System.out.println(methodInfo.getDescriptor());
            System.out.println("--------------------------");
        }
        System.out.println("=======Field Info========");
        List<FieldInfo> fieldInfoList = classFile.getFields();
        for (int i = 0; i < fieldInfoList.size(); i++) {
            FieldInfo fieldInfo = fieldInfoList.get(i);
            System.out.println(fieldInfo.getName());
            System.out.println(fieldInfo.getDescriptor());
            System.out.println("---------------------------");
        }
        System.out.println("=======Attributes Info========");
        List<AttributeInfo> attributeInfoList = classFile.getAttributes();
        for (AttributeInfo attributeInfo : attributeInfoList) {
            System.out.println(attributeInfo.getName());
        }

        System.out.println("=======All Ref classes========");
        System.out.println(constPool.getClassNames());
        System.out.println(Arrays.asList(classFile.getInterfaces()));

//        List<MethodInfo> methodInfoList = ctClass.getClassFile().getMethods();
//        if (methodInfoList != null) {
//            for (MethodInfo methodInfo : methodInfoList) {
//                System.out.println(methodInfo.getDescriptor());
//            }
//        }
//        CtClass string = ClassPool.getDefault().get("java.lang.String");
//        System.out.println(string.getClassFile().getConstPool().getClassNames());
    }
}

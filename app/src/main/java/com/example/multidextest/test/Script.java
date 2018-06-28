package com.example.multidextest.test;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.ConstPool;
/**
 * Author: lwh
 * Date: 4/7/17 15:36.
 */
public class Script {
    private static final java.lang.String TAG = "JavaViewerTest";
    private static final java.lang.String PATH = "/Users/mu/PeriodProject60/SeeyouClient/app/";
    private static final java.lang.String FILENAME = "dexknife_copy.txt";
    private static final java.lang.String[] ENTRIES = new String[]{
            "com.lingan.seeyou.ui.activity.WelcomeActivityTest",
            "com.lingan.seeyou.ui.application.SeeyouApplication",
            "com.lingan.seeyou.ui.application.TinkerApp",
            "com.lingan.seeyou.contentprovider.SeeyouContentProvider",
    };
    private static List<String> mToKeep = new ArrayList<>();
    public static void main(String args[]){
        parseFileJavassist();
    }
    private static void parseFileJavassist(){
        try {
            List<String> entryClass = new ArrayList<>();
//App总入口
            for(int i=0;i<ENTRIES.length;i++){
                entryClass.add(ENTRIES[i]);
            }//找找入口直接引用;
            for(int i=0;i<entryClass.size();i++) {
                String className = entryClass.get(i);
                if(!mToKeep.contains(className)){
                    mToKeep.add(className);
                }
                CtClass ctClass = ClassPool.getDefault().get(className);
                addDependency(ctClass);
            }
//查找间接引用
            String path =PATH;
            String filename = FILENAME;
            File fileResult = new File(path+filename);
            try {
                if (fileResult.exists()) {
                    fileResult.delete();
                }
                if (!fileResult.exists()) {
                    fileResult.createNewFile();
                }
                OutputStreamWriter os = null;
                os = new OutputStreamWriter(new FileOutputStream(fileResult), "utf-8");
                os.write("-donot-use-suggest/n");
                os.write("-split **/n");
                os.write("-keep com.lingan.seeyou.ui.view.RoundView.class/n");
                for(String value:mToKeep){
                    value = value.replace("/",".");
                    os.write("-keep "+value+".class/n");
                }
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private static void addDependency(CtClass ctClass){
        try {
            if(ctClass==null)
                return;
            System.out.print("==>excute dependency for class:"+ctClass.getName()+"/n");
//Direct Ref
            ConstPool constPool = ctClass.getClassFile().getConstPool();
            Iterator iterator = constPool.getClassNames().iterator();
            List<String> temp = new ArrayList<>();
            while (iterator.hasNext()) {
                String classname = (String)iterator.next();
                if(isNotCareClass(classname)){
                    continue;
                }
                if(!mToKeep.contains(classname)){
                    mToKeep.add(classname);
                    temp.add(classname);
                }
            }
//methods
            CtMethod[]methods = ctClass.getMethods();
            if(methods!=null){
                for(CtMethod method:methods){
                    String descriptor = method.getMethodInfo().getDescriptor();
                    if(descriptor.startsWith("(L") && descriptor.contains(";")){
                        String []array = descriptor.split(";");
                        String classname= array[0].substring(2,array[0].length());
                        if(isNotCareClass(classname)){
                            continue;
                        }
                        if(!mToKeep.contains(classname)){
                            mToKeep.add(classname);
                            temp.add(classname);
                        }
                    }
                }
            }
//super class
            if(ctClass.getSuperclass()!=null){
                addDependency(ctClass.getSuperclass());
            }
//interfaces
            if(ctClass.getInterfaces()!=null){
                CtClass[] interfaces = ctClass.getInterfaces();
                for(CtClass ctClassInterface:interfaces){
                    addDependency(ctClassInterface);
                }
            }
//un Direct Ref
            for(String classname:temp){
                classname = classname.replace("/",".");
                CtClass unDirectRef = ClassPool.getDefault().get(classname);
                addDependency(unDirectRef);
            }
//methods
/*if(ctClass.getMethods()!=null){
for(CtMethod method:ctClass.getMethods()){
System.out.print("method:"+method.getMethodInfo().toString()+"/n");
}
}*/
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    //忽略的类名
    private static boolean isNotCareClass(String className) {
        Boolean isNotCare =
                className.startsWith("java") ||
                        className.startsWith("scala") ||
                        className.startsWith("\"[") ||
                                className.startsWith("[") ||
                                        className.startsWith("com.bugtags") ||
                                        (className.startsWith("android") && !className.startsWith("android/support"));
        return isNotCare;
    }
}

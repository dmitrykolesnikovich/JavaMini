package Semantics.myVisitors;

import Semantics.myTypes.MyClass;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.MainClass;
import visitor.DepthFirstVisitor;

import java.util.HashMap;
import java.util.Map;

public class ClassVisitor extends DepthFirstVisitor {

    public static Map<String, MyClass> classes = new HashMap<>();
    public static Map<String, String> extendsMap = new HashMap<>();

    public static boolean superClassHasMember(String className, String methodName) {
        String superName = ClassVisitor.extendsMap.get(className);
        while(superName != null){
            if(ClassVisitor.classes.get(superName).hasMember(methodName)){
                if(!ClassVisitor.classes.get(superName).hasMember(methodName)) return true;
            }
            superName = ClassVisitor.extendsMap.get(superName);
        }
        return false;
    }


    @Override
    public void visit(MainClass n) {
        String className = n.f1.f0.toString();
        ClassVisitor.classes.put(n.f1.f0.toString(),new MyClass(className));
    }

    public void visit(ClassDeclaration n) throws SemanticsException {
        String className = n.f1.f0.toString();
        if(ClassVisitor.classes.containsKey(className)) SemanticsException.err("Class already defined: " + className);
        ClassVisitor.classes.put(className, new MyClass(className));
    }

    public void visit(ClassExtendsDeclaration n) throws SemanticsException {
        String className = n.f1.f0.toString();
        if(ClassVisitor.classes.containsKey(className)) SemanticsException.err("Class already defined: " + className);
        String superClassName = n.f3.f0.toString();
        if(!ClassVisitor.classes.containsKey(superClassName)) SemanticsException.err("Superclass not found: " + superClassName + "for class:" + className);
        ClassVisitor.classes.put(className,new MyClass(className));
        ClassVisitor.extendsMap.put(className,superClassName);
    }


}

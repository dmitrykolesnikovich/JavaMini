package Semantics.myVisitors;

import Semantics.myTypes.*;
import syntaxtree.*;
import visitor.GJDepthFirst;


import java.util.ArrayList;


public class ClassMemberVisitor extends GJDepthFirst<MyType,String> {
    private static ArrayList<MyType> currList = null;
    private static ArrayList<String> currParList = null;

    @Override
    public MyType visit(MainClass n, String argu) throws Exception {
        String className = n.f1.f0.toString();
        String mainMethodName = "main";
        ClassVisitor.classes.get(className).addMember(mainMethodName,new MyMainMethodType("main"));
        visit(n.f14,className);
        visit(n.f15,className);
        return null;
    }

    @Override
    public MyType visit(ClassDeclaration n, String argu) throws Exception {
        String className = n.f1.f0.toString();
        visit(n.f3,className);
        visit(n.f4,className);
        return null;
    }

    @Override
    public MyType visit(MethodDeclaration n, String argu) throws Exception {
        String methodName = n.f2.f0.toString();


        MyType retType = new TypeVisitor().visit(n.f1,argu);

        ArrayList<MyType> argTypes = new ArrayList<>();


        if(n.f4.present()){
            ClassMemberVisitor.currList = argTypes;
            n.f4.node.accept(this,null);
            argTypes = ClassMemberVisitor.currList;
            ClassMemberVisitor.currList = null;
        }


        MyMethodType t = new MyMethodType(retType,argTypes);
        if(ClassVisitor.classes.get(argu).hasMember(methodName)
            && ClassVisitor.classes.get(argu).getMembers().get(methodName).isMethod()
        )SemanticsException.err("Multiple definition of method: " + methodName);
        //check for overriding of superclass
        String superName = ClassVisitor.extendsMap.get(argu);
        while(superName != null){
            if(ClassVisitor.classes.get(superName).hasMember(methodName)){
                if(ClassVisitor.classes.get(superName).getMembers().get(methodName)!=null){
                    if(!ClassVisitor.classes.get(superName).getMembers().get(methodName).matches(t)) SemanticsException.err("Method has different signature than superclass: " + methodName);
                }

            }
            superName = ClassVisitor.extendsMap.get(superName);
        }
        t.setInstanceName(methodName);
        t.originClass = ClassVisitor.classes.get(argu);
        ClassVisitor.classes.get(argu).addMember(methodName,t/*new MyMethodType(retType,argTypes)*/);


        return null;

    }

    @Override
    public MyType visit(FormalParameter n, String argu) throws Exception {
        MyType t = new TypeVisitor().visit(n.f0,null);
        t.setInstanceName(n.f1.f0.toString());
        ClassMemberVisitor.currList.add(t);
        return null;
    }

    @Override
    public MyType visit(VarDeclaration n, String argu) throws Exception {
        MyType t = new TypeVisitor().visit(n.f0,null);
        String name = n.f1.f0.toString();
        if(ClassVisitor.classes.get(argu).hasMember(name)) SemanticsException.err("Var declared twice: " + name);
        ClassVisitor.classes.get(argu).addMember(name,t);
        return null;
    }

    @Override
    public MyType visit(ClassExtendsDeclaration n, String argu) throws Exception {
        String className = n.f1.f0.toString();
        visit(n.f5,className);
        visit(n.f6,className);
        return null;
    }
}


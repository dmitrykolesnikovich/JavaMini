package Semantics.myVisitors;

import Semantics.myTypes.*;

import syntaxtree.*;
import visitor.GJDepthFirst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeCheckVisitor extends GJDepthFirst<MyType, Scope> {

    private static int offsetFromType(Type t){
        NodeChoice nc = t.f0;
        switch (nc.which){
            case 0: //array
                return 8;
            case 1: //bool
                return 1;
            case 2://int
                return 4;
            case 3:
                return 8;

        }
        return -10000;
    }

    private MyType getMatchStatus(MyType[] A, MyType[] B, MyType ret,String err) throws SemanticsException {
        if(A.length != B.length) SemanticsException.err("Something went wrong with type arrays, idk \n" + err);
        for(int i=0; i < A.length; i++){
           if(!A[i].matches(B[i])) SemanticsException.err("Something went wrong with type arrays, idk(2) \n" + err);
        }

        return ret;
    }
    //output
    public static Map<String, Integer> varOffsets = new HashMap<>();
    public static Map<String, Integer> methodOffsets = new HashMap<>();
    //

    @Override
    public MyType visit(Goal n, Scope parent) throws Exception {
        Scope S = new Scope();
        parent.addChild(S);
        S.setParent(parent);
        n.f0.accept(this,S);
        n.f1.accept(this,S);
        return null;
    }

    @Override
    public MyType visit(MainClass n, Scope argu) throws Exception {
        String className = n.f1.f0.toString();
        Scope s = new Scope();
        s.setClassName(className);
        ClassVisitor.classes.get(className).setScope(s);
        String argsName = n.f11.f0.toString();
        //s.addEntry(argsName, new MyIntArrayType());
        n.f14.accept(this,s);
        n.f15.accept(this,s);


        //output
        //System.out.println(className + ".main : 0");
        TypeCheckVisitor.methodOffsets.put(className,8);
        TypeCheckVisitor.varOffsets.put(className,0);
        //
        return null;
    }

    @Override
    public MyType visit(ClassDeclaration n, Scope argu) throws Exception {
        String className = n.f1.f0.toString();
        Scope s = new Scope();
        s.setClassName(className);
        ClassVisitor.classes.get(className).setScope(s);
        n.f3.accept(this, s);
        n.f4.accept(this,s);


        //output
        System.out.println("-----------Class " + className + "-----------");
        //vars
        System.out.println("--Variables---");
        int x1=0,y1=0,x2=0,y2=0;

        for(Node m:n.f3.nodes){
            int offset = TypeCheckVisitor.offsetFromType((Type)(((VarDeclaration)m).f0));
            x1=y1;y1+=offset;
            String varName = ((VarDeclaration)(m)).f1.f0.toString();
            System.out.println(String.format("%s.%s : %d",className,varName,x1));
            ClassVisitor.classes.get(className).addOffset(varName, x1);
        }
        TypeCheckVisitor.varOffsets.put(className,y1);
        //methods
        System.out.println("---Methods---");
        for(Node m: n.f4.nodes){
            String methodName = ((MethodDeclaration)m).f2.f0.toString();
            int offset = 8;
            x2 = y2; y2 += offset;
            System.out.println(String.format("%s.%s : %d",className,methodName,x2));
            ClassVisitor.classes.get(className).addMethodOffset(methodName,x2);
        }
        TypeCheckVisitor.varOffsets.put(className,y1);
        TypeCheckVisitor.methodOffsets.put(className,y2);
        System.out.println("\n");
        //

        return null;
    }

    @Override
    public MyType visit(ClassExtendsDeclaration n, Scope argu) throws Exception {
        String className = n.f1.f0.toString();
        Scope s = new Scope();
        s.setClassName(className);
        ClassVisitor.classes.get(className).setScope(s);
        n.f5.accept(this, s);
        n.f6.accept(this,s);


        //output
        System.out.println("-----------Class " + className + "-----------");
        String superName = ClassVisitor.extendsMap.get(className);
        //vars
        System.out.println("--Variables---");
        int x1=0,y1=0,x2=0,y2=0;
        y1 = TypeCheckVisitor.varOffsets.get(superName);
        y2 = TypeCheckVisitor.methodOffsets.get(superName);

        for(Node m:n.f5.nodes){
            int offset = TypeCheckVisitor.offsetFromType((Type)(((VarDeclaration)m).f0));
            x1=y1;y1+=offset;
            String varName = ((VarDeclaration)(m)).f1.f0.toString();
            System.out.println(String.format("%s.%s : %d",className,varName,x1));
            ClassVisitor.classes.get(className).addOffset(varName, x1);
        }
        TypeCheckVisitor.varOffsets.put(className,y1);
        //methods
        System.out.println("--Methods---");
        for(Node m: n.f6.nodes){
            String methodName = ((MethodDeclaration)m).f2.f0.toString();
            if(ClassVisitor.classes.get(superName).getMethodMember(methodName) != null) continue;

            int offset = 8;
            x2 = y2; y2 += offset;
            System.out.println(String.format("%s.%s : %d",className,methodName,x2));
            ClassVisitor.classes.get(className).addMethodOffset(methodName,x2);

        }
        TypeCheckVisitor.varOffsets.put(className,y1);
        TypeCheckVisitor.methodOffsets.put(className,y2);
        System.out.println("\n");
        //
        return  null;
    }

    @Override
    public MyType visit(MethodDeclaration n, Scope argu) throws Exception {
        String methodName = n.f2.f0.toString();
        MyMethodType M = ((MyMethodType)(ClassVisitor.classes.get(argu.getClassName()).getMembers().get(methodName)));
        Scope s = new Scope();
        s.setParent(argu);
        //parameters
        Map<String, MyType> m = new HashMap<>();
        if(n.f4.present()){
            FormalParameterList fpl = ((FormalParameterList)(n.f4.node));
            String parName = fpl.f0.f1.f0.toString();
            List<MyType> parList = M.getArgTypes();

            m.put(parName,parList.get(0));
            for(int i=1;i<parList.size();i++){
                parName = ((FormalParameterTerm)( fpl.f1.f0.nodes.get(i-1))).f1.f1.f0.toString();
                if(m.containsKey(parName))SemanticsException.err("Duplicate arg name :" + parName + "in method: " + methodName);
                m.put(parName, parList.get(i));

            }
        }
        for(String key: m.keySet()){
            s.addEntry(key,m.get(key));
        }
        n.f7.accept(this,s);
        n.f8.accept(this,s);
        MyType t = n.f10.accept(this,s);
        if (!t.matches(M.getReturnType())) SemanticsException.err("Method return type not matching: " + methodName);
        return null;
    }

    @Override
    public MyType visit(CompareExpression n, Scope parent) throws Exception {
        return getMatchStatus(new MyType[]{n.f0.accept(this,parent),n.f2.accept(this,parent)}, new MyType[]{new MyIntType(), new MyIntType()}, new MyBooleanType(),"(in comareExpression)");
    }

    @Override
    public MyType visit(AndExpression n, Scope parent) throws Exception {
        return getMatchStatus(new MyType[]{n.f0.accept(this,parent),n.f2.accept(this,parent)}, new MyType[]{new MyBooleanType(), new MyBooleanType()}, new MyBooleanType(),"(in AndExpression)");
    }

    @Override
    public MyType visit(PlusExpression n, Scope parent) throws Exception {
        return getMatchStatus(new MyType[]{n.f0.accept(this,parent),n.f2.accept(this,parent)}, new MyType[]{new MyIntType(), new MyIntType()}, new MyIntType(),"(in PlusExpression)");
    }

    @Override
    public MyType visit(MinusExpression n, Scope parent) throws Exception {
        return getMatchStatus(new MyType[]{n.f0.accept(this,parent),n.f2.accept(this,parent)}, new MyType[]{new MyIntType(), new MyIntType()}, new MyIntType(),"(in MinusExpression)");
    }

    @Override
    public MyType visit(TimesExpression n, Scope parent) throws Exception {
        return getMatchStatus(new MyType[]{n.f0.accept(this,parent),n.f2.accept(this,parent)}, new MyType[]{new MyIntType(), new MyIntType()}, new MyIntType(),"(in TimesExpression)");
    }

    @Override
    public MyType visit(NotExpression n, Scope parent) throws Exception {
        return n.f1.accept(this,parent);
    }

    @Override
    public MyType visit(Clause n, Scope argu) throws Exception {
        int c = n.f0.which;
        switch(c){
            case 0:
                return ((NotExpression)n.f0.choice).accept(this,argu);
            case 1:
                return ((PrimaryExpression)n.f0.choice).accept(this,argu);
        }
        SemanticsException.err("Something went wrong while checking a clause");
        return null;
    }

    @Override
    public MyType visit(ArrayLookup n, Scope parent) throws Exception {
        return getMatchStatus(new MyType[]{n.f0.accept(this,parent),n.f2.accept(this,parent)}, new MyType[]{new MyIntArrayType(), new MyIntType()}, new MyIntType(),"(in arrayLookup)");
    }

    @Override
    public MyType visit(ArrayLength n, Scope parent) throws Exception {
        return getMatchStatus(new MyType[]{n.f0.accept(this,parent)}, new MyType[]{new MyIntArrayType()}, new MyIntType(),"(in Array length)");
    }

    @Override
    public MyType visit(ArrayAllocationExpression n, Scope parent) throws Exception {
        return getMatchStatus(new MyType[]{n.f3.accept(this,parent)}, new MyType[]{new MyIntType()}, new MyIntArrayType(),"(in Array alloc)");
    }

    @Override
    public MyType visit(ArrayAssignmentStatement n, Scope argu) throws Exception {
        String arrayName = n.f0.f0.toString();
        if(!getTypeFromIdSilent(arrayName,argu).matches(new MyIntArrayType())) SemanticsException.err("array does not exist:" + arrayName);
        if(!visit(n.f2,argu).matches(new MyIntType())) SemanticsException.err("Array index is not int");
        if(!visit(n.f5,argu).matches(new MyIntType())) SemanticsException.err("Incompatible assignment to array member");
        return null;
    }

    @Override
    public MyType visit(IfStatement n, Scope parent) throws Exception {
        getMatchStatus(new MyType[]{n.f2.accept(this,parent)}, new MyType[]{new MyBooleanType()}, null,"(in IF condition)");
        n.f4.accept(this,parent);
        return null;
    }

    @Override
    public MyType visit(WhileStatement n, Scope parent) throws Exception {
        getMatchStatus(new MyType[]{n.f2.accept(this,parent)}, new MyType[]{new MyBooleanType()}, null,"(in While condition)");
        n.f4.accept(this,parent);
        return null;
    }

    @Override
    public MyType visit(PrimaryExpression n, Scope argu) throws Exception {

        int c = n.f0.which;
        switch (c){
            case 0: //integer literal
                return new MyIntType();
            case 1: //true literal
            case 2: //false literal
                return new MyBooleanType();
            case 3: //Identifier
                return getTypeFromId(((Identifier)(n.f0.choice)).f0.toString(), argu);
            case 4: //this
                Scope ts = argu;
                while(ts.getParent()!= null){
                    ts = ts.getParent();
                }
                return ClassVisitor.classes.get(ts.getClassName());

            case 5: //array alloc
                return new MyIntArrayType();
            case 6: //alloc
                String className = ((AllocationExpression)n.f0.choice).f1.f0.toString();
                if(!ClassVisitor.classes.containsKey(className))SemanticsException.err("Class not found: " + className + " in primary expression");
                return ClassVisitor.classes.get(className);
            case 7: //bracket expr
                return ((BracketExpression)(n.f0.choice)).f1.accept(this,argu);


        }
        return null;
    }

    private MyType getTypeFromIdSilent(String varName, Scope s){
        if(s.lookup(varName) != null){
            return s.lookup(varName);
        }
        while(s.getParent() != null){
            s = s.getParent();
            if(s.lookup(varName) != null){
                return s.lookup(varName);
            }
        }
        s = s.getSuperScope();
        while(s != null){
            if(s.lookup(varName) != null){
                return s.lookup(varName);
            }
            s = s.getSuperScope();
        }
        return null;
    }

    private MyType getTypeFromId(String varName, Scope s) throws SemanticsException {

        MyType t = getTypeFromIdSilent(varName,s);
        if(t == null)SemanticsException.err("Identifier not recognised: " + varName);
        return t;
    }

    @Override
    public MyType visit(VarDeclaration n, Scope parent) throws SemanticsException {

        int c = n.f0.f0.which;
        String name = n.f1.f0.toString();
        if(getTypeFromIdSilent(name,parent) != null && parent.getClassName() == null && parent.lookup(name) != null) SemanticsException.err("Redeclaration of var: " + name);
        switch(c){
            case 0: //ArrayType
                parent.addEntry(name, new MyIntArrayType());
                break;
            case 1: //BooleanType
                parent.addEntry(name, new MyBooleanType());
                break;
            case 2: //IntegerType
                parent.addEntry(name,new MyIntType());
                break;
            case 3: //Identifier
                String className = ((Identifier)(n.f0.f0.choice)).f0.toString();
                if(ClassVisitor.classes.get(className) == null) SemanticsException.err("Unknown class type: " + className);
                parent.addEntry(name, new MyClass(className));
        }

        return null;
    }

    @Override
    public MyType visit(AssignmentStatement n, Scope argu) throws Exception {
        String leftName = n.f0.f0.toString();
        MyType leftType = getTypeFromId(leftName,argu);
        MyType rightType = n.f2.accept(this, argu);
        if(!leftType.matches(rightType)) SemanticsException.err("assignment to var " + leftName + " has incorrect type");
        if(leftType.isClass()){

            argu.update(leftName,rightType);
        }
        return null;
    }

    @Override
    public MyType visit(Expression n, Scope argu) throws Exception {
        return n.f0.choice.accept(this,argu);
    }

    @Override
    public MyType visit(MessageSend n, Scope argu) throws Exception {

        MyType a = n.f0.accept(this,argu);
        if(!a.isClass())SemanticsException.err("Trying to do a message pass on a non class ");
        String className = ((MyClass)a).getClassName();
        String methodName = n.f2.f0.toString();

        if(!ClassVisitor.classes.containsKey(className)) SemanticsException.err("Class not found for message pass: " + className);

        //get method as MyType

        ArrayList<MyType> args = evalExprList(n.f4,argu);
        MyMethodType M = ClassVisitor.classes.get(className).getMethodMember(methodName);
        // <className> has a method member that matches M
        if(M == null && !ClassVisitor.superClassHasMember(className,methodName)){

            SemanticsException.err("Class " + className + " does not have a matching member called:" + methodName);
        }
        //MyMethodType M = (MyMethodType)ClassVisitor.classes.get(className).getMembers().get(methodName);

        return getMatchStatus(
                args.toArray(new MyType[args.size()]),
                M.getArgTypes().toArray(new MyType[M.getArgTypes().size()]),
                M.getReturnType()
                ,"(in message send: args list in method "+ methodName + ")"
        );
    }

    private ArrayList<MyType> evalExprList(NodeOptional f4,Scope argu) throws Exception {
        if(!f4.present())return new ArrayList<MyType>();
        ArrayList<MyType> lst = new ArrayList<>();
        ExpressionList EL = (ExpressionList) f4.node;
        Expression e = EL.f0;
        lst.add(e.accept(this,argu));
        if(EL.f1.f0.present()){
            for(Node ex: EL.f1.f0.nodes){
                lst.add(((ExpressionTerm)ex).accept(this,argu));
            }
        }
        return lst;
    }

    @Override
    public MyType visit(ExpressionTerm n, Scope argu) throws Exception {
        return n.f1.accept(this,argu);
    }

    @Override
    public MyType visit(PrintStatement n, Scope parent) throws Exception {
        MyType t = n.f2.accept(this,parent);
        if(t.matches(new MyIntType()) || t.matches(new MyBooleanType())){
            return null;
        }
        SemanticsException.err("Something went wrong in print statement");
        return null;

    }
}

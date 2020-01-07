package Semantics.myTypes;

import IR.IRException;
import IR.LLVM;
import Semantics.myVisitors.ClassVisitor;
import Semantics.myVisitors.Scope;

import java.util.*;
import java.util.stream.Collectors;

public class MyClass extends MyType{

    private Map<String, MyType> members = new HashMap<>();
    private String className;
    private Scope scope;
    private String vtablePointer;
    private Map<String, Integer> offsets = new HashMap<>();
    private Map<String, Integer> methodOffsets = new HashMap<>();
    private int vtableSize;

    @Override
    public boolean isClass() {
        return true;
    }

    @Override
    boolean matches(MyIntType e) {
        return false;
    }

    public MyClass(MyClass c){
        this.scope = c.scope;
        this.offsets = c.offsets;
        this.methodOffsets = c.methodOffsets;
        this.className = c.className;
        this.members = c.members;
    }

    public ArrayList<MyMethodType> getMethodMembers(){
        ArrayList<MyMethodType> res = new ArrayList<>();
        for(MyType t: members.values()){
            if(t.isMethod()){
                res.add((MyMethodType) t);
            }
        }
        return res;
    }

    @Override
    boolean matches(MyIntArrayType e) {
        return false;
    }

    @Override
    boolean matches(MyMethodType e) {
        return false;
    }

    public MyClass(String cn){
        super(cn);
        className = cn;
    }

    public Map<String, MyType> getMembers(){
        return members;
    }

    public String getClassName(){
        return className;
    }

    public void addMember(String name, MyType e){
        members.put(name,e);
    }

    public boolean hasMember(String n){
        return members.containsKey(n);
    }

    public boolean matches(MyClass c){
        if (c.getClassName() == className) return true;
        //check if c extends this
        String superName = c.getClassName();
        while(superName != null){
            if(ClassVisitor.classes.get(superName).getClassName() == className) return true;
            superName = ClassVisitor.extendsMap.get(superName);
        }

        //check if this extends c
        superName = className;
        while(superName != null){
            if(ClassVisitor.classes.get(superName).getClassName() == c.getClassName()) return true;
            superName = ClassVisitor.extendsMap.get(superName);
        }
        return false;
    }

    @Override
    boolean matches(MyBooleanType e) {
        return false;
    }

    @Override
    int getSize() {
        return 8;
    }

    @Override
    public boolean matches(MyType t) {
        return t.matches(this);
    }

    @Override
    public boolean isMethod() {
        return false;
    }

    @Override
    public String getLLVMType() {
        return LLVM.getLLVMType(this);
    }

    public Scope getScope(){
        return scope;
    }

    public void setScope(Scope s) {
        scope = s;
    }

    public MyMethodType getMethodMember(String methodName) {
        String superName = className;
        while(superName!=null){
            if(ClassVisitor.classes.get(superName).hasMember(methodName)
                    && ClassVisitor.classes.get(superName).getMembers().get(methodName).isMethod()

            ){
                return (MyMethodType) ClassVisitor.classes.get(superName).getMembers().get(methodName);
            }
                superName = ClassVisitor.extendsMap.get(superName);
        }
        return null;
    }

    public List<MyType> getFieldMembers(){
        return members.values().stream().filter(x -> !x.isMethod()).collect(Collectors.toList());
    }

    /*public String getVtablePointer() {
        return vtablePointer;
    }*/

    public void setVtablePointer(String vtablePointer) {
        this.vtablePointer = vtablePointer;
    }

    public int getTotalSize() {
        int size = 0;
        for(MyType t: getFieldMembers()){
            size += t.getSize();
        }
        //int size = getFieldMembers().size();
        if(ClassVisitor.extendsMap.get(this.className) != null){
            size += ClassVisitor.classes.get(ClassVisitor.extendsMap.get(this.className)).getTotalSize();
        }
        return size;
    }

    public int getVtableSize() {
        return getVtableEntries().size();
    }

    private int access(String key, Map<String, Integer> m, IRException e) {
        if(m.containsKey(key)){
            return m.get(key).intValue();
        }else{
            e.printStackTrace();
            return -99999999;
        }
    }

    public int getOffset(String varName) throws IRException {
        if(offsets.containsKey(varName)){
            return offsets.get(varName);
        }else{
            if(ClassVisitor.extendsMap.get(className) == null){
                new IRException("var offset not stored").printStackTrace();
                return -999999999;
            }else{
                return ClassVisitor.classes.get(ClassVisitor.extendsMap.get(className)).getOffset(varName);

            }
        }

    }

    public int getMethodOffset(String methodName){
        if(methodOffsets.containsKey(methodName)){
            return methodOffsets.get(methodName);
        }else{
            String superClassName = ClassVisitor.extendsMap.get(className);
            return ClassVisitor.classes.get(superClassName).getMethodOffset(methodName);
        }

    }

    public void addOffset(String varName, int x) {
        offsets.put(varName, x);
    }

    public void addMethodOffset(String methodName, int x2) {
        methodOffsets.put(methodName, x2);
    }

    public List<MyMethodType> getVtableEntries(){
        List<MyMethodType> parentVtableEntries = new ArrayList<>();
        String superClassName = ClassVisitor.extendsMap.get(className);
        if(superClassName != null){
            parentVtableEntries = ClassVisitor.classes.get(superClassName).getVtableEntries();
        }
        List<MyMethodType> res = new ArrayList<>();
        for(MyMethodType m: parentVtableEntries){
            if( members.get(m.getInstanceName() )!= null && members.get(m.getInstanceName()).isMethod()){
                res.add(getMethodMember(m.getInstanceName()));
            }else{
                res.add(m);
            }
        }
        for(MyMethodType m: getMethodMembers()){
            if(res.stream().filter(x->x.getInstanceName() == m.getInstanceName()).collect(Collectors.toList()).size() < 1){
                res.add(m);
            }
        }
        vtableSize = res.size();
        return res;
    }
}

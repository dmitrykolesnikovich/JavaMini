package Semantics.myVisitors;

import Semantics.myTypes.MyType;

import java.util.*;

public class Scope {

    private Map<String, MyType> entries = new HashMap<>();
    private ArrayList<Scope> children = new ArrayList<>();
    private Scope parent;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    private String className = null;

    public Scope(Scope s){
        parent = s;
    }

    public Scope(){

    }

    public void addChild(Scope s){
        this.children.add(s);
    }

    public ArrayList<Scope> getChildren(){
        return children;
    }

    public void addEntry(String name, MyType e){
        entries.put(name,e);
    }

    public MyType lookup(String name){

        if(entries.containsKey(name)){
            return entries.get(name);
        }else{
            return null;
        }
    }


    public void setParent(Scope parent) {
        this.parent = parent;
    }
    public Scope getParent(){return parent;}

    public Scope getSuperScope() {
        String motherName = ClassVisitor.extendsMap.get(this.className);
        if(motherName == null) return null;
        return ClassVisitor.classes.get(motherName).getScope();
    }


    public void update(String leftName, MyType rightType) {
        if(!entries.containsKey(leftName)){
            //new Exception("cant update scope entry").printStackTrace();
        }else{
            entries.put(leftName, rightType);
        }
    }
}

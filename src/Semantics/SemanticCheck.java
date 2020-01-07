package Semantics;

import Semantics.myVisitors.*;
import syntaxtree.*;

import java.util.HashMap;
public class SemanticCheck{
    private Goal root;

    public SemanticCheck(Goal r){
        root = r;
        ClassVisitor.extendsMap = new HashMap<>();
        ClassVisitor.classes = new HashMap<>();
        TypeCheckVisitor.varOffsets = new HashMap<>();
        TypeCheckVisitor.methodOffsets = new HashMap<>();
    }

    public void check() throws Exception {
            Scope rootScope = new Scope();
            root.accept(new ClassVisitor());
            root.accept(new ClassMemberVisitor(), null);
            root.accept(new TypeCheckVisitor(), rootScope);
    }


}
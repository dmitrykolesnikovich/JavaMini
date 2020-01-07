package Semantics.myVisitors;

import Semantics.myTypes.*;
import syntaxtree.ArrayType;
import syntaxtree.BooleanType;
import syntaxtree.Identifier;
import syntaxtree.IntegerType;
import visitor.GJDepthFirst;

public class TypeVisitor extends GJDepthFirst<MyType, Object> {

    @Override
    public MyType visit(ArrayType n, Object argu) {
        return new MyIntArrayType();
    }

    @Override
    public MyType visit(BooleanType n, Object argu) {
        return new MyBooleanType();
    }

    @Override
    public MyType visit(IntegerType n, Object argu) {
        return new MyIntType();
    }

    @Override
    public MyType visit(Identifier n, Object argu) throws SemanticsException {
        String name = n.f0.toString();
        if(ClassVisitor.classes.containsKey(name)){
            return new MyClass(name);
        }else{
            SemanticsException.err("Class not found: " + name);
            return null;
        }
    }
}

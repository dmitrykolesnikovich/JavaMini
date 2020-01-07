package Semantics.myTypes;

import IR.LLVM;

public class MyIntType extends MyType {

    public MyIntType(String instanceName) {
        super(instanceName);
    }

    public MyIntType() {

    }

    public boolean matches(MyIntType e){
        return true;
    }

    @Override
    boolean matches(MyIntArrayType e) {
        return false;
    }

    @Override
    boolean matches(MyMethodType e) {
        return false;
    }

    @Override
    boolean matches(MyClass e) {
        return false;
    }

    @Override
    boolean matches(MyBooleanType e) {
        return false;
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

    @Override
    public boolean isClass() {
        return false;
    }

    @Override
    int getSize() {
        return 4;
    }
}

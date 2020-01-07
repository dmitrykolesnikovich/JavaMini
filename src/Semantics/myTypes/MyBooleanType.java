package Semantics.myTypes;

import IR.LLVM;

public class MyBooleanType extends MyType {


    public MyBooleanType(String instanceName) {
        super(instanceName);
    }

    public MyBooleanType() {

    }

    @Override
    boolean matches(MyIntType e) {
        return false;
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

    public boolean matches(MyBooleanType e) {
        return true;
    }

    @Override
    int getSize() {
        return 1;
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


}

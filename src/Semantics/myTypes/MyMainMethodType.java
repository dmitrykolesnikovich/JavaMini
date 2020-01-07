package Semantics.myTypes;

import IR.LLVM;

public class MyMainMethodType extends MyType{

    public MyMainMethodType(String instanceName){
        super(instanceName);
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
        return true;
    }

    @Override
    public String getLLVMType() {
        return LLVM.getLLVMType(this);
    }


}

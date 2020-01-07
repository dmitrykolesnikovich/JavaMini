package Semantics.myTypes;

import IR.LLVM;

public class MyIntArrayType extends MyType {

    public MyIntArrayType() {

    }

    public String getAllocatedSizeRegister() {
        return allocatedSizeRegister;
    }

    public void setAllocatedSizeRegister(String allocatedSizeRegister) {
        this.allocatedSizeRegister = allocatedSizeRegister;
    }

    private String allocatedSizeRegister;

    public MyIntArrayType(String instanceName){
        super(instanceName);
    }

    @Override
    boolean matches(MyIntType e) {
        return false;
    }

    public boolean matches(MyIntArrayType e){
        return true;
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
        return false;
    }

    @Override
    public String getLLVMType() {
        return LLVM.getLLVMType(this);
    }


}

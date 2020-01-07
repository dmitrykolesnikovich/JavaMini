package Semantics.myTypes;

import IR.IRException;

public abstract class MyType {

    private String instanceName = null;

    public MyType(String instanceName){
        this.instanceName = instanceName;
    }

    protected MyType() {
    }

    public boolean isClass(){return false;};
    abstract boolean matches(MyIntType e);
    abstract boolean matches(MyIntArrayType e);
    abstract boolean matches(MyMethodType e);
    abstract boolean matches(MyClass e);
    abstract boolean matches(MyBooleanType e);
    abstract int getSize();
    public abstract boolean matches(MyType t);
    public abstract boolean isMethod();

    public void setLLVMRegister(String LLVMregister) {
        this.LLVMregister = LLVMregister;
    }

    protected String LLVMregister;

    public String getLLVMRegister(){
        return LLVMregister;
    }

    abstract public String getLLVMType();

    public String getInstanceName()  {
        if(instanceName != null){
            return instanceName;
        }else{
            new IRException("undefined instanced name").printStackTrace();
            return null;
        }

    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }


}


package Semantics.myTypes;

import IR.LLVM;

import java.util.LinkedHashMap;
import java.util.List;

public class MyMethodType extends MyType {

    public MyClass originClass;
    private List<MyType> argTypes;
    private MyType returnType;


    public MyMethodType(MyType retType, List<MyType> argTypes){
        super();
        this.returnType = retType;
        this.argTypes = argTypes;
    }

    public MyType getReturnType(){
        return returnType;
    }

    public List<MyType> getArgTypes(){
        return argTypes;
    }

    @Override
    boolean matches(MyIntType e) {
        return false;
    }

    @Override
    boolean matches(MyIntArrayType e) {
        return false;
    }

    public boolean matches(MyMethodType t){
        /*return this.returnType == t.getReturnType()
                && this.argTypes == t.getArgTypes();*/
        if(!this.returnType.matches(t.getReturnType())) return false;
        if(this.argTypes.size() != t.getArgTypes().size()) return false;
        for(int i=0;i<this.getArgTypes().size();i++){
            if(!this.argTypes.get(i).matches(t.getArgTypes().get(i))){
                return false;
            }
        }
        return true;
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
        return true;
    }

    @Override
    public String getLLVMType() {
        return LLVM.getLLVMType(this);
    }

    @Override
    int getSize() {
        return 8;
    }


}

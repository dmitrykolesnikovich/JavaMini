package IR;

import Semantics.myTypes.*;

import java.util.stream.Collectors;

public class LLVM {

    public static final String defaultCode =
        "declare i8* @calloc(i32, i32)\n" +
        "declare i32 @printf(i8*, ...)\n" +
        "declare void @exit(i32)\n" +
        "\n" +
        "@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n" +
        "@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\n" +
        "define void @print_int(i32 %i) {\n" +
        "    %_str = bitcast [4 x i8]* @_cint to i8*\n" +
        "    call i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n" +
        "    ret void\n" +
        "}\n" +
        "\n" +
        "define void @throw_oob() {\n" +
        "    %_str = bitcast [15 x i8]* @_cOOB to i8*\n" +
        "    call i32 (i8*, ...) @printf(i8* %_str)\n" +
        "    call void @exit(i32 1)\n" +
        "    ret void\n" +
        "}\n";

    public static String getZeroValue(String llvmtype){
        switch (llvmtype) {
            case "i32":
            case "i1":
                return "0";
            default:
                return "null";
        }
    }

    public static String getLLVMType(MyIntType t){
        return "i32";
    }

    public static String getLLVMType(MyClass c)  {
        return "i8*";
    }

    public static String getLLVMType(MyBooleanType b) {
        return "i1";
    }

    public static String getLLVMType(MyIntArrayType a) {
        return "i32*";
    }

    public static String getLLVMType(MyMethodType m) {
        String llvmReturnType = m.getReturnType().getLLVMType();
        String llvmArgsType = "i8*," + String.join(",",m.getArgTypes().stream().map(t -> t.getLLVMType()).collect(Collectors.toList()));
        if(llvmArgsType.endsWith(",")){
            llvmArgsType = llvmArgsType.substring(0,llvmArgsType.length()-1);
        }
        return String.format("%s (%s)", llvmReturnType, llvmArgsType);
    }

    public static String getLLVMType(MyMainMethodType myMainMethodType) {
        try{
            throw new IRException("Not implemented");
        }catch (IRException e){
            e.printStackTrace();
        }
        return "Not implemented";
    }

    public static MyType MyTypeFromLLVMType(String llvmType, String className){
        switch (llvmType){
            case "i32":
                return new MyIntType();
            case "i1":
                return new MyBooleanType();
            case "i8*":
                return new MyClass(className);
            case "i32*":
                return new MyIntArrayType();
            default:
                new Exception().printStackTrace();
                return null;
        }
    }
}

package IR;

import Semantics.myTypes.MyType;
import syntaxtree.Goal;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IRGenerator {

    static final String defaultCode =
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

    String inFileName;
    String outFileName;

    FileOutputStream outStream;
    private Goal goal;

    private int regCounter = 0;
    private int labelCounter = 0;
    private int constCounter = 0;

    private Map<String, MyType> regTypes = new HashMap<>();

    public MyType getRegType(String reg) throws IRException {
        if(regTypes.containsKey(reg)){
            return regTypes.get(reg);
        }else{
            throw new IRException("forgot to store register type: " + reg);
        }
    }

    public void addRegType(String reg, MyType type) throws IRException {
        if(type == null){
            throw new IRException("storing null reg type");
        }
        if(regTypes.containsKey(reg)){
            //throw new IRException("storing register type twice: " + reg);
        }else{
            regTypes.put(reg,type);
        }
    }

    public void remRegType(String reg) throws IRException {
        if(!regTypes.containsKey(reg)){
            throw new IRException("deleting non existing reg: " + reg);
        }else{
            regTypes.remove(reg);
        }
    }

    public String newLabel(String labelType){
        labelCounter++;
        return "lbl_" + labelType + "_" + new Integer(labelCounter).toString();
    }

    public String newRegister(){
        String res = "%_" + new Integer(regCounter).toString();
        regCounter++;
        return res;
    }

    public void initRegCount(){
        regCounter = 0;
    }

    public void emit(String s, boolean indent) throws IOException {
        s = "" + (indent ? "\t":"") + s + "\n";
        outStream.write(s.getBytes());
        System.out.println(s);
    }

    public IRGenerator(String inputFile, Goal g) throws IRException, FileNotFoundException {
        goal = g;
        inFileName = inputFile;
        if (!inputFile.endsWith(".java")) {
            throw new IRException("input file does not end with '.java'");
        }
        outFileName = inputFile.substring(0, inputFile.length() - 5) + ".ll";
        outStream = new FileOutputStream(outFileName);
    }

    public void generate() throws Exception {
        emit(defaultCode,false);
        IRVisitor irvisitor = new IRVisitor(this);
        irvisitor.visit(goal,null);
    }

    public void emitDefineMethod(String methodType, String methodName, String args) throws IOException {
        emit(String.format("define %s @%s(%s) {",methodType, methodName,args),false);
    }

    public void emitStore(String valType, String valReg, String pointerType, String pointerReg) throws IOException {
        emit(String.format("store %s %s, %s %s", valType, valReg, pointerType, pointerReg),true);
    }

    public String emitAlloca(String llvmType) throws IOException {
        String res = resultOfCommand(String.format("alloca %s", llvmType));
        return res;
    }

    public String emitGetElementPtr(String elementType, String pointerType, String pointerReg, String indexType, String indexRegOrVal) throws IOException {
        String res = resultOfCommand(String.format("getelementptr %s, %s %s, %s %s", elementType, pointerType, pointerReg, indexType, indexRegOrVal));
        return res;
    }

    public String emitGetElementPtr2(String elementType, String pointerType, String pointerReg, String indexType, String indexRegOrVal) throws IOException {
        String res = resultOfCommand(String.format("getelementptr %s, %s %s, %s %s, i32 0", elementType, pointerType, pointerReg, indexType, indexRegOrVal));
        return res;
    }

    public void emitClosingBracket() throws IOException {
        emit("}", false);
    }

    public void emitReturn(String llvmType, String valOrRegister) throws IOException {
        emit(String.format("ret %s %s", llvmType, valOrRegister), true);
    }

    public String emitArithmeticExpression(String leftExprRegister, String rightExprRegister, String instruction) throws IOException {
        String res = newRegister();
        emit(String.format("%s = %s i32 %s, %s",res,instruction, leftExprRegister, rightExprRegister),true);
        return res;
    }

    public String loadIntRegister(String register) throws IOException {
        String a = resultOfCommand(String.format("load i32, i32* %s",register));
        return a;
    }

    public String resultOfCommand(String instruction) throws IOException {
        String res = newRegister();
        emit(String.format("%s = %s", res,instruction),true);
        return res;
    }

    public String loadIntConstant(int i, String type) throws IOException {
        String res = newRegister();
        String lit = String.format("%d", i);
        emit(String.format("%s = add %s 0, %s",res,type, lit),true);
        return res;
    }


    public String loadRegister(String register,String llvmType) throws IOException {
        String newReg = resultOfCommand(String.format("load %s, %s %s",llvmType,llvmType+"*",register));
        return newReg;
    }

    public String zext(String reg1, String reg1Type, String targetType) throws IOException{
        String newReg = resultOfCommand(String.format("zext %s %s to %s",reg1Type,reg1,targetType));
        return newReg;
    }

    public void brCondition(String conditionRegister, String ifLabel, String elseLabel) throws IOException {
        emit(String.format("br i1 %s, label %%%s, label %%%s", conditionRegister, ifLabel, elseLabel),true);
    }

    public void br(String label) throws IOException {
        emit(String.format("br label %%%s", label),true);
    }

    public void emitLabel(String label) throws IOException {

        emit(label + ":",true);
    }

    public String emitCallMethod(String methodType, String methodName, String args) throws IOException {
        if(methodType.startsWith("void")){
            emit(String.format("call %s %s (%s)",methodType, methodName, args), true);
            return null;
        }else{
            String res = resultOfCommand(String.format("call %s %s (%s)",methodType, methodName, args));
            return res;
        }
    }

    public String emitICMP(String llvmType, String opType, String reg1, String reg2) throws IOException {
        String res = resultOfCommand(String.format("icmp %s %s %s, %s", opType, llvmType, reg1, reg2));
        return res;
    }

    public String emitCalloc(String sizeRegType, String sizeRegOrVal, String countRegType, String countRegOrVal) throws IOException {
        String callocArgs = String.format("%s %s, %s %s",  countRegType, countRegOrVal,sizeRegType, sizeRegOrVal);
        String res = emitCallMethod("i8*", "@calloc", callocArgs);
        return res;
    }

    public void emitConstant(String s) throws IRException {
        throw new IRException("Not implemented yet!");
    }

    public String emitBitcast(String inpType, String inpRegOrVal, String outType) throws IOException {
        String res = resultOfCommand(String.format("bitcast %s %s to %s", inpType, inpRegOrVal, outType));
        return res;
    }


    public String emitPhi(String type, String reg1, String lbl1, String reg2, String lbl2) throws IOException {
        String res = resultOfCommand(String.format("phi %s [%s, %%%s], [%s, %%%s]", type, reg1, lbl1, reg2, lbl2));
        return res;
    }


}

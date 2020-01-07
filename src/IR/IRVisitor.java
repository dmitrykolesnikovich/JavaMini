package IR;


import Semantics.myTypes.*;
import Semantics.myVisitors.ClassVisitor;
import Semantics.myVisitors.Scope;
import Semantics.myVisitors.TypeVisitor;
import syntaxtree.*;
import visitor.GJDepthFirst;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class IRVisitor extends GJDepthFirst<String, Scope> {

    private final IRGenerator g;

    public IRVisitor(IRGenerator irGenerator) {
        g = irGenerator;
    }

    public void generateVtable(MyClass C) throws IRException, IOException {
        
        //todo: fix offset from semantic analysis! (fml)
        List<MyMethodType> methodMembers = C.getVtableEntries();
        methodMembers.sort(Comparator.comparing(m->C.getMethodOffset(m.getInstanceName())));
        List<String> vtableEntries = new ArrayList<>();
        for(MyMethodType mmt: methodMembers){
            String llvmReturnType = mmt.getReturnType().getLLVMType();
            List<MyType> argTypes = mmt.getArgTypes();
            String llvmArgStr ="i8*," + String.join(",",argTypes.stream().map(t -> t.getLLVMType()).collect(Collectors.toList()));
            if(llvmArgStr.endsWith(",")){
                llvmArgStr = llvmArgStr.substring(0,llvmArgStr.length()-1);
            }
            String vtableEntry = String.format("i8* bitcast (%s (%s)* @%s.%s to i8*)", llvmReturnType, llvmArgStr, mmt.originClass.getClassName(),mmt.getInstanceName());
            vtableEntries.add(vtableEntry);
        }

        g.emit(String.format("@.%s_vtable = global [%d x i8*] [",C.getClassName(), methodMembers.size()),false);
        g.emit(String.join(",\n\t", vtableEntries), true);
        g.emit("]",false);
        C.setVtablePointer(String.format("@.%s_vtable",C.getClassName()));
    }

    @Override
    public String visit(MainClass n, Scope s) throws Exception {
        Scope mainMethodScope = new Scope();
        mainMethodScope.setClassName(n.f1.f0.toString());
        g.emitDefineMethod("i32", "main", "");
        for(Node node: n.f14.nodes){
            visitMethodVarDeclaration(mainMethodScope, (VarDeclaration) node);
        }

        //emit main method body
        for(Node node: n.f15.nodes){
            node.accept(this,mainMethodScope);
        }

        g.emitReturn("i32", "0");
        g.emitClosingBracket();
        return null;
    }

    @Override
    public String visit(ClassExtendsDeclaration n, Scope argu) throws Exception {
        generateVtable(ClassVisitor.classes.get(n.f1.f0.toString()));
        Scope s = new Scope();
        String className = n.f1.f0.toString();
        s.setClassName(className);

        for(Node node: n.f6.nodes){
            node.accept(this,s );
        }

        return null;
    }

    private void visitMethodVarDeclaration(Scope methodScope, VarDeclaration node) throws Exception {
        VarDeclaration v = node;
        String varName = v.f1.f0.toString();
        MyType varType = (new TypeVisitor()).visit(v.f0,null);
        methodScope.addEntry(varName, varType);
        String LLVM_type = varType.getLLVMType();
        /*ALLOCATE STACK*/
        String pointer = g.emitAlloca(LLVM_type);
        varType.setLLVMRegister(pointer);
        g.addRegType(pointer, LLVM.MyTypeFromLLVMType(LLVM_type, null));
        /*INITIALIZE TO ZERO*/
       // g.emitStore(LLVM_type, LLVM.getZeroValue(LLVM_type),LLVM_type + "*",pointer);
    }

    @Override
    public String visit(ClassDeclaration n, Scope argu) throws Exception {

        generateVtable(ClassVisitor.classes.get(n.f1.f0.toString()));
        Scope s = new Scope();
        String className = n.f1.f0.toString();
        s.setClassName(className);
        for(Node node: n.f4.nodes){
            node.accept(this,s );
        }

        return null;
    }

    @Override
    public String visit(MethodDeclaration n, Scope argu) throws Exception {
        //g.initRegCount();

        //Get method info from semantic analysis
        String methodName = n.f2.f0.toString();
        MyMethodType methodSignature = ClassVisitor.classes.get(argu.getClassName()).getMethodMember(methodName);

        //create new scope for method body
        Scope methodScope = new Scope();
        methodScope.setParent(argu);
        methodScope.setClassName(argu.getClassName());
        //assign registers to method arguments and add them to scope
        for(MyType t: methodSignature.getArgTypes()){
            t.setLLVMRegister(g.newRegister());
            methodScope.addEntry(t.getInstanceName(),t);
        }

        //Generate llvm types
        g.addRegType("%this", ClassVisitor.classes.get(argu.getClassName()));
        String llvmMethodName = argu.getClassName() + "." + methodName;
        String methodLLVMReturnType = (new TypeVisitor()).visit(n.f1, null).getLLVMType();
        String llvmArgs = "i8* %this,"+String.join(",", methodSignature.getArgTypes().stream().map(t -> t.getLLVMType() + " " + t.getLLVMRegister()).collect(Collectors.toList()));
        if(llvmArgs.endsWith(",")){
            llvmArgs = llvmArgs.substring(0,llvmArgs.length()-1);
        }
        //emit method definition start
        g.emitDefineMethod(methodLLVMReturnType,llvmMethodName,llvmArgs);

        //allocate local variables in stack, add them to scope and store register values
        List<MyType> methodArgTypes = methodSignature.getArgTypes();
        for(MyType t: methodArgTypes){
            String llvmType = t.getLLVMType();
            String stackPtr = g.emitAlloca(llvmType);
            g.emitStore(llvmType,t.getLLVMRegister(),llvmType+"*", stackPtr);
            t.setLLVMRegister(stackPtr);
            methodScope.addEntry(t.getInstanceName(), t);
        }

        //visit method body
        for(Node node: n.f7.nodes){ //var declarations
            visitMethodVarDeclaration(methodScope, (VarDeclaration)node);
        }

        for(Node node: n.f8.nodes){ //statements
            node.accept(this, methodScope);
        }

        //emit return
        String returnExprValRegister = n.f10.accept(this, methodScope);
        g.emitReturn(methodLLVMReturnType, returnExprValRegister);

        g.remRegType("%this");
        g.emitClosingBracket();
        return null;
    }

    private String getClassMemberLValue(Scope s, String id) throws Exception {
        MyClass C = ClassVisitor.classes.get(s.getParent().getClassName());
        //get member info
        MyType member = C.getMembers().get(id);
        MyClass C2 = C;
        while(member == null){
            C2 = ClassVisitor.classes.get(ClassVisitor.extendsMap.get(C2.getClassName()));
            member = C2.getMembers().get(id);
        }
        String offset = String.format("%d",C.getOffset(id)+8);
        String llvmType = member.getLLVMType();
        //get pointer to this' member address based on offset
        String offsetedPtr = g.emitGetElementPtr("i8","i8*","%this","i32", offset);
        //cast to element pointer type
        String elementPtr = g.emitBitcast("i8*", offsetedPtr, llvmType+"*");
        return elementPtr;
    }

    @Override
    public String visit(AssignmentStatement n, Scope s) throws Exception {
        String leftName = n.f0.f0.toString();
        MyType leftType = s.lookup(leftName);
        String rightExprRegister = n.f2.accept(this,s); //register that holds VALUE OF RIGHT EXPRESSION
        MyClass C = ClassVisitor.classes.get(s.getClassName());

        if(leftType == null){//class member

            MyType member = C.getMembers().get(leftName);
            MyClass C2 = C;
            while(member == null){
                C2 = ClassVisitor.classes.get(ClassVisitor.extendsMap.get(C2.getClassName()));
                member = C2.getMembers().get(leftName);
            }
            String llvmType2 = member.getLLVMType();
            String memberPtr  = getClassMemberLValue(s, leftName);
            g.emitStore(llvmType2,rightExprRegister,llvmType2+"*", memberPtr);
            return null;
        }

        String llvmType = leftType.getLLVMType();
        String leftExprRegister = leftType.getLLVMRegister(); //register that holds POINTER TO LEFT VAR
        g.emitStore(llvmType, rightExprRegister, llvmType+"*", leftExprRegister);


        ///
        if(g.getRegType(rightExprRegister).isClass()){

            MyType t = g.getRegType(rightExprRegister);
            MyClass newClass = new MyClass((MyClass)t);
            newClass.setLLVMRegister(s.lookup(leftName).getLLVMRegister());
            s.update(leftName,newClass);
        }
        return null;
    }

    @Override
    public String visit(ArrayAssignmentStatement n, Scope s) throws Exception {
        String arrayName = n.f0.f0.toString();
        MyType t = s.lookup(arrayName);


        //get value of expression in a register
        String exprValueRegister = n.f5.accept(this, s);

        //get pointer to stack or class member in a register
        String lValueRegister;
        if(t != null){
            lValueRegister = t.getLLVMRegister();
        }else{
            lValueRegister = getClassMemberLValue(s,arrayName);
        }


        //load heap address from stack to a register
        String heapAddrRegister = g.resultOfCommand(String.format("load i32*, i32** %s",lValueRegister));

        //get index value in a register
        String indexValueRegister = n.f2.accept(this,s);

        //check oob
        String lenReg = loadArrayLength(heapAddrRegister);
        String isNotOob = g.emitICMP("i32", "ult", indexValueRegister, lenReg);
        String oobLabel = g.newLabel("aas_oob");
        String contLabel = g.newLabel("aas_ob_cont");
        g.brCondition(isNotOob, contLabel, oobLabel);
        g.emitLabel(oobLabel);
        g.emitCallMethod("void", "@throw_oob", "");
        g.br(contLabel);
        g.emitLabel(contLabel);

        //increase index by 1 to skip length at pos0
        String newIndexValueRegister = g.emitArithmeticExpression(indexValueRegister, "1", "add");

        //get pointer to arr[index] in register
        String elementAddrRegister = g.emitGetElementPtr("i32", "i32*", heapAddrRegister, "i32", newIndexValueRegister);

        //store expression value to element
        g.emitStore("i32", exprValueRegister, "i32*", elementAddrRegister);
        return null;
    }

    @Override
    public String visit(PlusExpression n, Scope s) throws Exception {
        String leftExprRegister = n.f0.accept(this,s);
        String rightExprRegister = n.f2.accept(this,s);
        String res = g.emitArithmeticExpression(leftExprRegister,rightExprRegister,"add");
        g.addRegType(res, new MyIntType());
        return res;
    }

    @Override
    public String visit(TimesExpression n, Scope s) throws Exception {
        String leftExprRegister = n.f0.accept(this,s);
        String rightExprRegister = n.f2.accept(this,s);
        String res = g.emitArithmeticExpression(leftExprRegister,rightExprRegister,"mul");
        g.addRegType(res, new MyIntType());
        return res;
    }

    @Override
    public String visit(MinusExpression n, Scope s) throws Exception {
        String leftExprRegister = n.f0.accept(this,s);
        String rightExprRegister = n.f2.accept(this,s);
        String res = g.emitArithmeticExpression(leftExprRegister,rightExprRegister,"sub");
        g.addRegType(res, new MyIntType());
        return res;
    }

    @Override
    public String visit(CompareExpression n, Scope s) throws Exception {
        String leftExprRegister = n.f0.accept(this,s);
        String rightExprRegister = n.f2.accept(this,s);
        String res = g.emitICMP("i32", "slt", leftExprRegister, rightExprRegister);
        g.addRegType(res, new MyBooleanType());
        return res;
    }

    @Override
    public String visit(ArrayLength n, Scope s) throws Exception {
        //get pointer to heap in a register
        String addrReg = n.f0.accept(this,s);
        String res = loadArrayLength(addrReg);

        return res;
    }

    private String loadArrayLength(String addrReg) throws IOException, IRException {
        //get pointer to element 0
        String elementPointer = g.emitGetElementPtr("i32", "i32*", addrReg, "i32", "0");

        //load element 0 to register
        String res = g.loadRegister(elementPointer,"i32");
        g.addRegType(res, new MyIntType());
        return res;
    }

    @Override
    public String visit(ArrayAllocationExpression n, Scope s) throws Exception {
        //get allocation size in a register
        String sizeRegister = n.f3.accept(this, s);

        //check negative size
            String okReg = g.emitICMP("i32", "slt", "-1", sizeRegister);
            String okLabel = g.newLabel("alloc_oob");
            String oobLabel = g.newLabel("alloc_ok");
            g.brCondition(okReg, okLabel, oobLabel);
            g.emitLabel(oobLabel);
            g.emitCallMethod("void", "@throw_oob", "");
            g.br(okLabel);
            g.emitLabel(okLabel);

        //get register with SizeRegister+1
        String newSizeRegister = g.emitArithmeticExpression("1", sizeRegister, "add");

        //allocate at heap
        String heapAddrReg = g.emitCalloc("i32", "32", "i32", newSizeRegister);

        //get register with heap address cast to int*
        String heapAddrReg2 = g.resultOfCommand(String.format("bitcast i8* %s to i32*",heapAddrReg ));

        //store SizeRegister to first position of array in heap
        String firstElementPointer = g.emitGetElementPtr("i32", "i32*", heapAddrReg2, "i32", "0");

        g.emitStore("i32", sizeRegister, "i32*", firstElementPointer);

        g.addRegType(heapAddrReg2, new MyIntArrayType());
        return heapAddrReg2;
    }

    @Override
    public String visit(ArrayLookup n, Scope s) throws Exception {
        //todo: oob
        //get heap address of array in a register
        String heapAddrRegister = n.f0.accept(this,s);

        //get index value in a register
        String indexValueRegister = n.f2.accept(this,s);

        //check oob
            //get length
            String lenPtr = g.emitGetElementPtr("i32", "i32*", heapAddrRegister, "i32", "0");
            String lenReg = g.loadRegister(lenPtr, "i32");
            String notOobReg = g.emitICMP("i32","ult", indexValueRegister, lenReg);
            String oob_label = g.newLabel("oob");
            String ok_label = g.newLabel("oob_ok");
            g.brCondition(notOobReg,ok_label, oob_label);
            g.emitLabel(oob_label);
            g.emitCallMethod("void","@throw_oob", "");
            g.br(ok_label);
            g.emitLabel(ok_label);

        //increase index by 1 to skip length at pos0
        String newIndexValueRegister = g.emitArithmeticExpression(indexValueRegister, "1", "add");
        g.addRegType(newIndexValueRegister, new MyIntType());


        //get pointer to arr[index] in register
        String pointer = g.emitGetElementPtr("i32", "i32*", heapAddrRegister,"i32", newIndexValueRegister);
        g.addRegType(pointer, new MyIntArrayType());

        //load element value to result register
        String res = g.loadIntRegister(pointer);
        g.addRegType(res, new MyIntArrayType());

        return res;
    }

    @Override
    public String visit(TrueLiteral n, Scope argu) throws Exception {
        String res = g.loadIntConstant(1,"i1");
        MyType t = new MyBooleanType();
        //t.setLLVMRegister(res);
        g.addRegType(res, t);
        return res;
    }

    @Override
    public String visit(FalseLiteral n, Scope argu) throws Exception {
        String res = g.loadIntConstant(0,"i1");
        MyType t = new MyBooleanType();
        //t.setLLVMRegister(res);
        g.addRegType(res, t);
        return res;
    }

    @Override
    public String visit(NotExpression n, Scope s) throws Exception {
        //todo: fix!
        String exprReg = n.f1.accept(this,s);
        String res = g.resultOfCommand(String.format("xor i1 %s, 1",exprReg));
        g.addRegType(res, new MyBooleanType());
        return res;
    }

    @Override
    public String visit(IntegerLiteral n, Scope argu) throws Exception {
        String literal = n.f0.toString();
        String res = g.loadIntConstant(Integer.parseInt(literal),"i32");
        g.addRegType(res, new MyIntType());
        return res;
    }

    @Override
    public String visit(PrintStatement n, Scope s) throws Exception {
        String exprReg = n.f2.accept(this, s);
        String zexted = exprReg;

        //cast to i32 if needed
        if(g.getRegType(exprReg).getLLVMType() == "i1"){
            zexted = g.zext(exprReg,g.getRegType(exprReg).getLLVMType(),"i32");
        }

        g.emitCallMethod("void (i32)", "@print_int", String.format("i32 %s",zexted));
        return null;
    }

    @Override
    public String visit(Expression n, Scope argu) throws Exception {
        String res = n.f0.choice.accept(this,argu);
        return res;
    }

    @Override
    public String visit(Clause n, Scope argu) throws Exception {
        String res = n.f0.choice.accept(this,argu);
        return res;
    }

    @Override
    public String visit(PrimaryExpression n, Scope s) throws Exception {
        switch (n.f0.which){
            case 4: //thisExpression
                //todo: check if it works
                return "%this";
            case 3: //identifier
                Identifier identifier = (Identifier)n.f0.choice;
                String evalRegister = evalIdentifier(identifier,s);
                return evalRegister;
            case 7: // (expression)
                BracketExpression be = (BracketExpression)n.f0.choice;
                return be.f1.accept(this,s);
            default: //integer, true , false literal visitors already implemented
                String res = n.f0.choice.accept(this,s);
                return res;
        }
    }

    @Override
    public String visit(AllocationExpression n, Scope argu) throws Exception {
        String className = n.f1.f0.toString();
        MyClass C = ClassVisitor.classes.get(className);

        //get allocation size: size of obj + 8 bits for vtable pointer
        String size = String.format("%d", ClassVisitor.classes.get(className).getTotalSize() + 8 /*don't forget Vtable*/);
        //make heap allocation
        String heapPtr = g.emitCalloc("i32", size, "i32", "1");
        //cast heap pointer to i*** in order to store vtable pointer
        String heapPtr2 = g.emitBitcast("i8*", heapPtr, "i8***");
        //get pointer to vtable
        String vtableType = String.format("[%d x i8*]", C.getVtableSize());
        String vtablePointer = g.emitGetElementPtr2(vtableType, vtableType+"*",String.format("@.%s_vtable", className), "i32", "0");
        //cast vtablePointer to i8**
        //String vtablePointerCasted = g.emitBitcast(vtableType+"*",vtablePointer, "i8**");
        //store vtable pointer to first position
        g.emitStore("i8**", vtablePointer, "i8***", heapPtr2);


        g.addRegType(heapPtr, C);
        return heapPtr;
    }

    @Override
    public String visit(IfStatement n, Scope argu) throws Exception {
        //get value of condition in a register
        String conditionValueRegister = n.f2.accept(this, argu);

        //get labels for if, else, end
        String ifLabel = g.newLabel("if"); String elseLabel = g.newLabel("else"); String endLabel = g.newLabel("if_end");

        //jump according to expr
        g.brCondition(conditionValueRegister, ifLabel, elseLabel);

        //emit if label ,visit if block and jump to end
        g.br(ifLabel);
        g.emitLabel(ifLabel);
        n.f4.accept(this,argu);
        g.br(endLabel);

        //emit else label and visit if block and jump to end
        g.br(elseLabel);
        g.emitLabel(elseLabel);
        n.f6.accept(this,argu);
        g.br(endLabel);

        //end label
        g.br(endLabel);
        g.emitLabel(endLabel);
        return null;
    }

    @Override
    public String visit(WhileStatement n, Scope argu) throws Exception {
        //get labels for: loop start, loop exit, loop continue
        String startLabel = g.newLabel("loop"); String exitLabel = g.newLabel("exit"); String continueLabel = g.newLabel("cont");

        //emit loop start label
        g.br(startLabel);
        g.emitLabel(startLabel);

        //get value of condition in a register
        String conditionValueRegister = n.f2.accept(this,argu);

        //continue or exit loop according to condition
        g.brCondition(conditionValueRegister,continueLabel,exitLabel);

        //emit continue label, visit loop body, jump to loop start
        g.emitLabel(continueLabel);
        n.f4.accept(this,argu);
        g.br(startLabel);

        //emit exit label
        g.br(exitLabel);
        g.emitLabel(exitLabel);

        return null;
    }

    @Override
    public String visit(MessageSend n, Scope argu) throws Exception {
        /*left primary expression evaluates to object pointer.*/

        //get object pointer in a register
        String objPointer = n.f0.accept(this, argu);

        //cast objPointer to v-table pointer pointer type
        String vtablePP = g.emitBitcast("i8*",objPointer,"i8***");

        //load vtable address to register
        String vtablePointer = g.loadRegister(vtablePP,"i8**");
        MyClass C;
        String className;
        //find vtable according to left primary expression
        if(n.f0.f0.which == 3){ //some local object variable or class member
            String objName = ((Identifier)(n.f0.f0.choice)).f0.toString();
            C = (MyClass) (argu.lookup(objName));
            /*if(C == null){
                C = (MyClass)( ClassVisitor.classes.get(argu.getClassName()).getMembers().get(objName));
            }*/
            if(C == null){
                MyClass contextClass = ClassVisitor.classes.get(argu.getClassName());

                while(contextClass.getMembers().get(objName) == null){
                    contextClass = ClassVisitor.classes.get(ClassVisitor.extendsMap.get(contextClass.getClassName()));
                }
                C = (MyClass) contextClass.getMembers().get(objName);
            }


        }else if(n.f0.f0.which == 4){ //this
            C = ClassVisitor.classes.get(argu.getClassName());
        }else if(n.f0.f0.which == 6){ //allocation
            C = (MyClass) (g.getRegType(objPointer));
        }else if(n.f0.f0.which == 7){
            C = (MyClass) (g.getRegType(objPointer));
        }else{
            throw new IRException("Unknown message send!");
        }
        //System.out.println(C);

        className = C.getClassName();
        //get vtable element according to offset of method
        String methodName = n.f2.f0.toString();
        String methodLLVMType = C.getMethodMember(methodName).getLLVMType();
        String offset = String.format("%d",ClassVisitor.classes.get(className).getMethodOffset(methodName)/8);
        String vtableElementPointer = g.emitGetElementPtr("i8*", "i8**", vtablePointer, "i32", offset);

        //retrieve element (method pointer as i8*) to a register
        String i8StarMethodPtr = g.loadRegister(vtableElementPointer, "i8*");

        //cast to method llvm type
        String methodPtr = g.emitBitcast("i8*", i8StarMethodPtr, methodLLVMType+"*");

        //form arguments
        String args = "i8* " + objPointer;
        if(n.f4.present()){
            args += "," + n.f4.node.accept(this,argu);
        }
        if(args.endsWith(", ")){
            args = args.substring(0,args.length()-2);
        }
        //call method
        String res = g.emitCallMethod(methodLLVMType, methodPtr, args);
        g.addRegType(res, C.getMethodMember(methodName).getReturnType());
        return res;
    }

    @Override
    public String visit(ExpressionList n, Scope argu) throws Exception {
        List<String> argStrings = new ArrayList<>();
        String firstExprReg = n.f0.accept(this,argu);
        String firstExprType = g.getRegType(firstExprReg).getLLVMType();
        argStrings.add(String.format("%s %s", firstExprType,firstExprReg));
        //String rest = "";
        if(n.f1.f0.present()){
            for(Node m: n.f1.f0.nodes){
                Expression e = (((ExpressionTerm)m).f1);
                String exprReg = e.accept(this,argu);
                String exprLlvmType = g.getRegType(exprReg).getLLVMType();
                argStrings.add(String.format("%s %s", exprLlvmType, exprReg));
                //rest += String.format(",%s %s", exprLlvmType, exprReg);
            }

        }
        String res = String.join(",", argStrings);
        //String.format("%s %s, %s", firstExprType, firstExprReg, rest);
        if(res.endsWith(" ,")){
            res = res.substring(0,res.length()-1);
        }

        return res;
    }

    @Override
    public String visit(AndExpression n, Scope argu) throws Exception {
        String c1Val = n.f0.accept(this, argu);
        String notC1Val = g.resultOfCommand(String.format("xor i1 %s, 1", c1Val));
        String andShort = g.newLabel("and_short");
        String andLong = g.newLabel("and_long");
        String andExit = g.newLabel("and_exit");
        g.brCondition(notC1Val, andShort,andLong);
        g.emitLabel(andShort);
        String regShort = g.resultOfCommand(String.format("add i1 0, 0"));
        g.br(andExit);
        g.emitLabel(andLong);
        String c2Val = n.f2.accept(this,argu);
        String regLong = g.resultOfCommand(String.format("add i1 %s, 0", c2Val));
        g.br(andExit);
        g.emitLabel(andExit);
        String res = g.emitPhi("i1", regShort, andShort, regLong, andLong);
        g.addRegType(res, new MyBooleanType());
        return res;

    }

    private String evalIdentifier(Identifier identifier, Scope s) throws Exception {
        //todo: check if it works
        String varName = identifier.f0.toString();
        if(s.lookup(varName) != null){ //local variable
            MyType varType = s.lookup(varName);
            String llvmType = varType.getLLVMType();
            String varRegister = varType.getLLVMRegister();
            String evalRegister = g.loadRegister(varRegister,llvmType);
            String classNameOrNull = varType.isClass() ? ((MyClass)varType).getClassName() : null;

            g.addRegType(evalRegister,varType/*LLVM.MyTypeFromLLVMType(llvmType,classNameOrNull)*/);
            return evalRegister;
        }else{//class member
            MyClass C = ClassVisitor.classes.get(s.getClassName());
            while(C.getMembers().get(varName) == null){
                C = ClassVisitor.classes.get(ClassVisitor.extendsMap.get(C.getClassName()));
                if(C == null) throw new IRException("identifier not found: " + varName);
            }
            MyType member = C.getMembers().get(varName);
            String llvmType = member.getLLVMType();
            String elementPtr = getClassMemberLValue(s, varName);
            //load from heap to register
            String res = g.loadRegister(elementPtr,llvmType);
            g.addRegType(res,C.getMembers().get(varName));
            return res;
        }
    }
}

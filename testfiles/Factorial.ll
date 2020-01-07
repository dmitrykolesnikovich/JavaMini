declare i8* @calloc(i32, i32)
declare i32 @printf(i8*, ...)
declare void @exit(i32)

@_cint = constant [4 x i8] c"%d\0a\00"
@_cOOB = constant [15 x i8] c"Out of bounds\0a\00"
define void @print_int(i32 %i) {
    %_str = bitcast [4 x i8]* @_cint to i8*
    call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
    ret void
}

define void @throw_oob() {
    %_str = bitcast [15 x i8]* @_cOOB to i8*
    call i32 (i8*, ...) @printf(i8* %_str)
    call void @exit(i32 1)
    ret void
}

define i32 @main() {
	%var1 = call i8* @calloc (i32 8, i32 1)
	%var2 = bitcast i8* %var1 to i8***
	%var3 = getelementptr [1 x i8*], [1 x i8*]* @.Fac_vtable, i32 0
	%var4 = bitcast [1 x i8*]* %var3 to i8**
	store i8** %var4, i8*** %var2
	%var5 = bitcast i8* %var1 to i8***
	%var6 = load i8**, i8*** %var5
	%var7 = getelementptr i8*, i8** %var6, i32 0
	%var8 = load i8*, i8** %var7
	%var9 = bitcast i8* %var8 to i32 (i8*,i32)*
	%var10 = call i32 (i8*,i32) %var9 (i8* %var1,i32 10)
	call void (i32) @print_int (i32 %var10)
	ret i32 0
}
@.Fac_vtable = global [1 x i8*] [
	i8* bitcast (i32 (i8*,i32)* @Fac.ComputeFac to i8*)
]
define i32 @Fac.ComputeFac(i8* %this,i32 %var11) {
	%var12 = alloca i32
	store i32 %var11, i32* %var12
	%var13 = alloca i32
	%var14 = load i32, i32* %var12
	%var15 = icmp slt i32 %var14, 1
	br i1 %var15, label %lbl_if_1, label %lbl_else_2
	br label %lbl_if_1
	lbl_if_1:
	store i32 1, i32* %var13
	br label %lbl_if_end_3
	br label %lbl_else_2
	lbl_else_2:
	%var16 = load i32, i32* %var12
	%var17 = bitcast i8* %this to i8***
	%var18 = load i8**, i8*** %var17
	%var19 = getelementptr i8*, i8** %var18, i32 0
	%var20 = load i8*, i8** %var19
	%var21 = bitcast i8* %var20 to i32 (i8*,i32)*
	%var22 = load i32, i32* %var12
	%var23 = sub i32 %var22, 1
	%var24 = call i32 (i8*,i32) %var21 (i8* %this,i32 %var23)
	%var25 = mul i32 %var16, %var24
	store i32 %var25, i32* %var13
	br label %lbl_if_end_3
	br label %lbl_if_end_3
	lbl_if_end_3:
	%var26 = load i32, i32* %var13
	ret i32 %var26
}

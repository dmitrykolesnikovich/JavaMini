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
	%var1 = call i8* @calloc (i32 32, i32 1)
	%var2 = bitcast i8* %var1 to i8***
	%var3 = getelementptr [2 x i8*], [2 x i8*]* @.Tost_vtable, i32 0
	%var4 = bitcast [2 x i8*]* %var3 to i8**
	store i8** %var4, i8*** %var2
	%var5 = bitcast i8* %var1 to i8***
	%var6 = load i8**, i8*** %var5
	%var7 = getelementptr i8*, i8** %var6, i32 1
	%var8 = load i8*, i8** %var7
	%var9 = bitcast i8* %var8 to i32 (i8*)*
	%var10 = call i32 (i8*) %var9 (i8* %var1)
	call void (i32) @print_int (i32 %var10)
	ret i32 0
}
@.Test_vtable = global [1 x i8*] [
	i8* bitcast (i32 (i8*)* @Test.bar to i8*)
]
define i32 @Test.bar(i8* %this) {
	%var11 = add i32 0, 1
	ret i32 %var11
}
@.Tost_vtable = global [2 x i8*] [
	i8* bitcast (i32 (i8*)* @Tost.bar to i8*),
	i8* bitcast (i32 (i8*)* @Tost.foo to i8*)
]
define i32 @Tost.bar(i8* %this) {
	%var12 = add i32 0, 2
	ret i32 %var12
}
define i32 @Tost.foo(i8* %this) {
	%var13 = call i8* @calloc (i32 32, i32 1)
	%var14 = bitcast i8* %var13 to i8***
	%var15 = getelementptr [2 x i8*], [2 x i8*]* @.Tost_vtable, i32 0
	%var16 = bitcast [2 x i8*]* %var15 to i8**
	store i8** %var16, i8*** %var14
	%var17 = getelementptr i8, i8* %this, i32 16
	%var18 = bitcast i8* %var17 to i8**
	store i8* %var13, i8** %var18
	%var19 = getelementptr i8, i8* %this, i32 16
	%var20 = bitcast i8* %var19 to i8**
	%var21 = load i8*, i8** %var20
	%var22 = bitcast i8* %var21 to i8***
	%var23 = load i8**, i8*** %var22
	%var24 = getelementptr i8*, i8** %var23, i32 0
	%var25 = load i8*, i8** %var24
	%var26 = bitcast i8* %var25 to i32 (i8*)*
	%var27 = call i32 (i8*) %var26 (i8* %var21)
	call void (i32) @print_int (i32 %var27)
	%var28 = add i32 0, 0
	ret i32 %var28
}


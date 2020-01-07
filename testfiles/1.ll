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
	%var3 = getelementptr [1 x i8*], [1 x i8*]* @.LL_vtable, i32 0
	%var4 = bitcast [1 x i8*]* %var3 to i8**
	store i8** %var4, i8*** %var2
	%var5 = bitcast i8* %var1 to i8***
	%var6 = load i8**, i8*** %var5
	%var7 = getelementptr i8*, i8** %var6, i32 0
	%var8 = load i8*, i8** %var7
	%var9 = bitcast i8* %var8 to i32 (i8*)*
	%var10 = call i32 (i8*) %var9 (i8* %var1)
	call void (i32) @print_int (i32 %var10)
	ret i32 0
}
@.Element_vtable = global [6 x i8*] [
	i8* bitcast (i1 (i8*,i32,i32,i1)* @Element.Init to i8*),
	i8* bitcast (i32 (i8*)* @Element.GetAge to i8*),
	i8* bitcast (i32 (i8*)* @Element.GetSalary to i8*),
	i8* bitcast (i1 (i8*)* @Element.GetMarried to i8*),
	i8* bitcast (i1 (i8*,i8*)* @Element.Equal to i8*),
	i8* bitcast (i1 (i8*,i32,i32)* @Element.Compare to i8*)
]
define i1 @Element.Init(i8* %this,i32 %var11,i32 %var12,i1 %var13) {
	%var14 = alloca i32
	store i32 %var11, i32* %var14
	%var15 = alloca i32
	store i32 %var12, i32* %var15
	%var16 = alloca i1
	store i1 %var13, i1* %var16
	%var17 = load i32, i32* %var14
	%var18 = getelementptr i8, i8* %this, i32 8
	%var19 = bitcast i8* %var18 to i32*
	store i32 %var17, i32* %var19
	%var20 = load i32, i32* %var15
	%var21 = getelementptr i8, i8* %this, i32 12
	%var22 = bitcast i8* %var21 to i32*
	store i32 %var20, i32* %var22
	%var23 = load i1, i1* %var16
	%var24 = getelementptr i8, i8* %this, i32 16
	%var25 = bitcast i8* %var24 to i1*
	store i1 %var23, i1* %var25
	ret i1 1
}
define i32 @Element.GetAge(i8* %this) {
	%var26 = getelementptr i8, i8* %this, i32 8
	%var27 = bitcast i8* %var26 to i32*
	%var28 = load i32, i32* %var27
	ret i32 %var28
}
define i32 @Element.GetSalary(i8* %this) {
	%var29 = getelementptr i8, i8* %this, i32 12
	%var30 = bitcast i8* %var29 to i32*
	%var31 = load i32, i32* %var30
	ret i32 %var31
}
define i1 @Element.GetMarried(i8* %this) {
	%var32 = getelementptr i8, i8* %this, i32 16
	%var33 = bitcast i8* %var32 to i1*
	%var34 = load i1, i1* %var33
	ret i1 %var34
}
define i1 @Element.Equal(i8* %this,i8* %var35) {
	%var36 = alloca i8*
	store i8* %var35, i8** %var36
	%var37 = alloca i1
	%var38 = alloca i32
	%var39 = alloca i32
	%var40 = alloca i32
	store i1 1, i1* %var37
	%var41 = load i8*, i8** %var36
	%var42 = bitcast i8* %var41 to i8***
	%var43 = load i8**, i8*** %var42
	%var44 = getelementptr i8*, i8** %var43, i32 1
	%var45 = load i8*, i8** %var44
	%var46 = bitcast i8* %var45 to i32 (i8*)*
	%var47 = call i32 (i8*) %var46 (i8* %var41)
	store i32 %var47, i32* %var38
	%var48 = bitcast i8* %this to i8***
	%var49 = load i8**, i8*** %var48
	%var50 = getelementptr i8*, i8** %var49, i32 5
	%var51 = load i8*, i8** %var50
	%var52 = bitcast i8* %var51 to i1 (i8*,i32,i32)*
	%var53 = load i32, i32* %var38
	%var54 = getelementptr i8, i8* %this, i32 8
	%var55 = bitcast i8* %var54 to i32*
	%var56 = load i32, i32* %var55
	%var57 = call i1 (i8*,i32,i32) %var52 (i8* %this,i32 %var53,i32 %var56)
	%var58 = xor i1 %var57, 1
	br i1 %var58, label %lbl_if_1, label %lbl_else_2
	br label %lbl_if_1
	lbl_if_1:
	store i1 0, i1* %var37
	br label %lbl_if_end_3
	br label %lbl_else_2
	lbl_else_2:
	%var59 = load i8*, i8** %var36
	%var60 = bitcast i8* %var59 to i8***
	%var61 = load i8**, i8*** %var60
	%var62 = getelementptr i8*, i8** %var61, i32 2
	%var63 = load i8*, i8** %var62
	%var64 = bitcast i8* %var63 to i32 (i8*)*
	%var65 = call i32 (i8*) %var64 (i8* %var59)
	store i32 %var65, i32* %var39
	%var66 = bitcast i8* %this to i8***
	%var67 = load i8**, i8*** %var66
	%var68 = getelementptr i8*, i8** %var67, i32 5
	%var69 = load i8*, i8** %var68
	%var70 = bitcast i8* %var69 to i1 (i8*,i32,i32)*
	%var71 = load i32, i32* %var39
	%var72 = getelementptr i8, i8* %this, i32 12
	%var73 = bitcast i8* %var72 to i32*
	%var74 = load i32, i32* %var73
	%var75 = call i1 (i8*,i32,i32) %var70 (i8* %this,i32 %var71,i32 %var74)
	%var76 = xor i1 %var75, 1
	br i1 %var76, label %lbl_if_4, label %lbl_else_5
	br label %lbl_if_4
	lbl_if_4:
	store i1 0, i1* %var37
	br label %lbl_if_end_6
	br label %lbl_else_5
	lbl_else_5:
	%var77 = getelementptr i8, i8* %this, i32 16
	%var78 = bitcast i8* %var77 to i1*
	%var79 = load i1, i1* %var78
	br i1 %var79, label %lbl_if_7, label %lbl_else_8
	br label %lbl_if_7
	lbl_if_7:
	%var80 = load i8*, i8** %var36
	%var81 = bitcast i8* %var80 to i8***
	%var82 = load i8**, i8*** %var81
	%var83 = getelementptr i8*, i8** %var82, i32 3
	%var84 = load i8*, i8** %var83
	%var85 = bitcast i8* %var84 to i1 (i8*)*
	%var86 = call i1 (i8*) %var85 (i8* %var80)
	%var87 = xor i1 %var86, 1
	br i1 %var87, label %lbl_if_10, label %lbl_else_11
	br label %lbl_if_10
	lbl_if_10:
	store i1 0, i1* %var37
	br label %lbl_if_end_12
	br label %lbl_else_11
	lbl_else_11:
	store i32 0, i32* %var40
	br label %lbl_if_end_12
	br label %lbl_if_end_12
	lbl_if_end_12:
	br label %lbl_if_end_9
	br label %lbl_else_8
	lbl_else_8:
	%var88 = load i8*, i8** %var36
	%var89 = bitcast i8* %var88 to i8***
	%var90 = load i8**, i8*** %var89
	%var91 = getelementptr i8*, i8** %var90, i32 3
	%var92 = load i8*, i8** %var91
	%var93 = bitcast i8* %var92 to i1 (i8*)*
	%var94 = call i1 (i8*) %var93 (i8* %var88)
	br i1 %var94, label %lbl_if_13, label %lbl_else_14
	br label %lbl_if_13
	lbl_if_13:
	store i1 0, i1* %var37
	br label %lbl_if_end_15
	br label %lbl_else_14
	lbl_else_14:
	store i32 0, i32* %var40
	br label %lbl_if_end_15
	br label %lbl_if_end_15
	lbl_if_end_15:
	br label %lbl_if_end_9
	br label %lbl_if_end_9
	lbl_if_end_9:
	br label %lbl_if_end_6
	br label %lbl_if_end_6
	lbl_if_end_6:
	br label %lbl_if_end_3
	br label %lbl_if_end_3
	lbl_if_end_3:
	%var95 = load i1, i1* %var37
	ret i1 %var95
}
define i1 @Element.Compare(i8* %this,i32 %var96,i32 %var97) {
	%var98 = alloca i32
	store i32 %var96, i32* %var98
	%var99 = alloca i32
	store i32 %var97, i32* %var99
	%var100 = alloca i1
	%var101 = alloca i32
	store i1 0, i1* %var100
	%var102 = load i32, i32* %var99
	%var103 = add i32 %var102, 1
	store i32 %var103, i32* %var101
	%var104 = load i32, i32* %var98
	%var105 = load i32, i32* %var99
	%var106 = icmp slt i32 %var104, %var105
	br i1 %var106, label %lbl_if_16, label %lbl_else_17
	br label %lbl_if_16
	lbl_if_16:
	store i1 0, i1* %var100
	br label %lbl_if_end_18
	br label %lbl_else_17
	lbl_else_17:
	%var107 = load i32, i32* %var98
	%var108 = load i32, i32* %var101
	%var109 = icmp slt i32 %var107, %var108
	%var110 = xor i1 %var109, 1
	br i1 %var110, label %lbl_if_19, label %lbl_else_20
	br label %lbl_if_19
	lbl_if_19:
	store i1 0, i1* %var100
	br label %lbl_if_end_21
	br label %lbl_else_20
	lbl_else_20:
	store i1 1, i1* %var100
	br label %lbl_if_end_21
	br label %lbl_if_end_21
	lbl_if_end_21:
	br label %lbl_if_end_18
	br label %lbl_if_end_18
	lbl_if_end_18:
	%var111 = load i1, i1* %var100
	ret i1 %var111
}
@.List_vtable = global [10 x i8*] [
	i8* bitcast (i1 (i8*)* @List.Init to i8*),
	i8* bitcast (i1 (i8*,i8*,i8*,i1)* @List.InitNew to i8*),
	i8* bitcast (i8* (i8*,i8*)* @List.Insert to i8*),
	i8* bitcast (i1 (i8*,i8*)* @List.SetNext to i8*),
	i8* bitcast (i8* (i8*,i8*)* @List.Delete to i8*),
	i8* bitcast (i32 (i8*,i8*)* @List.Search to i8*),
	i8* bitcast (i1 (i8*)* @List.GetEnd to i8*),
	i8* bitcast (i8* (i8*)* @List.GetElem to i8*),
	i8* bitcast (i8* (i8*)* @List.GetNext to i8*),
	i8* bitcast (i1 (i8*)* @List.Print to i8*)
]
define i1 @List.Init(i8* %this) {
	%var112 = getelementptr i8, i8* %this, i32 24
	%var113 = bitcast i8* %var112 to i1*
	store i1 1, i1* %var113
	ret i1 1
}
define i1 @List.InitNew(i8* %this,i8* %var114,i8* %var115,i1 %var116) {
	%var117 = alloca i8*
	store i8* %var114, i8** %var117
	%var118 = alloca i8*
	store i8* %var115, i8** %var118
	%var119 = alloca i1
	store i1 %var116, i1* %var119
	%var120 = load i1, i1* %var119
	%var121 = getelementptr i8, i8* %this, i32 24
	%var122 = bitcast i8* %var121 to i1*
	store i1 %var120, i1* %var122
	%var123 = load i8*, i8** %var117
	%var124 = getelementptr i8, i8* %this, i32 8
	%var125 = bitcast i8* %var124 to i8**
	store i8* %var123, i8** %var125
	%var126 = load i8*, i8** %var118
	%var127 = getelementptr i8, i8* %this, i32 16
	%var128 = bitcast i8* %var127 to i8**
	store i8* %var126, i8** %var128
	ret i1 1
}
define i8* @List.Insert(i8* %this,i8* %var129) {
	%var130 = alloca i8*
	store i8* %var129, i8** %var130
	%var131 = alloca i1
	%var132 = alloca i8*
	%var133 = alloca i8*
	store i8* %this, i8** %var132
	%var134 = call i8* @calloc (i32 25, i32 1)
	%var135 = bitcast i8* %var134 to i8***
	%var136 = getelementptr [10 x i8*], [10 x i8*]* @.List_vtable, i32 0
	%var137 = bitcast [10 x i8*]* %var136 to i8**
	store i8** %var137, i8*** %var135
	store i8* %var134, i8** %var133
	%var138 = load i8*, i8** %var133
	%var139 = bitcast i8* %var138 to i8***
	%var140 = load i8**, i8*** %var139
	%var141 = getelementptr i8*, i8** %var140, i32 1
	%var142 = load i8*, i8** %var141
	%var143 = bitcast i8* %var142 to i1 (i8*,i8*,i8*,i1)*
	%var144 = load i8*, i8** %var130
	%var145 = load i8*, i8** %var133
	%var146 = call i1 (i8*,i8*,i8*,i1) %var143 (i8* %var138,i8* %var144,i8* %var145,i1 0)
	store i1 %var146, i1* %var131
	%var147 = load i8*, i8** %var133
	ret i8* %var147
}
define i1 @List.SetNext(i8* %this,i8* %var148) {
	%var149 = alloca i8*
	store i8* %var148, i8** %var149
	%var150 = load i8*, i8** %var149
	%var151 = getelementptr i8, i8* %this, i32 16
	%var152 = bitcast i8* %var151 to i8**
	store i8* %var150, i8** %var152
	ret i1 1
}
define i8* @List.Delete(i8* %this,i8* %var153) {
	%var154 = alloca i8*
	store i8* %var153, i8** %var154
	%var155 = alloca i8*
	%var156 = alloca i1
	%var157 = alloca i1
	%var158 = alloca i8*
	%var159 = alloca i8*
	%var160 = alloca i1
	%var161 = alloca i8*
	%var162 = alloca i32
	%var163 = alloca i32
	store i8* %this, i8** %var155
	store i1 0, i1* %var156
	%var164 = sub i32 0, 1
	store i32 %var164, i32* %var162
	store i8* %this, i8** %var158
	store i8* %this, i8** %var159
	%var165 = getelementptr i8, i8* %this, i32 24
	%var166 = bitcast i8* %var165 to i1*
	%var167 = load i1, i1* %var166
	store i1 %var167, i1* %var160
	%var168 = getelementptr i8, i8* %this, i32 8
	%var169 = bitcast i8* %var168 to i8**
	%var170 = load i8*, i8** %var169
	store i8* %var170, i8** %var161
	br label %lbl_loop_22
	lbl_loop_22:
	%var171 = load i1, i1* %var160
	%var172 = xor i1 %var171, 1
	%var173 = xor i1 %var172, 1
	br i1 %var173, label %lbl_and_short_25, label %lbl_and_long_26
	lbl_and_short_25:
	%var174 = add i1 0, 0
	br label %lbl_and_exit_27
	lbl_and_long_26:
	%var175 = load i1, i1* %var156
	%var176 = xor i1 %var175, 1
	%var177 = add i1 %var176, 0
	br label %lbl_and_exit_27
	lbl_and_exit_27:
	%var178 = phi i1 [%var174, %lbl_and_short_25], [%var177, %lbl_and_long_26]
	br i1 %var178, label %lbl_cont_24, label %lbl_exit_23
	lbl_cont_24:
	%var179 = load i8*, i8** %var154
	%var180 = bitcast i8* %var179 to i8***
	%var181 = load i8**, i8*** %var180
	%var182 = getelementptr i8*, i8** %var181, i32 4
	%var183 = load i8*, i8** %var182
	%var184 = bitcast i8* %var183 to i1 (i8*,i8*)*
	%var185 = load i8*, i8** %var161
	%var186 = call i1 (i8*,i8*) %var184 (i8* %var179,i8* %var185)
	br i1 %var186, label %lbl_if_28, label %lbl_else_29
	br label %lbl_if_28
	lbl_if_28:
	store i1 1, i1* %var156
	%var187 = load i32, i32* %var162
	%var188 = icmp slt i32 %var187, 0
	br i1 %var188, label %lbl_if_31, label %lbl_else_32
	br label %lbl_if_31
	lbl_if_31:
	%var189 = load i8*, i8** %var159
	%var190 = bitcast i8* %var189 to i8***
	%var191 = load i8**, i8*** %var190
	%var192 = getelementptr i8*, i8** %var191, i32 8
	%var193 = load i8*, i8** %var192
	%var194 = bitcast i8* %var193 to i8* (i8*)*
	%var195 = call i8* (i8*) %var194 (i8* %var189)
	store i8* %var195, i8** %var159
	br label %lbl_if_end_33
	br label %lbl_else_32
	lbl_else_32:
	%var196 = sub i32 0, 555
	call void (i32) @print_int (i32 %var196)
	%var197 = load i8*, i8** %var159
	%var198 = bitcast i8* %var197 to i8***
	%var199 = load i8**, i8*** %var198
	%var200 = getelementptr i8*, i8** %var199, i32 3
	%var201 = load i8*, i8** %var200
	%var202 = bitcast i8* %var201 to i1 (i8*,i8*)*
	%var203 = load i8*, i8** %var159
	%var204 = bitcast i8* %var203 to i8***
	%var205 = load i8**, i8*** %var204
	%var206 = getelementptr i8*, i8** %var205, i32 8
	%var207 = load i8*, i8** %var206
	%var208 = bitcast i8* %var207 to i8* (i8*)*
	%var209 = call i8* (i8*) %var208 (i8* %var203)
	%var210 = call i1 (i8*,i8*) %var202 (i8* %var197,i8* %var209)
	store i1 %var210, i1* %var157
	%var211 = sub i32 0, 555
	call void (i32) @print_int (i32 %var211)
	br label %lbl_if_end_33
	br label %lbl_if_end_33
	lbl_if_end_33:
	br label %lbl_if_end_30
	br label %lbl_else_29
	lbl_else_29:
	store i32 0, i32* %var163
	br label %lbl_if_end_30
	br label %lbl_if_end_30
	lbl_if_end_30:
	%var212 = load i1, i1* %var156
	%var213 = xor i1 %var212, 1
	br i1 %var213, label %lbl_if_34, label %lbl_else_35
	br label %lbl_if_34
	lbl_if_34:
	%var214 = load i8*, i8** %var159
	store i8* %var214, i8** %var159
	%var215 = load i8*, i8** %var159
	%var216 = bitcast i8* %var215 to i8***
	%var217 = load i8**, i8*** %var216
	%var218 = getelementptr i8*, i8** %var217, i32 8
	%var219 = load i8*, i8** %var218
	%var220 = bitcast i8* %var219 to i8* (i8*)*
	%var221 = call i8* (i8*) %var220 (i8* %var215)
	store i8* %var221, i8** %var159
	%var222 = load i8*, i8** %var159
	%var223 = bitcast i8* %var222 to i8***
	%var224 = load i8**, i8*** %var223
	%var225 = getelementptr i8*, i8** %var224, i32 6
	%var226 = load i8*, i8** %var225
	%var227 = bitcast i8* %var226 to i1 (i8*)*
	%var228 = call i1 (i8*) %var227 (i8* %var222)
	store i1 %var228, i1* %var160
	%var229 = load i8*, i8** %var159
	%var230 = bitcast i8* %var229 to i8***
	%var231 = load i8**, i8*** %var230
	%var232 = getelementptr i8*, i8** %var231, i32 7
	%var233 = load i8*, i8** %var232
	%var234 = bitcast i8* %var233 to i8* (i8*)*
	%var235 = call i8* (i8*) %var234 (i8* %var229)
	store i8* %var235, i8** %var161
	store i32 1, i32* %var162
	br label %lbl_if_end_36
	br label %lbl_else_35
	lbl_else_35:
	store i32 0, i32* %var163
	br label %lbl_if_end_36
	br label %lbl_if_end_36
	lbl_if_end_36:
	br label %lbl_loop_22
	br label %lbl_exit_23
	lbl_exit_23:
	%var236 = load i8*, i8** %var159
	ret i8* %var236
}
define i32 @List.Search(i8* %this,i8* %var237) {
	%var238 = alloca i8*
	store i8* %var237, i8** %var238
	%var239 = alloca i32
	%var240 = alloca i8*
	%var241 = alloca i8*
	%var242 = alloca i1
	%var243 = alloca i32
	store i32 0, i32* %var239
	store i8* %this, i8** %var240
	%var244 = getelementptr i8, i8* %this, i32 24
	%var245 = bitcast i8* %var244 to i1*
	%var246 = load i1, i1* %var245
	store i1 %var246, i1* %var242
	%var247 = getelementptr i8, i8* %this, i32 8
	%var248 = bitcast i8* %var247 to i8**
	%var249 = load i8*, i8** %var248
	store i8* %var249, i8** %var241
	br label %lbl_loop_37
	lbl_loop_37:
	%var250 = load i1, i1* %var242
	%var251 = xor i1 %var250, 1
	br i1 %var251, label %lbl_cont_39, label %lbl_exit_38
	lbl_cont_39:
	%var252 = load i8*, i8** %var238
	%var253 = bitcast i8* %var252 to i8***
	%var254 = load i8**, i8*** %var253
	%var255 = getelementptr i8*, i8** %var254, i32 4
	%var256 = load i8*, i8** %var255
	%var257 = bitcast i8* %var256 to i1 (i8*,i8*)*
	%var258 = load i8*, i8** %var241
	%var259 = call i1 (i8*,i8*) %var257 (i8* %var252,i8* %var258)
	br i1 %var259, label %lbl_if_40, label %lbl_else_41
	br label %lbl_if_40
	lbl_if_40:
	store i32 1, i32* %var239
	br label %lbl_if_end_42
	br label %lbl_else_41
	lbl_else_41:
	store i32 0, i32* %var243
	br label %lbl_if_end_42
	br label %lbl_if_end_42
	lbl_if_end_42:
	%var260 = load i8*, i8** %var240
	%var261 = bitcast i8* %var260 to i8***
	%var262 = load i8**, i8*** %var261
	%var263 = getelementptr i8*, i8** %var262, i32 8
	%var264 = load i8*, i8** %var263
	%var265 = bitcast i8* %var264 to i8* (i8*)*
	%var266 = call i8* (i8*) %var265 (i8* %var260)
	store i8* %var266, i8** %var240
	%var267 = load i8*, i8** %var240
	%var268 = bitcast i8* %var267 to i8***
	%var269 = load i8**, i8*** %var268
	%var270 = getelementptr i8*, i8** %var269, i32 6
	%var271 = load i8*, i8** %var270
	%var272 = bitcast i8* %var271 to i1 (i8*)*
	%var273 = call i1 (i8*) %var272 (i8* %var267)
	store i1 %var273, i1* %var242
	%var274 = load i8*, i8** %var240
	%var275 = bitcast i8* %var274 to i8***
	%var276 = load i8**, i8*** %var275
	%var277 = getelementptr i8*, i8** %var276, i32 7
	%var278 = load i8*, i8** %var277
	%var279 = bitcast i8* %var278 to i8* (i8*)*
	%var280 = call i8* (i8*) %var279 (i8* %var274)
	store i8* %var280, i8** %var241
	br label %lbl_loop_37
	br label %lbl_exit_38
	lbl_exit_38:
	%var281 = load i32, i32* %var239
	ret i32 %var281
}
define i1 @List.GetEnd(i8* %this) {
	%var282 = getelementptr i8, i8* %this, i32 24
	%var283 = bitcast i8* %var282 to i1*
	%var284 = load i1, i1* %var283
	ret i1 %var284
}
define i8* @List.GetElem(i8* %this) {
	%var285 = getelementptr i8, i8* %this, i32 8
	%var286 = bitcast i8* %var285 to i8**
	%var287 = load i8*, i8** %var286
	ret i8* %var287
}
define i8* @List.GetNext(i8* %this) {
	%var288 = getelementptr i8, i8* %this, i32 16
	%var289 = bitcast i8* %var288 to i8**
	%var290 = load i8*, i8** %var289
	ret i8* %var290
}
define i1 @List.Print(i8* %this) {
	%var291 = alloca i8*
	%var292 = alloca i1
	%var293 = alloca i8*
	store i8* %this, i8** %var291
	%var294 = getelementptr i8, i8* %this, i32 24
	%var295 = bitcast i8* %var294 to i1*
	%var296 = load i1, i1* %var295
	store i1 %var296, i1* %var292
	%var297 = getelementptr i8, i8* %this, i32 8
	%var298 = bitcast i8* %var297 to i8**
	%var299 = load i8*, i8** %var298
	store i8* %var299, i8** %var293
	br label %lbl_loop_43
	lbl_loop_43:
	%var300 = load i1, i1* %var292
	%var301 = xor i1 %var300, 1
	br i1 %var301, label %lbl_cont_45, label %lbl_exit_44
	lbl_cont_45:
	%var302 = load i8*, i8** %var293
	%var303 = bitcast i8* %var302 to i8***
	%var304 = load i8**, i8*** %var303
	%var305 = getelementptr i8*, i8** %var304, i32 1
	%var306 = load i8*, i8** %var305
	%var307 = bitcast i8* %var306 to i32 (i8*)*
	%var308 = call i32 (i8*) %var307 (i8* %var302)
	call void (i32) @print_int (i32 %var308)
	%var309 = load i8*, i8** %var291
	%var310 = bitcast i8* %var309 to i8***
	%var311 = load i8**, i8*** %var310
	%var312 = getelementptr i8*, i8** %var311, i32 8
	%var313 = load i8*, i8** %var312
	%var314 = bitcast i8* %var313 to i8* (i8*)*
	%var315 = call i8* (i8*) %var314 (i8* %var309)
	store i8* %var315, i8** %var291
	%var316 = load i8*, i8** %var291
	%var317 = bitcast i8* %var316 to i8***
	%var318 = load i8**, i8*** %var317
	%var319 = getelementptr i8*, i8** %var318, i32 6
	%var320 = load i8*, i8** %var319
	%var321 = bitcast i8* %var320 to i1 (i8*)*
	%var322 = call i1 (i8*) %var321 (i8* %var316)
	store i1 %var322, i1* %var292
	%var323 = load i8*, i8** %var291
	%var324 = bitcast i8* %var323 to i8***
	%var325 = load i8**, i8*** %var324
	%var326 = getelementptr i8*, i8** %var325, i32 7
	%var327 = load i8*, i8** %var326
	%var328 = bitcast i8* %var327 to i8* (i8*)*
	%var329 = call i8* (i8*) %var328 (i8* %var323)
	store i8* %var329, i8** %var293
	br label %lbl_loop_43
	br label %lbl_exit_44
	lbl_exit_44:
	ret i1 1
}
@.LL_vtable = global [1 x i8*] [
	i8* bitcast (i32 (i8*)* @LL.Start to i8*)
]
define i32 @LL.Start(i8* %this) {
	%var330 = alloca i8*
	%var331 = alloca i8*
	%var332 = alloca i1
	%var333 = alloca i8*
	%var334 = alloca i8*
	%var335 = alloca i8*
	%var336 = call i8* @calloc (i32 25, i32 1)
	%var337 = bitcast i8* %var336 to i8***
	%var338 = getelementptr [10 x i8*], [10 x i8*]* @.List_vtable, i32 0
	%var339 = bitcast [10 x i8*]* %var338 to i8**
	store i8** %var339, i8*** %var337
	store i8* %var336, i8** %var331
	%var340 = load i8*, i8** %var331
	%var341 = bitcast i8* %var340 to i8***
	%var342 = load i8**, i8*** %var341
	%var343 = getelementptr i8*, i8** %var342, i32 0
	%var344 = load i8*, i8** %var343
	%var345 = bitcast i8* %var344 to i1 (i8*)*
	%var346 = call i1 (i8*) %var345 (i8* %var340)
	store i1 %var346, i1* %var332
	%var347 = load i8*, i8** %var331
	store i8* %var347, i8** %var330
	%var348 = load i8*, i8** %var330
	%var349 = bitcast i8* %var348 to i8***
	%var350 = load i8**, i8*** %var349
	%var351 = getelementptr i8*, i8** %var350, i32 0
	%var352 = load i8*, i8** %var351
	%var353 = bitcast i8* %var352 to i1 (i8*)*
	%var354 = call i1 (i8*) %var353 (i8* %var348)
	store i1 %var354, i1* %var332
	%var355 = load i8*, i8** %var330
	%var356 = bitcast i8* %var355 to i8***
	%var357 = load i8**, i8*** %var356
	%var358 = getelementptr i8*, i8** %var357, i32 9
	%var359 = load i8*, i8** %var358
	%var360 = bitcast i8* %var359 to i1 (i8*)*
	%var361 = call i1 (i8*) %var360 (i8* %var355)
	store i1 %var361, i1* %var332
	%var362 = call i8* @calloc (i32 17, i32 1)
	%var363 = bitcast i8* %var362 to i8***
	%var364 = getelementptr [6 x i8*], [6 x i8*]* @.Element_vtable, i32 0
	%var365 = bitcast [6 x i8*]* %var364 to i8**
	store i8** %var365, i8*** %var363
	store i8* %var362, i8** %var333
	%var366 = load i8*, i8** %var333
	%var367 = bitcast i8* %var366 to i8***
	%var368 = load i8**, i8*** %var367
	%var369 = getelementptr i8*, i8** %var368, i32 0
	%var370 = load i8*, i8** %var369
	%var371 = bitcast i8* %var370 to i1 (i8*,i32,i32,i1)*
	%var372 = call i1 (i8*,i32,i32,i1) %var371 (i8* %var366,i32 25,i32 37000,i1 0)
	store i1 %var372, i1* %var332
	%var373 = load i8*, i8** %var330
	%var374 = bitcast i8* %var373 to i8***
	%var375 = load i8**, i8*** %var374
	%var376 = getelementptr i8*, i8** %var375, i32 2
	%var377 = load i8*, i8** %var376
	%var378 = bitcast i8* %var377 to i8* (i8*,i8*)*
	%var379 = load i8*, i8** %var333
	%var380 = call i8* (i8*,i8*) %var378 (i8* %var373,i8* %var379)
	store i8* %var380, i8** %var330
	%var381 = load i8*, i8** %var330
	%var382 = bitcast i8* %var381 to i8***
	%var383 = load i8**, i8*** %var382
	%var384 = getelementptr i8*, i8** %var383, i32 9
	%var385 = load i8*, i8** %var384
	%var386 = bitcast i8* %var385 to i1 (i8*)*
	%var387 = call i1 (i8*) %var386 (i8* %var381)
	store i1 %var387, i1* %var332
	call void (i32) @print_int (i32 10000000)
	%var388 = call i8* @calloc (i32 17, i32 1)
	%var389 = bitcast i8* %var388 to i8***
	%var390 = getelementptr [6 x i8*], [6 x i8*]* @.Element_vtable, i32 0
	%var391 = bitcast [6 x i8*]* %var390 to i8**
	store i8** %var391, i8*** %var389
	store i8* %var388, i8** %var333
	%var392 = load i8*, i8** %var333
	%var393 = bitcast i8* %var392 to i8***
	%var394 = load i8**, i8*** %var393
	%var395 = getelementptr i8*, i8** %var394, i32 0
	%var396 = load i8*, i8** %var395
	%var397 = bitcast i8* %var396 to i1 (i8*,i32,i32,i1)*
	%var398 = call i1 (i8*,i32,i32,i1) %var397 (i8* %var392,i32 39,i32 42000,i1 1)
	store i1 %var398, i1* %var332
	%var399 = load i8*, i8** %var333
	store i8* %var399, i8** %var334
	%var400 = load i8*, i8** %var330
	%var401 = bitcast i8* %var400 to i8***
	%var402 = load i8**, i8*** %var401
	%var403 = getelementptr i8*, i8** %var402, i32 2
	%var404 = load i8*, i8** %var403
	%var405 = bitcast i8* %var404 to i8* (i8*,i8*)*
	%var406 = load i8*, i8** %var334
	%var407 = call i8* (i8*,i8*) %var405 (i8* %var400,i8* %var406)
	store i8* %var407, i8** %var330
	%var408 = load i8*, i8** %var330
	%var409 = bitcast i8* %var408 to i8***
	%var410 = load i8**, i8*** %var409
	%var411 = getelementptr i8*, i8** %var410, i32 9
	%var412 = load i8*, i8** %var411
	%var413 = bitcast i8* %var412 to i1 (i8*)*
	%var414 = call i1 (i8*) %var413 (i8* %var408)
	store i1 %var414, i1* %var332
	call void (i32) @print_int (i32 10000000)
	%var415 = call i8* @calloc (i32 17, i32 1)
	%var416 = bitcast i8* %var415 to i8***
	%var417 = getelementptr [6 x i8*], [6 x i8*]* @.Element_vtable, i32 0
	%var418 = bitcast [6 x i8*]* %var417 to i8**
	store i8** %var418, i8*** %var416
	store i8* %var415, i8** %var334
	%var419 = load i8*, i8** %var334
	%var420 = bitcast i8* %var419 to i8***
	%var421 = load i8**, i8*** %var420
	%var422 = getelementptr i8*, i8** %var421, i32 0
	%var423 = load i8*, i8** %var422
	%var424 = bitcast i8* %var423 to i1 (i8*,i32,i32,i1)*
	%var425 = call i1 (i8*,i32,i32,i1) %var424 (i8* %var419,i32 22,i32 34000,i1 0)
	store i1 %var425, i1* %var332
	%var426 = load i8*, i8** %var330
	%var427 = bitcast i8* %var426 to i8***
	%var428 = load i8**, i8*** %var427
	%var429 = getelementptr i8*, i8** %var428, i32 2
	%var430 = load i8*, i8** %var429
	%var431 = bitcast i8* %var430 to i8* (i8*,i8*)*
	%var432 = load i8*, i8** %var334
	%var433 = call i8* (i8*,i8*) %var431 (i8* %var426,i8* %var432)
	store i8* %var433, i8** %var330
	%var434 = load i8*, i8** %var330
	%var435 = bitcast i8* %var434 to i8***
	%var436 = load i8**, i8*** %var435
	%var437 = getelementptr i8*, i8** %var436, i32 9
	%var438 = load i8*, i8** %var437
	%var439 = bitcast i8* %var438 to i1 (i8*)*
	%var440 = call i1 (i8*) %var439 (i8* %var434)
	store i1 %var440, i1* %var332
	%var441 = call i8* @calloc (i32 17, i32 1)
	%var442 = bitcast i8* %var441 to i8***
	%var443 = getelementptr [6 x i8*], [6 x i8*]* @.Element_vtable, i32 0
	%var444 = bitcast [6 x i8*]* %var443 to i8**
	store i8** %var444, i8*** %var442
	store i8* %var441, i8** %var335
	%var445 = load i8*, i8** %var335
	%var446 = bitcast i8* %var445 to i8***
	%var447 = load i8**, i8*** %var446
	%var448 = getelementptr i8*, i8** %var447, i32 0
	%var449 = load i8*, i8** %var448
	%var450 = bitcast i8* %var449 to i1 (i8*,i32,i32,i1)*
	%var451 = call i1 (i8*,i32,i32,i1) %var450 (i8* %var445,i32 27,i32 34000,i1 0)
	store i1 %var451, i1* %var332
	%var452 = load i8*, i8** %var330
	%var453 = bitcast i8* %var452 to i8***
	%var454 = load i8**, i8*** %var453
	%var455 = getelementptr i8*, i8** %var454, i32 5
	%var456 = load i8*, i8** %var455
	%var457 = bitcast i8* %var456 to i32 (i8*,i8*)*
	%var458 = load i8*, i8** %var335
	%var459 = call i32 (i8*,i8*) %var457 (i8* %var452,i8* %var458)
	call void (i32) @print_int (i32 %var459)
	%var460 = load i8*, i8** %var330
	%var461 = bitcast i8* %var460 to i8***
	%var462 = load i8**, i8*** %var461
	%var463 = getelementptr i8*, i8** %var462, i32 5
	%var464 = load i8*, i8** %var463
	%var465 = bitcast i8* %var464 to i32 (i8*,i8*)*
	%var466 = load i8*, i8** %var335
	%var467 = call i32 (i8*,i8*) %var465 (i8* %var460,i8* %var466)
	call void (i32) @print_int (i32 %var467)
	call void (i32) @print_int (i32 10000000)
	%var468 = call i8* @calloc (i32 17, i32 1)
	%var469 = bitcast i8* %var468 to i8***
	%var470 = getelementptr [6 x i8*], [6 x i8*]* @.Element_vtable, i32 0
	%var471 = bitcast [6 x i8*]* %var470 to i8**
	store i8** %var471, i8*** %var469
	store i8* %var468, i8** %var335
	%var472 = load i8*, i8** %var335
	%var473 = bitcast i8* %var472 to i8***
	%var474 = load i8**, i8*** %var473
	%var475 = getelementptr i8*, i8** %var474, i32 0
	%var476 = load i8*, i8** %var475
	%var477 = bitcast i8* %var476 to i1 (i8*,i32,i32,i1)*
	%var478 = call i1 (i8*,i32,i32,i1) %var477 (i8* %var472,i32 28,i32 35000,i1 0)
	store i1 %var478, i1* %var332
	%var479 = load i8*, i8** %var330
	%var480 = bitcast i8* %var479 to i8***
	%var481 = load i8**, i8*** %var480
	%var482 = getelementptr i8*, i8** %var481, i32 2
	%var483 = load i8*, i8** %var482
	%var484 = bitcast i8* %var483 to i8* (i8*,i8*)*
	%var485 = load i8*, i8** %var335
	%var486 = call i8* (i8*,i8*) %var484 (i8* %var479,i8* %var485)
	store i8* %var486, i8** %var330
	%var487 = load i8*, i8** %var330
	%var488 = bitcast i8* %var487 to i8***
	%var489 = load i8**, i8*** %var488
	%var490 = getelementptr i8*, i8** %var489, i32 9
	%var491 = load i8*, i8** %var490
	%var492 = bitcast i8* %var491 to i1 (i8*)*
	%var493 = call i1 (i8*) %var492 (i8* %var487)
	store i1 %var493, i1* %var332
	call void (i32) @print_int (i32 2220000)
	%var494 = load i8*, i8** %var330
	%var495 = bitcast i8* %var494 to i8***
	%var496 = load i8**, i8*** %var495
	%var497 = getelementptr i8*, i8** %var496, i32 4
	%var498 = load i8*, i8** %var497
	%var499 = bitcast i8* %var498 to i8* (i8*,i8*)*
	%var500 = load i8*, i8** %var335
	%var501 = call i8* (i8*,i8*) %var499 (i8* %var494,i8* %var500)
	store i8* %var501, i8** %var330
	%var502 = load i8*, i8** %var330
	%var503 = bitcast i8* %var502 to i8***
	%var504 = load i8**, i8*** %var503
	%var505 = getelementptr i8*, i8** %var504, i32 9
	%var506 = load i8*, i8** %var505
	%var507 = bitcast i8* %var506 to i1 (i8*)*
	%var508 = call i1 (i8*) %var507 (i8* %var502)
	store i1 %var508, i1* %var332
	call void (i32) @print_int (i32 33300000)
	%var509 = load i8*, i8** %var330
	%var510 = bitcast i8* %var509 to i8***
	%var511 = load i8**, i8*** %var510
	%var512 = getelementptr i8*, i8** %var511, i32 4
	%var513 = load i8*, i8** %var512
	%var514 = bitcast i8* %var513 to i8* (i8*,i8*)*
	%var515 = load i8*, i8** %var335
	%var516 = call i8* (i8*,i8*) %var514 (i8* %var509,i8* %var515)
	store i8* %var516, i8** %var330
	%var517 = load i8*, i8** %var330
	%var518 = bitcast i8* %var517 to i8***
	%var519 = load i8**, i8*** %var518
	%var520 = getelementptr i8*, i8** %var519, i32 9
	%var521 = load i8*, i8** %var520
	%var522 = bitcast i8* %var521 to i1 (i8*)*
	%var523 = call i1 (i8*) %var522 (i8* %var517)
	store i1 %var523, i1* %var332
	call void (i32) @print_int (i32 44440000)
	ret i32 0
}

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
	%_0 = alloca i8*
	%_1 = call i8* @calloc (i32 1, i32 20)
	%_2 = bitcast i8* %_1 to i8***
	%_3 = getelementptr [1 x i8*], [1 x i8*]* @.ArrayTest_vtable, i32 0, i32 0
	store i8** %_3, i8*** %_2
	store i8* %_1, i8** %_0
	%_4 = load i8*, i8** %_0
	%_5 = bitcast i8* %_4 to i8***
	%_6 = load i8**, i8*** %_5
	%_7 = getelementptr i8*, i8** %_6, i32 0
	%_8 = load i8*, i8** %_7
	%_9 = bitcast i8* %_8 to i32 (i8*,i32)*
	%_10 = add i32 0, 3
	%_11 = call i32 (i8*,i32) %_9 (i8* %_4,i32 %_10)
	call void (i32) @print_int (i32 %_11)
	ret i32 0
}
@.ArrayTest_vtable = global [1 x i8*] [
	i8* bitcast (i32 (i8*,i32)* @ArrayTest.test to i8*)
]
define i32 @ArrayTest.test(i8* %this,i32 %_12) {
	%_13 = alloca i32
	store i32 %_12, i32* %_13
	%_14 = alloca i32
	%_15 = alloca i32*
	%_16 = load i32, i32* %_13
	%_17 = icmp slt i32 -1, %_16
	br i1 %_17, label %lbl_alloc_oob_1, label %lbl_alloc_ok_2
	lbl_alloc_ok_2:
	call void @throw_oob ()
	br label %lbl_alloc_oob_1
	lbl_alloc_oob_1:
	%_18 = add i32 1, %_16
	%_19 = call i8* @calloc (i32 %_18, i32 32)
	%_20 = bitcast i8* %_19 to i32*
	%_21 = getelementptr i32, i32* %_20, i32 0
	store i32 %_16, i32* %_21
	store i32* %_20, i32** %_15
	%_22 = add i32 0, 0
	%_23 = getelementptr i8, i8* %this, i32 16
	%_24 = bitcast i8* %_23 to i32*
	store i32 %_22, i32* %_24
	%_25 = getelementptr i8, i8* %this, i32 16
	%_26 = bitcast i8* %_25 to i32*
	%_27 = load i32, i32* %_26
	call void (i32) @print_int (i32 %_27)
	%_28 = load i32*, i32** %_15
	%_29 = getelementptr i32, i32* %_28, i32 0
	%_30 = load i32, i32* %_29
	call void (i32) @print_int (i32 %_30)
	%_31 = add i32 0, 0
	store i32 %_31, i32* %_14
	%_32 = add i32 0, 111
	call void (i32) @print_int (i32 %_32)
	br label %lbl_loop_3
	lbl_loop_3:
	%_33 = load i32, i32* %_14
	%_34 = load i32*, i32** %_15
	%_35 = getelementptr i32, i32* %_34, i32 0
	%_36 = load i32, i32* %_35
	%_37 = icmp slt i32 %_33, %_36
	br i1 %_37, label %lbl_cont_5, label %lbl_exit_4
	lbl_cont_5:
	%_38 = load i32, i32* %_14
	%_39 = add i32 0, 1
	%_40 = add i32 %_38, %_39
	call void (i32) @print_int (i32 %_40)
	%_41 = load i32, i32* %_14
	%_42 = add i32 0, 1
	%_43 = add i32 %_41, %_42
	%_44 = load i32*, i32** %_15
	%_45 = load i32, i32* %_14
	%_46 = getelementptr i32, i32* %_44, i32 0
	%_47 = load i32, i32* %_46
	%_48 = icmp ult i32 %_45, %_47
	br i1 %_48, label %lbl_aas_ob_cont_7, label %lbl_aas_oob_6
	lbl_aas_oob_6:
	call void @throw_oob ()
	br label %lbl_aas_ob_cont_7
	lbl_aas_ob_cont_7:
	%_49 = add i32 %_45, 1
	%_50 = getelementptr i32, i32* %_44, i32 %_49
	store i32 %_43, i32* %_50
	%_51 = load i32, i32* %_14
	%_52 = add i32 0, 1
	%_53 = add i32 %_51, %_52
	store i32 %_53, i32* %_14
	br label %lbl_loop_3
	br label %lbl_exit_4
	lbl_exit_4:
	%_54 = add i32 0, 222
	call void (i32) @print_int (i32 %_54)
	%_55 = add i32 0, 0
	store i32 %_55, i32* %_14
	br label %lbl_loop_8
	lbl_loop_8:
	%_56 = load i32, i32* %_14
	%_57 = load i32*, i32** %_15
	%_58 = getelementptr i32, i32* %_57, i32 0
	%_59 = load i32, i32* %_58
	%_60 = icmp slt i32 %_56, %_59
	br i1 %_60, label %lbl_cont_10, label %lbl_exit_9
	lbl_cont_10:
	%_61 = load i32*, i32** %_15
	%_62 = load i32, i32* %_14
	%_63 = getelementptr i32, i32* %_61, i32 0
	%_64 = load i32, i32* %_63
	%_65 = icmp ult i32 %_62, %_64
	br i1 %_65, label %lbl_oob_ok_12, label %lbl_oob_11
	lbl_oob_11:
	call void @throw_oob ()
	br label %lbl_oob_ok_12
	lbl_oob_ok_12:
	%_66 = add i32 %_62, 1
	%_67 = getelementptr i32, i32* %_61, i32 %_66
	%_68 = load i32, i32* %_67
	call void (i32) @print_int (i32 %_68)
	%_69 = load i32, i32* %_14
	%_70 = add i32 0, 1
	%_71 = add i32 %_69, %_70
	store i32 %_71, i32* %_14
	br label %lbl_loop_8
	br label %lbl_exit_9
	lbl_exit_9:
	%_72 = add i32 0, 333
	call void (i32) @print_int (i32 %_72)
	%_73 = load i32*, i32** %_15
	%_74 = getelementptr i32, i32* %_73, i32 0
	%_75 = load i32, i32* %_74
	ret i32 %_75
}
@.B_vtable = global [1 x i8*] [
	i8* bitcast (i32 (i8*,i32)* @B.test to i8*)
]
define i32 @B.test(i8* %this,i32 %_76) {
	%_77 = alloca i32
	store i32 %_76, i32* %_77
	%_78 = alloca i32
	%_79 = alloca i32*
	%_80 = load i32, i32* %_77
	%_81 = icmp slt i32 -1, %_80
	br i1 %_81, label %lbl_alloc_oob_13, label %lbl_alloc_ok_14
	lbl_alloc_ok_14:
	call void @throw_oob ()
	br label %lbl_alloc_oob_13
	lbl_alloc_oob_13:
	%_82 = add i32 1, %_80
	%_83 = call i8* @calloc (i32 %_82, i32 32)
	%_84 = bitcast i8* %_83 to i32*
	%_85 = getelementptr i32, i32* %_84, i32 0
	store i32 %_80, i32* %_85
	store i32* %_84, i32** %_79
	%_86 = add i32 0, 12
	%_87 = getelementptr i8, i8* %this, i32 20
	%_88 = bitcast i8* %_87 to i32*
	store i32 %_86, i32* %_88
	%_89 = getelementptr i8, i8* %this, i32 20
	%_90 = bitcast i8* %_89 to i32*
	%_91 = load i32, i32* %_90
	call void (i32) @print_int (i32 %_91)
	%_92 = load i32*, i32** %_79
	%_93 = getelementptr i32, i32* %_92, i32 0
	%_94 = load i32, i32* %_93
	call void (i32) @print_int (i32 %_94)
	%_95 = add i32 0, 0
	store i32 %_95, i32* %_78
	%_96 = add i32 0, 111
	call void (i32) @print_int (i32 %_96)
	br label %lbl_loop_15
	lbl_loop_15:
	%_97 = load i32, i32* %_78
	%_98 = load i32*, i32** %_79
	%_99 = getelementptr i32, i32* %_98, i32 0
	%_100 = load i32, i32* %_99
	%_101 = icmp slt i32 %_97, %_100
	br i1 %_101, label %lbl_cont_17, label %lbl_exit_16
	lbl_cont_17:
	%_102 = load i32, i32* %_78
	%_103 = add i32 0, 1
	%_104 = add i32 %_102, %_103
	call void (i32) @print_int (i32 %_104)
	%_105 = load i32, i32* %_78
	%_106 = add i32 0, 1
	%_107 = add i32 %_105, %_106
	%_108 = load i32*, i32** %_79
	%_109 = load i32, i32* %_78
	%_110 = getelementptr i32, i32* %_108, i32 0
	%_111 = load i32, i32* %_110
	%_112 = icmp ult i32 %_109, %_111
	br i1 %_112, label %lbl_aas_ob_cont_19, label %lbl_aas_oob_18
	lbl_aas_oob_18:
	call void @throw_oob ()
	br label %lbl_aas_ob_cont_19
	lbl_aas_ob_cont_19:
	%_113 = add i32 %_109, 1
	%_114 = getelementptr i32, i32* %_108, i32 %_113
	store i32 %_107, i32* %_114
	%_115 = load i32, i32* %_78
	%_116 = add i32 0, 1
	%_117 = add i32 %_115, %_116
	store i32 %_117, i32* %_78
	br label %lbl_loop_15
	br label %lbl_exit_16
	lbl_exit_16:
	%_118 = add i32 0, 222
	call void (i32) @print_int (i32 %_118)
	%_119 = add i32 0, 0
	store i32 %_119, i32* %_78
	br label %lbl_loop_20
	lbl_loop_20:
	%_120 = load i32, i32* %_78
	%_121 = load i32*, i32** %_79
	%_122 = getelementptr i32, i32* %_121, i32 0
	%_123 = load i32, i32* %_122
	%_124 = icmp slt i32 %_120, %_123
	br i1 %_124, label %lbl_cont_22, label %lbl_exit_21
	lbl_cont_22:
	%_125 = load i32*, i32** %_79
	%_126 = load i32, i32* %_78
	%_127 = getelementptr i32, i32* %_125, i32 0
	%_128 = load i32, i32* %_127
	%_129 = icmp ult i32 %_126, %_128
	br i1 %_129, label %lbl_oob_ok_24, label %lbl_oob_23
	lbl_oob_23:
	call void @throw_oob ()
	br label %lbl_oob_ok_24
	lbl_oob_ok_24:
	%_130 = add i32 %_126, 1
	%_131 = getelementptr i32, i32* %_125, i32 %_130
	%_132 = load i32, i32* %_131
	call void (i32) @print_int (i32 %_132)
	%_133 = load i32, i32* %_78
	%_134 = add i32 0, 1
	%_135 = add i32 %_133, %_134
	store i32 %_135, i32* %_78
	br label %lbl_loop_20
	br label %lbl_exit_21
	lbl_exit_21:
	%_136 = add i32 0, 333
	call void (i32) @print_int (i32 %_136)
	%_137 = load i32*, i32** %_79
	%_138 = getelementptr i32, i32* %_137, i32 0
	%_139 = load i32, i32* %_138
	ret i32 %_139
}

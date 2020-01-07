all: generateJJ compileJava runJava compileLLVM runLLVM clean



generateJJ:
	java -jar lib/jtb132di.jar -te ./minijava.jj
	java -jar lib/javacc5.jar minijava-jtb.jj

runJava:
	java -cp ./src Main testfiles/irtest0.java

compileLLVM:
	sudo clang-4.0 ./testfiles/irtest0.ll
	sudo chmod +x a.out

runLLVM: compileLLVM
	sudo ./a.out

compileJava:
	javac `find ./src/ -name "*.java"` -g

clean:
	find ./ -name "*.class" | xargs rm



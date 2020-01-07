package Parsing;

import syntaxtree.Goal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Parsing {

    private final String inFileName;
    private final FileInputStream inStream;
    private final MiniJavaParser parser;


    public Parsing(String inputFile) throws FileNotFoundException {

        inFileName = inputFile;
        inStream = new FileInputStream(inFileName);
        parser = new MiniJavaParser(inStream);

    }

    public Goal parse() throws ParseException {
        return parser.Goal();
    }
}

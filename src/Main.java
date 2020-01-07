import IR.IRException;
import IR.IRGenerator;
import Parsing.*;
import Semantics.SemanticCheck;
import Semantics.myVisitors.SemanticsException;
import syntaxtree.Goal;

class Main {
    public static void main (String [] args){
        for(String arg: args){
            try{
                Parsing parsing = new Parsing(arg);
                Goal g = parsing.parse();

                SemanticCheck semanticCheck = new SemanticCheck(g);
                semanticCheck.check();

                IRGenerator irGenerator = new IRGenerator(arg,g);
                irGenerator.generate();

            }catch (ParseException e){
                e.printStackTrace();
            }catch (SemanticsException e){
                e.printStackTrace();
                System.out.println(String.format("Semantics exception in %s :", arg));
                System.out.println(e.getMessage());
            }catch (IRException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}

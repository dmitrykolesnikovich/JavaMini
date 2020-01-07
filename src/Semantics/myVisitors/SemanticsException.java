package Semantics.myVisitors;

public class SemanticsException extends Exception{

    private String errStr = "";

    public SemanticsException(){

    }

    public SemanticsException(String err){
        errStr = err;

    }

    public static void err(String s) throws SemanticsException {
        throw new SemanticsException(s);
    }

    public String getErrStr(){
        return errStr;
    }

    @Override
    public String getMessage() {
        return errStr;
    }

    /*
    public static void err(){
        System.out.println("Semantics exception");
        try{
            int[] x = new int[1];
            x[5]=0;
        }catch (Exception e){
            e.printStackTrace();
        }
        System.exit(0);
    }*/
}

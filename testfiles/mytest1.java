class MainClass{
    public static void main(String[] a){
        System.out.println(5);
    }
}

class A{
    int x;
    int y;

    public int getX(){
        return x;
    }

    public int getY(boolean b){
        return y;
    }
}

class B extends A{
    int x;
    boolean y;

    public int getX(){
        return x;
    }


}

class C extends B{
    public int getY(boolean b){
        return 1;
    }
}
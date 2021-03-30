package examples.cpp;

public class CppExample {
    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.parse("int a, **b, *c;   double dd;");
        System.out.println("----------");
        parser.parse("int &a, **&b, *c;   double &dd;");
        System.out.println("----------");
        parser.parse("int &abacaba, t;");
        try {
            parser.parse("int *&*t;");
        } catch (IllegalArgumentException e){
            System.out.println("Success - should fail " + e.getMessage());
        }
        try {
            parser.parse("int *&&t;");
        } catch (IllegalArgumentException e){
            System.out.println("Success - should fail " + e.getMessage());
        }
    }
}

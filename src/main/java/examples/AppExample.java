package examples;

import examples.math.Parser;

public class AppExample {
    public static void main(String[] args) {

        Parser parser = new Parser();
        System.out.println(parser.parse("1 - 2 - 3 - 4 + 5 - 6").val); // -9
        System.out.println(parser.parse("1 - (2 - 3)").val); // 2
        System.out.println(parser.parse("2 + 2 * 2").val); // 6
        System.out.println(parser.parse("100 / 3 / 10 * (4 + 2) - 10").val); // 8
        System.out.println(parser.parse("15 / 10").val); // 1
        System.out.println(parser.parse("((2 + 2) * 2 - (14 * 1 + 22) * 2 + 7) % (10 / 2) + 9").val); // -57 % 5 + 9 = -2 + 9 = 7
        System.out.println(parser.parse("10 - 7 %   2").val); // 9
        System.out.println(parser.parse("10 * (2 % 8)").val); // 20
        System.out.println(parser.parse("(1 - 10) * 2 % 8").val); // -2


    }
}

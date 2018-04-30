package cn.lightfish;

import cn.lightfish.codeGenerator.CodeGenerator;
import cn.lightfish.lr.LRParsingTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cison {
    public static void main(String[] args) throws IOException {
        String arg = "D:\\git\\cison\\src\\main\\resources\\ANSI_C_backup.yacc";
        Context context = parse(arg);
        context.findFirstFollowSet();
//        print(context.toString());
        Production root = context.addRealStartSymbol();
        context.whereIsTerminalSymbol("MODA");
        context.whereIsNonTerminalSymbol("statement_list");
        LRParsingTable lrpt = LRParsingTable.build(context);
        System.out.println(lrpt);
        CodeGenerator generator = new CodeGenerator(context, lrpt);
        generator.generate("\\Parser.java", "cn.lightfish", generator.autoIndentFormatter);
    }

    public static Context parse(String arg) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(arg)));
        Context context = new Context();
        try (Scanner scanner = new Scanner(text)) {
            pickStart(scanner, context);
            while (scanner.hasNext()) {
                pickRule(scanner, context);
            }
        }
        context.getToken(Symbol.DollarR);
        return context;
    }

    public static void pickStart(Scanner scanner, Context context) {
        State state = State.Symbol;
        while (scanner.hasNext()) {
            String next = scanner.next();
            switch (next) {
                case "%token": {
                    continue;
                }
                case "%start": {
                    context.setStart(context.getToken(scanner.next()));
                    scanner.next();
                    return;
                }
                case "%%": {
                    if (state == State.Symbol) {
                        context.setStart(context.getToken(next = scanner.next()));
                        scanner.next();
                        return;
                    }

                    break;
                }
                default: {
                    context.getToken(next);
                    // print(next);
                }
            }
        }
    }

    ;

    public static void pickRule(Scanner scanner, Context context) {
        Symbol left = context.getToken(scanner.next());
        List<Production> productions = new ArrayList<>();
        Production production = new Production();
        production.setLeft(left);
        while (scanner.hasNext()) {
            String right = scanner.next();
            switch (right) {
                case ":": {
                    continue;
                }
                case "|": {
                    productions.add(production);
                    production = new Production();
                    production.setLeft(left);
                    break;
                }
                case "{": {
                    while (scanner.hasNext()) {
                        right = scanner.next();
                        if (!"}".equals(right)) {
                            production.addCode(right);
                        }
                    }
                }
                case ";": {
                    productions.add(production);
                    left.productions.addAll(productions);
                    context.addRule(left, productions);
                    return;
                }
                default: {
                    production.addRight(context.getToken(right));
                    break;
                }
            }
        }
    }

    public static void print(String... args) {
        System.out.println(Stream.of(args).collect(Collectors.joining(" ")));
    }

    enum State {
        Symbol, Start, Production
    }
}

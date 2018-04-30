package cn.lightfish;

import java.util.*;
import java.util.stream.Collectors;

public class Context {
    public HashMap<Symbol, Set<Symbol>> first = new HashMap<>();
    public HashMap<Symbol, Set<Symbol>> follow = new HashMap<>();
    private Symbol start;
    private Symbol realStart;
    private ArrayList<Symbol> symbols = new ArrayList<>();
    private HashMap<Symbol, List<Production>> productionsMap = new HashMap<>();
    private ArrayList<Production> productions = new ArrayList<>();

    public Symbol getStart() {
        return start;
    }

    public void setStart(Symbol start) {
        this.start = start;
    }

    public Symbol getRealStart() {
        return realStart;
    }

    public Symbol getToken(String token) {
        token = token.trim();
        for (Symbol symbol : this.symbols) {
            if (symbol.name.equals(token)) {
                return symbol;
            }
        }
        Symbol n = new Symbol(token);
        this.symbols.add(n);
        return n;
    }

    public Symbol getToken(Symbol token) {
        for (Symbol symbol : this.symbols) {
            if (symbol.name.equals(token.name)) {
                return symbol;
            }
        }
        this.symbols.add(token);
        return token;
    }

    public void addRule(Symbol left, List<Production> rules) {
        this.productionsMap.put(left, rules);
        for (Production rule : rules) {
            for (Symbol right : rule.rights) {
                right.in.add(rule);
            }
            this.productions.add(rule);
        }
    }

    public void addRule(Symbol left, Production rules) {
        ArrayList<Production> productions = new ArrayList<>();
        productions.add(rules);
        addRule(left, productions);
    }

    public void removeRule(Production rule) {
        this.productionsMap.get(rule.getLeft()).remove(rule);
        this.productions.remove(rule);
        for (Symbol symbol : this.symbols) {
            symbol.in.remove(rule);
        }

    }

    public Production addRealStartSymbol() {
        realStart = this.getToken("START_");
        Production startProduction = new Production();
        startProduction.setLeft(realStart);
        startProduction.addRight(start);
        start.in.add(startProduction);
        productionsMap.put(realStart, Collections.singletonList(startProduction));
        return startProduction;
    }

    public void findFirstFollowSet() {
        for (Symbol symbol : symbols) {
            first.put(symbol, new HashSet<>());
            follow.put(symbol, new HashSet<>());
        }
        findFirstSet();
        findFollowSet();
    }

    public void findFirstSet() {
        for (Symbol x : this.symbols) {
            if (x.isTerminal()) {
                first.get(x).add(x);
            }
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Symbol x : this.symbols) {
                //如果symbol是个非终结符，那么：
                //1.symbol推出的第一个符号是终结符a，那么first(X)必定包含a。该情况由递归调用的终结情况控制。
                //2.symbol推出的第一个符号是非终结符Y，那么first(X)必定包含first(Y)中所有的非空符号。
                //3.symbol推出的前i个符号都能够推出空，那么first(X)必定包含前i个符号的first的非空符号和第i+1个符号的first的所有符号。
                for (Production production : x.productions) {
                    boolean allCanNull = false;
                    for (int i = 0; i < production.rights.size(); i++) {
                        Symbol Yi = production.rights.get(i);
                        try {
                            changed = changed || first.get(x).addAll(first.get(Yi));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if (Yi.canDeduceToNull()) {
                            first.get(x).remove(Symbol.Null);
                        } else {
                            break;
                        }
                        if (i == production.rights.size() - 1) {
                            allCanNull = true;
                        }
                    }
                    if (allCanNull) {
                        changed = changed || first.get(x).add(Symbol.Null);
                    }
                }
            }
        }
        return;
    }

    public void findFollowSet() {
        follow.get(start).add(Symbol.DollarR);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Symbol B : this.symbols) {
                for (Production production : B.in) {
                    int indexB = production.rights.indexOf(B);
                    Symbol A = production.left;
                    if (indexB == production.rights.size() - 1) {
                        changed = changed || follow.get(B).addAll(follow.get(A));
                    } else {
                        Symbol beta = production.rights.get(indexB + 1);
                        changed = changed || follow.get(B).addAll(first.get(beta));
                        follow.get(B).remove(Symbol.Null);
                        if (beta.canDeduceToNull()) {
                            changed = changed || follow.get(B).addAll(follow.get(A));
                        }
                    }
                }
            }
        }
        return;
    }

    public Set<Symbol> first(List<Symbol> betaA) {
        Set<Symbol> firstSet = new HashSet<>();
        if (betaA.size() == 0) {
            return firstSet;
        }
        Symbol symbol = betaA.get(0);
        Collection v = first.get(symbol);
        try {
            firstSet.addAll(v);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return firstSet;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        return "Context{" +
                "start='" + start + '\'' +
                ", symbols=" + symbols +
                ", productionsMap=\n" + productionsMap.entrySet().stream().map((i) -> {
            String name = i.getKey().name;
            stringBuilder.append("\n").append(name).append("\n");
            i.getValue().forEach((s) -> stringBuilder.append(s.rights).append("\n"));
            return stringBuilder;
        }).collect(Collectors.joining("\n")) +
                '}';
    }


    public void whereIsTerminalSymbol(String symbol) {
        System.out.println("====================terminal symbol " + symbol + "==================== ");
        System.out.println("====================first:");
        this.first.entrySet().stream().forEach((i) -> {
            if (i.getValue().contains(this.getToken(symbol))) {
                System.out.println(i.getKey());
            }
        });
        System.out.println("====================follow:");
        this.follow.entrySet().stream().forEach((i) -> {
            if (i.getValue().contains(this.getToken(symbol))) {
                System.out.println(i.getKey());
            }
        });
    }

    public void whereIsNonTerminalSymbol(String symbol) {
        System.out.println("==================== non terminal symbol " + symbol + "==================== ");
        System.out.println("====================first:");
        this.first.entrySet().stream().forEach((i) -> {
            if (i.getKey().equals(this.getToken(symbol))) {
                System.out.println(i.getValue());
            }
        });
        System.out.println("====================follow:");
        this.follow.entrySet().stream().forEach((i) -> {
            if (i.getKey().equals(this.getToken(symbol))) {
                System.out.println(i.getValue());
            }
        });
    }

    public Set<Symbol> getAllTerminals() {
        Set<Symbol> terminals = new HashSet<>();
        for (Symbol symbol : symbols) {
            if (symbol.isTerminal()) {
                terminals.add(symbol);
            }
        }
        return terminals;
    }

    public Set<Symbol> getAllNonTerminals() {
        Set<Symbol> nonTerminals = new HashSet<>();
        for (Symbol symbol : symbols) {
            if ((!symbol.isTerminal()) && (!symbol.equals(realStart))) {
                nonTerminals.add(symbol);
            }
        }
        return nonTerminals;
    }

    public Production findProductionById(int id) {
        for (Production production : productions) {
            if (production.id == id) {
                return production;
            }
        }
        return null;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public Map<Symbol, List<Production>> getProductionsMap() {
        return productionsMap;
    }

    public ArrayList<Production> getProductions() {
        return productions;
    }
}

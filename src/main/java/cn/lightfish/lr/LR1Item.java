package cn.lightfish.lr;

import cn.lightfish.Context;
import cn.lightfish.Production;
import cn.lightfish.Symbol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LR1Item {
    public Production production;
    public Set<Symbol> predicts;
    public int dot;

    public LR1Item(Production production, Set<Symbol> predicts, int dot) {
        this.production = production;
        this.predicts = predicts;
        this.dot = dot;
    }

    public LR1Item(Production production, Set<Symbol> predicts) {
        this(production, predicts, 0);
    }

    public LR1Item(Production production) {
        this(production, null);
        Set<Symbol> predicts = new HashSet<>();
        predicts.add(Symbol.DollarR);
        this.predicts = predicts;
    }

    public boolean nextNonTerminal() {
        if (dot == production.getRights().size()) {
            return false;
        }
        return production.getRights().get(dot).getProductions().size() != 0;
    }

    public Symbol next() {
        if (dot == production.getRights().size()) {
            return null;
        }
        return production.getRights().get(dot);
    }

    public void shift() {
        dot++;
    }

    public boolean isReducable() {
        if (dot == production.getRights().size()) {
            return true;
        }
        return false;
    }

    public List<LR1Item> inStateExtension(Context yaccFile) {
        if (!nextNonTerminal()) {
            //如果是终结符，就不要拓展。
            return new ArrayList<>();
        }
        List<Symbol> betaA = new ArrayList<>(production.getRights().subList(dot + 1, production.getRights().size()));
        Set<Symbol> firstBetaA = yaccFile.first(betaA);
        if (firstBetaA == null) {
            System.out.println("=<");
        }
        firstBetaA.addAll(predicts);
        List<LR1Item> itemsToBeAdded = new ArrayList<>();
        for (Production production : production.getRights().get(dot).getProductions()) {
            itemsToBeAdded.add(new LR1Item(production, firstBetaA));
        }
        return itemsToBeAdded;
    }

    @Override
    protected LR1Item clone() {
        return new LR1Item(production, new HashSet<>(predicts), dot);
    }

    public boolean productionEquals(LR1Item item) {
        if (item.production.equals(production) && item.dot == dot) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LR1Item)) {
            return false;
        } else {
            return ((LR1Item) obj).predicts.equals(predicts) && ((LR1Item) obj).production.equals(production) && ((LR1Item) obj).dot == dot;
        }
    }

    @Override
    public String toString() {
        String pro = "";
        String prd = "";
        for (int i = 0; i < production.getRights().size() + 1; i++) {
            if (dot == i) {
                pro += ". ";
            }
            if (i < production.getRights().size()) {
                pro += production.getRights().get(i).toString() + " ";
            }
        }
        for (Symbol pred : predicts) {
            prd += pred.toString() + "|";
        }
        return "(" + production.getLeft() + "->" + pro + "," + prd + ")";
    }
}

package cn.lightfish;

import java.util.ArrayList;
import java.util.List;

public class Symbol {
    public static final Symbol Null = new Symbol("~");
    public static final Symbol DollarR = new Symbol("DollarR");
    String name;
    List<Production> productions;
    List<Production> in;
    List<String> attributes;

    public Symbol(String name) {
        this.name = name;
        this.productions = new ArrayList<>();
        this.in = new ArrayList<>();
        this.attributes = new ArrayList<>();
    }

    public Symbol() {
        this("");
    }

    public boolean isTerminal() {
        return productions.size() == 0;
    }

    /**
     * 判断一个符号能否推出Null。
     *
     * @return
     */
    public boolean canDeduceToNull() {
        return false;
    }

    public List<Production> getProductions() {
        return productions;
    }

    public String getClassName() {
        return this.name.toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return this.name.equals((((Symbol) o).name));
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }
}

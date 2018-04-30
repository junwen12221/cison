package cn.lightfish.ll1;

import cn.lightfish.Context;
import cn.lightfish.Production;
import cn.lightfish.Symbol;

import java.util.*;
import java.util.stream.Collectors;

public class AL {
    public static void eliminateDirectLeftRecursion(Context context) {
        boolean changed = true;
        while (changed) {
            changed = false;
            Set<Symbol> keys = new HashSet<>(context.getProductionsMap().keySet());
            for (Symbol symbol : keys) {
                Symbol left = symbol;
                List<Production> rights = context.getProductionsMap().get(left);
                Map<Boolean, List<Production>> leftRec = rights.stream().collect(Collectors.partitioningBy(m -> m.getRights().get(0).equals(left)));
                List<Production> leftRecProductions = leftRec.get(Boolean.TRUE);
                List<Production> rightRroductions = leftRec.get(Boolean.FALSE);
                if (leftRecProductions.isEmpty()) continue;
                System.out.println("直接左递归");
                System.out.println(leftRecProductions);
                changed = true;
                /*
                1、把所有产生式写成候选式形式。如A→Aa1｜Aa2……｜Aan｜b1｜b2……｜bm。其中每个a都不等于ε，而第个b都不以A开头。
　　2、变换候选式成如下形式：
　　A→b1A’｜b2A’……｜bmA’
　　A’ →a1A’｜a2A’……｜anA’｜ε

                 */
                Symbol factorName = context.getToken(left.getName() + "'");
                // System.out.println("A’ →a1A’｜a2A’……｜anA’｜ε");
                for (Production leftRecProduction : leftRecProductions) {
                    Production factor = new Production();
                    factor.setLeft(factorName);
                    factor.getRights().addAll(leftRecProduction.getRights().subList(1, leftRecProduction.getRights().size()));
                    factor.getRights().add(factor.getLeft());
                    System.out.println("重写得到的规则:" + factor);
                    context.addRule(factor.getLeft(), factor);
                }
                //System.out.println("A→b1A’｜b2A’……｜bmA’");
                for (Production production : rightRroductions) {
                    Production current = new Production();
                    current.setLeft(left);
                    current.getRights().addAll(production.getRights());
                    current.getRights().add(factorName);
                    System.out.println("重写得到的规则:" + current);
                    context.addRule(left, current);
                }
                for (Production rightRroduction : rightRroductions) {
                    context.removeRule(rightRroduction);
                }
                for (Production leftRecProduction : leftRecProductions) {
                    context.removeRule(leftRecProduction);
                }
            }
        }
    }

    /*
    　要求文法不存在A 经过一次或多次能推导出A和不存在ε产生式（形如Ａ→ε）。
    　　1、以某种顺序排列非终结符A1，A2，……，An；
    　　2、for i ＝ 1 to n do
    　　　　{for j ＝ 1 to i - l do
    　　　　　{ 用产生式Ａi→a1b｜a2b｜……｜akb代替每个开如Ai→Ajb的产生式，其中，Aj→a1｜a2｜……｜ak是所有的当前Aj产生式；}
    　　　　消除关于Ai产生式中的直接左递归性}
    　　　}
     */
    public static void removeLeftRecursion(Context context) {
        List<Symbol> symbols = context.getSymbols().stream().filter(i -> !i.isTerminal()).collect(Collectors.toList());
        for (int i = 0; i < symbols.size(); i++) {
            Symbol ai = symbols.get(i);
            for (int j = 0; j < i - 1; j++) {
                Symbol aj = symbols.get(j);
                List<Production> productions = new ArrayList<>(context.getProductionsMap().get(ai));
                if (productions == null || productions.isEmpty()) continue;
                for (Production production : productions) {
                    if (production.getRights().get(0).equals(aj)) {
                        System.out.println("间接左递归");
                        System.out.println(production);
                        System.out.println(aj);
                        List<Production> ajs = context.getProductionsMap().get(aj);
                        List<Symbol> ys = production.getRights().subList(1, production.getRights().size());
                        context.removeRule(production);
                        for (Production aj1 : ajs) {
                            Production aip = new Production();
                            aip.setLeft(ai);
                            aip.getRights().addAll(aj1.getRights());
                            aip.getRights().addAll(ys);
                            System.out.println("重写得到的规则:" + aip);
                            context.addRule(ai, aip);
                        }
                    }
                }
            }
        }
    }

    public static void noOrIsTerminatingSymbol(Context context) {
        //去掉没有使用的production
        boolean changed = true;
        while (changed) {
//            int size1 = context.getSymbols().size();
            int size2 = context.getProductions().size();
            // context.getSymbols().retainAll(context.getProductions().stream().flatMap(i -> i.getRights().stream()).collect(Collectors.toSet()));
            Set<Production> productions = context.getSymbols().stream().flatMap((c) -> c.getProductions().stream()).collect(Collectors.toSet());
            List<Production> oldProduction = context.getProductions();
            if (
                    context.getProductions().size() < size2) {
                ArrayList<Production> re = new ArrayList<>(oldProduction);
                oldProduction.clear();
                oldProduction.addAll(productions);
                System.out.println("执行了优化,去掉多余的规则");
                re.removeAll(oldProduction);
                re.forEach(System.out::println);
                changed = true;
            } else if (context.getProductions().size() > size2) {
                System.out.println("错误");
                break;
            } else {
                break;
            }
        }
        context.getProductionsMap().clear();
        context.getProductionsMap().putAll(context.getProductions().stream()
                .collect(Collectors.groupingBy((k) -> k.getLeft(), Collectors.toList())));
        System.out.println("最终的规则");
        for (Map.Entry<Symbol, List<Production>> symbolListEntry : context.getProductionsMap().entrySet()) {
            System.out.println(symbolListEntry.getKey());
            for (Production production : symbolListEntry.getValue()) {
                System.out.println("\t" + production);
            }
        }

    }

}

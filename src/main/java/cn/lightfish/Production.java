package cn.lightfish;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Production {
    private static int sid;

    int id;
    Symbol left;
    List<Symbol> rights;
    List<String> translations;
    private Boolean canNull;

    public Production() {
        id = sid++;
        rights = new ArrayList<>();
        translations = new ArrayList<>();
    }

    public static int getSid() {
        return sid;
    }

    public void addRight(Symbol right) {
        rights.add(right);
    }

    public void addCode(String right) {
        translations.add(right);
    }

    public Boolean canNull() {
        if (canNull != null) {
            return canNull;
        } else {
            boolean productionCanNull = true;
            for (Symbol symbol : rights) {
                productionCanNull = productionCanNull && symbol.canDeduceToNull();
            }
            canNull = productionCanNull;
            return canNull;
        }
    }

    @Override
    public String toString() {
        String rightSide = "";
        for (Symbol right : rights) {
            rightSide += right.toString() + " ";
        }

        return left.toString() + ":" + rightSide;
    }

//    public String walk(Context context) {
//        String rightSide = "";
//        for (Symbol right : rights) {
//            rightSide += right.productions.stream().map(i->i.lookUp(context).toString()).collect(Collectors.joining("\n")) + " ";
//        }
//        return left.toString() + ":" + rightSide;
//    }

    public int getId() {
        return id;
    }

    public Symbol getLeft() {
        return left;
    }

    public void setLeft(Symbol left) {
        this.left = (left);
    }

    public List<Symbol> getRights() {
        return rights;
    }

    public List<String> getTranslations() {
        return translations;
    }

    public Boolean getCanNull() {
        return canNull;
    }

    public Set<Symbol> lookUp(Context context) {
        return context.first.entrySet().stream().filter((i) -> {
            if (i.getValue().contains(this.rights.get(0))) {
                return true;
            }
            return false;
        }).flatMap(i -> i.getValue().stream()).collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Production)) {
            return false;
        } else {
            return ((Production) obj).left.equals(left) && ((Production) obj).rights.equals(rights);
        }
    }
}

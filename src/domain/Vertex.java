package domain;

import java.util.LinkedHashSet;
import java.util.Set;

public class Vertex {

    private char symbol;
    private boolean nullable;
    private Set<Integer> firstPos = new LinkedHashSet<>();
    private Set<Integer> lastPos = new LinkedHashSet<>();
    private Vertex leftSon;
    private Vertex rightSon;

    public void addFirstPos(Integer x) {
        firstPos.add(x);
    }

    public void addLastPos(Integer x) {
        lastPos.add(x);
    }

    public void addFirstPos(Set<Integer> xSet) {
        firstPos.addAll(xSet);
    }

    public void addLastPos(Set<Integer> xSet) {
        lastPos.addAll(xSet);
    }

    public Vertex getLeftSon() {
        return leftSon;
    }

    public void setLeftSon(Vertex leftSon) {
        this.leftSon = leftSon;
    }

    public Vertex getRightSon() {
        return rightSon;
    }

    public void setRightSon(Vertex rightSon) {
        this.rightSon = rightSon;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public Set<Integer> getFirstPos() {
        return firstPos;
    }

    public void setFirstPos(Set<Integer> firstPos) {
        this.firstPos = firstPos;
    }

    public Set<Integer> getLastPos() {
        return lastPos;
    }

    public void setLastPos(Set<Integer> lastPos) {
        this.lastPos = lastPos;
    }
}

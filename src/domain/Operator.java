package domain;

public enum Operator {

    CONCATENATION('.'),
    KLEENE_ITERATION('*'),
    DISJUNCTION('|');

    private char symbol;

    Operator(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }
}

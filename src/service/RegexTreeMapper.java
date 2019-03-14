package service;

import domain.DfaNode;
import domain.Operator;
import domain.Graph;
import domain.Vertex;

import java.util.*;
import java.util.stream.Collectors;

public class RegexTreeMapper {

    private static final char ENDING_SYMBOL = '#';
    private static final char ANY_CHAR_SYMBOL = '_';
    private int numberOfLeafs = 0;
    private int numberOfNodes = 0;
    private Map<Integer, Set<Integer>> followPos = new HashMap<>();

    private List<DfaNode> dfaNodeList = new ArrayList<>();
    private Map<String, Set<String>> adiacency = new HashMap<>();

    private Map<Integer, Character> orderSymbol = new HashMap<>();
    private Map<Character, Integer> symbolOrder = new HashMap<>();
    private char[][] EdgeSymbol = new char[100][100];

    public char[][] getEdgeSymbol() {
        return EdgeSymbol;
    }

    public void setEdgeSymbol(char[][] edgeSymbol) {
        EdgeSymbol = edgeSymbol;
    }

    public Map<Integer, Character> getOrderSymbol() {
        return orderSymbol;
    }

    public Map<Character, Integer> getSymbolOrder() {
        return symbolOrder;
    }

    public RegexTreeMapper() {
    }

    public Graph regexToTree(String regex) {

        regex = '(' + regex + ')' + Operator.CONCATENATION.getSymbol() + '(' + ENDING_SYMBOL + ')';
        Vertex vertex = generateTree(regex);


        Graph graph = generateGraph();

        return graph;
    }

    private Graph generateGraph() {
        Graph graph = new Graph();

        DfaNode dfaNode = new DfaNode();
        dfaNode.setId(++numberOfNodes);
        dfaNode.setStart(true);
        dfaNode.setData(followPos.get(1).stream().map(String::valueOf).sorted().collect(Collectors.joining()));
        dfaNode.setFinal(false);
        adiacency.put(dfaNode.getData(), new HashSet<>());

        dfaNodeList.add(dfaNode);
        int leftIt = 0;
        while(leftIt < dfaNodeList.size()) {

            DfaNode currentNode = dfaNodeList.get(leftIt);

            Map<Character, Set<Integer>> possibleWays = new HashMap<>();

            for(int i = 0; i < currentNode.getData().length(); i++) {
                int symbolIndex = currentNode.getData().charAt(i) - 48;
                char symbol = orderSymbol.get(symbolIndex);

                Set<Integer> currentWay = followPos.get(symbolIndex);
                if(possibleWays.containsKey(symbol)) {
                    Set<Integer> temp = possibleWays.get(symbol);
                    temp.addAll(currentWay);
                    possibleWays.replace(symbol,temp);
                } else {
                    possibleWays.put(symbol, currentWay);
                }
            }

            for (Character c:possibleWays.keySet()) {
                String newData = possibleWays.get(c).stream().map(String::valueOf).sorted().collect(Collectors.joining());

                if (!adiacency.containsKey(newData)) {

                    DfaNode dfaNodeNew = new DfaNode();
                    dfaNodeNew.setId(++numberOfNodes);
                    dfaNodeNew.setData(newData);
                    if (newData.contains(String.valueOf(numberOfLeafs))) {
                        dfaNodeNew.setFinal(true);
                    } else {
                        dfaNodeNew.setFinal(false);
                    }
                    dfaNodeNew.setStart(false);
                    dfaNodeList.add(dfaNodeNew);
                    adiacency.put(dfaNodeNew.getData(), new HashSet<>());
                    EdgeSymbol[currentNode.getId()][dfaNodeNew.getId()] = c;

                } else {
                    for (DfaNode dfaNode1 : dfaNodeList) {
                        if (dfaNode1.getData().equals(newData)) {
                            EdgeSymbol[currentNode.getId()][dfaNode1.getId()] = c;
                        }
                    }
                }

                Set<String> newAdiacencySet = adiacency.get(currentNode.getData());
                newAdiacencySet.add(newData);
                adiacency.replace(currentNode.getData(), newAdiacencySet);
            }
            leftIt++;
        }

        graph.setDfaNodeList(dfaNodeList);
        graph.setAdiacency(adiacency);
        return graph;
    }


    @SuppressWarnings("Duplicates")
    private Vertex generateTree(String expression) {

        Vertex vertex = new Vertex();

        if(expression.length() > 1) {
            // Meaning we don't have a single character
            // 1. Find first operand
            int operatorPosition = getMainOperandIndex(expression);
            vertex.setSymbol(expression.charAt(operatorPosition));

            String firstOperand = expression.substring(1, operatorPosition - 1);
            vertex.setLeftSon(generateTree(firstOperand));

            if(vertex.getSymbol() != Operator.KLEENE_ITERATION.getSymbol()) {
                String secondOperand = expression.substring(operatorPosition + 2, expression.length() - 1);
                vertex.setRightSon(generateTree(secondOperand));
            }
        } else {
            //Meaning it's a single character
            numberOfLeafs++;
            orderSymbol.put(numberOfLeafs, expression.charAt(0));
            symbolOrder.put(expression.charAt(0), numberOfLeafs);
            vertex.setSymbol(expression.charAt(0));
            followPos.put(numberOfLeafs, new HashSet<>());

            if(vertex.getSymbol() == ANY_CHAR_SYMBOL) {
                vertex.setNullable(true);
            } else {
                vertex.setNullable(false);
                vertex.addFirstPos(numberOfLeafs);
                vertex.addLastPos(numberOfLeafs);
            }

            return vertex;
        }

        if(vertex.getSymbol() == Operator.DISJUNCTION.getSymbol()) {
            vertex.setNullable(vertex.getLeftSon().isNullable() || vertex.getRightSon().isNullable());
            vertex.addFirstPos(vertex.getLeftSon().getFirstPos());
            vertex.addFirstPos(vertex.getRightSon().getFirstPos());
            vertex.addLastPos(vertex.getLeftSon().getLastPos());
            vertex.addLastPos(vertex.getRightSon().getLastPos());

        } else if(vertex.getSymbol() == Operator.CONCATENATION.getSymbol()) {
            vertex.setNullable(vertex.getLeftSon().isNullable() && vertex.getRightSon().isNullable());
            if(vertex.getLeftSon().isNullable()) {
                vertex.addFirstPos(vertex.getLeftSon().getFirstPos());
                vertex.addFirstPos(vertex.getRightSon().getFirstPos());
            } else {
                vertex.addFirstPos(vertex.getLeftSon().getFirstPos());
            }
            if(vertex.getRightSon().isNullable()) {
                vertex.addLastPos(vertex.getLeftSon().getLastPos());
                vertex.addLastPos(vertex.getRightSon().getLastPos());
            } else {
                vertex.addLastPos(vertex.getRightSon().getLastPos());
            }

            for (Integer i : vertex.getLeftSon().getLastPos()) {
                for (Integer j : vertex.getRightSon().getFirstPos()) {
                    addElementToFollowPos(i, j);
                }
            }
        } else if(vertex.getSymbol() == Operator.KLEENE_ITERATION.getSymbol()) {
            vertex.setNullable(true);
            vertex.addFirstPos(vertex.getLeftSon().getFirstPos());
            vertex.addLastPos(vertex.getLeftSon().getLastPos());

            for (Integer i : vertex.getLeftSon().getLastPos()) {
                for (Integer j : vertex.getLeftSon().getFirstPos()) {
                    addElementToFollowPos(i, j);
                }
            }

        }

        return vertex;
    }

    private int getMainOperandIndex(String expression) {
        int openBracketsCount = 0;
        for(int i = 0; i < expression.length(); i++) {
            if(expression.charAt(i) == '(') {
                openBracketsCount++;
            } else if(expression.charAt(i) == ')') {
                openBracketsCount--;
            }

            if(openBracketsCount == 0) {
                return i + 1;
            }
        }

        // Something is wrong if it reaches this point
        return 0;

    }

    private void addElementToFollowPos(Integer key, Integer element) {
        Set<Integer> values = followPos.get(key);
        values.add(element);
        followPos.replace(key, values);
    }


}

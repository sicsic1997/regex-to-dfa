package domain;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {

    private List<DfaNode> dfaNodeList;
    private Map<String, Set<String>> adiacency;

    public List<DfaNode> getDfaNodeList() {
        return dfaNodeList;
    }

    public void setDfaNodeList(List<DfaNode> dfaNodeList) {
        this.dfaNodeList = dfaNodeList;
    }

    public Map<String, Set<String>> getAdiacency() {
        return adiacency;
    }

    public void setAdiacency(Map<String, Set<String>> adiacency) {
        this.adiacency = adiacency;
    }
}

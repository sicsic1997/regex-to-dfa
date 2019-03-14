package domain;

public class DfaNode {

    private int id;
    private String data;
    private boolean isFinal;
    private boolean isStart;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    @Override
    public String toString() {
        String toReturn = data;
        if(isFinal) {
            toReturn = "FINAL_" + toReturn;
        }
        if(isStart) {
            toReturn = "START_" + toReturn;
        }
        return toReturn;
    }
}

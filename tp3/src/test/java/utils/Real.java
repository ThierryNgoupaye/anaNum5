package utils;

public class Real {
    public int value;
    public String error;
    
    public Pair(int value, String error) {
        this.value = value;
        this.error = error;
    }
    public int getVaue() {
        return value;
    }
    public String getError() {
        return error;
    }
}

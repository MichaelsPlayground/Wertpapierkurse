package de.androidcrypto.wertpapierkurse;

public class StockModel {

    String isin;
    String isinName;
    boolean active;
    String na;

    public StockModel(String isin, String isinName, boolean active, String na) {
        this.isin = isin;
        this.isinName = isinName;
        this.active = active;
        this.na = na;
    }

    public String getIsin() {
        return isin;
    }

    public String getIsinName() {
        return isinName;
    }

    public boolean getActive() {
        return active;
    }

    public String getNa() {
        return na;
    }
}
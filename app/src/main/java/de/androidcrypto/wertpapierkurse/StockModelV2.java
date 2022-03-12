package de.androidcrypto.wertpapierkurse;

public class StockModelV2 {

    String isin;
    String isinName;
    String symbolYahooApi;
    String symbolApi;
    boolean active;
    String group;

    public StockModelV2(String isin, String isinName, String symbolYahooApi, String symbolApi,  boolean active, String group) {
        this.isin = isin;
        this.isinName = isinName;
        this.symbolYahooApi = symbolYahooApi;
        this.symbolApi = symbolApi;
        this.active = active;
        this.group = group;
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

    public String getGroup() {
        return group;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }
}
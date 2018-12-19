package andreyskakunenko.exchangeratespbandnbu.Model;

public class PbResult {

    private String resultCcy;
    private String resultBuy;
    private String resultSale;

    public PbResult() {
    }

    public PbResult(String resultCcy, String resultBuy, String resultSale) {
        this.resultCcy = resultCcy;
        this.resultBuy = resultBuy;
        this.resultSale = resultSale;
    }

    public String getResultCcy() {
        return resultCcy;
    }

    public void setResultCcy(String resultCcy) {
        this.resultCcy = resultCcy;
    }

    public String getResultBuy() {
        return resultBuy;
    }

    public void setResultBuy(String resultBuy) {
        this.resultBuy = resultBuy;
    }

    public String getResultSale() {
        return resultSale;
    }

    public void setResultSale(String resultSale) {
        this.resultSale = resultSale;
    }
}

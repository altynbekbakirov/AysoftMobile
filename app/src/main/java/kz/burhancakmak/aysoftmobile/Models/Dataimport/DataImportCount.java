package kz.burhancakmak.aysoftmobile.Models.Dataimport;

public class DataImportCount {
    String dataName;
    Integer dataCount;

    public DataImportCount(String dataName, Integer dataCount) {
        this.dataName = dataName;
        this.dataCount = dataCount;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Integer getDataCount() {
        return dataCount;
    }

    public void setDataCount(Integer dataCount) {
        this.dataCount = dataCount;
    }
}

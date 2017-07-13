package com.billmartam.report;

import com.billmartam.expenditure.ExpenditureCalculator;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pp00344204 on 06/06/17.
 */
public class TransactionReport implements Serializable{
    private List<String> contents;

    public TransactionReport() {
    }

    public TransactionReport(List<String> contents) {
        this.contents = contents;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionReport that = (TransactionReport) o;

        return contents != null ? contents.equals(that.contents) : that.contents == null;
    }

    @Override
    public int hashCode() {
        return contents != null ? contents.hashCode() : 0;
    }

    @Override
    public String toString() {
        String result = "";
        for(String content : contents){
            result+=content+"\n";
        }
        return result.trim();
    }

    public double getTotal(){
        ExpenditureCalculator calculator = ExpenditureCalculator.getCalculator();
        return Math.floor(calculator.calculateTotalExpenditure(this));
    }

    public Set getKeys() {
        Set keys = new HashSet();
        for(String key : contents){
            keys.add(key);
        }
        return keys;
    }
}

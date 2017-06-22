package com.billmartam.transaction;

import com.billmartam.report.TransactionReport;
import com.billmartam.util.Util;

import java.util.*;

/**
 * Created by pp00344204 on 06/06/17.
 */
public class TransactionSearch {
    private TransactionReport transactionReport;

    @SuppressWarnings("unused")
    private TransactionSearch() {
    }

    private TransactionSearch(TransactionReport transactionReport) {
        this.transactionReport = transactionReport;
    }

    public Map<String, TransactionReport> getIndividualSearchTransaction(String searchTerm){
        String[] terms = getSearchTerms(searchTerm);
        return processSearchTerms(terms);
    }

    private Map<String, TransactionReport> processSearchTerms(String[] terms) {
        List<String> contents = transactionReport.getContents();
        Map<String, TransactionReport> reports = initReportSearchMap(terms);
        for(String content : contents){
            String term = findTermInContent(content, terms);
            if (contentHasSearchTerm(term)){
                reports.get(term).getContents().add(content);
            }
        }
        return reports;
    }

    private boolean contentHasSearchTerm(String term) {
        return term != null;
    }

    private Map<String, TransactionReport> initReportSearchMap(String[] terms) {
        Map<String, TransactionReport> reports = new HashMap<>();
        for(String term : terms){
            reports.put(term, new TransactionReport(new ArrayList<>()));
        }
        return reports;
    }

    public TransactionReport searchTransaction(String searchTerm) {
        if(transactionReport == null) {
            return null;
        }
        List<String> contents = transactionReport.getContents();
        if(contents.size() <= 0){
            return null;
        }
        String[] terms = getSearchTerms(searchTerm);
        List<String> searchResults = new ArrayList<>();
        for(String transaction : contents){
            if(contentHasSearchTerm(findTermInContent(transaction, terms))){
                searchResults.add(transaction);
            }
        }
        TransactionReport transactionReport = new TransactionReport();
        transactionReport.setContents(searchResults);
        return transactionReport;
    }

    private String[] getSearchTerms(String searchTerm) {
        searchTerm = searchTerm.toUpperCase();
        searchTerm = Util.replaceSpaceInStringWithComma(searchTerm);
        return Util.getCommaSplittedString(searchTerm, ",");
    }

    private String findTermInContent(String transaction, String[] terms) {
        for(String term : terms){
            if(Arrays.asList(Util.getCommaSplittedString(transaction, " ")).contains(term)){
                return term;
            }
        }
        return null;
    }

    public static TransactionSearch getSearchEngine(TransactionReport from) {
        return new TransactionSearch(from);
    }
}
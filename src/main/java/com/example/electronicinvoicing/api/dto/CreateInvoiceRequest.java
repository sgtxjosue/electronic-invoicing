package com.example.electronicinvoicing.api.dto;

import java.util.List;

public class CreateInvoiceRequest {

    private String documentType;
    private String currency;
    private String conditionSale;
    private String paymentMethod;

    private PartyRequest emitter;
    private PartyRequest receiver;
    private List<InvoiceLineRequest> lines;

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getConditionSale() {
        return conditionSale;
    }

    public void setConditionSale(String conditionSale) {
        this.conditionSale = conditionSale;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PartyRequest getEmitter() {
        return emitter;
    }

    public void setEmitter(PartyRequest emitter) {
        this.emitter = emitter;
    }

    public PartyRequest getReceiver() {
        return receiver;
    }

    public void setReceiver(PartyRequest receiver) {
        this.receiver = receiver;
    }

    public List<InvoiceLineRequest> getLines() {
        return lines;
    }

    public void setLines(List<InvoiceLineRequest> lines) {
        this.lines = lines;
    }
}


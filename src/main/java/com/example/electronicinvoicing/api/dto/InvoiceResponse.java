package com.example.electronicinvoicing.api.dto;

import com.example.electronicinvoicing.domain.HaciendaStatus;
import com.example.electronicinvoicing.domain.InvoiceStatus;
import java.math.BigDecimal;

public class InvoiceResponse {

    private Long id;
    private String clave;
    private String consecutivo;
    private InvoiceStatus status;
    private HaciendaStatus haciendaStatus;
    private String haciendaMessage;
    private BigDecimal totalAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public HaciendaStatus getHaciendaStatus() {
        return haciendaStatus;
    }

    public void setHaciendaStatus(HaciendaStatus haciendaStatus) {
        this.haciendaStatus = haciendaStatus;
    }

    public String getHaciendaMessage() {
        return haciendaMessage;
    }

    public void setHaciendaMessage(String haciendaMessage) {
        this.haciendaMessage = haciendaMessage;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}


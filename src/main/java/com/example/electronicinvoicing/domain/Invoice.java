package com.example.electronicinvoicing.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true)
    private String clave;

    @Column(length = 20)
    private String consecutivo;

    @Column(length = 4)
    private String documentType;

    private OffsetDateTime issueDateTime;

    @Column(length = 3)
    private String currency;

    @Column(length = 2)
    private String conditionSale;

    @Column(length = 2)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private HaciendaStatus haciendaStatus = HaciendaStatus.PENDING;

    @Column(length = 255)
    private String haciendaMessage;

    private BigDecimal totalAmount;
    private BigDecimal totalTax;
    private BigDecimal totalDiscount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emitter_id")
    private Party emitter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Party receiver;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceLine> lines = new ArrayList<>();

    public Long getId() {
        return id;
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

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public OffsetDateTime getIssueDateTime() {
        return issueDateTime;
    }

    public void setIssueDateTime(OffsetDateTime issueDateTime) {
        this.issueDateTime = issueDateTime;
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

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public Party getEmitter() {
        return emitter;
    }

    public void setEmitter(Party emitter) {
        this.emitter = emitter;
    }

    public Party getReceiver() {
        return receiver;
    }

    public void setReceiver(Party receiver) {
        this.receiver = receiver;
    }

    public List<InvoiceLine> getLines() {
        return lines;
    }

    public void setLines(List<InvoiceLine> lines) {
        this.lines = lines;
    }
}


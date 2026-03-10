package com.example.electronicinvoicing.service;

import com.example.electronicinvoicing.api.dto.CreateInvoiceRequest;
import com.example.electronicinvoicing.api.dto.InvoiceResponse;
import com.example.electronicinvoicing.api.dto.InvoiceLineRequest;
import com.example.electronicinvoicing.api.dto.PartyRequest;
import com.example.electronicinvoicing.domain.Invoice;
import com.example.electronicinvoicing.domain.InvoiceLine;
import com.example.electronicinvoicing.domain.InvoiceStatus;
import com.example.electronicinvoicing.domain.Party;
import com.example.electronicinvoicing.domain.PartyType;
import com.example.electronicinvoicing.repository.InvoiceRepository;
import com.example.electronicinvoicing.repository.PartyRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PartyRepository partyRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, PartyRepository partyRepository) {
        this.invoiceRepository = invoiceRepository;
        this.partyRepository = partyRepository;
    }

    @Transactional
    public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
        Party emitter = buildAndSaveParty(request.getEmitter(), PartyType.EMITTER);
        Party receiver = buildAndSaveParty(request.getReceiver(), PartyType.RECEIVER);

        Invoice invoice = new Invoice();
        invoice.setDocumentType(request.getDocumentType());
        invoice.setCurrency(request.getCurrency());
        invoice.setConditionSale(request.getConditionSale());
        invoice.setPaymentMethod(request.getPaymentMethod());
        invoice.setIssueDateTime(OffsetDateTime.now());
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setEmitter(emitter);
        invoice.setReceiver(receiver);

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        if (request.getLines() != null) {
            for (InvoiceLineRequest lineRequest : request.getLines()) {
                InvoiceLine line = new InvoiceLine();
                line.setInvoice(invoice);
                line.setCabysCode(lineRequest.getCabysCode());
                line.setDescription(lineRequest.getDescription());
                line.setQuantity(lineRequest.getQuantity());
                line.setUnitPrice(lineRequest.getUnitPrice());
                line.setDiscount(lineRequest.getDiscount());
                line.setTaxRate(lineRequest.getTaxRate());

                BigDecimal qty = lineRequest.getQuantity() != null ? lineRequest.getQuantity() : BigDecimal.ZERO;
                BigDecimal price = lineRequest.getUnitPrice() != null ? lineRequest.getUnitPrice() : BigDecimal.ZERO;
                BigDecimal discount = lineRequest.getDiscount() != null ? lineRequest.getDiscount() : BigDecimal.ZERO;
                BigDecimal subtotal = qty.multiply(price).subtract(discount);
                BigDecimal taxRate = lineRequest.getTaxRate() != null ? lineRequest.getTaxRate() : BigDecimal.ZERO;
                BigDecimal taxAmount = subtotal.multiply(taxRate).divide(BigDecimal.valueOf(100));

                line.setTaxAmount(taxAmount);
                line.setLineTotal(subtotal.add(taxAmount));

                invoice.getLines().add(line);

                totalAmount = totalAmount.add(line.getLineTotal());
                totalTax = totalTax.add(taxAmount);
                totalDiscount = totalDiscount.add(discount);
            }
        }

        invoice.setTotalAmount(totalAmount);
        invoice.setTotalTax(totalTax);
        invoice.setTotalDiscount(totalDiscount);

        Invoice saved = invoiceRepository.save(invoice);
        return toResponse(saved);
    }

    public InvoiceResponse getInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + id));
        return toResponse(invoice);
    }

    public List<InvoiceResponse> listInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Party buildAndSaveParty(PartyRequest dto, PartyType type) {
        Party party = new Party();
        party.setType(type);
        party.setIdentificationType(dto.getIdentificationType());
        party.setIdentificationNumber(dto.getIdentificationNumber());
        party.setName(dto.getName());
        party.setCommercialName(dto.getCommercialName());
        party.setEmail(dto.getEmail());
        party.setPhone(dto.getPhone());
        party.setProvince(dto.getProvince());
        party.setCanton(dto.getCanton());
        party.setDistrict(dto.getDistrict());
        party.setAddressLine(dto.getAddressLine());
        return partyRepository.save(party);
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        InvoiceResponse resp = new InvoiceResponse();
        resp.setId(invoice.getId());
        resp.setClave(invoice.getClave());
        resp.setConsecutivo(invoice.getConsecutivo());
        resp.setStatus(invoice.getStatus());
        resp.setHaciendaStatus(invoice.getHaciendaStatus());
        resp.setHaciendaMessage(invoice.getHaciendaMessage());
        resp.setTotalAmount(invoice.getTotalAmount());
        return resp;
    }
}


package com.example.electronicinvoicing.service;

import com.example.electronicinvoicing.domain.HaciendaStatus;
import com.example.electronicinvoicing.domain.Invoice;
import com.example.electronicinvoicing.domain.InvoiceStatus;
import com.example.electronicinvoicing.hacienda.HaciendaSubmissionService;
import com.example.electronicinvoicing.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceSubmissionService {

    private final InvoiceRepository invoiceRepository;
    private final HaciendaSubmissionService haciendaSubmissionService;

    public InvoiceSubmissionService(InvoiceRepository invoiceRepository,
                                    HaciendaSubmissionService haciendaSubmissionService) {
        this.invoiceRepository = invoiceRepository;
        this.haciendaSubmissionService = haciendaSubmissionService;
    }

    @Transactional
    public void submitInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

        invoice.setStatus(InvoiceStatus.SENT);
        invoice.setHaciendaStatus(HaciendaStatus.PENDING);

        haciendaSubmissionService.submitInvoice(invoice);
    }
}


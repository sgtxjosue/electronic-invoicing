package com.example.electronicinvoicing.api;

import com.example.electronicinvoicing.api.dto.CreateInvoiceRequest;
import com.example.electronicinvoicing.api.dto.InvoiceResponse;
import com.example.electronicinvoicing.service.InvoiceService;
import com.example.electronicinvoicing.service.InvoiceSubmissionService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceSubmissionService submissionService;

    public InvoiceController(InvoiceService invoiceService, InvoiceSubmissionService submissionService) {
        this.invoiceService = invoiceService;
        this.submissionService = submissionService;
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@RequestBody CreateInvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.createInvoice(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoice(id));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> list() {
        return ResponseEntity.ok(invoiceService.listInvoices());
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Void> submit(@PathVariable Long id) {
        submissionService.submitInvoice(id);
        return ResponseEntity.accepted().build();
    }
}


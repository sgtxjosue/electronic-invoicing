package com.example.electronicinvoicing.repository;

import com.example.electronicinvoicing.domain.Invoice;
import com.example.electronicinvoicing.domain.InvoiceStatus;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByStatus(InvoiceStatus status);

    List<Invoice> findByIssueDateTimeBetween(OffsetDateTime from, OffsetDateTime to);
}


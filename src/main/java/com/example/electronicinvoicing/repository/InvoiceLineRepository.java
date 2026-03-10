package com.example.electronicinvoicing.repository;

import com.example.electronicinvoicing.domain.InvoiceLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceLineRepository extends JpaRepository<InvoiceLine, Long> {
}


package com.example.electronicinvoicing.repository;

import com.example.electronicinvoicing.domain.HaciendaSubmission;
import com.example.electronicinvoicing.domain.Invoice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HaciendaSubmissionRepository extends JpaRepository<HaciendaSubmission, Long> {

    List<HaciendaSubmission> findByInvoice(Invoice invoice);
}


package com.example.electronicinvoicing.hacienda;

import com.example.electronicinvoicing.domain.HaciendaStatus;
import com.example.electronicinvoicing.domain.HaciendaSubmission;
import com.example.electronicinvoicing.domain.Invoice;
import com.example.electronicinvoicing.repository.HaciendaSubmissionRepository;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HaciendaSubmissionService {

    private static final Logger logger = LoggerFactory.getLogger(HaciendaSubmissionService.class);

    private final HaciendaAuthClient authClient;
    private final FacturaElectronicaXmlBuilder xmlBuilder;
    private final HaciendaSubmissionRepository submissionRepository;

    public HaciendaSubmissionService(
            HaciendaAuthClient authClient,
            FacturaElectronicaXmlBuilder xmlBuilder,
            HaciendaSubmissionRepository submissionRepository) {
        this.authClient = authClient;
        this.xmlBuilder = xmlBuilder;
        this.submissionRepository = submissionRepository;
    }

    public HaciendaSubmission submitInvoice(Invoice invoice) {
        String token = authClient.getAccessToken();
        String xml = xmlBuilder.buildXml(invoice);

        logger.info("Submitting invoice id={} to Hacienda with token={} and xml length={}",
                invoice.getId(), token != null ? "present" : "missing", xml.length());

        HaciendaSubmission submission = new HaciendaSubmission();
        submission.setInvoice(invoice);
        submission.setSubmissionDate(OffsetDateTime.now());
        submission.setStatus(HaciendaStatus.PENDING.name());
        submission.setRawResponse("STUB: submission not yet implemented");

        return submissionRepository.save(submission);
    }
}


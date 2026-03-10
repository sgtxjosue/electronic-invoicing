package com.example.electronicinvoicing.service;

import com.example.electronicinvoicing.api.dto.CreateInvoiceRequest;
import com.example.electronicinvoicing.api.dto.InvoiceLineRequest;
import com.example.electronicinvoicing.api.dto.PartyRequest;
import com.example.electronicinvoicing.repository.InvoiceRepository;
import com.example.electronicinvoicing.repository.PartyRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(InvoiceService.class)
class InvoiceServiceTest {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PartyRepository partyRepository;

    @Test
    void createInvoice_persistsInvoiceAndParties() {
        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setDocumentType("01");
        request.setCurrency("CRC");
        request.setConditionSale("01");
        request.setPaymentMethod("01");

        PartyRequest emitter = new PartyRequest();
        emitter.setIdentificationType("01");
        emitter.setIdentificationNumber("123456789");
        emitter.setName("Emisor SA");
        request.setEmitter(emitter);

        PartyRequest receiver = new PartyRequest();
        receiver.setIdentificationType("01");
        receiver.setIdentificationNumber("987654321");
        receiver.setName("Receptor SA");
        request.setReceiver(receiver);

        InvoiceLineRequest line = new InvoiceLineRequest();
        line.setDescription("Servicio");
        line.setQuantity(BigDecimal.ONE);
        line.setUnitPrice(BigDecimal.TEN);
        line.setTaxRate(BigDecimal.valueOf(13));
        request.setLines(List.of(line));

        var response = invoiceService.createInvoice(request);

        assertThat(response.getId()).isNotNull();
        assertThat(invoiceRepository.findAll()).hasSize(1);
        assertThat(partyRepository.findAll()).hasSize(2);
    }
}


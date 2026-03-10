package com.example.electronicinvoicing.hacienda;

import com.example.electronicinvoicing.domain.Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FacturaElectronicaXmlBuilder {

    private static final Logger logger = LoggerFactory.getLogger(FacturaElectronicaXmlBuilder.class);

    public String buildXml(Invoice invoice) {
        logger.info("Building XML for invoice id={}", invoice.getId());
        // Backbone only: return a minimal placeholder XML; later align with v4.4 XSD.
        return "<FacturaElectronica>" +
                "<Clave>" + (invoice.getClave() != null ? invoice.getClave() : "") + "</Clave>" +
                "<NumeroConsecutivo>" + (invoice.getConsecutivo() != null ? invoice.getConsecutivo() : "") + "</NumeroConsecutivo>" +
                "</FacturaElectronica>";
    }
}


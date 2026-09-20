package com.org.bgv.policy.service;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import com.org.bgv.common.UserDto;
import com.org.bgv.policy.entity.PolicyConsent;
import com.org.bgv.policy.enums.EntityType;
import com.org.bgv.policy.pdf.PdfApplicantSection;
import com.org.bgv.policy.pdf.PdfFooterHandler;
import com.org.bgv.policy.pdf.PdfPolicySection;
import com.org.bgv.policy.pdf.PdfSignatureSection;
import com.org.bgv.s3.S3StorageService;
import com.org.bgv.service.util.UserServiceUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.util.Pair;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyConsentPdfServiceImpl implements PolicyConsentPdfService {

    private final PdfApplicantSection applicantSection;
    private final PdfFooterHandler footerHandler;
    private final PdfPolicySection policySection;
    private final PdfSignatureSection signatureSection;
    private final UserServiceUtil userServiceUtil;
    private final S3StorageService s3StorageService;

    @Override
    public Pair<String, String> generateConsentPdf(PolicyConsent consent) {

        try {

            UserDto dto = null;

            if (consent.getEntityType() != null
                    && consent.getEntityType().equals(EntityType.USER)) {
                dto = userServiceUtil.getUserDetails(consent.getEntityId());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            document.setMargins(30, 30, 30, 30);

            // PAGE 1
            addHeader(document, consent);

            applicantSection.buildApplicantSection(
                    document,
                    consent,
                    dto,
                    consent.getLivePhotoS3Key()
            );

            document.add(signatureSection.createSignatureSection(
                    consent.getSignatureS3Key(),
                    consent.getAcceptedAt() != null ? consent.getAcceptedAt().toString() : null
            ));

            document.add(footerHandler.createFooter(
                    consent.getReferenceNumber(),
                    consent.getPolicyVersion().getPolicy().getName(),
                    consent.getPolicyVersion().getVersion(),
                    consent.getAcceptedAt() != null ? consent.getAcceptedAt().toString() : null,
                    1,
                    2
            ));

            // PAGE 2
            pdf.addNewPage();

            addPolicyHeader(document);

            policySection.createPolicySection(
                    document,
                    consent.getPolicyVersion().getContent()
            );

            document.add(footerHandler.createFooter(
                    consent.getReferenceNumber(),
                    consent.getPolicyVersion().getPolicy().getName(),
                    consent.getPolicyVersion().getVersion(),
                    consent.getAcceptedAt() != null ? consent.getAcceptedAt().toString() : null,
                    2,
                    2
            ));

            document.close();

            // =========================
            // UPLOAD PDF TO S3 (FIX)
            // =========================
            byte[] pdfBytes = outputStream.toByteArray();

            InputStream inputStream = new ByteArrayInputStream(pdfBytes);

            File tempFile = File.createTempFile("consent-", ".pdf");

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(pdfBytes);
            }

            Pair<String, String> upload =
                    s3StorageService.uploadFile(tempFile, "policy/pdf");

            log.info("Consent PDF uploaded: {}", upload.getFirst());

            return upload;

        } catch (Exception ex) {

            log.error("Error generating consent PDF", ex);
            throw new RuntimeException("PDF generation failed", ex);
        }
    }

    private void addHeader(Document document, PolicyConsent consent) {

        document.add(new com.itextpdf.layout.element.Paragraph("VERIFICATION CONSENT FORM")
                .setBold()
                .setFontSize(16));

        document.add(new com.itextpdf.layout.element.Paragraph(
                "Reference No: " + consent.getReferenceNumber()
        ).setFontSize(10));
    }

    private void addPolicyHeader(Document document) {

        document.add(new com.itextpdf.layout.element.Paragraph("POLICY DETAILS")
                .setBold()
                .setFontSize(14)
                .setMarginBottom(10));
    }
}
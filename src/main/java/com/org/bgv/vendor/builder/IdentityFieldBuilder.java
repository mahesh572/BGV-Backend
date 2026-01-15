package com.org.bgv.vendor.builder;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.org.bgv.candidate.entity.IdentityProof;
import com.org.bgv.vendor.dto.ObjectFieldDTO;

@Component
public class IdentityFieldBuilder implements ObjectFieldBuilder<IdentityProof> {

	@Override
    public List<ObjectFieldDTO> buildFields(IdentityProof identity) {

        List<ObjectFieldDTO> fields = new ArrayList();

        fields.add(FieldsUtil.text(
                "DOCUMENT_NUMBER",
                "Document Number",
                identity.getDocumentNumber(),
                true
        ));

        fields.add(FieldsUtil.date(
                "ISSUE_DATE",
                "Issue Date",
                identity.getIssueDate(),
                false
        ));

        fields.add(FieldsUtil.date(
                "EXPIRY_DATE",
                "Expiry Date",
                identity.getExpiryDate(),
                true
        ));

        fields.add(FieldsUtil.bool(
                "EXPIRED",
                "Is Expired",
                identity.isExpired(),
                false
                
        ));
/*
        fields.add(FieldsUtil.number(
                "DAYS_TO_EXPIRY",
                "Days Until Expiry",
                identity.getDaysUntilExpiry(),
                false,
                true
        ));
*/
        return fields;
    }
}

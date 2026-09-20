package com.org.bgv.user.kyc.response;

import com.org.bgv.constants.DocumentType;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DigilockerDocumentResponse {
	 
	private DocumentType documentType;

	    private String providerReference;

	    /**
	     * Provider-specific document JSON.
	     * Can later be mapped to AadhaarDetails, PanDetails, PassportDetails, etc.
	     */
	    private Object document;

	    /**
	     * Complete provider response for auditing/debugging.
	     */
	    private Object rawResponse;
	    
	    private String documentNumber;

	    private String holderName;
}

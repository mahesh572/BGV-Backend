   ┌──────────────┐
                │ Action API   │
                └──────┬───────┘
                       ↓
               ActionCreatedEvent
                       ↓
        ┌──────────────┼──────────────┐
        ↓              ↓              ↓
 DocumentUpdater   CheckUpdater   NotificationService
        ↓              ↓              ↓
 DocumentUpdated   CheckStatus   Email / Push
      Event          Event
           ↓
     SyncService

     
     
     {
    "success": true,
    "message": "Verification check retrieved successfully",
    "data": {
        "actions": [
            {
                "code": "VERIFY",
                "label": "VERIFY",
                "level": "CHECK",
                "requiresReason": false,
                "requiresRemarks": false,
                "requiresEvidence": false,
                "enabled": true
            },
            {
                "code": "REQUEST_INFO",
                "label": "REQUEST INFO",
                "level": "CHECK",
                "requiresReason": false,
                "requiresRemarks": false,
                "requiresEvidence": false,
                "enabled": true
            },
            {
                "code": "REJECT",
                "label": "REJECT",
                "level": "CHECK",
                "requiresReason": false,
                "requiresRemarks": false,
                "requiresEvidence": false,
                "enabled": true
            },
            {
                "code": "INSUFFICIENT",
                "label": "INSUFFICIENT",
                "level": "CHECK",
                "requiresReason": false,
                "requiresRemarks": false,
                "requiresEvidence": false,
                "enabled": true
            }
        ],
        "sendNotification": false,
        "caseId": "121",
        "caseRef": "CASE-121",
        "checkId": "284",
        "checkRef": "CHECK-2026-000240",
        "checkType": "WORK_EXPERIENCE",
        "checkName": "Work Experience",
        "status": "PENDING_AGENT_ASSIGNMENT",
        "candidate": {
            "name": "Profile Pending",
            "email": null,
            "phone": null,
            "candidateId": "15",
            "candidateRef": "CAND-2026-000008"
        },
        "objects": [
            {
                "status": "PENDING",
                "fields": [
                    {
                        "comparisonId": 219,
                        "fieldName": "companyName",
                        "displayName": "Company Name",
                        "candidateValue": "sdjkdsf",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 220,
                        "fieldName": "position",
                        "displayName": "Designation",
                        "candidateValue": "f",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 221,
                        "fieldName": "employeeId",
                        "displayName": "Employee Id",
                        "candidateValue": "1234",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 222,
                        "fieldName": "startDate",
                        "displayName": "Start Date",
                        "candidateValue": "2026-06-01",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 223,
                        "fieldName": "endDate",
                        "displayName": "End Date",
                        "candidateValue": "2026-06-02",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 224,
                        "fieldName": "employmentType",
                        "displayName": "Employment Type",
                        "candidateValue": "Full-time",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 225,
                        "fieldName": "address",
                        "displayName": "Address",
                        "candidateValue": "pathipaka",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 226,
                        "fieldName": "city",
                        "displayName": "City",
                        "candidateValue": "Warangal",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 227,
                        "fieldName": "state",
                        "displayName": "State",
                        "candidateValue": "Telangana",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 228,
                        "fieldName": "country",
                        "displayName": "Country",
                        "candidateValue": "India",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    }
                ],
                "fieldSatusOptions": [
                    {
                        "value": "PENDING",
                        "label": "Pending",
                        "color": "warning"
                    },
                    {
                        "value": "MATCH",
                        "label": "Match",
                        "color": "success"
                    },
                    {
                        "value": "MISMATCH",
                        "label": "Mismatch",
                        "color": "error"
                    },
                    {
                        "value": "NOT_AVAILABLE",
                        "label": "Not Available",
                        "color": "default"
                    },
                    {
                        "value": "NOT_APPLICABLE",
                        "label": "Not Applicable",
                        "color": "default"
                    },
                    {
                        "value": "MANUAL_REVIEW",
                        "label": "Manual Review",
                        "color": "info"
                    }
                ],
                "objectId": 55,
                "objectType": "WORK_EXPERIENCE",
                "displayName": "sdjkdsf",
                "documentTypes": [
                    {
                        "actions": [
                            {
                                "code": "VIEW",
                                "label": "VIEW",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            },
                            {
                                "code": "DOWNLOAD",
                                "label": "DOWNLOAD",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            },
                            {
                                "code": "VERIFY",
                                "label": "VERIFY",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            },
                            {
                                "code": "REQUEST_INFO",
                                "label": "REQUEST INFO",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            },
                            {
                                "code": "REJECT",
                                "label": "REJECT",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            },
                            {
                                "code": "INSUFFICIENT",
                                "label": "INSUFFICIENT",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            }
                        ],
                        "documentTypeId": "10",
                        "type": "Experience Letter",
                        "status": "PENDING",
                        "files": [
                            {
                                "docId": 270,
                                "fileId": 270,
                                "fileName": "A059TZ5 F2 2505PA Workprints.pdf",
                                "fileUrl": "https://bgv-doc-mahesh1.s3.ap-south-1.amazonaws.com/Work Experience/51525df7-18ab-448a-9ea1-86a3332188ea_A059TZ5 F2 2505PA Workprints.pdf",
                                "fileSize": 5173933,
                                "status": "UPLOADED",
                                "fileType": null,
                                "thumbnailUrl": null,
                                "fileKey": "Work Experience/51525df7-18ab-448a-9ea1-86a3332188ea_A059TZ5 F2 2505PA Workprints.pdf",
                                "uploadedBy": "",
                                "uploadedAt": "2026-06-22T19:20:08Z",
                                "actions": [
                                    {
                                        "code": "VIEW",
                                        "label": "VIEW",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    },
                                    {
                                        "code": "DOWNLOAD",
                                        "label": "DOWNLOAD",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    },
                                    {
                                        "code": "VERIFY",
                                        "label": "VERIFY",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    },
                                    {
                                        "code": "REQUEST_INFO",
                                        "label": "REQUEST INFO",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    },
                                    {
                                        "code": "REJECT",
                                        "label": "REJECT",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    },
                                    {
                                        "code": "INSUFFICIENT",
                                        "label": "INSUFFICIENT",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    }
                                ],
                                "size": null,
                                "mimeType": null,
                                "verificationNotes": null,
                                "comments": null,
                                "verified": false,
                                "verifiedAt": null,
                                "verifiedBy": null,
                                "createdAt": "2026-06-22T19:20:08.122381",
                                "updatedAt": "2026-06-22T19:20:44.27382",
                                "isAddOn": null,
                                "required": null,
                                "documentPrice": null,
                                "verificationStatus": null
                            }
                        ]
                    }
                ]
            },
            {
                "status": "IN_PROGRESS",
                "fields": [
                    {
                        "comparisonId": 209,
                        "fieldName": "companyName",
                        "displayName": "Company Name",
                        "candidateValue": "fgg",
                        "sourceValue": null,
                        "result": "MATCH",
                        "verified": true,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 210,
                        "fieldName": "position",
                        "displayName": "Designation",
                        "candidateValue": "dsds",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 211,
                        "fieldName": "employeeId",
                        "displayName": "Employee Id",
                        "candidateValue": "1234",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 212,
                        "fieldName": "startDate",
                        "displayName": "Start Date",
                        "candidateValue": "2026-06-01",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 213,
                        "fieldName": "endDate",
                        "displayName": "End Date",
                        "candidateValue": "2026-06-03",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 214,
                        "fieldName": "employmentType",
                        "displayName": "Employment Type",
                        "candidateValue": "Full-time",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 215,
                        "fieldName": "address",
                        "displayName": "Address",
                        "candidateValue": "",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 216,
                        "fieldName": "city",
                        "displayName": "City",
                        "candidateValue": "",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 217,
                        "fieldName": "state",
                        "displayName": "State",
                        "candidateValue": "",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    },
                    {
                        "comparisonId": 218,
                        "fieldName": "country",
                        "displayName": "Country",
                        "candidateValue": "India",
                        "sourceValue": null,
                        "result": "PENDING",
                        "verified": false,
                        "remarks": null,
                        "status": null
                    }
                ],
                "fieldSatusOptions": [
                    {
                        "value": "PENDING",
                        "label": "Pending",
                        "color": "warning"
                    },
                    {
                        "value": "MATCH",
                        "label": "Match",
                        "color": "success"
                    },
                    {
                        "value": "MISMATCH",
                        "label": "Mismatch",
                        "color": "error"
                    },
                    {
                        "value": "NOT_AVAILABLE",
                        "label": "Not Available",
                        "color": "default"
                    },
                    {
                        "value": "NOT_APPLICABLE",
                        "label": "Not Applicable",
                        "color": "default"
                    },
                    {
                        "value": "MANUAL_REVIEW",
                        "label": "Manual Review",
                        "color": "info"
                    }
                ],
                "objectId": 56,
                "objectType": "WORK_EXPERIENCE",
                "displayName": "fgg",
                "documentTypes": [
                    {
                        "actions": [
                            {
                                "code": "VIEW",
                                "label": "VIEW",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            },
                            {
                                "code": "DOWNLOAD",
                                "label": "DOWNLOAD",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            },
                            {
                                "code": "VERIFY",
                                "label": "VERIFY",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            },
                            {
                                "code": "REQUEST_INFO",
                                "label": "REQUEST INFO",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            },
                            {
                                "code": "REJECT",
                                "label": "REJECT",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            },
                            {
                                "code": "INSUFFICIENT",
                                "label": "INSUFFICIENT",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            }
                        ],
                        "documentTypeId": "10",
                        "type": "Experience Letter",
                        "status": "PENDING",
                        "files": [
                            {
                                "docId": 271,
                                "fileId": 271,
                                "fileName": "A059TZ5 2505PA F2 WPS.pdf",
                                "fileUrl": "https://bgv-doc-mahesh1.s3.ap-south-1.amazonaws.com/Work Experience/058d39e1-8924-4a84-9869-061357272286_A059TZ5 2505PA F2 WPS.pdf",
                                "fileSize": 3999677,
                                "status": "UPLOADED",
                                "fileType": null,
                                "thumbnailUrl": null,
                                "fileKey": "Work Experience/058d39e1-8924-4a84-9869-061357272286_A059TZ5 2505PA F2 WPS.pdf",
                                "uploadedBy": "",
                                "uploadedAt": "2026-06-22T19:20:30Z",
                                "actions": [
                                    {
                                        "code": "VIEW",
                                        "label": "VIEW",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    },
                                    {
                                        "code": "DOWNLOAD",
                                        "label": "DOWNLOAD",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    },
                                    {
                                        "code": "VERIFY",
                                        "label": "VERIFY",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    },
                                    {
                                        "code": "REQUEST_INFO",
                                        "label": "REQUEST INFO",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    },
                                    {
                                        "code": "REJECT",
                                        "label": "REJECT",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    },
                                    {
                                        "code": "INSUFFICIENT",
                                        "label": "INSUFFICIENT",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    }
                                ],
                                "size": null,
                                "mimeType": null,
                                "verificationNotes": null,
                                "comments": null,
                                "verified": false,
                                "verifiedAt": null,
                                "verifiedBy": null,
                                "createdAt": "2026-06-22T19:20:30.286159",
                                "updatedAt": "2026-06-22T19:20:44.218243",
                                "isAddOn": null,
                                "required": null,
                                "documentPrice": null,
                                "verificationStatus": null
                            }
                        ]
                    },
                    {
                        "actions": [
                            {
                                "code": "VIEW",
                                "label": "VIEW",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            },
                            {
                                "code": "DOWNLOAD",
                                "label": "DOWNLOAD",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            },
                            {
                                "code": "VERIFY",
                                "label": "VERIFY",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            },
                            {
                                "code": "REQUEST_INFO",
                                "label": "REQUEST INFO",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            },
                            {
                                "code": "REJECT",
                                "label": "REJECT",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            },
                            {
                                "code": "INSUFFICIENT",
                                "label": "INSUFFICIENT",
                                "level": "DOCUMENT",
                                "requiresReason": false,
                                "requiresRemarks": false,
                                "requiresEvidence": false,
                                "enabled": true
                            }
                        ],
                        "documentTypeId": "12",
                        "type": "Relieving Letter",
                        "status": "PENDING",
                        "files": [
                            {
                                "docId": 272,
                                "fileId": 272,
                                "fileName": "A059TZ5 2505PA F2 WPS.pdf",
                                "fileUrl": "https://bgv-doc-mahesh1.s3.ap-south-1.amazonaws.com/Work Experience/23a9b220-3b3f-47dc-b0cb-73b9d89acacc_A059TZ5 2505PA F2 WPS.pdf",
                                "fileSize": 3999677,
                                "status": "UPLOADED",
                                "fileType": null,
                                "thumbnailUrl": null,
                                "fileKey": "Work Experience/23a9b220-3b3f-47dc-b0cb-73b9d89acacc_A059TZ5 2505PA F2 WPS.pdf",
                                "uploadedBy": "",
                                "uploadedAt": "2026-06-22T19:20:36Z",
                                "actions": [
                                    {
                                        "code": "VIEW",
                                        "label": "VIEW",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    },
                                    {
                                        "code": "DOWNLOAD",
                                        "label": "DOWNLOAD",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    },
                                    {
                                        "code": "VERIFY",
                                        "label": "VERIFY",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    },
                                    {
                                        "code": "REQUEST_INFO",
                                        "label": "REQUEST INFO",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    },
                                    {
                                        "code": "REJECT",
                                        "label": "REJECT",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    },
                                    {
                                        "code": "INSUFFICIENT",
                                        "label": "INSUFFICIENT",
                                        "level": "DOCUMENT",
                                        "requiresReason": false,
                                        "requiresRemarks": false,
                                        "requiresEvidence": false,
                                        "enabled": true
                                    }
                                ],
                                "size": null,
                                "mimeType": null,
                                "verificationNotes": null,
                                "comments": null,
                                "verified": false,
                                "verifiedAt": null,
                                "verifiedBy": null,
                                "createdAt": "2026-06-22T19:20:36.277038",
                                "updatedAt": "2026-06-22T19:20:44.218243",
                                "isAddOn": null,
                                "required": null,
                                "documentPrice": null,
                                "verificationStatus": null
                            }
                        ]
                    }
                ]
            }
        ],
        "documentTypeInfos": [],
        "evidenceTypeList": [
            {
                "id": 1,
                "name": "Manual Verification",
                "value": "MANUAL_VERIFICATION"
            },
            {
                "id": 2,
                "name": "Discrepancy Proof",
                "value": "DISCREPANCY_PROOF"
            }
        ]
    },
    "statusCode": 200
}
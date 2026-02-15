package com.org.bgv.candidate.dto;

import java.util.List;

import com.org.bgv.common.DocumentStatus;
import com.org.bgv.vendor.action.dto.ActionDTO;
import com.org.bgv.vendor.dto.ActionLevel;
import com.org.bgv.vendor.dto.ActionType;

public class CandidateActionCatalog {

    public static List<ActionDTO> documentActions(
            DocumentStatus status
    ) {

        boolean canReupload =
                status == DocumentStatus.REQUEST_INFO ||
                status == DocumentStatus.INSUFFICIENT;

        return List.of(
                base(ActionType.VIEW, ActionLevel.DOCUMENT, true),
                base(ActionType.DOWNLOAD, ActionLevel.DOCUMENT, true),
                base(ActionType.RE_UPLOAD, ActionLevel.DOCUMENT, canReupload)
        );
    }

    private static ActionDTO base(
            ActionType type,
            ActionLevel level,
            boolean enabled
    ) {
        return ActionDTO.builder()
                .code(type)
                .label(type.name().replace("_", " "))
                .level(level)
                .enabled(enabled)
                .build();
    }
}

package com.org.bgv.candidate.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.bgv.common.DocumentStatus;
import com.org.bgv.constants.SectionStatus;
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
    
    
    public static List<ActionDTO> getBaseActions() {
        return List.of(
                ActionDTO.builder()
                        .code(ActionType.VIEW)
                        .label("View")
                        .level(ActionLevel.SECTION)
                        .build(),

                ActionDTO.builder()
                        .code(ActionType.EDIT)
                        .label("Edit")
                        .level(ActionLevel.SECTION)
                        .build(),

                ActionDTO.builder()
                        .code(ActionType.DELETE)
                        .label("Delete")
                        .level(ActionLevel.SECTION)
                        .build()
        );
    }
    
    
    public static final Map<SectionStatus, Set<ActionType>> STATUS_ACTION_MAP = Map.of(
            SectionStatus.SUBMITTED, Set.of(ActionType.VIEW),
            SectionStatus.ACTION_REQUIRED, Set.of(ActionType.VIEW, ActionType.EDIT),
            SectionStatus.IN_PROGRESS, Set.of(ActionType.VIEW, ActionType.EDIT, ActionType.DELETE),
            SectionStatus.PENDING, Set.of(ActionType.VIEW, ActionType.EDIT),
            SectionStatus.VERIFIED, Set.of(ActionType.VIEW),
            SectionStatus.FAILED, Set.of(ActionType.VIEW)
    );
    
}

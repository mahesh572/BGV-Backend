package com.org.bgv.vendor.action.dto;

import java.util.List;

import com.org.bgv.vendor.dto.ActionLevel;
import com.org.bgv.vendor.dto.ActionType;

public class VendorActionCatalog {

    public static List<ActionDTO> checkActions() {
        return List.of(
          //  base(ActionType.VIEW, ActionLevel.SECTION),
          //  base(ActionType.DOWNLOAD, ActionLevel.CHECK),
            base(ActionType.VERIFY, ActionLevel.CHECK),
            base(ActionType.REQUEST_INFO, ActionLevel.CHECK),
            base(ActionType.REJECT, ActionLevel.CHECK),
            base(ActionType.INSUFFICIENT, ActionLevel.CHECK)
            
        );
    }

    public static List<ActionDTO> objectActions() {
        return List.of(
           // base(ActionType.VIEW, ActionLevel.OBJECT),
            base(ActionType.VERIFY, ActionLevel.OBJECT),
            base(ActionType.REQUEST_INFO, ActionLevel.OBJECT),
            base(ActionType.REJECT, ActionLevel.OBJECT),
            base(ActionType.INSUFFICIENT, ActionLevel.OBJECT)
        );
    }

    public static List<ActionDTO> documentActions() {
        return List.of(
            base(ActionType.VIEW, ActionLevel.DOCUMENT),
            base(ActionType.DOWNLOAD, ActionLevel.DOCUMENT),
            base(ActionType.VERIFY, ActionLevel.DOCUMENT),
            base(ActionType.REQUEST_INFO, ActionLevel.DOCUMENT),
            base(ActionType.REJECT, ActionLevel.DOCUMENT),
            base(ActionType.INSUFFICIENT, ActionLevel.DOCUMENT)
        );
    }

    private static ActionDTO base(ActionType type, ActionLevel level) {
        return ActionDTO.builder()
                .code(type)
                .label(type.name().replace("_", " "))
                .level(level)
                .enabled(true)   // 🔒 default
                .build();
    }
}


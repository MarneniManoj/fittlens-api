package com.fittlens.core.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class CreateEquipmentRequest {
    private String name;
    private String imageIcon;
    private String gymId;


}
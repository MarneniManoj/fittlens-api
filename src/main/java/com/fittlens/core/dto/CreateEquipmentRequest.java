package com.fittlens.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEquipmentRequest {
    private String name;
    private String imageIcon;
    private String gymId;


}
package com.hrm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DuyetDonRequest {
    @NotNull(message = "Phai chon duyet hoac tu choi")
    private Boolean duyet;
    private String phanHoi;
}

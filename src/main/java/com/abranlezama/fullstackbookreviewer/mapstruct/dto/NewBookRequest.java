package com.abranlezama.fullstackbookreviewer.mapstruct.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewBookRequest {
    @Size(min = 13, max = 13)
    private String isbn;
}

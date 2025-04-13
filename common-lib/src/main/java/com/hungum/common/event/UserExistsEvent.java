package com.hungum.common.event;

import com.hungum.common.dto.RegisterRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserExistsEvent {
    private RegisterRequestDto registerRequestDto;
}

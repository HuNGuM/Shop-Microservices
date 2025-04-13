package com.hungum.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemoveFromCartEvent {
    private String username;
    private String productName;
}

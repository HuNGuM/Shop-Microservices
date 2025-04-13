// UserDetailsRequestEvent.java
package com.hungum.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDetailsRequestEvent {
    private String username;
}

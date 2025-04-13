package com.hungum.common.event;

import com.hungum.common.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseEvent {
    private String username;
    private String email;
    private boolean enabled;

    public UserResponseEvent(UserDto userDto) {
        this.username = userDto.getUsername();
        this.email = userDto.getEmail();
        this.enabled = userDto.isEnabled();
    }
}

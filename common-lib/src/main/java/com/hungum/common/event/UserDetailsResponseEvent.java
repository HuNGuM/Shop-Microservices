// UserDetailsResponseEvent.java
package com.hungum.common.event;

import com.hungum.common.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDetailsResponseEvent {
    private UserDto userDto;  // Объект с данными пользователя
}

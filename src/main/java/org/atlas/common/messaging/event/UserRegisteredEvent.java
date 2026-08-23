package org.atlas.common.messaging.event;


import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor

public class UserRegisteredEvent{

        String email;
        String token;

}
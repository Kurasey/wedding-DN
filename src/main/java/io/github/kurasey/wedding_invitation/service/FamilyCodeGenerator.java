package io.github.kurasey.wedding_invitation.service;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FamilyCodeGenerator {

    public String nextUniqueCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

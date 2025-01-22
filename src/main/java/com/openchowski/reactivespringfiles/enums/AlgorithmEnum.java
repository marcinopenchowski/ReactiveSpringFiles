package com.openchowski.reactivespringfiles.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlgorithmEnum {
    SHA256("SHA-256"),
    ;

    private final String name;
}

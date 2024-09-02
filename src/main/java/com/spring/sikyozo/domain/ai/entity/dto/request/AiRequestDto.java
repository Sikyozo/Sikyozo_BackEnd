package com.spring.sikyozo.domain.ai.entity.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AiRequestDto {

    private List<Content> contents = new ArrayList<>();

    @Getter
    @Setter
    public static class Content {
        private List<Part> parts = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Part {
        private String text;
    }
}

package ru.yandex.practicum.interaction.dto_store;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SortObject {
    private String direction;
    private String nullHandling;
    private Boolean ascending;
    private String property;
    private Boolean ignoreCase;
}

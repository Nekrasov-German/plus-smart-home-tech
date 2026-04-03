package ru.yandex.practicum.interaction.dto_store;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageableObject {
    private Long offset;
    private SortObject sort;
    private Boolean unpaged;
    private Boolean paged;
    private Integer pageNumber;
    private Integer pageSize;
}

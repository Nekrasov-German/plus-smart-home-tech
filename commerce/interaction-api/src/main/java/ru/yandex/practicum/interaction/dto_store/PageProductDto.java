package ru.yandex.practicum.interaction.dto_store;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageProductDto {
    private Integer totalElements;
    private Integer totalPages;
    private Boolean first;
    private Boolean last;
    private Integer size;
    private List<ProductDto> content;
    private Integer number;
    private List<SortObject> sort;
    private Integer numberOfElements;
    private PageableObject pageable;
    private Boolean empty;
}

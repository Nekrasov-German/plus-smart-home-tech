package ru.yandex.practicum.model.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import ru.yandex.practicum.interaction.dto_store.*;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.model.ProductCategoryEnum;
import ru.yandex.practicum.model.ProductStateEnum;
import ru.yandex.practicum.model.QuantityStateEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ProductMapper {
    public Product productDtoToProduct(ProductDto productDto) {

        return Product.builder()
                .productName(productDto.getProductName())
                .productState(ProductStateEnum.valueOf(productDto.getProductState().toString()))
                .productCategory(ProductCategoryEnum.valueOf(productDto.getProductCategory().toString()))
                .price(productDto.getPrice())
                .description(productDto.getDescription())
                .quantityState(QuantityStateEnum.valueOf(productDto.getQuantityState().toString()))
                .productId(productDto.getProductId())
                .imageSrc(productDto.getImageSrc())
                .build();
    }

    public ProductDto productToProductDto(Product product) {
        return ProductDto.builder()
                .productName(product.getProductName())
                .productState(ProductState.valueOf(product.getProductState().toString()))
                .productCategory(ProductCategory.valueOf(product.getProductCategory().toString()))
                .price(product.getPrice())
                .description(product.getDescription())
                .quantityState(QuantityState.valueOf(product.getQuantityState().toString()))
                .productId(product.getProductId())
                .imageSrc(product.getImageSrc())
                .build();
    }

    public PageProductDto convertToPageProductDto(Page<Product> page) {
        List<ProductDto> content = page.getContent().stream()
                .map(ProductMapper::productToProductDto)
                .collect(Collectors.toList());

        // Создаём SortObject из Sort
        SortObject sortObject = null;
        if (page.getSort().isSorted()) {
            Sort.Order order = page.getSort().toList().get(0);
            sortObject = SortObject.builder()
                    .direction(order.getDirection().name())
                    .property(order.getProperty())
                    .ascending(order.isAscending())
                    .build();
        }

        List<SortObject> result = new ArrayList<>();
        result.add(sortObject);

        // Создаём PageableObject
        PageableObject pageableObject = PageableObject.builder()
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .offset(((long) page.getNumber() * page.getSize()))
                .sort(sortObject)
                .paged(true)
                .unpaged(false)
                .build();

        return PageProductDto.builder()
                .content(content)
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .numberOfElements(page.getNumberOfElements())
                .empty(page.isEmpty())
                .sort(result)
                .pageable(pageableObject)
                .build();
    }
}

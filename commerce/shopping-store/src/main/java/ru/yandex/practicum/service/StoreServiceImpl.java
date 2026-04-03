package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dal.ProductRepository;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.interaction.dto_store.ProductDto;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.model.ProductCategoryEnum;
import ru.yandex.practicum.model.ProductStateEnum;
import ru.yandex.practicum.model.QuantityStateEnum;
import ru.yandex.practicum.model.mapper.ProductMapper;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final ProductRepository repository;

    @Override
    public Page<Product> getPage(String category, Integer page, Integer size, List<String> sort) {
        Pageable pageable = createPageable(page, size, sort);
        log.info("CATEGORY: " + category + " PAGE: " + page + " SIZE: " + size + " SORT: " + sort);

        Specification<Product> spec = Specification.where(null);

        if (category != null) {
            try {
                ProductCategoryEnum categoryEnum = ProductCategoryEnum.valueOf(category.toUpperCase());
                spec = spec.and(hasCategory(categoryEnum));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        ("Invalid product category: " + category + ". Valid values: " +
                                Arrays.toString(ProductCategoryEnum.values())), e);
            }
        }

        return repository.findAll(spec, pageable);
    }

    private Specification<Product> hasCategory(ProductCategoryEnum category) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("productCategory"), category);
    }

    private Pageable createPageable(Integer page, Integer size, List<String> sortParams) {
        Sort sort = Sort.unsorted();

        if (sortParams != null && sortParams.size() >= 2) {
            String field = sortParams.get(0);
            String direction = sortParams.get(1).toLowerCase();

            Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            sort = Sort.by(sortDirection, field);
        }

        return PageRequest.of(page, size, sort);
    }

    @Override
    public ProductDto addProduct(ProductDto productDto) {
        Product product = repository.save(ProductMapper.productDtoToProduct(productDto));
        return ProductMapper.productToProductDto(product);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        if (repository.existsById(productDto.getProductId())) {
            Product product = repository.save(ProductMapper.productDtoToProduct(productDto));
            return ProductMapper.productToProductDto(product);
        } else {
            throw new ProductNotFoundException(productDto.getProductId());
        }
    }

    @Override
    public Boolean removeProduct(UUID id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setProductState(ProductStateEnum.DEACTIVATE);
        repository.save(product);
        return true;
    }

    @Override
    public Boolean quantityState(UUID productId, String quantityState) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        product.setQuantityState(QuantityStateEnum.valueOf(quantityState));
        repository.save(product);
        return true;
    }

    @Override
    public ProductDto getInfoProduct(UUID id) {
        Product product = repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return ProductMapper.productToProductDto(product);
    }
}

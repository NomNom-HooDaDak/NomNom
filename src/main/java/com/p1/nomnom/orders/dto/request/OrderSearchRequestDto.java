package com.p1.nomnom.orders.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Setter
@Getter
public class OrderSearchRequestDto {
    private UUID storeId;
    private String keyword;
    private String sort;
    private String filterKey;
    private String filterValue;
    private int page;
    private int size;

    public Pageable toPageable() {
        return PageRequest.of(page, size);
    }
}

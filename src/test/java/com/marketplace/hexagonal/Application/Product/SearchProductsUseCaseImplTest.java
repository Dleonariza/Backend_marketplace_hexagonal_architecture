package com.marketplace.hexagonal.Application.Product;

import com.marketplace.hexagonal.Application.Product.UseCases.SearchProductsUseCaseImpl;
import com.marketplace.hexagonal.Domain.Product.Models.PageResult;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductSearchCriteria;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchProductsUseCaseImplTest {

    @Mock ProductRepositoryPort productRepo;
    SearchProductsUseCaseImpl useCase;

    private static final ProductSearchCriteria EMPTY_CRITERIA =
            new ProductSearchCriteria(null, null, null, null, null, null);

    private static final PageResult<Product> EMPTY_PAGE =
            new PageResult<>(List.of(), 0, 20, 0L, 0, true);

    @BeforeEach
    void setUp() {
        useCase = new SearchProductsUseCaseImpl(productRepo);
    }

    @Test
    void searchProducts_shouldDelegateToRepository() {
        when(productRepo.search(EMPTY_CRITERIA, 0, 20)).thenReturn(EMPTY_PAGE);

        PageResult<Product> result = useCase.searchProducts(EMPTY_CRITERIA, 0, 20);

        assertThat(result).isEqualTo(EMPTY_PAGE);
        verify(productRepo).search(EMPTY_CRITERIA, 0, 20);
    }

    @Test
    void searchProducts_whenPageIsNegative_shouldClampToZero() {
        when(productRepo.search(any(), eq(0), anyInt())).thenReturn(EMPTY_PAGE);

        useCase.searchProducts(EMPTY_CRITERIA, -5, 20);

        ArgumentCaptor<Integer> pageCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(productRepo).search(any(), pageCaptor.capture(), anyInt());
        assertThat(pageCaptor.getValue()).isEqualTo(0);
    }

    @Test
    void searchProducts_whenSizeIsZero_shouldClampToOne() {
        when(productRepo.search(any(), anyInt(), eq(1))).thenReturn(EMPTY_PAGE);

        useCase.searchProducts(EMPTY_CRITERIA, 0, 0);

        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(productRepo).search(any(), anyInt(), sizeCaptor.capture());
        assertThat(sizeCaptor.getValue()).isEqualTo(1);
    }

    @Test
    void searchProducts_whenSizeIsNegative_shouldClampToOne() {
        when(productRepo.search(any(), anyInt(), eq(1))).thenReturn(EMPTY_PAGE);

        useCase.searchProducts(EMPTY_CRITERIA, 0, -10);

        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(productRepo).search(any(), anyInt(), sizeCaptor.capture());
        assertThat(sizeCaptor.getValue()).isEqualTo(1);
    }

    @Test
    void searchProducts_whenSizeExceeds100_shouldClampTo100() {
        when(productRepo.search(any(), anyInt(), eq(100))).thenReturn(EMPTY_PAGE);

        useCase.searchProducts(EMPTY_CRITERIA, 0, 999);

        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(productRepo).search(any(), anyInt(), sizeCaptor.capture());
        assertThat(sizeCaptor.getValue()).isEqualTo(100);
    }

    @Test
    void searchProducts_whenSizeIsExactly100_shouldNotClamp() {
        when(productRepo.search(any(), anyInt(), eq(100))).thenReturn(EMPTY_PAGE);

        useCase.searchProducts(EMPTY_CRITERIA, 0, 100);

        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(productRepo).search(any(), anyInt(), sizeCaptor.capture());
        assertThat(sizeCaptor.getValue()).isEqualTo(100);
    }

    @Test
    void searchProducts_shouldReturnExactPageResultFromRepo() {
        PageResult<Product> expected = new PageResult<>(List.of(), 2, 10, 25L, 3, false);
        when(productRepo.search(EMPTY_CRITERIA, 2, 10)).thenReturn(expected);

        PageResult<Product> result = useCase.searchProducts(EMPTY_CRITERIA, 2, 10);

        assertThat(result.page()).isEqualTo(2);
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(25L);
        assertThat(result.totalPages()).isEqualTo(3);
        assertThat(result.last()).isFalse();
    }
}

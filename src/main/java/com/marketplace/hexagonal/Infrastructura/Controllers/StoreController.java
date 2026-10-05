package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Product.Services.ProductService;
import com.marketplace.hexagonal.Application.Seller.Services.SellerService;
import com.marketplace.hexagonal.Domain.Product.Models.PageResult;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductSearchCriteria;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerStatus;
import com.marketplace.hexagonal.Infrastructura.DTO.Seller.StoreResponseDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Seller.StoreSummaryResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stores")
@AllArgsConstructor
public class StoreController {

    private final SellerService sellerService;
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<StoreSummaryResponseDTO>> listActiveStores() {
        List<StoreSummaryResponseDTO> stores = sellerService.getAllSellers().stream()
                .filter(seller -> seller.getStatus() == SellerStatus.ACTIVE)
                .map(StoreSummaryResponseDTO::from)
                .toList();
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<StoreResponseDTO> getStore(
            @PathVariable String sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        SellerProfile seller = sellerService.getById(SellerProfileId.of(sellerId));

        if (seller.getStatus() != SellerStatus.ACTIVE)
            throw new SellerProfileNotFoundException("Store not found.");

        ProductSearchCriteria criteria = new ProductSearchCriteria(
                null, null, SellerProfileId.of(sellerId), null, null, null);
        PageResult<Product> products = productService.searchProducts(criteria, page, size);

        return ResponseEntity.ok(StoreResponseDTO.from(seller, products));
    }
}

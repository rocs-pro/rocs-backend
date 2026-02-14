package com.nsbm.rocs.inventory.service;

import com.nsbm.rocs.inventory.dto.ProductDTO;
import com.nsbm.rocs.entity.inventory.Product;
import com.nsbm.rocs.inventory.exception.DuplicateResourceException;
import com.nsbm.rocs.inventory.exception.ResourceNotFoundException;
import com.nsbm.rocs.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final BrandRepository brandRepository;
    private final UnitRepository unitRepository;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getActiveProducts() {
        return productRepository.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return convertToDTO(product);
    }

    public ProductDTO getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
        return convertToDTO(product);
    }

    public ProductDTO getProductByBarcode(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with barcode: " + barcode));
        return convertToDTO(product);
    }

    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsBySubCategory(Long subCategoryId) {
        return productRepository.findBySubcategoryId(subCategoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByBrand(Long brandId) {
        return productRepository.findByBrandId(brandId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        if (productRepository.findBySku(productDTO.getSku()).isPresent()) {
            throw new DuplicateResourceException("Product with SKU " + productDTO.getSku() + " already exists");
        }
        if (productDTO.getBarcode() != null && !productDTO.getBarcode().isEmpty()) {
            if (productRepository.findByBarcode(productDTO.getBarcode()).isPresent()) {
                throw new DuplicateResourceException("Product with barcode " + productDTO.getBarcode() + " already exists");
            }
        }
        Product product = convertToEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!product.getSku().equals(productDTO.getSku())) {
            if (productRepository.findBySku(productDTO.getSku()).isPresent()) {
                throw new DuplicateResourceException("Product with SKU " + productDTO.getSku() + " already exists");
            }
        }

        if (productDTO.getBarcode() != null && !productDTO.getBarcode().isEmpty()) {
            if (!productDTO.getBarcode().equals(product.getBarcode())) {
                if (productRepository.findByBarcode(productDTO.getBarcode()).isPresent()) {
                    throw new DuplicateResourceException("Product with barcode " + productDTO.getBarcode() + " already exists");
                }
            }
        }

        // Update fields
        product.setSku(productDTO.getSku());
        product.setBarcode(productDTO.getBarcode());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setCategoryId(productDTO.getCategoryId());
        product.setSubcategoryId(productDTO.getSubcategoryId());
        product.setBrandId(productDTO.getBrandId());
        product.setUnitId(productDTO.getUnitId());
        product.setCostPrice(productDTO.getCostPrice());
        product.setSellingPrice(productDTO.getSellingPrice());
        product.setMrp(productDTO.getMrp());
        product.setReorderLevel(productDTO.getReorderLevel());
        product.setMaxStockLevel(productDTO.getMaxStockLevel());
        product.setIsSerialized(productDTO.getIsSerialized());
        product.setTaxRate(productDTO.getTaxRate());
        product.setWarrantyMonths(productDTO.getWarrantyMonths());
        product.setIsActive(productDTO.getIsActive());

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setIsActive(false);
        productRepository.save(product);
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setSku(product.getSku());
        dto.setBarcode(product.getBarcode());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setCategoryId(product.getCategoryId());
        dto.setSubcategoryId(product.getSubcategoryId());
        dto.setBrandId(product.getBrandId());
        dto.setUnitId(product.getUnitId());
        dto.setCostPrice(product.getCostPrice());
        dto.setSellingPrice(product.getSellingPrice());
        dto.setMrp(product.getMrp());
        dto.setReorderLevel(product.getReorderLevel());
        dto.setMaxStockLevel(product.getMaxStockLevel());
        dto.setIsSerialized(product.getIsSerialized());
        dto.setTaxRate(product.getTaxRate());
        dto.setWarrantyMonths(product.getWarrantyMonths());
        dto.setIsActive(product.getIsActive());

        // Set display names
        if (product.getCategoryId() != null) {
            categoryRepository.findById(product.getCategoryId())
                    .ifPresent(category -> dto.setCategoryName(category.getName()));
        }
        if (product.getSubcategoryId() != null) {
            subCategoryRepository.findById(product.getSubcategoryId())
                    .ifPresent(subCategory -> dto.setSubcategoryName(subCategory.getName()));
        }
        if (product.getBrandId() != null) {
            brandRepository.findById(product.getBrandId())
                    .ifPresent(brand -> dto.setBrandName(brand.getName()));
        }
        if (product.getUnitId() != null) {
            unitRepository.findById(product.getUnitId())
                    .ifPresent(unit -> {
                        dto.setUnitName(unit.getName());
                        dto.setUnitSymbol(unit.getSymbol());
                    });
        }

        return dto;
    }

    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setProductId(dto.getProductId());
        product.setSku(dto.getSku());
        product.setBarcode(dto.getBarcode());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCategoryId(dto.getCategoryId());
        product.setSubcategoryId(dto.getSubcategoryId());
        product.setBrandId(dto.getBrandId());
        product.setUnitId(dto.getUnitId());
        product.setCostPrice(dto.getCostPrice());
        product.setSellingPrice(dto.getSellingPrice());
        product.setMrp(dto.getMrp());
        product.setReorderLevel(dto.getReorderLevel());
        product.setMaxStockLevel(dto.getMaxStockLevel());
        product.setIsSerialized(dto.getIsSerialized());
        product.setTaxRate(dto.getTaxRate());
        product.setWarrantyMonths(dto.getWarrantyMonths());
        product.setIsActive(dto.getIsActive());
        return product;
    }
}


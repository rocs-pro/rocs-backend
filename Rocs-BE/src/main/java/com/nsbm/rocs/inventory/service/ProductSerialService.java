package com.nsbm.rocs.inventory.service;

import com.nsbm.rocs.inventory.dto.DamagedProductDTO;
import com.nsbm.rocs.inventory.dto.ProductSerialDTO;
import com.nsbm.rocs.inventory.entity.Batch;
import com.nsbm.rocs.inventory.entity.Product;
import com.nsbm.rocs.inventory.entity.ProductSerial;
import com.nsbm.rocs.inventory.exception.DuplicateResourceException;
import com.nsbm.rocs.inventory.exception.InsufficientStockException;
import com.nsbm.rocs.inventory.exception.ResourceNotFoundException;
import com.nsbm.rocs.inventory.repository.BatchRepository;
import com.nsbm.rocs.inventory.repository.ProductRepository;
import com.nsbm.rocs.inventory.repository.ProductSerialRepository;
import com.nsbm.rocs.inventory.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductSerialService {

    private final ProductSerialRepository productSerialRepository;
    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;
    private final StockRepository stockRepository;

    public List<ProductSerialDTO> getAllSerials() {
        return productSerialRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductSerialDTO> getSerialsByProduct(Long productId) {
        return productSerialRepository.findByProductId(productId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductSerialDTO> getSerialsByBranch(Long branchId) {
        return productSerialRepository.findByBranchId(branchId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductSerialDTO> getSerialsByStatus(String status) {
        return productSerialRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductSerialDTO> getAvailableSerials(Long branchId, Long productId) {
        return productSerialRepository.findByBranchIdAndProductIdAndStatus(branchId, productId, "IN_STOCK").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductSerialDTO getSerialById(Long id) {
        ProductSerial serial = productSerialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serial not found with id: " + id));
        return convertToDTO(serial);
    }

    public ProductSerialDTO getSerialBySerialNo(String serialNo) {
        ProductSerial serial = productSerialRepository.findBySerialNo(serialNo)
                .orElseThrow(() -> new ResourceNotFoundException("Serial not found with number: " + serialNo));
        return convertToDTO(serial);
    }

    @Transactional
    public ProductSerialDTO createSerial(ProductSerialDTO serialDTO) {
        Product product = productRepository.findById(serialDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + serialDTO.getProductId()));

        if (Boolean.FALSE.equals(product.getIsSerialized())) {
            throw new IllegalArgumentException("Product is not serialized");
        }

        // Check for duplicate serial number
        if (productSerialRepository.findBySerialNo(serialDTO.getSerialNo()).isPresent()) {
            throw new DuplicateResourceException("Serial number " + serialDTO.getSerialNo() + " already exists");
        }

        // Check for duplicate barcode if provided
        if (serialDTO.getBarcode() != null && !serialDTO.getBarcode().isEmpty()) {
            if (productSerialRepository.findByBarcode(serialDTO.getBarcode()).isPresent()) {
                throw new DuplicateResourceException("Barcode " + serialDTO.getBarcode() + " already exists");
            }
        }

        ProductSerial serial = convertToEntity(serialDTO);
        ProductSerial savedSerial = productSerialRepository.save(serial);
        return convertToDTO(savedSerial);
    }

    public List<ProductSerialDTO> createBulkSerials(List<ProductSerialDTO> serialDTOs) {
        return serialDTOs.stream()
                .map(this::createSerial)
                .collect(Collectors.toList());
    }

    public ProductSerialDTO updateSerial(Long id, ProductSerialDTO serialDTO) {
        ProductSerial serial = productSerialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serial not found with id: " + id));

        Product product = productRepository.findById(serial.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + serial.getProductId()));
        if (Boolean.FALSE.equals(product.getIsSerialized())) {
            throw new IllegalArgumentException("Product is not serialized");
        }

        // Check for duplicate serial number if changed
        if (!serial.getSerialNo().equals(serialDTO.getSerialNo())) {
            if (productSerialRepository.findBySerialNo(serialDTO.getSerialNo()).isPresent()) {
                throw new DuplicateResourceException("Serial number " + serialDTO.getSerialNo() + " already exists");
            }
        }

        if (serialDTO.getStatus() != null && !serialDTO.getStatus().equals(serial.getStatus())) {
            validateTransition(serial.getStatus(), serialDTO.getStatus());
        }

        serial.setProductId(serialDTO.getProductId());
        serial.setBranchId(serialDTO.getBranchId());
        serial.setSerialNo(serialDTO.getSerialNo());
        serial.setBarcode(serialDTO.getBarcode());
        serial.setBatchId(serialDTO.getBatchId());
        if (serialDTO.getStatus() != null) {
            serial.setStatus(serialDTO.getStatus());
        }
        serial.setGrnId(serialDTO.getGrnId());
        serial.setSaleId(serialDTO.getSaleId());

        return convertToDTO(productSerialRepository.save(serial));
    }

    @Transactional
    public ProductSerialDTO markAsSold(Long serialId, Long saleId) {
        ProductSerial serial = productSerialRepository.findById(serialId)
                .orElseThrow(() -> new ResourceNotFoundException("Serial not found with id: " + serialId));

        Product product = productRepository.findById(serial.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + serial.getProductId()));
        if (Boolean.FALSE.equals(product.getIsSerialized())) {
            throw new IllegalArgumentException("Product is not serialized");
        }

        validateTransition(serial.getStatus(), "SOLD");

        // ensure stock availability
        int updated = stockRepository.decrementQuantityIfAvailable(serial.getBranchId(), serial.getProductId(), 1);
        if (updated == 0) {
            throw new InsufficientStockException("Insufficient stock");
        }

        serial.setStatus("SOLD");
        serial.setSaleId(saleId);
        serial.setSoldAt(LocalDateTime.now());

        ProductSerial updatedSerial = productSerialRepository.save(serial);
        return convertToDTO(updatedSerial);
    }

    @Transactional
    public ProductSerialDTO markAsDamaged(Long serialId) {
        ProductSerial serial = productSerialRepository.findById(serialId)
                .orElseThrow(() -> new ResourceNotFoundException("Serial not found with id: " + serialId));

        Product product = productRepository.findById(serial.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + serial.getProductId()));
        if (Boolean.FALSE.equals(product.getIsSerialized())) {
            throw new IllegalArgumentException("Product is not serialized");
        }

        validateTransition(serial.getStatus(), "DAMAGED");

        int updated = stockRepository.decrementQuantityIfAvailable(serial.getBranchId(), serial.getProductId(), 1);
        if (updated == 0) {
            throw new InsufficientStockException("Insufficient stock");
        }

        serial.setStatus("DAMAGED");

        ProductSerial updatedSerial = productSerialRepository.save(serial);
        return convertToDTO(updatedSerial);
    }

    @Transactional
    public ProductSerialDTO markAsReturned(Long serialId) {
        ProductSerial serial = productSerialRepository.findById(serialId)
                .orElseThrow(() -> new ResourceNotFoundException("Serial not found with id: " + serialId));

        Product product = productRepository.findById(serial.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + serial.getProductId()));
        if (Boolean.FALSE.equals(product.getIsSerialized())) {
            throw new IllegalArgumentException("Product is not serialized");
        }

        validateTransition(serial.getStatus(), "RETURNED");

        serial.setStatus("RETURNED");
        serial.setSaleId(null);
        serial.setSoldAt(null);

        // increment stock back
        stockRepository.incrementQuantity(serial.getBranchId(), serial.getProductId(), 1);

        ProductSerial updatedSerial = productSerialRepository.save(serial);
        return convertToDTO(updatedSerial);
    }

    private void validateTransition(String from, String to) {
        if (from == null) return;
        switch (from) {
            case "IN_STOCK" -> {
                if (!to.equals("SOLD") && !to.equals("DAMAGED")) {
                    throw new IllegalArgumentException("Invalid transition from IN_STOCK to " + to);
                }
            }
            case "SOLD" -> {
                if (!to.equals("RETURNED")) {
                    throw new IllegalArgumentException("Invalid transition from SOLD to " + to);
                }
            }
            case "RETURNED" -> {
                if (!to.equals("IN_STOCK")) {
                    throw new IllegalArgumentException("Invalid transition from RETURNED to " + to);
                }
            }
            case "DAMAGED" -> throw new IllegalArgumentException("Cannot transition from DAMAGED state");
            default -> throw new IllegalArgumentException("Unknown current status: " + from);
        }
    }

    public void deleteSerial(Long id) {
        if (!productSerialRepository.existsById(id)) {
            throw new ResourceNotFoundException("Serial not found with id: " + id);
        }
        productSerialRepository.deleteById(id);
    }

    public List<DamagedProductDTO> getDamagedProducts(Long branchId) {
        List<ProductSerial> damagedSerials = branchId != null
                ? productSerialRepository.findByBranchIdAndStatus(branchId, "DAMAGED")
                : productSerialRepository.findByStatus("DAMAGED");

        return damagedSerials.stream()
                .map(serial -> {
                    DamagedProductDTO dto = new DamagedProductDTO();
                    dto.setSerialId(serial.getSerialId());
                    dto.setSerialNo(serial.getSerialNo());
                    dto.setProductId(serial.getProductId());
                    dto.setBranchId(serial.getBranchId());

                    productRepository.findById(serial.getProductId()).ifPresent(product -> {
                        dto.setProductName(product.getName());
                        dto.setProductSku(product.getSku());
                    });

                    if (serial.getBatchId() != null) {
                        batchRepository.findById(serial.getBatchId()).ifPresent(batch -> {
                            dto.setBatchCode(batch.getBatchCode());
                        });
                    }

                    dto.setDamageReason(serial.getStatus());
                    dto.setMessage("Damaged serial reported");
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private ProductSerialDTO convertToDTO(ProductSerial serial) {
        ProductSerialDTO dto = new ProductSerialDTO();
        dto.setSerialId(serial.getSerialId());
        dto.setProductId(serial.getProductId());
        dto.setBranchId(serial.getBranchId());
        dto.setSerialNo(serial.getSerialNo());
        dto.setBarcode(serial.getBarcode());
        dto.setBatchId(serial.getBatchId());
        dto.setStatus(serial.getStatus());
        dto.setGrnId(serial.getGrnId());
        dto.setSaleId(serial.getSaleId());
        dto.setCreatedAt(serial.getCreatedAt());
        dto.setSoldAt(serial.getSoldAt());

        // Set product details
        productRepository.findById(serial.getProductId()).ifPresent(product -> {
            dto.setProductName(product.getName());
            dto.setProductSku(product.getSku());
        });

        // Set batch details
        if (serial.getBatchId() != null) {
            batchRepository.findById(serial.getBatchId()).ifPresent(batch -> {
                dto.setBatchCode(batch.getBatchCode());
            });
        }

        return dto;
    }

    private ProductSerial convertToEntity(ProductSerialDTO dto) {
        ProductSerial serial = new ProductSerial();
        serial.setSerialId(dto.getSerialId());
        serial.setProductId(dto.getProductId());
        serial.setBranchId(dto.getBranchId());
        serial.setSerialNo(dto.getSerialNo());
        serial.setBarcode(dto.getBarcode());
        serial.setBatchId(dto.getBatchId());
        serial.setStatus(dto.getStatus());
        serial.setGrnId(dto.getGrnId());
        serial.setSaleId(dto.getSaleId());
        serial.setSoldAt(dto.getSoldAt());
        return serial;
    }
}

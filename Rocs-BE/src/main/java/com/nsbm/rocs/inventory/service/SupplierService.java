package com.nsbm.rocs.inventory.service;

import com.nsbm.rocs.inventory.dto.SupplierRequestDTO;
import com.nsbm.rocs.inventory.dto.SupplierResponseDTO;

import java.util.List;

public interface SupplierService {

    SupplierResponseDTO createSupplier(SupplierRequestDTO requestDTO);

    SupplierResponseDTO updateSupplier(Long supplierId, SupplierRequestDTO requestDTO);

    void deleteSupplier(Long supplierId);

    SupplierResponseDTO getSupplierById(Long supplierId);

    List<SupplierResponseDTO> getAllSuppliers();
}


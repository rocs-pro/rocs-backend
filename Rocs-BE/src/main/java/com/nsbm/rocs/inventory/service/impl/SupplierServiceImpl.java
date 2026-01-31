package com.nsbm.rocs.inventory.service.impl;

import com.nsbm.rocs.entity.inventory.Supplier;
import com.nsbm.rocs.entity.inventory.SupplierBranch;
import com.nsbm.rocs.entity.inventory.SupplierBranchId;
import com.nsbm.rocs.entity.inventory.SupplierContact;
import com.nsbm.rocs.inventory.dto.SupplierBranchDTO;
import com.nsbm.rocs.inventory.dto.SupplierContactDTO;
import com.nsbm.rocs.inventory.dto.SupplierRequestDTO;
import com.nsbm.rocs.inventory.dto.SupplierResponseDTO;
import com.nsbm.rocs.inventory.exception.DuplicateResourceException;
import com.nsbm.rocs.inventory.exception.ResourceNotFoundException;
import com.nsbm.rocs.inventory.repository.SupplierRepository;
import com.nsbm.rocs.inventory.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public SupplierResponseDTO createSupplier(SupplierRequestDTO requestDTO) {
        validateDuplicateCode(requestDTO.getCode(), null);
        Supplier supplier = mapToEntity(new Supplier(), requestDTO);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return mapToResponse(savedSupplier);
    }

    @Override
    public SupplierResponseDTO updateSupplier(Long supplierId, SupplierRequestDTO requestDTO) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + supplierId));
        validateDuplicateCode(requestDTO.getCode(), supplierId);
        supplier.getContacts().clear();
        supplier.getBranches().clear();
        Supplier updated = supplierRepository.save(mapToEntity(supplier, requestDTO));
        return mapToResponse(updated);
    }

    @Override
    public void deleteSupplier(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + supplierId));
        supplierRepository.delete(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponseDTO getSupplierById(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + supplierId));
        return mapToResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateDuplicateCode(String code, Long existingId) {
        supplierRepository.findByCode(code).ifPresent(existing -> {
            boolean isDuplicate = existingId == null || !existingId.equals(existing.getSupplierId());
            if (isDuplicate) {
                throw new DuplicateResourceException("Supplier with code " + code + " already exists");
            }
        });
    }

    private Supplier mapToEntity(Supplier supplier, SupplierRequestDTO dto) {
        supplier.setCode(dto.getCode());
        supplier.setName(dto.getName());
        supplier.setCompanyName(dto.getCompanyName());
        supplier.setContactPerson(dto.getContactPerson());
        supplier.setPhone(dto.getPhone());
        supplier.setMobile(dto.getMobile());
        supplier.setEmail(dto.getEmail());
        supplier.setWebsite(dto.getWebsite());
        supplier.setAddressLine1(dto.getAddressLine1());
        supplier.setAddressLine2(dto.getAddressLine2());
        supplier.setCity(dto.getCity());
        supplier.setCountry(dto.getCountry());
        supplier.setTaxId(dto.getTaxId());
        supplier.setCreditDays(dto.getCreditDays());
        supplier.setCreditLimit(dto.getCreditLimit());
        supplier.setIsActive(dto.getIsActive());
        supplier.setCreatedBy(dto.getCreatedBy());

        supplier.getContacts().clear();
        dto.getContacts().forEach(contactDTO -> {
            SupplierContact contact = new SupplierContact();
            contact.setSupplier(supplier);
            contact.setName(contactDTO.getName());
            contact.setDesignation(contactDTO.getDesignation());
            contact.setPhone(contactDTO.getPhone());
            contact.setEmail(contactDTO.getEmail());
            contact.setIsPrimary(contactDTO.getIsPrimary());
            supplier.getContacts().add(contact);
        });

        supplier.getBranches().clear();
        dto.getBranches().forEach(branchDTO -> {
            SupplierBranch branch = new SupplierBranch();
            SupplierBranchId id = new SupplierBranchId();
            id.setSupplierId(supplier.getSupplierId());
            id.setBranchId(branchDTO.getBranchId());
            branch.setId(id);
            branch.setSupplier(supplier);
            branch.setIsPreferred(branchDTO.getIsPreferred());
            branch.setDiscountPercentage(branchDTO.getDiscountPercentage());
            branch.setNotes(branchDTO.getNotes());
            supplier.getBranches().add(branch);
        });

        return supplier;
    }

    private SupplierResponseDTO mapToResponse(Supplier supplier) {
        SupplierResponseDTO dto = new SupplierResponseDTO();
        dto.setSupplierId(supplier.getSupplierId());
        dto.setCode(supplier.getCode());
        dto.setName(supplier.getName());
        dto.setCompanyName(supplier.getCompanyName());
        dto.setContactPerson(supplier.getContactPerson());
        dto.setPhone(supplier.getPhone());
        dto.setMobile(supplier.getMobile());
        dto.setEmail(supplier.getEmail());
        dto.setWebsite(supplier.getWebsite());
        dto.setAddressLine1(supplier.getAddressLine1());
        dto.setAddressLine2(supplier.getAddressLine2());
        dto.setCity(supplier.getCity());
        dto.setCountry(supplier.getCountry());
        dto.setTaxId(supplier.getTaxId());
        dto.setCreditDays(supplier.getCreditDays());
        dto.setCreditLimit(supplier.getCreditLimit());
        dto.setIsActive(supplier.getIsActive());
        dto.setCreatedBy(supplier.getCreatedBy());
        dto.setCreatedAt(supplier.getCreatedAt());
        dto.setUpdatedAt(supplier.getUpdatedAt());

        dto.setContacts(supplier.getContacts().stream()
                .map(contact -> {
                    SupplierContactDTO contactDTO = new SupplierContactDTO();
                    contactDTO.setContactId(contact.getContactId());
                    contactDTO.setName(contact.getName());
                    contactDTO.setDesignation(contact.getDesignation());
                    contactDTO.setPhone(contact.getPhone());
                    contactDTO.setEmail(contact.getEmail());
                    contactDTO.setIsPrimary(contact.getIsPrimary());
                    return contactDTO;
                }).collect(Collectors.toList()));

        dto.setBranches(supplier.getBranches().stream()
                .map(branch -> {
                    SupplierBranchDTO branchDTO = new SupplierBranchDTO();
                    branchDTO.setBranchId(branch.getId().getBranchId());
                    branchDTO.setIsPreferred(branch.getIsPreferred());
                    branchDTO.setDiscountPercentage(branch.getDiscountPercentage());
                    branchDTO.setNotes(branch.getNotes());
                    return branchDTO;
                }).collect(Collectors.toList()));

        return dto;
    }
}


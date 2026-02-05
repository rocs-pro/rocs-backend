package com.nsbm.rocs.admin.service.impl;

import com.nsbm.rocs.admin.dto.BranchDTO;
import com.nsbm.rocs.admin.service.BranchService;
import com.nsbm.rocs.entity.main.Branch;
import com.nsbm.rocs.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BranchServiceImpl implements BranchService {


    private final BranchRepository branchRepository;

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    // Create
    @Override
    public BranchDTO createBranch(BranchDTO dto) {
        Branch entity = toEntity(dto);
        Branch saved = branchRepository.save(entity);
        return toDTO(saved);
    }

    // Read all
    @Override
    public List<BranchDTO> getAllBranches() {
        return branchRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Read by id
    @Override
    public BranchDTO getBranchById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));
        return toDTO(branch);
    }

    // Update
    @Override
    public BranchDTO updateBranch(Long id, BranchDTO dto) {
        Branch existing = branchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));

        // update fields
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getCode() != null) existing.setCode(dto.getCode());
        if (dto.getAddress() != null) existing.setAddress(dto.getAddress());
        if (dto.getLocation() != null) existing.setLocation(dto.getLocation());
        if (dto.getPhone() != null) existing.setPhone(dto.getPhone());
        if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
        if (dto.getIsActive() != null) existing.setIsActive(dto.getIsActive());

        Branch saved = branchRepository.save(existing);
        return toDTO(saved);
    }

    // Delete
    @Override
    public void deleteBranch(Long id) {
        Branch existing = branchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));
        branchRepository.delete(existing);
    }

    // Mapping helpers
    private BranchDTO toDTO(Branch b) {
        if (b == null) return null;
        BranchDTO dto = new BranchDTO();
        dto.setId(b.getBranchId());
        dto.setName(b.getName());
        dto.setCode(b.getCode());
        dto.setAddress(b.getAddress());
        dto.setLocation(b.getLocation());
        dto.setPhone(b.getPhone());
        dto.setEmail(b.getEmail());
        dto.setIsActive(b.getIsActive());
        return dto;
    }

    private Branch toEntity(BranchDTO dto) {
        if (dto == null) return null;
        Branch b = new Branch();
        // id left null for create; if provided, it's ignored to let JPA handle it
        b.setName(dto.getName());
        b.setCode(dto.getCode());
        b.setAddress(dto.getAddress());
        b.setLocation(dto.getLocation());
        b.setPhone(dto.getPhone());
        b.setEmail(dto.getEmail());
        b.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return b;
    }
}

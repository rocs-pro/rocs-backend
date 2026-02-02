package com.nsbm.rocs.inventory.service;

import com.nsbm.rocs.inventory.dto.DamagedProductDTO;

import java.util.List;

public interface DamageService {

    List<DamagedProductDTO> getAllDamages(Long branchId, Long productId);

    DamagedProductDTO createDamage(DamagedProductDTO damageDTO);
}


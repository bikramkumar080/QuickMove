package com.quickMove.service;

import com.quickMove.model.VehicleType;
import com.quickMove.repository.VehicleTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleTypeService {
    @Autowired
    VehicleTypeRepository vehicleTypeRepository;

    public List<VehicleType> fetchVehiclesTypesOfOrg(Long id){
        return vehicleTypeRepository.findByOrganization_Id(id);
    }
}

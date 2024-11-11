package com.quickMove.service.Impl;

import com.quickMove.model.Organization;
import com.quickMove.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganizationService {
    @Autowired
    private OrganizationRepository organizationRepository;

    public Organization fetchOrganizationById(Long id) {
        Optional<Organization> organizationOptional = organizationRepository.findById(id);
        if(organizationOptional.isPresent()){
            return organizationOptional.get();
        } else {
            return null;
        }
    }

    public Organization save(Organization organization) {
        return organizationRepository.save(organization);
    }
}

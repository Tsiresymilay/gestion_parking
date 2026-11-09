package com.example.parking.service.impl;

import com.example.parking.dto.request.VehicleRequest;
import com.example.parking.dto.response.VehicleResponse;
import com.example.parking.entity.AppUser;
import com.example.parking.entity.Vehicle;
import com.example.parking.entity.VehicleType;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.mapper.VehicleMapper;
import com.example.parking.repository.AppUserRepository;
import com.example.parking.repository.VehicleRepository;
import com.example.parking.service.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final AppUserRepository userRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, AppUserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public VehicleResponse create(VehicleRequest request) {
        if (vehicleRepository.existsByPlateNumber(request.getPlateNumber())) {
            throw new ConflictException("Un vehicule avec l'immatriculation " + request.getPlateNumber() + " existe deja");
        }
        Vehicle vehicle = new Vehicle();
        apply(request, vehicle);
        return VehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle vehicle = getEntity(id);
        vehicleRepository.findByPlateNumber(request.getPlateNumber()).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new ConflictException("Un vehicule avec l'immatriculation " + request.getPlateNumber() + " existe deja");
            }
        });
        apply(request, vehicle);
        return VehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse findById(Long id) {
        return VehicleMapper.toResponse(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleResponse> search(String keyword, VehicleType type, Pageable pageable) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return vehicleRepository.search(kw, type, pageable).map(VehicleMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> findAll() {
        return vehicleRepository.findAll().stream().map(VehicleMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        vehicleRepository.delete(getEntity(id));
    }

    private void apply(VehicleRequest request, Vehicle vehicle) {
        vehicle.setPlateNumber(request.getPlateNumber());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setType(request.getType());
        if (request.getOwnerId() != null) {
            AppUser owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Utilisateur", request.getOwnerId()));
            vehicle.setOwner(owner);
        } else {
            vehicle.setOwner(null);
        }
    }

    private Vehicle getEntity(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Vehicule", id));
    }
}

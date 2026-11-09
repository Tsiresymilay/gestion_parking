package com.example.parking.service.impl;

import com.example.parking.dto.request.SpaceRequest;
import com.example.parking.dto.response.OccupancyResponse;
import com.example.parking.dto.response.SpaceResponse;
import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.SpaceStatus;
import com.example.parking.entity.SpaceType;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.InvalidOperationException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.mapper.SpaceMapper;
import com.example.parking.repository.ParkingSpaceRepository;
import com.example.parking.service.SpaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SpaceServiceImpl implements SpaceService {

    private final ParkingSpaceRepository spaceRepository;

    public SpaceServiceImpl(ParkingSpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }

    @Override
    public SpaceResponse create(SpaceRequest request) {
        if (spaceRepository.existsByNumber(request.getNumber())) {
            throw new ConflictException("Une place avec le numero " + request.getNumber() + " existe deja");
        }
        ParkingSpace space = SpaceMapper.toEntity(request);
        return SpaceMapper.toResponse(spaceRepository.save(space));
    }

    @Override
    public SpaceResponse update(Long id, SpaceRequest request) {
        ParkingSpace space = getEntity(id);
        spaceRepository.findByNumber(request.getNumber()).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new ConflictException("Une place avec le numero " + request.getNumber() + " existe deja");
            }
        });
        SpaceMapper.apply(request, space);
        return SpaceMapper.toResponse(spaceRepository.save(space));
    }

    @Override
    @Transactional(readOnly = true)
    public SpaceResponse findById(Long id) {
        return SpaceMapper.toResponse(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SpaceResponse> search(String keyword, SpaceStatus status, SpaceType type, String zone, Pageable pageable) {
        return spaceRepository.search(normalize(keyword), status, type, normalize(zone), pageable)
                .map(SpaceMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpaceResponse> findAll() {
        return spaceRepository.findAll().stream().map(SpaceMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpaceResponse> findAvailable() {
        return spaceRepository.findByStatus(SpaceStatus.LIBRE).stream()
                .map(SpaceMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public SpaceResponse changeStatus(Long id, SpaceStatus status) {
        ParkingSpace space = getEntity(id);
        space.setStatus(status);
        return SpaceMapper.toResponse(spaceRepository.save(space));
    }

    @Override
    public void delete(Long id) {
        ParkingSpace space = getEntity(id);
        if (space.getStatus() == SpaceStatus.OCCUPEE) {
            throw new InvalidOperationException("Impossible de supprimer une place occupee");
        }
        spaceRepository.delete(space);
    }

    @Override
    @Transactional(readOnly = true)
    public OccupancyResponse occupancy() {
        long total = spaceRepository.count();
        long libre = spaceRepository.countByStatus(SpaceStatus.LIBRE);
        long reservee = spaceRepository.countByStatus(SpaceStatus.RESERVEE);
        long occupee = spaceRepository.countByStatus(SpaceStatus.OCCUPEE);
        long horsService = spaceRepository.countByStatus(SpaceStatus.HORS_SERVICE);

        OccupancyResponse response = new OccupancyResponse();
        response.setTotal(total);
        response.setLibre(libre);
        response.setReservee(reservee);
        response.setOccupee(occupee);
        response.setHorsService(horsService);
        response.setTauxOccupation(total == 0 ? 0 : (occupee * 100.0) / total);
        return response;
    }

    private ParkingSpace getEntity(Long id) {
        return spaceRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Place", id));
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}

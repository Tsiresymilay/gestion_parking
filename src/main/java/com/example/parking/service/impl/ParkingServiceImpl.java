package com.example.parking.service.impl;

import com.example.parking.dto.request.EntryRequest;
import com.example.parking.dto.request.ExitRequest;
import com.example.parking.dto.response.SessionResponse;
import com.example.parking.entity.AppUser;
import com.example.parking.entity.ParkingSession;
import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.SessionStatus;
import com.example.parking.entity.SpaceStatus;
import com.example.parking.entity.SpaceType;
import com.example.parking.entity.UserType;
import com.example.parking.entity.Vehicle;
import com.example.parking.exception.BusinessException;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.mapper.SessionMapper;
import com.example.parking.repository.AppUserRepository;
import com.example.parking.repository.ParkingSessionRepository;
import com.example.parking.repository.ParkingSpaceRepository;
import com.example.parking.repository.VehicleRepository;
import com.example.parking.service.ParkingService;
import com.example.parking.service.PricingService;
import com.example.parking.service.SubscriptionService;
import com.example.parking.util.SpaceSelectionUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ParkingServiceImpl implements ParkingService {

    private final ParkingSessionRepository sessionRepository;
    private final ParkingSpaceRepository spaceRepository;
    private final VehicleRepository vehicleRepository;
    private final AppUserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final PricingService pricingService;

    public ParkingServiceImpl(ParkingSessionRepository sessionRepository,
                              ParkingSpaceRepository spaceRepository,
                              VehicleRepository vehicleRepository,
                              AppUserRepository userRepository,
                              SubscriptionService subscriptionService,
                              PricingService pricingService) {
        this.sessionRepository = sessionRepository;
        this.spaceRepository = spaceRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.subscriptionService = subscriptionService;
        this.pricingService = pricingService;
    }

    @Override
    public SessionResponse enter(EntryRequest request) {
        Vehicle vehicle = vehicleRepository.findByPlateNumber(request.getPlateNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicule introuvable avec l'immatriculation " + request.getPlateNumber()));

        if (sessionRepository.findFirstByVehicleAndStatus(vehicle, SessionStatus.EN_COURS).isPresent()) {
            throw new ConflictException("Le vehicule " + vehicle.getPlateNumber() + " est deja dans le parking");
        }

        AppUser user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Utilisateur", request.getUserId()));
        } else if (vehicle.getOwner() != null) {
            user = vehicle.getOwner();
        }

        ParkingSpace space = resolveSpace(request.getSpaceId(), user);

        ParkingSession session = new ParkingSession();
        session.setVehicle(vehicle);
        session.setSpace(space);
        session.setUser(user);
        session.setEntryTime(LocalDateTime.now());
        session.setExpectedDurationHours(request.getExpectedDurationHours());
        session.setStatus(SessionStatus.EN_COURS);

        space.setStatus(SpaceStatus.OCCUPEE);
        spaceRepository.save(space);
        return SessionMapper.toResponse(sessionRepository.save(session));
    }

    @Override
    public SessionResponse exit(ExitRequest request) {
        Vehicle vehicle = vehicleRepository.findByPlateNumber(request.getPlateNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicule introuvable avec l'immatriculation " + request.getPlateNumber()));

        ParkingSession session = sessionRepository
                .findFirstByVehicleAndStatus(vehicle, SessionStatus.EN_COURS)
                .orElseThrow(() -> new BusinessException(
                        "Aucune entree active pour le vehicule " + request.getPlateNumber()));

        LocalDateTime exitTime = LocalDateTime.now();
        long actualMinutes = Duration.between(session.getEntryTime(), exitTime).toMinutes();
        boolean subscriber = subscriptionService.isVehicleCovered(vehicle);

        session.setExitTime(exitTime);
        session.setAmount(pricingService.computeCharge(session.getSpace(), session.getUser(),
                actualMinutes, session.getExpectedDurationHours(), subscriber));
        session.setStatus(SessionStatus.TERMINEE);

        ParkingSpace space = session.getSpace();
        space.setStatus(SpaceStatus.LIBRE);
        spaceRepository.save(space);
        return SessionMapper.toResponse(sessionRepository.save(session));
    }

    @Override
    @Transactional(readOnly = true)
    public SessionResponse findById(Long id) {
        ParkingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Session", id));
        return SessionMapper.toResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SessionResponse> search(String keyword, SessionStatus status, Pageable pageable) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return sessionRepository.search(kw, status, pageable).map(SessionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionResponse> findActive() {
        return sessionRepository.findByStatus(SessionStatus.EN_COURS).stream()
                .map(SessionMapper::toResponse).collect(Collectors.toList());
    }

    private ParkingSpace resolveSpace(Long spaceId, AppUser user) {
        if (spaceId != null) {
            ParkingSpace space = spaceRepository.findById(spaceId)
                    .orElseThrow(() -> ResourceNotFoundException.of("Place", spaceId));
            if (space.getStatus() != SpaceStatus.LIBRE) {
                throw new ConflictException("La place " + space.getNumber() + " n'est pas libre");
            }
            return space;
        }
        UserType userType = user != null ? user.getUserType() : UserType.STANDARD;
        for (SpaceType type : SpaceSelectionUtil.preferredTypes(userType)) {
            List<ParkingSpace> candidates = spaceRepository.findByStatusAndType(SpaceStatus.LIBRE, type);
            if (!candidates.isEmpty()) {
                return candidates.get(0);
            }
        }
        throw new BusinessException("Aucune place libre disponible");
    }
}

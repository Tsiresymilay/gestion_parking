package com.example.parking.service.impl;

import com.example.parking.dto.request.ReservationRequest;
import com.example.parking.dto.response.ReservationResponse;
import com.example.parking.entity.AppUser;
import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.Reservation;
import com.example.parking.entity.ReservationStatus;
import com.example.parking.entity.SpaceStatus;
import com.example.parking.entity.SpaceType;
import com.example.parking.entity.UserType;
import com.example.parking.entity.Vehicle;
import com.example.parking.exception.BusinessException;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.mapper.ReservationMapper;
import com.example.parking.repository.AppUserRepository;
import com.example.parking.repository.ParkingSpaceRepository;
import com.example.parking.repository.ReservationRepository;
import com.example.parking.repository.VehicleRepository;
import com.example.parking.service.ReservationService;
import com.example.parking.util.SpaceSelectionUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final AppUserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSpaceRepository spaceRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  AppUserRepository userRepository,
                                  VehicleRepository vehicleRepository,
                                  ParkingSpaceRepository spaceRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.spaceRepository = spaceRepository;
    }

    @Override
    public ReservationResponse create(ReservationRequest request) {
        validateDates(request.getExpectedArrival(), request.getExpectedDeparture());
        AppUser user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> ResourceNotFoundException.of("Utilisateur", request.getUserId()));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> ResourceNotFoundException.of("Vehicule", request.getVehicleId()));

        ParkingSpace space = resolveSpace(request.getSpaceId(), user,
                request.getExpectedArrival(), request.getExpectedDeparture(), null);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setVehicle(vehicle);
        reservation.setSpace(space);
        reservation.setExpectedArrival(request.getExpectedArrival());
        reservation.setExpectedDeparture(request.getExpectedDeparture());
        reservation.setStatus(ReservationStatus.CONFIRMEE);
        return ReservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse update(Long id, ReservationRequest request) {
        validateDates(request.getExpectedArrival(), request.getExpectedDeparture());
        Reservation reservation = getEntity(id);
        if (reservation.getStatus() == ReservationStatus.ANNULEE) {
            throw new BusinessException("Impossible de modifier une reservation annulee");
        }
        AppUser user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> ResourceNotFoundException.of("Utilisateur", request.getUserId()));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> ResourceNotFoundException.of("Vehicule", request.getVehicleId()));

        ParkingSpace space = resolveSpace(request.getSpaceId(), user,
                request.getExpectedArrival(), request.getExpectedDeparture(), id);

        reservation.setUser(user);
        reservation.setVehicle(vehicle);
        reservation.setSpace(space);
        reservation.setExpectedArrival(request.getExpectedArrival());
        reservation.setExpectedDeparture(request.getExpectedDeparture());
        return ReservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse findById(Long id) {
        return ReservationMapper.toResponse(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationResponse> search(String keyword, ReservationStatus status, Pageable pageable) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return reservationRepository.search(kw, status, pageable).map(ReservationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public ReservationResponse confirm(Long id) {
        Reservation reservation = getEntity(id);
        if (reservation.getStatus() != ReservationStatus.EN_ATTENTE) {
            throw new BusinessException("Seule une reservation en attente peut etre confirmee");
        }
        reservation.setStatus(ReservationStatus.CONFIRMEE);
        return ReservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse cancel(Long id) {
        Reservation reservation = getEntity(id);
        if (reservation.getStatus() == ReservationStatus.TERMINEE) {
            throw new BusinessException("Impossible d'annuler une reservation terminee");
        }
        reservation.setStatus(ReservationStatus.ANNULEE);
        return ReservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public void delete(Long id) {
        reservationRepository.delete(getEntity(id));
    }

    private ParkingSpace resolveSpace(Long spaceId, AppUser user, LocalDateTime arrival,
                                      LocalDateTime departure, Long excludeId) {
        if (spaceId != null) {
            ParkingSpace space = spaceRepository.findById(spaceId)
                    .orElseThrow(() -> ResourceNotFoundException.of("Place", spaceId));
            checkSpaceAvailable(space, arrival, departure, excludeId);
            return space;
        }
        return autoAssign(user, arrival, departure, excludeId);
    }

    private ParkingSpace autoAssign(AppUser user, LocalDateTime arrival,
                                    LocalDateTime departure, Long excludeId) {
        for (SpaceType type : SpaceSelectionUtil.preferredTypes(user.getUserType())) {
            List<ParkingSpace> candidates = spaceRepository.findByStatusAndType(SpaceStatus.LIBRE, type);
            for (ParkingSpace space : candidates) {
                if (reservationRepository.findOverlapping(space.getId(), arrival, departure, excludeId).isEmpty()) {
                    return space;
                }
            }
        }
        throw new BusinessException("Aucune place disponible pour la periode demandee");
    }

    private void checkSpaceAvailable(ParkingSpace space, LocalDateTime arrival,
                                     LocalDateTime departure, Long excludeId) {
        if (space.getStatus() == SpaceStatus.HORS_SERVICE) {
            throw new ConflictException("La place " + space.getNumber() + " est hors service");
        }
        List<Reservation> overlaps = reservationRepository
                .findOverlapping(space.getId(), arrival, departure, excludeId);
        if (!overlaps.isEmpty()) {
            throw new ConflictException("La place " + space.getNumber()
                    + " est deja reservee sur cette periode");
        }
    }

    private void validateDates(LocalDateTime arrival, LocalDateTime departure) {
        if (arrival != null && departure != null && !departure.isAfter(arrival)) {
            throw new BusinessException("La date de depart doit etre posterieure a la date d'arrivee");
        }
    }

    private Reservation getEntity(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Reservation", id));
    }
}

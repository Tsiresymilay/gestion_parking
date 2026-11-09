package com.example.parking.service.impl;

import com.example.parking.dto.request.SubscriptionRequest;
import com.example.parking.dto.response.SubscriptionResponse;
import com.example.parking.entity.AppUser;
import com.example.parking.entity.Subscription;
import com.example.parking.entity.SubscriptionStatus;
import com.example.parking.entity.UserType;
import com.example.parking.entity.Vehicle;
import com.example.parking.exception.BusinessException;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.mapper.SubscriptionMapper;
import com.example.parking.repository.AppUserRepository;
import com.example.parking.repository.SubscriptionRepository;
import com.example.parking.repository.VehicleRepository;
import com.example.parking.service.SubscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final AppUserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository,
                                   AppUserRepository userRepository,
                                   VehicleRepository vehicleRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public SubscriptionResponse create(SubscriptionRequest request) {
        AppUser user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> ResourceNotFoundException.of("Utilisateur", request.getUserId()));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> ResourceNotFoundException.of("Vehicule", request.getVehicleId()));

        if (!subscriptionRepository.findValidByVehicle(vehicle, LocalDate.now()).isEmpty()) {
            throw new ConflictException("Ce vehicule possede deja un abonnement actif");
        }

        LocalDate endDate = request.getStartDate().plusDays(request.getType().getDays());
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setVehicle(vehicle);
        subscription.setType(request.getType());
        subscription.setStartDate(request.getStartDate());
        subscription.setEndDate(endDate);
        subscription.setStatus(SubscriptionStatus.ACTIF);

        if (user.getUserType() == UserType.STANDARD) {
            user.setUserType(UserType.ABONNE);
            userRepository.save(user);
        }
        return SubscriptionMapper.toResponse(subscriptionRepository.save(subscription));
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse findById(Long id) {
        return SubscriptionMapper.toResponse(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubscriptionResponse> search(String keyword, SubscriptionStatus status, Pageable pageable) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return subscriptionRepository.search(kw, status, pageable).map(SubscriptionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> findAll() {
        return subscriptionRepository.findAll().stream()
                .map(SubscriptionMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public SubscriptionResponse cancel(Long id) {
        Subscription subscription = getEntity(id);
        if (subscription.getStatus() == SubscriptionStatus.ANNULE) {
            throw new BusinessException("Cet abonnement est deja annule");
        }
        subscription.setStatus(SubscriptionStatus.ANNULE);
        return SubscriptionMapper.toResponse(subscriptionRepository.save(subscription));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVehicleCovered(Vehicle vehicle) {
        return !subscriptionRepository.findValidByVehicle(vehicle, LocalDate.now()).isEmpty();
    }

    @Override
    public void refreshExpired() {
        List<Subscription> actives = subscriptionRepository.findByStatus(SubscriptionStatus.ACTIF);
        LocalDate today = LocalDate.now();
        for (Subscription subscription : actives) {
            if (subscription.getEndDate().isBefore(today)) {
                subscription.setStatus(SubscriptionStatus.EXPIRE);
            }
        }
        subscriptionRepository.saveAll(actives);
    }

    private Subscription getEntity(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Abonnement", id));
    }
}

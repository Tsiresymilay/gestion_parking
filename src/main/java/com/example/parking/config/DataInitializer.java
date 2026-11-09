package com.example.parking.config;

import com.example.parking.entity.AppUser;
import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.Role;
import com.example.parking.entity.SpaceStatus;
import com.example.parking.entity.SpaceType;
import com.example.parking.entity.UserType;
import com.example.parking.entity.Vehicle;
import com.example.parking.entity.VehicleType;
import com.example.parking.repository.AppUserRepository;
import com.example.parking.repository.ParkingSpaceRepository;
import com.example.parking.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final ParkingSpaceRepository spaceRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AppUserRepository userRepository,
                           ParkingSpaceRepository spaceRepository,
                           VehicleRepository vehicleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.spaceRepository = spaceRepository;
        this.vehicleRepository = vehicleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(createUser("admin", "admin123", "Admin", "Systeme",
                    "admin@parking.local", Role.ADMIN, UserType.STANDARD));
            userRepository.save(createUser("manager", "manager123", "Maria", "Gestion",
                    "manager@parking.local", Role.MANAGER, UserType.STANDARD));
            userRepository.save(createUser("operateur", "operateur123", "Paul", "Accueil",
                    "operateur@parking.local", Role.OPERATOR, UserType.STANDARD));
            userRepository.save(createUser("client", "client123", "Jean", "Client",
                    "client@parking.local", Role.USER, UserType.STANDARD));
            userRepository.save(createUser("clientvip", "vip123", "Sophie", "Vip",
                    "vip@parking.local", Role.USER, UserType.VIP));
        }

        if (spaceRepository.count() == 0) {
            for (int i = 1; i <= 12; i++) {
                spaceRepository.save(createSpace("A" + i, "A", SpaceType.STANDARD));
            }
            for (int i = 1; i <= 4; i++) {
                spaceRepository.save(createSpace("B" + i, "B", SpaceType.ABONNE));
            }
            spaceRepository.save(createSpace("VIP1", "VIP", SpaceType.VIP));
            spaceRepository.save(createSpace("VIP2", "VIP", SpaceType.VIP));
            spaceRepository.save(createSpace("PMR1", "A", SpaceType.PMR));
        }

        if (vehicleRepository.count() == 0) {
            AppUser client = userRepository.findByUsername("client").orElse(null);
            AppUser vip = userRepository.findByUsername("clientvip").orElse(null);
            vehicleRepository.save(createVehicle("1234 TAA", "Toyota", "Corolla", 2019, VehicleType.VOITURE, client));
            vehicleRepository.save(createVehicle("5678 TBA", "Renault", "Clio", 2021, VehicleType.VOITURE, vip));
            vehicleRepository.save(createVehicle("9999 MOTO", "Yamaha", "X", 2022, VehicleType.MOTO, vip));
        }
    }

    private AppUser createUser(String username, String password, String firstName, String lastName,
                               String email, Role role, UserType userType) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setRole(role);
        user.setUserType(userType);
        user.setEnabled(true);
        return user;
    }

    private ParkingSpace createSpace(String number, String zone, SpaceType type) {
        ParkingSpace space = new ParkingSpace();
        space.setNumber(number);
        space.setZone(zone);
        space.setType(type);
        space.setStatus(SpaceStatus.LIBRE);
        return space;
    }

    private Vehicle createVehicle(String plate, String brand, String model, int year,
                                  VehicleType type, AppUser owner) {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlateNumber(plate);
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setYear(year);
        vehicle.setType(type);
        vehicle.setOwner(owner);
        return vehicle;
    }
}

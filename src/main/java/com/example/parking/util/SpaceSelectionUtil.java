package com.example.parking.util;

import com.example.parking.entity.SpaceType;
import com.example.parking.entity.UserType;

import java.util.List;

public final class SpaceSelectionUtil {

    private SpaceSelectionUtil() {
    }

    public static List<SpaceType> preferredTypes(UserType userType) {
        if (userType == UserType.VIP) {
            return List.of(SpaceType.VIP, SpaceType.ABONNE, SpaceType.STANDARD);
        }
        if (userType == UserType.ABONNE) {
            return List.of(SpaceType.ABONNE, SpaceType.STANDARD);
        }
        return List.of(SpaceType.STANDARD, SpaceType.PMR);
    }
}

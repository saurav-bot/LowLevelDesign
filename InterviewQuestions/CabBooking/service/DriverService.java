package InterviewQuestions.CabBooking.service;


import InterviewQuestions.CabBooking.entity.Location;
import InterviewQuestions.CabBooking.entity.Driver;
import java.util.concurrent.ConcurrentHashMap;


import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

public class DriverService {
    ConcurrentHashMap<String, Driver> driverMap = new ConcurrentHashMap<>();

    public void addDriver(Driver driver) {
        driverMap.put(driver.getDriverId(), driver);
    }

    public List<Driver> getAllNearestDriver(Location pickupLocation, int distance) {
        List<Driver> drivers = new ArrayList<>(driverMap.values());

        return drivers.stream().filter(x -> x.isAvailable() && pickupLocation.distanceTo(x.getLocation()) < distance).toList();

    }
}

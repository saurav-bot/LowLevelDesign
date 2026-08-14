package InterviewQuestions.CabBooking.service;

import InterviewQuestions.CabBooking.entity.Rider;

import java.util.concurrent.ConcurrentHashMap;

public class RiderService {
    ConcurrentHashMap<String, Rider> riderMap = new ConcurrentHashMap<>();

    public void addRider(Rider rider) {
        riderMap.put(rider.getRiderId(), rider);
    }

    public Rider getRider(String id) {
        return riderMap.get(id);
    }

}

package InterviewQuestions.CabBooking.enums;

public enum VehicleType {
    GO(100, 6),
    SEDAN(200, 8),
    XL(300, 10);

    private int baseFare;
    private double perKmRate;

    private VehicleType(int baseFare, double perKmRate) {
        this.baseFare = baseFare;
        this.perKmRate = perKmRate;
    }

    public int getBaseFare() {
        return baseFare;
    }

    public double getPerKmRate() {
        return perKmRate;
    }
}

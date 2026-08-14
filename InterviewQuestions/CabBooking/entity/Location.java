package InterviewQuestions.CabBooking.entity;

public class Location {
    private double x;
    private double y;

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double distanceTo(Location other) {
        double xDiff = x - other.getX();
        double yDiff = y - other.getY();

        return Math.sqrt(xDiff*xDiff + yDiff*yDiff);
    }
}
package InterviewQuestions.BookMyShow.entities;

import java.util.UUID;

public class Address {
    private final String addressId;
    private final String city;
    private final String addLine1;
    private final String addline2;
    private final String state;
    private final String country;
    private final String pincode;

    public Address(String city, String addLine1, String addline2, String state , String country, String pincode) {
        this.city = city;
        this.addLine1 = addLine1;
        this.addline2 = addline2;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
        this.addressId = UUID.randomUUID().toString();
    }

    public String getCity() {
        return this.city;
    }

    public String getAddLine1()  {
        return this.addLine1;
    }

    public String getAddline2() {
        return this.addline2;
    }

    public String getState() {
        return this.state;
    }

    public String getCountry() {
        return this.country;
    }

    public String getPincode() {
        return this.pincode;
    }

    public String getAddressId() {
        return this.addressId;
    }


}

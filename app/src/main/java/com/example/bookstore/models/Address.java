package com.example.bookstore.models;

public class Address {
    private String phone;
    private String addressId;
    private String country;
    private String province;
    private String district;
    private String ward;
    private String street;
    private String latitude;
    private String longitude;

    public Address() {}

    public Address(String phone, String country, String province, String district, String ward, String street, String latitude, String longitude) {
        this.phone = phone;
        this.country = country;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.street = street;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPhone() {return phone;}
    public void setPhone(String phoneNumber) {this.phone = phone;}

    public String getAddressId() {return addressId;}
    public void setAddressId(String addressId) {this.addressId = addressId;}

    public String getCountry() {return country;}
    public void setCountry(String country) {this.country = country;}

    public String getProvince() {return province;}
    public void setProvince(String province) {this.province = province;}

    public String getDistrict() {return district;}
    public void setDistrict(String district) {this.district = district;}

    public String getWard() {return ward;}
    public void setWard(String ward) {this.ward = ward;}

    public String getStreet() {return street;}
    public void setStreet(String street) {this.street = street;}

    public String getLatitude() {return latitude;}
    public void setLatitude(String latitude) {this.latitude = latitude;}

    public String getLongitude() {return longitude;}
    public void setLongitude(String longitude) {this.longitude = longitude;}
}

package com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.iplocation;

public class Location {
    private String country;
    private String city;
    private String site;

    public String getCountry() {
        return country;
    }

    public Location(String country, String city, String site) {
        this.country = country;
        this.city = city;
        this.site = site;
    }

    public void setCountry(String country) {
        this.country = country;

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public String toString() {
        return "Location{" +
                "country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", site='" + site + '\'' +
                '}';
    }
}

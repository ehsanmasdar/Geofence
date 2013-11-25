package com.asdar.geofence;

import android.location.Address;

public class GeofenceAddress implements Comparable<Object> {
    private double distance;
    private Address address;
    /*public GeofenceAddress(Locale locale) {
        super(locale);
	}*/

    public GeofenceAddress(Address ad, double lat1, double lon1) {
        address = ad;
        double lon2 = ad.getLongitude();
        double lat2 = ad.getLatitude();
        final int R = 6371; // Radius of the earth
        Double dLat = toRad(lat2 - lat1);
        Double dLon = toRad(lon2 - lon1);
        lat1 = toRad(lat1);
        lat2 = toRad(lat2);
        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
                * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        //
        Double d = R * c;
        distance = d;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    @Override
    public int compareTo(Object arg0) {
        GeofenceAddress other = (GeofenceAddress) arg0;
        return (int) (this.distance - other.distance);
    }

}

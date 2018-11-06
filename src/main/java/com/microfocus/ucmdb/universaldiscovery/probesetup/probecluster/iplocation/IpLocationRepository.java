package com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.iplocation;

import java.util.*;

public class IpLocationRepository {
    private Map<String, Integer> countryLoad = new HashMap<String, Integer>();
    private Map<String, Set<String>> countryDetail = new HashMap<String, Set<String>>();

    private Map<String, Integer> cityLoad = new HashMap<String, Integer>();
    private Map<String, Set<String>> cityDetail = new HashMap<String, Set<String>>();

    private Map<String, Integer> siteLoad = new HashMap<String, Integer>();
    private Map<String, Set<Long>> siteDetal = new HashMap<String, Set<Long>>();

    public Map<String, Set<Long>> getSiteDetal() {
        if(siteDetal == null){
            siteDetal = new HashMap<String, Set<Long>>();
        }
        return siteDetal;
    }

    public void setSiteDetal(Map<String, Set<Long>> siteDetal) {
        this.siteDetal = siteDetal;
    }

    public Map<String, Integer> getCountryLoad() {
        if(countryLoad == null){
            countryLoad = new HashMap<String, Integer>();
        }
        return countryLoad;
    }

    public void setCountryLoad(Map<String, Integer> countryLoad) {
        this.countryLoad = countryLoad;
    }

    public Map<String, Set<String>> getCountryDetail() {
        if(countryDetail == null){
            countryDetail = new HashMap<String, Set<String>>();
        }
        return countryDetail;
    }

    public void setCountryDetail(Map<String, Set<String>> countryDetail) {
        this.countryDetail = countryDetail;
    }

    public Map<String, Integer> getCityLoad() {
        if(cityLoad == null){
            cityLoad = new HashMap<String, Integer>();
        }
        return cityLoad;
    }

    public void setCityLoad(Map<String, Integer> cityLoad) {
        this.cityLoad = cityLoad;
    }

    public Map<String, Set<String>> getCityDetail() {
        if(cityDetail == null){
            cityDetail = new HashMap<String, Set<String>>();
        }
        return cityDetail;
    }

    public void setCityDetail(Map<String, Set<String>> cityDetail) {
        this.cityDetail = cityDetail;
    }

    public Map<String, Integer> getSiteLoad() {
        if(siteLoad == null){
            siteLoad = new HashMap<String, Integer>();
        }
        return siteLoad;
    }

    public void setSiteLoad(Map<String, Integer> siteLoad) {
        this.siteLoad = siteLoad;
    }

    public void addIpRange(Long ipStart, Long ipEnd, Location location){
        addLocation(location, ipStart);
        Integer load = new Integer((int) (ipEnd - ipStart + 1));
        getCountryLoad().put(location.getCountry(), getCountryLoad().getOrDefault(location.getCountry(), 0) + load);
        getCityLoad().put(location.getCity(), getCityLoad().getOrDefault(location.getCity(), 0) + load);
        getSiteLoad().put(location.getSite(), getSiteLoad().getOrDefault(location.getSite(), 0) + load);
    }

    private void addLocation(Location location, Long ip) {
        Set<Long> ipList = getSiteDetal().getOrDefault(location.getSite(), new HashSet<>());
        if(!ipList.contains(ip)){
            ipList.add(ip);
        }
        getSiteDetal().put(location.getSite(), ipList);

        Set<String> siteList = getCityDetail().getOrDefault(location.getCity(), new HashSet<String>());
        if(!siteList.contains(location.getSite())){
            siteList.add(location.getSite());
        }
        getCityDetail().put(location.getCity(), siteList);


        Set<String> cityList = getCountryDetail().getOrDefault(location.getCountry(), new HashSet<String>());
        if(!cityList.contains(location.getCountry())){
            cityList.add(location.getCity());
        }
        getCountryDetail().put(location.getCountry(), cityList);
    }

    public Map<String, List<Long>> generateProbeIprange(int maxIp, int suggestIp){
        Map<String, List<Long>> rlt = new HashMap<String, List<Long>>();
        int count = 1;
        String sharedProbe = "";
        int sharedProbeLoad = 0;
        List<Long> sharedProbeips = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : getCountryLoad().entrySet()){
            if(entry.getValue() <= maxIp && entry.getValue() >= suggestIp){
                // assign 1 probe
                String probeName = "Probe" + count;
                List<Long> ipList = getIpListFromCountry(entry.getKey());

                rlt.put(probeName, ipList);
                count++;
            } else if(entry.getValue() < suggestIp){
                // add to share probe
                if(sharedProbeLoad == 0){
                    sharedProbe = "Probe" + count;
                    count++;
                    sharedProbeLoad = getCountryLoad().get(entry.getKey());
                    sharedProbeips = getIpListFromCountry(entry.getKey());
                } else{
                    sharedProbeips.addAll(getIpListFromCountry(entry.getKey()));
                    sharedProbeLoad += getCountryLoad().get(entry.getKey());
                    if(sharedProbeLoad > maxIp){
                        rlt.put(sharedProbe, sharedProbeips);
                        sharedProbeLoad = 0;
                        sharedProbeips = null;
                        sharedProbe = "";
                    }
                }
            } else if(entry.getValue() > maxIp){
                // split probe
                int load = 0;
                List<Long> ipList = new ArrayList<Long>();
                for(String city:getCountryDetail().get(entry.getKey())){
                    load += getCityLoad().get(city);
                    ipList.addAll(getIpListFromCity(entry.getKey(), city));
                    if(load > maxIp){
                        String probeName = "Probe" + count;
                        count++;
                        rlt.put(probeName, ipList);
                        load = 0;
                        ipList = new ArrayList<Long>();
                    }
                }
                String probeName = "Probe" + count;
                count++;
                rlt.put(probeName, ipList);
                load = 0;
                ipList = new ArrayList<Long>();
            }
        }
        // add shared probe
        if(sharedProbeLoad > 0){
            rlt.put(sharedProbe, sharedProbeips);
        }


        return rlt;
    }

    private List<Long> getIpListFromCity(String country, String city) {
        List<Long> rlt = new ArrayList<Long>();
        for(String site: getCityDetail().get(city)){
            rlt.addAll(getSiteDetal().get(site));
        }
        return rlt;
    }

    private List<Long> getIpListFromCountry(String country) {
        List<Long> rlt = new ArrayList<Long>();
        ArrayList<String> siteList = new ArrayList<String>();
        for(String city : getCountryDetail().get(country)){
            siteList.addAll(getCityDetail().get(city));
        }
        for(String site: siteList){
            rlt.addAll(siteDetal.get(site));
        }
        return rlt;
    }

    public void init(Map<Long, Long> ipRangeSet) {
        IpLocationDB locationDB = IpLocationDB.getInstance();
        for(Map.Entry<Long, Long> entry : ipRangeSet.entrySet()){
            Location location = locationDB.getLocation(entry.getKey());
            if(location != null){
                addIpRange(entry.getKey(), entry.getValue(), location);
            }

        }

    }
}

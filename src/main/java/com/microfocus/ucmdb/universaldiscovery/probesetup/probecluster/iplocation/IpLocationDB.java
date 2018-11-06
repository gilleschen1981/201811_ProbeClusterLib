package com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.iplocation;

import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.util.IpRangeUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class IpLocationDB {
    private static IpLocationDB instance = null;
    public static IpLocationDB getInstance(){
        if(instance == null){
            instance = new IpLocationDB();
        }
        return instance;
    }

    Map<Long, Location> locationMap = new HashMap<Long, Location>();
    Map<Long, Long> ipRangeMap = new HashMap<Long, Long>();
    List<Long> orderedIpRangesList =  new ArrayList<Long>();

    public IpLocationDB() {
        File file = new File("IP2LOCATION-LITE-DB3.CSV");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;

            while ((tempString = reader.readLine()) != null) {
                String split[] = tempString.split(",");
                Long startIp = Long.valueOf(split[0].substring(1, split[0].length() - 1));
                Long endIp = Long.valueOf(split[1].substring(1, split[1].length() - 1));
                String country = split[2].substring(1, split[2].length() - 1);
                String city = split[4].substring(1, split[4].length() - 1);
                String site = split[5].substring(1, split[5].length() - 1);
                Location location = new Location(country, city, site);
                locationMap.put(startIp, location);
                ipRangeMap.put(startIp, endIp);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public Location getLocation(Long ip) {
        if(orderedIpRangesList.size() <= 0){
            orderedIpRangesList = new ArrayList<Long>(ipRangeMap.keySet());
            Collections.sort(orderedIpRangesList);
        }

        Location rlt = null;
        for(int i = 0; i < orderedIpRangesList.size() - 1; i++){
            if(ip >= orderedIpRangesList.get(i) && ip < orderedIpRangesList.get(i + 1)){
                rlt = locationMap.get(orderedIpRangesList.get(i));
            }
        }
        return rlt;
    }
}

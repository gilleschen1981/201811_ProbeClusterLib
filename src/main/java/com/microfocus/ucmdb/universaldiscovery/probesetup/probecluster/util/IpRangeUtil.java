package com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.util;


import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.model.IpClassification;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to do ipralated calculation
 */
public class IpRangeUtil {
    static long AClassPublicStart1 = convertIPFromString2Int("1.0.0.0");
    static long AClassPublicEnd1 = convertIPFromString2Int("9.255.255.255");

    static long AClassPrivateStart =  convertIPFromString2Int("10.0.0.0");
    static long AClassPrivateEnd =  convertIPFromString2Int("10.255.255.255");

    static long AClassPublicStart2 = convertIPFromString2Int("11.0.0.0");
    static long AClassPublicEnd2 = convertIPFromString2Int("126.255.255.255");

    static long BClassPublicStart1 = convertIPFromString2Int("128.0.0.0");
    static long BClassPublicEnd1 = convertIPFromString2Int("172.15.255.255");

    static long BClassPrivateStart =  convertIPFromString2Int("172.16.0.0");
    static long BClassPrivateEnd =  convertIPFromString2Int("172.31.255.255");

    static long BClassPublicStart2 = convertIPFromString2Int("172.32.0.0");
    static long BClassPublicEnd2 = convertIPFromString2Int("191.255.255.255");

    static long CClassPublicStart1 = convertIPFromString2Int("192.0.0.0");
    static long CClassPublicEnd1 = convertIPFromString2Int("192.167.255.255");


    static long CClassPrivateStart =  convertIPFromString2Int("192.168.0.0");
    static long CClassPrivateEnd =  convertIPFromString2Int("192.168.255.255");

    static long CClassPublicStart2 = convertIPFromString2Int("192.169.0.0");
    static long CClassPublicEnd2 = convertIPFromString2Int("223.255.255.255");





    /**
     * change ipaddress to long type for calculation purpose
     * @param ip : 192.168.0.1
     * @return : 3232235521
     */
    static public long convertIPFromString2Int(String ip){
        String[] split = ip.split("\\.");
        if(split.length != 4){
            return -1;
        }
        long sum = 0;
        for(String v : split){
            sum *= 256;
            sum += Integer.valueOf(v);
        }

        return sum;
    }

    /**
     * change ip to String for readability
     * @param ip : 3232235521
     * @return : 192.168.0.1
     */
    static public String convertIPFromInt2tring(long ip){
        StringBuilder rlt = new StringBuilder();
        while(ip > 0){
            long number = ip%256;
            rlt = rlt.insert(0, new String(String.valueOf(number) + "."));
            ip = (ip - number)/256;
        }
        rlt.deleteCharAt(rlt.length() - 1);
        return rlt.toString();
    }

    /**
     * give the classification of ip. Public or private, ClassA,B,C
     * @param ip
     * @return
     */
    static public IpClassification analyzeIPType(String ip){
        long ipInt = convertIPFromString2Int(ip);

        return analyzeIPType(ipInt);
    }

    /**
     * give the classification of ip. Public or private, ClassA,B,C
     * @param ip
     * @return
     */
    static public IpClassification analyzeIPType(Long ip){
        if((ip >= AClassPublicStart1 && ip <= AClassPublicEnd1)||(ip >= AClassPublicStart2 && ip <= AClassPublicEnd2)){
            return IpClassification.publicClassA;
        }
        if(ip >= AClassPrivateStart && ip <= AClassPrivateEnd){
            return IpClassification.privateClassA;
        }
        if((ip >= BClassPublicStart1 && ip <= BClassPublicEnd1)||(ip >= BClassPublicStart2 && ip <= BClassPublicEnd2)){
            return IpClassification.publicClassB;
        }
        if(ip >= BClassPrivateStart && ip <= BClassPrivateEnd){
            return IpClassification.privateClassB;
        }

        if((ip >= CClassPublicStart1 && ip <= CClassPublicEnd1)||(ip >= CClassPublicStart2 && ip <= CClassPublicEnd2)){
            return IpClassification.publicClassC;
        }
        if(ip >= CClassPrivateStart && ip <= CClassPrivateEnd){
            return IpClassification.privateClassC;
        }


        return IpClassification.omit;
    }


    /**
     * Calculate the distance  between 2 ip
     * For public: use geographic db. same city, same contry, same continent
     * For private: switch to double type and calculate the distance.
     *     Consider the subnet, when subnet = 24, 192.168.0.0 to 192.168.0.255 is smaller than 192.168.0.255 to 192.168.1.0
     * @param ip1
     * @param ip2
     * @param subnetMask
     * @return
     */
    static public double calculateDistance(String ip1, String ip2, int[] subnetMask){
        return calculateDistance(IpRangeUtil.convertIPFromString2Int(ip1), IpRangeUtil.convertIPFromString2Int(ip2), subnetMask);
    }

    /**
     * Calculate the distance  between 2 ip
     * For public: use geographic db. same city, same contry, same continent
     * For private: switch to double type and calculate the distance.
     *     Consider the subnet, when subnet = 24, 192.168.0.0 to 192.168.0.255 is smaller than 192.168.0.255 to 192.168.1.0
     * @param ip1
     * @param ip2
     * @param subnetMask
     * @return
     */
    static public double calculateDistance(Long ip1, Long ip2, int[] subnetMask){
        double rlt = 0;

        // type don't match
        IpClassification ip1Type = analyzeIPType(ip1);
        IpClassification ip2Type = analyzeIPType(ip2);
        if((ip1Type.getClasstype() -ip2Type.getClasstype())!= 0){
            return Double.MAX_VALUE;
        }

        // private ip calculation
        if(ip1Type.isPrivateIp()){
            return calculatePrivateIPDistance(ip1, ip2, subnetMask);
        }

        // public ip calculation
        if(ip1Type.isPublicIp()){
            return calculatePublicIPDistance(ip1, ip2);
        }

        return rlt;
    }


    private static double calculatePublicIPDistance(Long ip1, Long ip2) {
        return 0;
    }

    private static double calculatePrivateIPDistance(Long ip1, Long ip2, int[] subnet) {
        if(subnet == null){
            subnet = new int[32];
        }

        return Math.abs(ip1 - ip2);
    }


    public static void main(String[] args) {
        long max = Long.MAX_VALUE;
        int count = 0;
        while (max > 0){
            max = (long)Math.floor(max/2);
            count ++;
        }
        System.out.println(count);
    }

    public static List<Long> getListOrderedByDistance(Long ip, List<Long> probeIpList) {
        List<Long> rlt = new ArrayList<Long>();
        List<Double> distanceList = new ArrayList<Double>();
        for(int i = 0; i < probeIpList.size(); i++){
            distanceList.add(IpRangeUtil.calculateDistance(ip, probeIpList.get(i), null));
        }

        while(rlt.size() < probeIpList.size()){
            Double tempDis = Double.MAX_VALUE;
            for(Double d : distanceList){
                if(d < tempDis){
                    tempDis = d;
                }
            }
            rlt.add(probeIpList.get(distanceList.indexOf(tempDis)));
            distanceList.set(distanceList.indexOf(tempDis), Double.MAX_VALUE);
        }
        return rlt;
    }
}

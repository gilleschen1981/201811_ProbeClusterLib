package com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.model;

import com.hp.ucmdb.api.discovery.types.IPRange;
import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.iplocation.IpLocationDB;
import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.iplocation.IpLocationRepository;
import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.util.IpRangeUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;



/**
 * to process no duplicate iprange (from 1 domain)
 */
public class IPRangeSetRepository {
    static final public int IPCI_PERPROBE_THRESHOLD = 15000;
    static final public int IP_PERPROBE_THRESHOLD = 100000;
    static final public int IPCI_PERPROBE_SUGGESTION = 3000;
    static final public int IP_PERPROBE_SUGGESTION = 10000;

    /**
     * FromIp, ToIp
     */
    private Map<Long, Long> ipRangeSet = new HashMap<Long, Long>();
    /**
     * FromIp, number of ipaddress(triggers) in range
     */
    private Map<Long, Integer> ipRangeSize = new HashMap<Long, Integer>();
    /**
     * iprange type map
     * IpType, FromIp
     */
    private Map<IpClassification, Long> ipTypeMap = new HashMap<>();

    /**
     *  probename, List<iprange>
     */
    Map<String, List<Long>> probeIprange = new HashMap<String, List<Long>>();

    /**
     * probename, probeip
     */
    Map<String, Long>probeIps = new HashMap<String, Long>();

    public Map<String, Long> getProbeIps() {
        return probeIps;
    }

    public void setProbeIps(Map<String, Long> probeIps) {
        this.probeIps = probeIps;
    }

    public Map<Long, Long> getIpRangeSet() {
        return ipRangeSet;
    }

    public Map<IpClassification, Long> getIpTypeMap() {
        return ipTypeMap;
    }


    public Map<Long, Integer> getIpRangeSize() {
        return ipRangeSize;
    }

    public void setIpRangeSize(Map<Long, Integer> ipRangeSize) {
        this.ipRangeSize = ipRangeSize;
    }

    public Map<String, List<Long>> getProbeIprange() {
        if(probeIprange == null){
            probeIprange = new HashMap<String, List<Long>>();
        }
        return probeIprange;
    }

    public void setProbeIprange(Map<String, List<Long>> probeIprange) {
        this.probeIprange = probeIprange;
    }



    public int count() {
        return ipRangeSet.size();
    }

    public int countIpCis(){
        int rlt = 0;
        for(Integer i : ipRangeSize.values()){
            rlt += i;
        }
        return rlt;
    }

    public int countIps(){
        int rlt = 0;
        for(Map.Entry<Long, Long> entry : ipRangeSet.entrySet()){
            rlt += (entry.getValue() - entry.getKey() + 1);
        }
        return rlt;
    }

    /**
     * get a list of ipaddress, and fit it to exisitng range
     * @param ipList
     */
    public void fitIpaddress2IpRange(List<String> ipList) {
        List<Long> ipRangesList =  new ArrayList<Long>(ipRangeSet.keySet());
        Collections.sort(ipRangesList);

        for(String ip: ipList){
            long ipAddress = IpRangeUtil.convertIPFromString2Int(ip);
            if(ipAddress <= 0){
                System.out.println("[ERROR]ipAddress error: " + ip);
                continue;
            }

            long ipRange = findIntInSortedList(ipRangesList, ipAddress);
            if(ipAddress >= ipRange && ipAddress <= ipRangeSet.get(ipRange)){
                ipRangeSize.put(ipRange, ipRangeSize.getOrDefault(ipRange, 0) + 1);
            } else{
//                System.out.println("[WARNING]ipAddress " + ip + " is not in the range " +
//                        IpRangeUtil.convertIPFromInt2tring(ipRange) + " to " +
//                        IpRangeUtil.convertIPFromInt2tring(ipRangeSet.get(ipRange)));
                continue;
            }
        }
    }

    /**
     * find the closest number to n in a sorted list
     * @param list
     * @param n
     * @return
     */
    private long findIntInSortedList(List<Long> list, long n) {
        if (list == null || n < 0){
            return 0;
        }

        int start = 0;
        int end = list.size() - 1;
        if(n <= list.get(start)){
            return list.get(start);
        }
        if(n >= list.get(end)){
            return list.get(end);
        }

        while(end > start + 1){
            if(list.get((start + end)/2) == n){
                return list.get((start + end)/2);
            }
            else if(list.get((start + end)/2) > n){
                end = (start + end)/2;
            } else{
                start = (start + end)/2;
            }
        }

        return list.get(start);
    }


    /**
     * give a list of probe with ip, automatically fit the ipranges to the probe.
     * @param probeIps
     */
    public void generateProbeRange(Map<Long, String> probeIps) {

        List<Long> ipRangesList =  new ArrayList<Long>(ipRangeSet.keySet());
        Collections.sort(ipRangesList);

        int totalIpNumber = 0;
        for(Map.Entry<Long, Integer> entry : ipRangeSize.entrySet()){
            totalIpNumber += entry.getValue();
        }
        int averageIPNumber = totalIpNumber/probeIps.size();
        // detect range with unusual size
        List<Long> bigRangeList = new ArrayList<Long>();
        for(Map.Entry<Long, Integer> entry : ipRangeSize.entrySet()){
            if(entry.getValue() > averageIPNumber){
                System.out.println("[WARNING]IpRange " + IpRangeUtil.convertIPFromInt2tring(entry.getKey()) + " has " +
                        entry.getValue() + "Ipaddress.");
                bigRangeList.add(entry.getKey());
            }
        }

        // split range to probes
        List<Long> probeIpList =  new ArrayList<Long>(probeIps.keySet());
        Collections.sort(probeIpList);

        // deal with huge range first
        for(long iprange: bigRangeList){
            long probeIp = findIntInSortedList(probeIpList, iprange);
            List<Long> rangelist = probeIprange.getOrDefault(probeIps.get(probeIp), new ArrayList<Long>());
            rangelist.add(iprange);
            probeIprange.put(probeIps.get(probeIp), rangelist);
            ipRangesList.remove(ipRangesList.indexOf(iprange));
            probeIps.remove(probeIp);
        }
        int ipNumber = 0;
        int start = 0;
        probeIpList =  new ArrayList<Long>(probeIps.keySet());
        Collections.sort(probeIpList);
        totalIpNumber = 0;
        for(long i : ipRangesList){
            totalIpNumber += ipRangeSize.get(i);
        }
        averageIPNumber = totalIpNumber/probeIps.size();
        for(int i = 0 ; i < ipRangesList.size(); i++ ){
            long iprange = ipRangesList.get(i);
            ipNumber += ipRangeSize.get(iprange);
            if(ipNumber >= averageIPNumber){
                List<Long> rangelist = probeIprange.getOrDefault(probeIps.get(probeIpList.get(0)), new ArrayList<Long>());
                rangelist.addAll(ipRangesList.subList(start, i + 1));
                probeIprange.put(probeIps.get(probeIpList.get(0)), rangelist);
                start = i + 1;
                probeIpList.remove(0);
                ipNumber = 0;
            }
        }

        if(probeIpList.size() > 0){
            List<Long> rangelist = probeIprange.getOrDefault(probeIps.get(probeIpList.get(0)), new ArrayList<Long>());
            rangelist.addAll(ipRangesList.subList(start, ipRangesList.size()));
            probeIprange.put(probeIps.get(probeIpList.get(0)), rangelist);
        }

    }

    /**
     * print the generated result to a file.
     * @param outputFile
     */
    public void output(File outputFile) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(outputFile);
            for(Map.Entry<String, List<Long>> entry : probeIprange.entrySet()){
                if(entry.getValue() != null){
                    for(long fromIp : entry.getValue()){
                        long toIp = ipRangeSet.get(fromIp);
                        String s = entry.getKey() + ", " + IpRangeUtil.convertIPFromInt2tring(fromIp) + ", "
                                + IpRangeUtil.convertIPFromInt2tring(toIp) + ", "
                                + ipRangeSize.get(fromIp);
                        fw.write(s + "\n");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            if (fw != null){
                try {
                    fw.close();
                } catch (IOException e) {
                    throw new RuntimeException("Close file failure!!!");
                }
            }
        }
    }

    public void init(Map<Long, Long> ipranges, Map<Long, Integer> ipRangeSize, Map<String, Long> probeIps, Map<String, List<Long>> probeIprange) {
        if(ipranges == null ){
            return;
        }

        for(Map.Entry<Long, Long> entry : ipranges.entrySet()){
            ipRangeSet.put(entry.getKey(), entry.getValue());
            ipTypeMap.put(IpRangeUtil.analyzeIPType(IpRangeUtil.convertIPFromInt2tring(entry.getKey())), entry.getKey());
        }
        if(ipRangeSize != null && ipRangeSize.size() >0){
            setIpRangeSize(ipRangeSize);
        }
        if(probeIps != null && probeIps.size() > 0){
            setProbeIps(probeIps);
        }
        if(probeIprange != null && probeIprange.size() > 0){
            setProbeIprange(probeIprange);
        }
    }

    public int getProbeNumberSuggestion() {
        int probeFromIpCi = countIpCis()/IPCI_PERPROBE_SUGGESTION + 1;
        int probeFromIp = countIps()/IP_PERPROBE_SUGGESTION + 1;
        return Math.max(probeFromIp, probeFromIpCi);
    }

    public Map<String,Integer> calculateProbeLoad() {
        Map<String, Integer> rlt = new HashMap<String, Integer>();
        for(Map.Entry<String,List<Long>> entry : getProbeIprange().entrySet()){
            int load = 0;
            for(Long l : entry.getValue()){
                load += ipRangeSize.get(l);
            }
            rlt.put(entry.getKey(), load);
        }
        return rlt;
    }

    public boolean isBalanced() {
        boolean rlt = true;
        Map<String, Integer> probeLoad = calculateProbeLoad();
        int totalLoad = 0;
        int maxLoad = 0;
        int probeNumber = probeLoad.size();
        for(Integer i : probeLoad.values()){
            totalLoad += i;
            if(i > maxLoad){
                maxLoad = i;
            }
        }

        /*
            probe with max load is bigger than suggested probe load,
            And bigger than n * average load
            N = 1 + 0.5 * (probenumber - 1)
            *** When all other probes are not even half loaded
         */

        if((maxLoad > IPCI_PERPROBE_SUGGESTION) && (maxLoad > ((totalLoad/probeNumber) * ( 1 + 0.5 * (probeNumber - 1))))){
            rlt = false;
        }

        return rlt;
    }

    public IpMoveAction generateBalanceAction() {
        IpMoveAction rlt = new IpMoveAction(0L, "", "");
        if(isBalanced()){
            return null;
        }
        Map<String, Integer> probeLoad = calculateProbeLoad();
        int maxLoad = 0;
        int minLoad = Integer.MAX_VALUE;
        String maxLoadProbe = null;
        String minLoadProbe = null;
        for(Map.Entry<String, Integer> entry : probeLoad.entrySet()){
            if(entry.getValue() > maxLoad){
                maxLoad = entry.getValue();
                maxLoadProbe = entry.getKey();
            }
            if(entry.getValue() < minLoad){
                minLoad = entry.getValue();
                minLoadProbe = entry.getKey();
            }
        }
        if(minLoad < IPCI_PERPROBE_THRESHOLD){
            rlt.setSourceProbe(maxLoadProbe);
            rlt.setTargetProbe(minLoadProbe);
        }

        // find the appropriate ip of maxloadprobe to move.
        if(maxLoadProbe != null && minLoadProbe != null){
            List<Long> ipRanges = getProbeIprange().get(maxLoadProbe);
            Long probeIp = getProbeIps().get(maxLoadProbe);
            int[] subnet = new int[32];
            for(int i = 0; i < subnet.length; i++){
                subnet[i] = 0;
            }

            if(probeIp > 0 && ipRanges != null && ipRanges.size() > 0){
                long distance = 0;

                for(Long l : ipRanges){
                    double tDis = IpRangeUtil.calculateDistance(IpRangeUtil.convertIPFromInt2tring(probeIp),IpRangeUtil.convertIPFromInt2tring(l), subnet);
                    if (distance < tDis){
                        distance = (long)tDis;
                        rlt.setIpRange(l);
                    }
                }

            }
        }

        return rlt;
    }

    public String findProbeFromIpRange(Long ipRange) {
        String rlt = "";
        for(Map.Entry<String, List<Long>> entry : probeIprange.entrySet()){
            for(Long l : entry.getValue()){
                if(ipRange.equals(l)){
                    return entry.getKey();
                }
            }
        }
        return rlt;
    }

    public String dataConsistancyCheck(){
        String rlt = "";
        if(getIpRangeSet() == null && getIpRangeSet().size() <= 0){
            rlt += "[ERROR]No IpRange configured!\n";
            return rlt;
        }
        IpClassification type = IpRangeUtil.analyzeIPType(getIpRangeSet().keySet().toArray(new Long[getIpRangeSet().size()])[0]);
        for(Long l : getIpRangeSet().keySet()){
            if(type != IpRangeUtil.analyzeIPType(l)){
                rlt += "[ERROR]Ip " + IpRangeUtil.convertIPFromInt2tring(l) + " is not of type " + type;
            }
        }

        if(getIpRangeSize() == null || getIpRangeSize().size() <= 0){
            rlt += "[ERROR]Missing IpRange load information! " + getIpRangeSet().size() + " Iprange configured, " + getIpRangeSize() + " has load info." + "\n";
        }

        if(getProbeIps() == null || getProbeIps().size() <= 0){
            rlt += "[INFO]No probe information!\n";
        }

        if(getProbeIps() != null){
            if(getProbeIps().size() * IPCI_PERPROBE_THRESHOLD < countIpCis()){
                rlt += "[ERROR]Cluster overload. Probe numer: " + getProbeIps().size() + ". Probe max load: " + IPCI_PERPROBE_THRESHOLD + ". Total load: " + countIpCis();
            }

        }
        if(getProbeIprange() == null || getProbeIprange().size() <= 0){
            rlt += "[INFO]Not yet load balanced.\n";
        }
        if(getProbeIprange() != null && getProbeIps() != null && getProbeIprange().size()!=getProbeIps().size()){
            rlt += "[ERROR]" + getProbeIps().size() + " probes exist, but only " + getProbeIprange().size() + " are load balaced!\n";
        }

        if(getProbeIprange() != null){
            int iprangeCount = 0;
            for(Map.Entry<String, List<Long>> entry : getProbeIprange().entrySet()){
                iprangeCount += entry.getValue().size();
            }
            if(iprangeCount != getIpRangeSet().size()){
                rlt += "[ERROR]" + getIpRangeSet().size() + " iprange in total, but " + iprangeCount + " are balanced to probe.\n";
            }
        }


        return rlt;
    }

    public List<IpSplitAction> inputValidation() {
        List<IpSplitAction> rlt = new ArrayList<IpSplitAction>();
        int totalIp = countIps();
        int probeNumber = getProbeIps().size();
        for(Map.Entry<Long, Long> entry : getIpRangeSet().entrySet()){
            if((entry.getValue() - entry.getKey()) > totalIp/(2 * probeNumber)){
                rlt.add(new IpSplitAction("Range too big.", entry.getKey(), getIpRangeSet().get(entry.getKey()), getIpRangeSize().get(entry.getKey())));
            }
        }

        int totalIpCis = countIpCis();
        for(Map.Entry<Long, Integer> entry : getIpRangeSize().entrySet()){
            if(entry.getValue() > totalIpCis/probeNumber){
                rlt.add(new IpSplitAction("Too much load.", entry.getKey(), getIpRangeSet().get(entry.getKey()), getIpRangeSize().get(entry.getKey())));
            }
        }
        return rlt;
    }

    public void generateProbeRangeByDistance() {
        probeIprange = new HashMap<String, List<Long>>();
        // iprange list by order
        List<Long> ipRangeList = new ArrayList<Long>();
        for(Long l : getIpRangeSet().keySet()){
            ipRangeList.add(l);
        }
        Collections.sort(ipRangeList);

        // probe list by order
        List<Long> probeIpList = new ArrayList<Long>();
        for(Long l : getProbeIps().values()){
            probeIpList.add(l);
        }
        Collections.sort(probeIpList);

        // iterate iprangeset

        List<Long> availableProbeList = getAvailableProbeList();
        for(Long l : ipRangeList){
            // if all probe full, find the nearest probe
            if(availableProbeList.size() == 0){
                List<Long> probeOrderedbyDistance = IpRangeUtil.getListOrderedByDistance(l, probeIpList);
                addProbeIprange(probeOrderedbyDistance.get(0), l);
            } else{
                // find the probe list in order
                List<Long> probeOrderedbyDistance = IpRangeUtil.getListOrderedByDistance(l, availableProbeList);
                addProbeIprange(probeOrderedbyDistance.get(0), l);
                // update the available probelist
                availableProbeList = getAvailableProbeList();
            }


        }


    }

    public List<Long> getAvailableProbeList() {
        List<Long> rlt = new ArrayList<Long>();
        for(Map.Entry<String, Long> entry : getProbeIps().entrySet()){
            int load = getProbeLoad(entry.getKey());
            if(load < IPCI_PERPROBE_THRESHOLD){
                rlt.add(entry.getValue());
            }
        }
        return rlt;
    }

    public int getProbeLoad(String probeName) {
        int load = 0;
        List<Long> ipRangeList = getProbeIprange().getOrDefault(probeName, new ArrayList<Long>());
        for(Long l : ipRangeList){
            load += getIpRangeSize().getOrDefault(l, 0);
        }
        return load;
    }

    private void addProbeIprange(Long probeIp, Long ip) {
        String probeName = findProbeByIp(probeIp);
        List<Long> ipRangeList = getProbeIprange().getOrDefault(probeName, new ArrayList<Long>());
        ipRangeList.add(ip);
        getProbeIprange().put(probeName, ipRangeList);
    }

    private String findProbeByIp(Long probeIp) {
        for(Map.Entry<String, Long> entry : probeIps.entrySet()){
            if(probeIp.equals(entry.getValue())){
                return entry.getKey();
            }
        }
        return null;
    }

    public String outputProbeIpRange() {
        String rlt = "In total " + probeIprange.size() + " probes: \n";
        for(Map.Entry<String, List<Long>> entry : getProbeIprange().entrySet()){
            List<Long> list = entry.getValue();
            rlt += "Probe " + entry.getKey() + ": Ip: " +  IpRangeUtil.convertIPFromInt2tring(getProbeIps().get(entry.getKey())) +  ". Ipranges count: " + (list == null ? 0 : list.size()) + ".  Load: " +  getProbeLoad(entry.getKey()) + "\n";
            Long probeIp = getProbeIps().get(entry.getKey());
            if(IpRangeUtil.analyzeIPType(probeIp).equals(IpClassification.publicClassA) || IpRangeUtil.analyzeIPType(probeIp).equals(IpClassification.publicClassB) || IpRangeUtil.analyzeIPType(probeIp).equals(IpClassification.publicClassC)){
                rlt += "Probe location: " + IpLocationDB.getInstance().getLocation(probeIp);
            }


            if(list != null){
                for(Long l : list){
                    rlt += "\t" + IpRangeUtil.convertIPFromInt2tring(l) + "-" + IpRangeUtil.convertIPFromInt2tring(getIpRangeSet().get(l)) + "\n";
                }
            }
        }
        return rlt;
    }

    public String addNewRange(String ipRangeStart, String ipRangeEnd, Integer load){
        return addNewRange(IpRangeUtil.convertIPFromString2Int(ipRangeStart), IpRangeUtil.convertIPFromString2Int(ipRangeEnd), load);
    }
    public String addNewRange(Long ipRangeStart, Long ipRangeEnd, Integer load) {
        String rlt = null;
        if(getIpRangeSet().get(ipRangeStart) != null){
            return rlt;
        }
        List<Long> probeIpList = new ArrayList<Long>();
        for(Long l : getProbeIps().values()){
            probeIpList.add(l);
        }
        Collections.sort(probeIpList);
        List<Long> availableProbeList = getAvailableProbeList();
        List<Long> probeOrderedbyDistance;
        if(availableProbeList.size() == 0){
            probeOrderedbyDistance = IpRangeUtil.getListOrderedByDistance(ipRangeStart, probeIpList);

        } else{
            // find the probe list in order
            probeOrderedbyDistance = IpRangeUtil.getListOrderedByDistance(ipRangeStart, availableProbeList);

        }
        getIpRangeSet().put(ipRangeStart, ipRangeEnd);
        getIpRangeSize().put(ipRangeStart, load);
        addProbeIprange(probeOrderedbyDistance.get(0), ipRangeStart);
        rlt = findProbeFromIpRange(probeOrderedbyDistance.get(0));

        return rlt;

    }

    public void generateProbePublicIpRange() {
        IpLocationRepository ipLocationRepo = new IpLocationRepository();
        ipLocationRepo.init(ipRangeSet);
        probeIprange = ipLocationRepo.generateProbeIprange(IP_PERPROBE_THRESHOLD, IP_PERPROBE_SUGGESTION);
        for(Map.Entry<String, List<Long>> entry: probeIprange.entrySet()){
            List<Long> value = entry.getValue();
            if(value != null && value.size() > 0){
                Long ip = value.get(0);
                getProbeIps().put(entry.getKey(), ip);
            }
        }
    }
}

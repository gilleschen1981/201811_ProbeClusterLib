package com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.input;

import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.model.IPRangeSetRepository;
import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.util.IpRangeUtil;
import jdk.internal.org.objectweb.asm.tree.InnerClassNode;

import java.io.*;
import java.util.*;

/**
 * used to automatically generate iprange set test data.
 */
public class TestDataRepository {
    static final public String IPRANGE_SEPARATOR =  "-";
    static final public String FILED_SEPARATOR =  "\t";
    static final public String TESTREPO_FILE = "testdata.csv";

    static final private int IPRANGE_POS = 0;
    static final private int IPRANGESIZE_POS = 1;
    static final private int IPRANGEPROBE_POS = 2;

    public static void main( String[] astrArgs )
    {
        generateFullRepo();

    }

    private static void generateFullRepo() {
        TestDataRepository testRepo = new TestDataRepository();
        IPRangeSetRepository repo = new IPRangeSetRepository();
        Map<Long, Long> testData = testRepo.generateTestRange("172.16.0.0", "172.16.100.0", 24);
        List<Long> ipRangeList = new ArrayList<Long>();
        for(Long l : testData.keySet()){
            ipRangeList.add(l);
        }
        Collections.sort(ipRangeList);

        Map<Long, Integer> ipRangeSize = new HashMap<Long, Integer>();
        int count = 0;
        for(Long l : ipRangeList){
            ipRangeSize.put(l, 200);
        }
        Map<String, Long> probeIp = new HashMap<String, Long>();
        probeIp.put("Probe1", IpRangeUtil.convertIPFromString2Int("172.16.10.0"));
        probeIp.put("Probe2", IpRangeUtil.convertIPFromString2Int("172.16.30.0"));
        probeIp.put("Probe2", IpRangeUtil.convertIPFromString2Int("172.16.50.0"));
        probeIp.put("Probe2", IpRangeUtil.convertIPFromString2Int("172.16.70.0"));
        probeIp.put("Probe2", IpRangeUtil.convertIPFromString2Int("172.16.90.0"));
        Map<String, List<Long>> probeMap = new HashMap<String, List<Long>>();
        count = 0;
        for(Long l: ipRangeList){
            if(count < ipRangeList.size()/3){
                List<Long> list = probeMap.getOrDefault("Probe1", new ArrayList<Long>());
                list.add(l);
                probeMap.put("Probe1", list);
            } else if(count > 2*ipRangeList.size()/3){
                List<Long> list = probeMap.getOrDefault("Probe3", new ArrayList<Long>());
                list.add(l);
                probeMap.put("Probe3", list);
            } else{
                List<Long> list = probeMap.getOrDefault("Probe2", new ArrayList<Long>());
                list.add(l);
                probeMap.put("Probe2", list);
            }
            count++;
        }

        repo.init(testData, ipRangeSize, probeIp, probeMap);
        testRepo.writeToFile(TESTREPO_FILE, repo);
    }

    public IPRangeSetRepository loadTestData(String s){
        IPRangeSetRepository rlt = new IPRangeSetRepository();
        File file = new File(s);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            Map<Long, Long> ipRangeMap = new HashMap<Long, Long>();
            Map<Long, Integer> ipRangeSizeMap = new HashMap<Long, Integer>();
            Map<String, Long> probeIps = new HashMap<String, Long>();
            Map<String, List<Long>> probeIpMap = new HashMap<String , List<Long>>();
            while ((tempString = reader.readLine()) != null) {
                String[] split = tempString.split(FILED_SEPARATOR);
                // iprange
                String iprange = split[IPRANGE_POS];
                String[] ipranges = iprange.split(IPRANGE_SEPARATOR);
                ipRangeMap.put(IpRangeUtil.convertIPFromString2Int(ipranges[0]), IpRangeUtil.convertIPFromString2Int(ipranges[1]));

                // iprane Size
                if(split.length > IPRANGESIZE_POS){
                    Integer size = Integer.valueOf(split[IPRANGESIZE_POS]);
                    ipRangeSizeMap.put(IpRangeUtil.convertIPFromString2Int(ipranges[0]), size);
                }

                // probe
                if(split.length > IPRANGEPROBE_POS){
                    String probeName = split[IPRANGEPROBE_POS];
                    if(probeName != null && !"".equals(probeName)){
                        List<Long> ipRangeList = probeIpMap.getOrDefault(probeName, new ArrayList<Long>());
                        ipRangeList.add(IpRangeUtil.convertIPFromString2Int(ipranges[0]));
                        probeIpMap.put(probeName, ipRangeList);
                    }
                }

                line++;
            }
            reader.close();
            for(Map.Entry<String, List<Long>> entry : probeIpMap.entrySet()){
                List<Long> ipRangeList = entry.getValue();
                if(ipRangeList != null && ipRangeList.size() > 0){
                    probeIps.put(entry.getKey(), ipRangeList.get(0));
                }

            }
            rlt.init(ipRangeMap, ipRangeSizeMap, probeIps, probeIpMap);
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

        return rlt;
    }

    private void writeToFile(String s, IPRangeSetRepository repo) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(new File(s));
            String line = "";

            List<Long> ipRangeList = new ArrayList<Long>();
            for(Long l : repo.getIpRangeSet().keySet()){
                ipRangeList.add(l);
            }
            Collections.sort(ipRangeList);

            for(Long l : ipRangeList){
                line = IpRangeUtil.convertIPFromInt2tring(l) + IPRANGE_SEPARATOR + IpRangeUtil.convertIPFromInt2tring(repo.getIpRangeSet().get(l)) + "\t";
                line += repo.getIpRangeSize().getOrDefault(l, 0) + "\t";
                String probeName = repo.findProbeFromIpRange(l);
                line += probeName + "\t";
                fw.write(line + "\n");
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

    private Map<Long, Long> generateTestRange(String fromIp, String toIp, int subnet) {
        long start = IpRangeUtil.convertIPFromString2Int(fromIp);
        long end = IpRangeUtil.convertIPFromString2Int(toIp);
        int subnetSize = (int) Math.round(Math.pow(2, 32 - subnet));
        Map<Long, Long> rlt = new HashMap<>();
        while(start < end){
            long endIp = start + subnetSize - 1;
            if(endIp <= end){
                rlt.put(start, endIp);
            }
            start += subnetSize;
        }
        return rlt;
    }
}

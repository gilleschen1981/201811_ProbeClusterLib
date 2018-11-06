package com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster;

import com.hp.ucmdb.api.*;
import com.hp.ucmdb.api.discovery.services.DDMConfigurationService;
import com.hp.ucmdb.api.discovery.types.ProbeCluster;
import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.algo.KmeansDataHelper;
import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.algo.KmeansForIP;
import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.input.IPRangeJMXXMLLoader;
import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.input.TestDataRepository;
import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.model.IPRangeSetRepository;
import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.model.IpMoveAction;
import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.model.IpSplitAction;
import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.util.IpRangeUtil;

import java.awt.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;
import java.util.function.IntFunction;

public class ProbeClusterEnabler {

    public static void main(String[] args) {
        ProbeClusterEnabler enabler = new ProbeClusterEnabler();
        IPRangeSetRepository repo = enabler.initiateIpRangeRepo("test.csv");
        String output = repo.dataConsistancyCheck();
        System.out.println(output);
        List<IpSplitAction> actions = repo.inputValidation();
        System.out.println(actions);

        repo.generateProbeRangeByDistance();
        output = repo.outputProbeIpRange();
//        System.out.println(output);

//        System.out.println("The current cluster is " + (repo.isBalanced()?"":"not") + " balanced");
        repo.addNewRange("172.17.0.0", "172.17.10.0", 10000);
        output = repo.dataConsistancyCheck();
        System.out.println(output);
        actions = repo.inputValidation();
        System.out.println(actions);
//
//
        System.out.println("The current cluster is " + (repo.isBalanced()?"":"not") + " balanced");
//        System.out.println(repo.generateBalanceAction());
    }

    public void publicIpCluster(String f){
        IPRangeSetRepository repo = initiateIpRangeRepo(f);
        repo.generateProbePublicIpRange();
        System.out.println(repo.outputProbeIpRange());

    }

    /**
     *
     */
    public void usecase1IprangeCluster(String f){
        IPRangeSetRepository repo = initiateIpRangeRepo(f);
        // prepare input data for Kmeans
        List<Long> ipRangeList = new ArrayList<Long>();
        for(Long l : repo.getIpRangeSet().keySet()){
            ipRangeList.add(l);
        }
        Collections.sort(ipRangeList);
        Long[] orderedIpRangeSet = ipRangeList.toArray(new Long[ipRangeList.size()]);
        double[][] data = KmeansDataHelper.prepareData(orderedIpRangeSet);

        // calculate
        int number = ipRangeList.size();
        int dim = 1;
        KmeansForIP KM = new KmeansForIP(data, number,dim);
        int probeNumber = repo.getProbeNumberSuggestion();

        KM.clustering(probeNumber, 30, null);

        System.out.println("Suggest to assign " + KM.nclusters() + " probes in this cluster.");
        // print the result
        for(int i = 0; i < KM.nclusters(); i++){
            int count = 0;
            String output = "";
            for(int j = 0; j < KM.getLabel().length; j++){
                if(KM.getLabel()[j] == i){
                    count ++;
                    output += "\t" + IpRangeUtil.convertIPFromInt2tring(orderedIpRangeSet[j]) + " -> " + (i+1) + "\n";
                }
            }
            output = "Probe number: " + (i+1) + " has " + count + "ipranges" + "\n" + output;
            System.out.println(output);
        }

    }


    public void useCase2InputCheck(String f){
        IPRangeSetRepository repo = initiateIpRangeRepo(f);
        List<IpSplitAction> splitIps = repo.inputValidation();
        for(IpSplitAction action : splitIps){
            System.out.println(action);
        }
    }

    public void useCase4AddNewRange(String f, Long ipRangeStart, Long ipRangeEnd, Integer load){
        IPRangeSetRepository repo = initiateIpRangeRepo(f);
        System.out.println(repo.outputProbeIpRange());
        String probeName = repo.addNewRange(ipRangeStart, ipRangeEnd, load);
        System.out.println(probeName);
    }

    public void useCase3ProbeClusterSplit(String f){
        IPRangeSetRepository repo = initiateIpRangeRepo(f);

        repo.setProbeIprange(null);
        repo.generateProbeRangeByDistance();
        String output = repo.outputProbeIpRange();
        System.out.println(output);
    }

    public void useCase5DetectUnbalancedCluster(String f){
        IPRangeSetRepository repo = initiateIpRangeRepo(f);
        repo.setProbeIprange(null);
        repo.generateProbeRangeByDistance();
        repo.addNewRange(IpRangeUtil.convertIPFromString2Int("192.168.255.0"), IpRangeUtil.convertIPFromString2Int("192.168.255.255"), 10000);
        System.out.println(repo.isBalanced());
        System.out.println(repo.outputProbeIpRange());
    }

    public void useCase6SuggestLBOperation(String f){
        IPRangeSetRepository repo = initiateIpRangeRepo(f);
        repo.setProbeIprange(null);
        repo.generateProbeRangeByDistance();
        repo.addNewRange(IpRangeUtil.convertIPFromString2Int("192.168.255.0"), IpRangeUtil.convertIPFromString2Int("192.168.255.255"), 10000);
        IpMoveAction action = repo.generateBalanceAction();
        System.out.println(action);
    }


    /**
     *
     * @param file
     * @return
     */
    private IPRangeSetRepository initiateIpRangeRepo(String file) {
        TestDataRepository testRepo = new TestDataRepository();
        IPRangeSetRepository rlt = testRepo.loadTestData(file);
        return rlt;
    }
}

package com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.input;

import com.hp.ucmdb.api.UcmdbService;
import com.hp.ucmdb.api.topology.*;
import com.hp.ucmdb.api.types.TopologyCI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IPAddressCILoader {
    static public List<String> loadIpAddress(String file){
        List<String> rlt = new ArrayList<String>();
        File ipFile = new File(file);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(ipFile));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                String[] split = tempString.split(",");
                String[] ipSplit = split[0].split("\\.");
                if(ipSplit.length == 4){
                    rlt.add(split[0]);
                }
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
        System.out.println("[INFO]Total " + rlt.size() + " ipaddress loaded.");
        return rlt;
    }

    static public List<String> loadIpAddressFromTql(UcmdbService service) {
        List<String> rlt = new ArrayList<String>();
        TopologyQueryService topologyQueryService = service.getTopologyQueryService();
        TopologyQueryFactory queryFactory = topologyQueryService.getFactory();
        QueryDefinition queryDefinition = queryFactory.createQueryDefinition("query tql");

        QueryNode hostNode = queryDefinition.addNode("IPAddress").ofType("ip_address").queryProperties("name", "ip_address_type");
        PropertyConditionBuilder b = null;
        b = hostNode.propertiesConditionBuilder();
        hostNode.withPropertiesConditions(b.use(b.property("ip_address_type").isEqualTo("ipv4"))).setAsPerspectiveContact();

        Topology topology = topologyQueryService.executeQuery(queryDefinition);
        Collection<TopologyCI> ips = topology.getAllCIs();

        for(TopologyCI ci : ips){
            rlt.add(ci.getPropertyValue("name").toString());
        }

        return rlt;
    }
}

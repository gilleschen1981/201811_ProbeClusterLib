package com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.input;

import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.util.IpRangeUtil;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class IPRangeJMXXMLLoader {

    final static private String FROM_TAG = "FROM";
    final static private String TO_TAG = "TO";


    static public Map<Long, Long> importIPRangeString(String t) {
        Map<Long, Long> rlt = new HashMap<Long, Long>(5000);
        XMLInputFactory inputFactory = XMLInputFactory.newFactory();
        try {
            XMLEventReader eventReader = inputFactory.createXMLEventReader(new ByteArrayInputStream(t.getBytes()));
            eventReader = inputFactory.createFilteredReader(eventReader, new EventFilter() {
                @Override
                public boolean accept(XMLEvent event) {
                    int type = event.getEventType();
                    return type == XMLStreamConstants.START_ELEMENT
                            || type == XMLStreamConstants.END_ELEMENT
                            || type == XMLStreamConstants.CHARACTERS;
                }
            });

            long tempFromIP = 0;
            while(eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    String tag = event.asStartElement().getName().getLocalPart();
                    if (FROM_TAG.equalsIgnoreCase(tag)) {
                        String ip = eventReader.nextEvent().asCharacters().getData();
                        long fromIp = IpRangeUtil.convertIPFromString2Int(ip);
                        if(fromIp <= 0){
                            System.out.println("[ERROR]from ip error: " + ip);
                            return rlt;
                        }
                        if(rlt.containsKey(fromIp)){
                            System.out.println("[ERROR]from ip duplicate: " + ip);
                            return rlt;
                        } else{
                            rlt.put(fromIp, tempFromIP);
                            tempFromIP = fromIp;
                        }
                    } else if (TO_TAG.equalsIgnoreCase(tag)){
                        String ip = eventReader.nextEvent().asCharacters().getData();
                        long toIp = IpRangeUtil.convertIPFromString2Int(ip);
                        if(toIp <= 0){
                            System.out.println("[ERROR]to ip error: " + ip);
                            return rlt;
                        }
                        if(tempFromIP <= 0){
                            System.out.println("[ERROR]no from ip for to ip: " + ip);
                            return rlt;
                        } else{
                            rlt.put(tempFromIP, toIp);
                            tempFromIP = 0;
                        }
                    }
                }
            }

            eventReader.close();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return rlt;
    }
}

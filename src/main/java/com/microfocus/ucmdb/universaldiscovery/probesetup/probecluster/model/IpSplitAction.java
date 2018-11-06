package com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.model;

import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.util.IpRangeUtil;

public class IpSplitAction {

    private String message;
    private Long ipRangeStart;
    private Long ipRangeEnd;
    private Integer load;

    public IpSplitAction(String message, Long ipRangeStart, Long ipRangeEnd, Integer load) {
        this.message = message;
        this.ipRangeStart = ipRangeStart;
        this.ipRangeEnd = ipRangeEnd;
        this.load = load;
    }

    @Override
    public String toString() {
        return "IpSplitAction{" +
                "message='" + message + '\'' +
                ", ipRangeStart=" + IpRangeUtil.convertIPFromInt2tring(ipRangeStart) +
                ", ipRangeEnd=" + IpRangeUtil.convertIPFromInt2tring(ipRangeEnd) +
                ", load=" + load +
                '}';
    }

    public Long getIpRangeStart() {
        return ipRangeStart;
    }

    public void setIpRangeStart(Long ipRangeStart) {
        this.ipRangeStart = ipRangeStart;
    }

    public Long getIpRangeEnd() {
        return ipRangeEnd;
    }

    public void setIpRangeEnd(Long ipRangeEnd) {
        this.ipRangeEnd = ipRangeEnd;
    }

    public Integer getLoad() {
        return load;
    }

    public void setLoad(Integer load) {
        this.load = load;
    }
}

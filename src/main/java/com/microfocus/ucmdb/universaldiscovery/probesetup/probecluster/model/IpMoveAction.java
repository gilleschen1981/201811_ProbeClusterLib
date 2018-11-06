package com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.model;

import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.util.IpRangeUtil;

import java.util.List;

public class IpMoveAction {
    private Long ipRange;
    private String sourceProbe;
    private String targetProbe;

    @Override
    public String toString() {
        return "IpMoveAction{" +
                "ipRange=" + IpRangeUtil.convertIPFromInt2tring(ipRange) +
                ", sourceProbe='" + sourceProbe + '\'' +
                ", targetProbe='" + targetProbe + '\'' +
                '}';
    }

    public IpMoveAction(Long ipRange, String sourceProbe, String targetProbe) {
        this.ipRange = ipRange;
        this.sourceProbe = sourceProbe;
        this.targetProbe = targetProbe;
    }

    public Long getIpRange() {
        return ipRange;
    }

    public void setIpRange(Long ipRange) {
        this.ipRange = ipRange;
    }

    public String getSourceProbe() {
        return sourceProbe;
    }

    public void setSourceProbe(String sourceProbe) {
        this.sourceProbe = sourceProbe;
    }

    public String getTargetProbe() {
        return targetProbe;
    }

    public void setTargetProbe(String targetProbe) {
        this.targetProbe = targetProbe;
    }
}

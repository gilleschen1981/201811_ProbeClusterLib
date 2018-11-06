package com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.model;

public enum IpClassification {
    /**
     * classA
     */
    privateClassA(1),
    /**
     * classB
     */
    privateClassB(2),
    /**
     * classC
     */
    privateClassC(3),
    /**
     * classA
     */
    publicClassA(11),
    /**
     * classB
     */
    publicClassB(12),
    /**
     * classC
     */
    publicClassC(13),

    /**
     * omit
     */
    omit(0);



    private int classtype;

    IpClassification(int classtype) {
        this.classtype = classtype;
    }

    public int getClasstype() {
        return classtype;
    }

    public boolean isPrivateIp() {
        if(classtype == privateClassA.getClasstype() || classtype == privateClassB.getClasstype() || classtype == privateClassC.getClasstype()){
            return true;
        }
        return false;
    }

    public boolean isPublicIp() {
        if(classtype == publicClassA.getClasstype() || classtype == publicClassB.getClasstype() || classtype == publicClassC.getClasstype()){
            return true;
        }
        return false;
    }
}
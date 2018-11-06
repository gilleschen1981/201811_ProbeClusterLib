package com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.algo;

public class KmeansDataHelper {
    static public double[][] prepareData(Long[] data){
        double [][] rlt = new double[data.length][];
        for (int i=0; i<data.length; i++){
            rlt[i] = new double[1];
            rlt[i][0] = data[i] - data[0];
        }
        return rlt;
    }
}

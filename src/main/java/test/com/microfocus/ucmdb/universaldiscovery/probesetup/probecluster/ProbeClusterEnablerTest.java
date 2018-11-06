package test.com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster; 

import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.ProbeClusterEnabler;
import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.util.IpRangeUtil;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After; 

/** 
* ProbeClusterEnabler Tester. 
* 
* @author <Authors name> 
* @since <pre>Oct 13, 2018</pre> 
* @version 1.0 
*/ 
public class ProbeClusterEnablerTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: main(String[] args) 
* 
*/ 
@Test
public void testMain() throws Exception { 
//TODO: Test goes here... 
}


@Test
public void testPublicIpCluster() throws Exception {
    ProbeClusterEnabler test = new ProbeClusterEnabler();
    test.publicIpCluster("1.0.64.0-1.0.255.255.csv");
}


/** 
* 
* Method: usecase1IprangeCluster(String f) 
* 
*/ 
@Test
public void testUsecase1IprangeCluster() throws Exception {
    ProbeClusterEnabler test = new ProbeClusterEnabler();
    test.usecase1IprangeCluster("192.168.0.0-192.168.255.255.csv");
//    test.usecase1IprangeCluster("192.168.0.0-192.168.255.255_Full.csv");
}

@Test
public void testUsecase1IprangeClusterFull() throws Exception {
    ProbeClusterEnabler test = new ProbeClusterEnabler();
    test.usecase1IprangeCluster("192.168.0.0-192.168.255.255_Full.csv");
}


    /**
* 
* Method: useCase3ProbeClusterSplit(String f)
* 
*/ 
@Test
public void testUseCase2InputCheck() throws Exception {
    ProbeClusterEnabler test = new ProbeClusterEnabler();
    test.useCase2InputCheck("192.168.0.0-192.168.255.255_Full_Invalid.csv");
}

/**
 *
 * Method: useCase3ProbeClusterSplit(String f)
 *
 */
@Test
public void testUseCase3ProbeClusterSplit() throws Exception {
    ProbeClusterEnabler test = new ProbeClusterEnabler();
    test.useCase3ProbeClusterSplit("192.168.0.0-192.168.255.255_Full.csv");
}

@Test
public void testUseCase4AddNewRange() throws Exception {
    ProbeClusterEnabler test = new ProbeClusterEnabler();
    test.useCase4AddNewRange("192.168.0.0-192.168.200.255_Avg.csv", IpRangeUtil.convertIPFromString2Int("192.168.255.0"), IpRangeUtil.convertIPFromString2Int("192.168.255.255"), 100);


}

@Test
public void testUseCase5DetectUnbalancedCluster() throws Exception {
    ProbeClusterEnabler test = new ProbeClusterEnabler();
    test.useCase5DetectUnbalancedCluster("192.168.0.0-192.168.200.255_Avg.csv");


}

@Test
public void testUseCase6SuggestLBOperation() throws Exception {
    ProbeClusterEnabler test = new ProbeClusterEnabler();
    test.useCase6SuggestLBOperation("192.168.0.0-192.168.200.255_Avg.csv");


}



    /**
* 
* Method: initiateIpRangeRepo(String file) 
* 
*/ 
@Test
public void testInitiateIpRangeRepo() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = ProbeClusterEnabler.getClass().getMethod("initiateIpRangeRepo", String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 

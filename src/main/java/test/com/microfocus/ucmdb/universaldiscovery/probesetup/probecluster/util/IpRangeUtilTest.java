package test.com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.util; 

import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.model.IpClassification;
import com.microfocus.ucmdb.universaldiscovery.probesetup.probecluster.util.IpRangeUtil;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After; 

/** 
* IpRangeUtil Tester. 
* 
* @author <Authors name> 
* @since <pre>Oct 8, 2018</pre> 
* @version 1.0 
*/ 
public class IpRangeUtilTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: convertIPFromString2Int(String ip) 
* 
*/ 
@Test
public void testConvertIPFromString2Int() throws Exception { 
    String test1 = "192.168.0.1";
    long rlt1 = IpRangeUtil.convertIPFromString2Int(test1);
    assert rlt1 == 3232235521L;

    String test2 = "0.0.0.0";
    long rlt2 = IpRangeUtil.convertIPFromString2Int(test2);
    assert rlt2 == 0L;

    System.out.println(IpRangeUtil.convertIPFromInt2tring(16793600L));
    System.out.println(IpRangeUtil.convertIPFromInt2tring(16842751));
} 

/** 
* 
* Method: convertIPFromInt2tring(long ip) 
* 
*/ 
@Test
public void testConvertIPFromInt2tring() throws Exception { 
    long test1 = 3232235521L;
    String rlt1 = IpRangeUtil.convertIPFromInt2tring(test1);
    assert rlt1.equals("192.168.0.1");
} 

/** 
* 
* Method: analyzeIPType(String ip) 
* 
*/ 
@Test
public void testAnalyzeIPType() throws Exception { 
    String test1 = "9.255.255.255";
    IpClassification rlt1 = IpRangeUtil.analyzeIPType(test1);
    assert rlt1.equals(IpClassification.publicClassA);

    String test2 = "10.255.255.255";
    IpClassification rlt2 = IpRangeUtil.analyzeIPType(test2);
    assert rlt2.equals(IpClassification.privateClassA);

    String test3 = "126.255.255.255";
    IpClassification rlt3 = IpRangeUtil.analyzeIPType(test3);
    assert rlt3.equals(IpClassification.publicClassA);

    String test4 = "172.255.255.255";
    IpClassification rlt4 = IpRangeUtil.analyzeIPType(test4);
    assert rlt4.equals(IpClassification.publicClassB);

    String test5 = "172.16.255.255";
    IpClassification rlt5 = IpRangeUtil.analyzeIPType(test5);
    assert rlt5.equals(IpClassification.privateClassB);

    String test6 = "192.0.0.0";
    IpClassification rlt6 = IpRangeUtil.analyzeIPType(test6);
    assert rlt6.equals(IpClassification.publicClassC);

    String test7 = "192.168.255.255";
    IpClassification rlt7 = IpRangeUtil.analyzeIPType(test7);
    assert rlt7.equals(IpClassification.privateClassC);

    String test8 = "223.255.255.255";
    IpClassification rlt8 = IpRangeUtil.analyzeIPType(test8);
    assert rlt8.equals(IpClassification.publicClassC);
} 


/** 
* 
* Method: calculateDistance(String ip1, String ip2, int subnetMask) 
* 
*/ 
@Test
public void testCalculateDistance() throws Exception { 
//TODO: Test goes here... 
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


/** 
* 
* Method: calculatePublicIPDistance(String ip1, String ip2) 
* 
*/ 
@Test
public void testCalculatePublicIPDistance() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = IpRangeUtil.getClass().getMethod("calculatePublicIPDistance", String.class, String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: calculatePrivateIPDistance(String ip1, String ip2) 
* 
*/ 
@Test
public void testCalculatePrivateIPDistance() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = IpRangeUtil.getClass().getMethod("calculatePrivateIPDistance", String.class, String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 

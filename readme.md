# Probe ipaddress lib
## 1, use case
1. probe建议：针对给出的ipranges，建议probe的个数和对应的ipranges
	Input: ipranges
	Output: probeList with ipranges

2, Input check: 
	任何一个iprange里面的ip数目必须小于总ip数除以probe数×2。不然就平分range
	任何一个iprange里面的ipci数目必须小于ipci总数除以probe数。
	Input: iprange, iprangesize, probeLoad
	Output: valid or not
		*iprange change suggestion

3. probeCluster分配
	Input: Iprange, iprange charge, probelist
	Output: ProbeList with ipranges.

4. 给cluster添加range，找到一个probe
	Input: a valid iprangeRepo, 1 iprange to add with size
	Output: probe name

	
5. 判断ProbeCluster是否unbalanced. (最大loadprobe的load大于suggestedload，并且load大于平均load的n倍 n = 1 + 0.5*(probecount-1))
	Input: ipranges, probeList with ipranges, iprange charge
	Output: most unbalanced probe

6. 生成一个平衡的probeCluster
	Input: probelist with ipranges, iprange charge， problematic probe
	Ouput: Operation to move n iprange from probeA 2 probeB.



## 2, basic function
+ ip 分类：private, public, omit
	Input: ipaddress
	Output: Ip Type

+ 计算ip之间的距离
	Input: 2 ipaddress
	Output: distance
	
+ k-means算法
	Input: Ipranges, k
	Output: k probe with ipranges.

+ 列出空ipranges
	Input: ipranges, iprange charge
	Output: ipranges with 0 charge
	
+ input check
	

	

	
package com.smydp.hbase.chore

/*
HBase手动Major Compact工具
#!/bin/bash
cd /hadoop/bigdata/hplsql/daily/hbase-chore
/usr/java/groovy-3.0.8/bin/groovy -cp "lib/*" RunMajorCompact.groovy 2>&1 |tee -a RunMajorCompact.log
 */

import groovy.transform.Field
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.ConnectionFactory

def log_info(s){
    println(new Date().format("yyyy-MM-dd HH:mm:ss SSS")+" - ${s}")
}

def curTime(){
    new Date().getTime()/1000
}

hbase_zk="hbzk1,hbzk2,hbzk3"
concurrency=5 //并行度
max_duration=3600*7 //最大运行时间(sec)
//max_duration=50
shell_start_time=curTime()

@Field Map<String,Long> region_pool=[:]
allCounter=0
tableCounter=0

tbs = ['hbase_table1','hbase_table2','hbase_table_n']


log_info("------RunMajorCompact start-----")

conf = HBaseConfiguration.create()
conf.set("hbase.zookeeper.quorum", hbase_zk)
if(1==1){ //kerberos authentication
    krb5_path="/etc/krb5.conf"
    keytab_path="/kerberos/keytab/hbase.keytab"
    System.setProperty("java.security.krb5.conf", krb5_path);
    conf.set("hadoop.security.authentication", "Kerberos");
    conf.set("hbase.security.authentication", "Kerberos");
    conf.set("hbase.master.kerberos.principal", "hbase/_HOST@XXX.COM");
    conf.set("hbase.regionserver.kerberos.principal","hbase/_HOST@XXX.COM");
    org.apache.hadoop.security.UserGroupInformation.setConfiguration(conf);
    org.apache.hadoop.security.UserGroupInformation.loginUserFromKeytab("hbase@XXX.COM", keytab_path);
}

hAdmin = ConnectionFactory.createConnection(conf).getAdmin()

def getCheckpoint(){
    File file = new File("./checkpoint.txt")
    if(file.exists()){
        return file.text
    }else{
        return ""
    }
}
def saveCheckpoint(region){
    File file = new File("./checkpoint.txt")
    file.text = region
}

def evictRegion(keepNum){
    while(true){
        def removeKeys=[]
        region_pool.keySet().each {key->
            try {
                def st=hAdmin.getCompactionStateForRegion(key.getBytes())
                if(st.getNumber()==0){
                    def ct=curTime()-region_pool.get(key)
                    log_info("finish compact, cost ${ct} s, [${key}]")
                    removeKeys.add(key)
                }
            }catch(Exception e){
                def ct=curTime()-region_pool.get(key)
                log_info("exception, cost ${ct} s, [${key}][${e.toString()}]")
                removeKeys.add(key)
            }
        }
        //avoid ConcurrentModificationException in each loop
        removeKeys.each {key->
            region_pool.remove(key)
        }
        if(region_pool.size() <= keepNum){
            break
        }
        Thread.sleep(1000)
    }
}


def lastRegion=getCheckpoint()
def startCompact=false
if(lastRegion==""){
    startCompact=true
}
tbs.each{tb->
    tableCounter=0
    def regions=hAdmin.getTableRegions(TableName.valueOf(tb))
    log_info("---------start to process table [$tb][${regions.size()} regions]")
    for(def region:regions){
        def regionName=region.getRegionNameAsString()
        if(!startCompact){
            if(lastRegion==regionName){
                startCompact=true
            }else{
                continue
            }
        }
        if(region_pool.size() >= concurrency) {
            //if queue full, need to pop one region that finish compaction
            evictRegion(concurrency-1)
        }
        log_info("start to process region[${++tableCounter}-${++allCounter}][${regionName}], table[$tb]")
        hAdmin.majorCompactRegion(regionName.getBytes())
        region_pool.put(regionName,curTime())

        if(curTime()-shell_start_time> max_duration){ //only run off-peak
            saveCheckpoint(regionName) //next time run from this region
            log_info("reach max_duration(${max_duration}), forcibly exit(-1), cost ${curTime()-shell_start_time} s ")
            System.exit(-1)
        }
    }
}

evictRegion(0)

saveCheckpoint("")
log_info("------RunMajorCompact end, cost ${curTime()-shell_start_time} s-----")


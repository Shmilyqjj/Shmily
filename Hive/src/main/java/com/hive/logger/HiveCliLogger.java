package com.hive.logger;

import org.apache.hadoop.hive.ql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiveCliLogger {
    static final private String CLASS_NAME = Driver.class.getName();
    static final private Logger LOG = LoggerFactory.getLogger(CLASS_NAME);

    public static void main(String[] args) {
        LOG.info("Log class name: {}", CLASS_NAME);
        LOG.warn("This is a hive client log.");
    }
}

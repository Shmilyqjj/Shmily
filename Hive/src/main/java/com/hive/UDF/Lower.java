package com.hive.UDF;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * Usage:
 * add jar /opt/modules/hive/auxlib/Hive-1.0-SNAPSHOT.jar;
 * CREATE TEMPORARY FUNCTION lower_case AS 'com.hive.UDF.Lower';
 * select lower_case('Haha');
 * @author shmily
 */
@Description(name = "lower",
        value = "lower(string_value) - Returns the lower string.",
        extended = "Example:\n"
                + "  > SELECT lower('HAHA123');\n" + "  haha123")
public class Lower extends UDF {

    public String evaluate (final String s) {

        if (s == null) {
            return null;
        }

        return s.toLowerCase();
    }
}

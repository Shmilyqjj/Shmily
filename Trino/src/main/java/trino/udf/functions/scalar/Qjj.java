package trino.udf.functions.scalar;

import io.airlift.slice.Slice;
import io.airlift.slice.Slices;
import io.trino.spi.function.Description;
import io.trino.spi.function.ScalarFunction;
import io.trino.spi.function.SqlType;
import io.trino.spi.type.StandardTypes;

public class Qjj {
    @ScalarFunction("qjj")
    @Description("Converts the string to qjj_string_jjq")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice qjj(@SqlType(StandardTypes.VARCHAR) Slice slice)
    {
        if(slice == null){
            return null;
        }
        return Slices.utf8Slice("qjj_" + slice.toStringUtf8() + "_jjq");
    }

    @ScalarFunction("test")
    @Description("Converts the string to qjj_test==>string")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice qjjTest(@SqlType(StandardTypes.VARCHAR) Slice slice)
    {
        if(slice == null){
            return null;
        }
        return Slices.utf8Slice("qjj_test==>" + slice.toStringUtf8());
    }
}
package trino.udf.functions.deprecated;

import io.trino.spi.function.Description;
import io.trino.spi.function.ScalarFunction;
import io.trino.spi.function.SqlType;
import io.trino.spi.type.StandardTypes;

public class ExampleDeprecatedFunction {
    @Deprecated
    @ScalarFunction("deprecated_function")
    @Description("(DEPRECATED) Use function xxx instead")
    @SqlType(StandardTypes.BOOLEAN)
    public static boolean deprecated_function()
    {
        return false;
    }
}
package trino.udf.functions.scalar;

import io.airlift.slice.Slice;
import io.trino.spi.function.*;
import io.trino.spi.type.StandardTypes;

@ScalarFunction("is_null")
@Description("Returns TRUE if the argument is NULL")
public final class IsNullFunction
{
    @TypeParameter("T")
    @SqlType(StandardTypes.BOOLEAN)
    public static boolean isNullSlice(@SqlNullable @SqlType("T") Slice value)
    {
        return (value == null);
    }

    @TypeParameter("T")
    @SqlType(StandardTypes.BOOLEAN)
    public static boolean isNullLong(@SqlNullable @SqlType("T") Long value)
    {
        return (value == null);
    }

    @TypeParameter("T")
    @SqlType(StandardTypes.BOOLEAN)
    public static boolean isNullDouble(@SqlNullable @SqlType("T") Double value)
    {
        return (value == null);
    }

    // ...and so on for each native container type
}
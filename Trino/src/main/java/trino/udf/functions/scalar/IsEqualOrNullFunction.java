package trino.udf.functions.scalar;

import io.airlift.slice.Slice;
import io.trino.spi.function.*;
import io.trino.spi.type.StandardTypes;
import java.lang.invoke.MethodHandle;
import static io.trino.spi.function.InvocationConvention.InvocationArgumentConvention.NEVER_NULL;
import static io.trino.spi.function.InvocationConvention.InvocationReturnConvention.NULLABLE_RETURN;

@ScalarFunction("is_equal_or_null")
@Description("Returns TRUE if arguments are equal or both NULL")
public final class IsEqualOrNullFunction {

    @TypeParameter("T")
    @SqlType(StandardTypes.BOOLEAN)
    public static boolean isEqualOrNullSlice(
            @OperatorDependency(
                    operator = OperatorType.EQUAL,
                    argumentTypes = {"T", "T"},
                    convention = @Convention(arguments = { NEVER_NULL, NEVER_NULL }, result = NULLABLE_RETURN)
            ) MethodHandle equals,
            @SqlNullable @SqlType("T") Slice value1,
            @SqlNullable @SqlType("T") Slice value2) throws Throwable {
        if (value1 == null && value2 == null) {
            return true;
        }
        if (value1 == null || value2 == null) {
            return false;
        }
        return (boolean) equals.invokeExact(value1, value2);
    }

    // ...and so on for each native container type
}
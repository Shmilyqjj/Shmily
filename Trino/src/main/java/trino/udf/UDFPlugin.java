package trino.udf;

import com.google.common.collect.ImmutableSet;
import io.trino.spi.Plugin;
import trino.udf.functions.aggregation.*;
import trino.udf.functions.deprecated.*;
import trino.udf.functions.scalar.*;

import java.util.Set;

public class UDFPlugin implements Plugin {
    @Override
    public Set<Class<?>> getFunctions()
    {
        return ImmutableSet.<Class<?>>builder()
                .add(ExampleNullFunction.class)
                .add(IsNullFunction.class)
                .add(IsEqualOrNullFunction.class)
                .add(ExampleStringFunction.class)
                .add(AverageAggregation.class)
                .add(ExampleDeprecatedFunction.class)
                .add(Qjj.class)
                .build();
    }


}
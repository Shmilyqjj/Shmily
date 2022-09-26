package trino.udf.functions.model;

import io.trino.spi.function.AccumulatorState;

public interface LongAndDoubleState extends AccumulatorState {
    long getLong();

    void setLong(long value);

    double getDouble();

    void setDouble(double value);
}
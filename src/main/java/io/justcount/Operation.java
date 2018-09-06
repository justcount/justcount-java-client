package io.justcount;

import java.util.*;

public class Operation {

    public static class Builder {

        public String realm;
        public String tenant;
        public String metric;
        public Date date;
        public Map<String, String> params = new HashMap<String, String>();
        public Map<String, String> dimensions = new HashMap<String, String>();
        public Op op;

        public Builder() {
        }

        public Operation build() {
            return new Operation(realm, tenant, metric, params, dimensions, date, op);
        }

        public Builder setRealm(String realm) {
            this.realm = realm;
            return this;
        }

        public Builder setTenant(String tenant) {
            this.tenant = tenant;
            return this;
        }

        public Builder setMetric(String metric) {
            this.metric = metric;
            return this;
        }

        public Builder setParam(String name, String value) {
            params.put(name, value);
            return this;
        }

        public Builder setDimension(String name, String value) {
            dimensions.put(name, value);
            return this;
        }

        public Builder setDate(Date date) {
            this.date = date;
            return this;
        }

        public Builder setOp(Op op) {
            this.op = op;
            return this;
        }

    }

    public final String realm;
    public final String tenant;
    public final String metric;
    public final Map<String, String> params = new HashMap<>();
    public final Map<String, String> dimensions = new HashMap<>();
    public final Date date;
    public final Op op;

    public Operation(
            String realm,
            String tenant,
            String metric,                  // @NotNull
            Map<String, String> params,     // @Nullable
            Map<String, String> dimensions, // @Nullable
            Date date,                      // @Nullable
            Op op                           // @NotNull
    ) {
        if (metric == null) {
            throw new IllegalArgumentException("An Operation must have a non-null metric");
        }
        if (op == null) {
            throw new IllegalArgumentException("An Operation must have a non-null op");
        }

        this.realm = realm;
        this.tenant = tenant;
        this.metric = metric;
        this.date = date;
        if (params != null) {
            this.params.putAll(params);
        }
        if (dimensions != null) {
            this.dimensions.putAll(dimensions);
        }
        this.op = op;
    }

    public static class Op {

        public static class IntegerValue {

            public final int value;

            public IntegerValue(int value) {
                this.value = value;
            }

        }

        public static class DoubleValue {

            public final double value;

            public DoubleValue(double value) {
                this.value = value;
            }

        }

        public static class StringArrayValue {

            public final Collection<String> value;

            public StringArrayValue(Collection<String> value) {
                if (value == null) {
                    this.value = null;
                } else {
                    this.value = new ArrayList<>(value);
                }
            }

        }

        public final IntegerValue addInt;
        public final DoubleValue addFlt;
        public final IntegerValue setInt;
        public final DoubleValue setFlt;
        public final StringArrayValue insert;

        private Op(
                IntegerValue addInt,    // @Nullable
                DoubleValue addFlt,     // @Nullable
                IntegerValue setInt,    // @Nullable
                DoubleValue setFlt,     // @Nullable
                StringArrayValue insert // @Nullable
        ) {
            this.addInt = addInt;
            this.addFlt = addFlt;
            this.setInt = setInt;
            this.setFlt = setFlt;
            this.insert = insert;

            int count = 0;
            if (this.addInt != null) ++count;
            if (this.addFlt != null) ++count;
            if (this.setInt != null) ++count;
            if (this.setFlt != null) ++count;
            if (this.insert != null) ++count;
            if (count != 1) {
                throw new IllegalArgumentException("An Op must have exactly one non-null member");
            }
        }

        public static Op AddInt(int value) {
            return new Op(
                    new IntegerValue(value),
                    null,
                    null,
                    null,
                    null
            );
        }

        public static Op AddFlt(double value) {
            return new Op(
                    null,
                    new DoubleValue(value),
                    null,
                    null,
                    null
            );
        }

        public static Op SetInt(int value) {
            return new Op(
                    null,
                    null,
                    new IntegerValue(value),
                    null,
                    null
            );
        }

        public static Op SetFlt(double value) {
            return new Op(
                    null,
                    null,
                    null,
                    new DoubleValue(value),
                    null
            );
        }

        public static Op Insert(Collection<String> value) {
            return new Op(
                    null,
                    null,
                    null,
                    null,
                    new StringArrayValue(value)
            );
        }

    }

    public static class Bulk {

        public Collection<Operation> bulk;

        public Bulk(Collection<Operation> operations) {
            this.bulk = operations;
        }

    }

}

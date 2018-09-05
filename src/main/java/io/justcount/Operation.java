package io.justcount;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Operation {

    public static class Builder {

        private Operation operation = new Operation();

        private Builder() {
        }

        public Operation build() {
            return operation;
        }

        public static Builder AddInt(Metric metric, Integer value) {
            Builder builder = new Builder();
            builder.operation.metric = metric;
            builder.operation.op = new Op();
            builder.operation.op.addInt = new Op.IntegerValue();
            builder.operation.op.addInt.value = value;
            return builder;
        }

        public static Builder AddFlt(Metric metric, Double value) {
            Builder builder = new Builder();
            builder.operation.metric = metric;
            builder.operation.op = new Op();
            builder.operation.op.addFlt = new Op.DoubleValue();
            builder.operation.op.addFlt.value = value;
            return builder;
        }

        public static Builder SetInt(Metric metric, Integer value) {
            Builder builder = new Builder();
            builder.operation.metric = metric;
            builder.operation.op = new Op();
            builder.operation.op.setInt = new Op.IntegerValue();
            builder.operation.op.setInt.value = value;
            return builder;
        }

        public static Builder SetFlt(Metric metric, Double value) {
            Builder builder = new Builder();
            builder.operation.metric = metric;
            builder.operation.op = new Op();
            builder.operation.op.setFlt = new Op.DoubleValue();
            builder.operation.op.setFlt.value = value;
            return builder;
        }

        public static Builder Insert(Metric metric, String[] value) {
            Builder builder = new Builder();
            builder.operation.metric = metric;
            builder.operation.op = new Op();
            builder.operation.op.insert = new Op.StringArrayValue();
            builder.operation.op.insert.value = value;
            return builder;
        }

        public Builder setParam(String name, String value) {
            operation.params.put(name, value);
            return this;
        }

        public Builder setDimension(String name, String value) {
            operation.dimensions.put(name, value);
            return this;
        }

        public Builder setTenant(String tenant) {
            operation.tenant = tenant;
            return this;
        }

    }

    public Metric metric;
    public String tenant;
    public Date date;
    public Map<String, String> params = new HashMap<String, String>();
    public Map<String, String> dimensions = new HashMap<String, String>();
    public Op op;

    public static class Op {

        public static class IntegerValue {
            public Integer value;
        }

        public static class DoubleValue {
            public Double value;
        }

        public static class StringArrayValue {
            public String[] value;
        }

        public IntegerValue addInt;
        public DoubleValue addFlt;
        public IntegerValue setInt;
        public DoubleValue setFlt;
        public StringArrayValue insert;

    }

    public static class Bulk {

        public Operation[] bulk;

        public Bulk(Operation[] operations) {
            this.bulk = operations;
        }

    }

}

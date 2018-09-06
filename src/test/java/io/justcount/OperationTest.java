package io.justcount;

import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.*;

public class OperationTest {

    @Test
    public void testGson() {
        Gson gson = new Gson();

        Operation[] operations = {
                new Operation.Builder()
                        .setRealm("wp")
                        .setTenant("testTenant")
                        .setMetric("events")
                        .setParam("browser", "safari")
                        .setDimension("version", "10")
                        .setOp(Operation.Op.AddInt(1))
                        .build(),
                new Operation.Builder()
                        .setRealm("wp")
                        .setTenant("testTenant")
                        .setMetric("othercounter")
                        .setParam("browser", "safari")
                        .setDimension("version", "10")
                        .setOp(Operation.Op.AddInt(1))
                        .build(),
        };

        assertEquals("{\"realm\":\"wp\",\"tenant\":\"testTenant\",\"metric\":\"events\",\"params\":{\"browser\":\"safari\"},\"dimensions\":{\"version\":\"10\"},\"op\":{\"addInt\":{\"value\":1}}}", gson.toJson(operations[0]));
    }

}

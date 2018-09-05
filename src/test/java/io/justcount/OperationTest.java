package io.justcount;

import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.*;

public class OperationTest {

    @Test
    public void testGson() {
        Gson gson = new Gson();

        Operation[] operations = {
                Operation.Builder.AddInt(Metric.from("wp:events:somecounter"), 1)
                        .setParam("browser", "safari")
                        .setDimension("version", "10")
                        .setTenant("testTenant").build(),
                Operation.Builder.AddInt(Metric.from("wp:events:othercounter"), 1)
                        .setParam("browser", "safari")
                        .setDimension("version", "10")
                        .setTenant("testTenant").build(),
        };

        assertEquals("{\"metric\":{\"name\":\"somecounter\",\"realm\":\"wp\",\"backend\":\"events\"},\"tenant\":\"testTenant\",\"params\":{\"browser\":\"safari\"},\"dimensions\":{\"version\":\"10\"},\"op\":{\"addInt\":{\"value\":1}}}", gson.toJson(operations[0]));
    }

}
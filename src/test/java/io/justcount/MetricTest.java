package io.justcount;

import org.junit.Test;

import static org.junit.Assert.*;

public class MetricTest {

    @Test
    public void testFrom() {
        assertEquals(new Metric("wp", "events", "counter"), Metric.from("wp:events:counter"));
        assertEquals(new Metric("w:p", "e vents", "c ounter"), Metric.from("w%3Ap:e%20vents:c%20ounter"));
        assertNull(Metric.from(""));
        assertNull(Metric.from("a:b"));
    }

    @Test
    public void testEquality() {
        Metric m1 = new Metric("wp", "events", "counter");
        Metric m2 = new Metric("wp", "events", "counter");
        assertEquals(m1, m2);
        m1.backend = "";
        assertNotEquals(m1, m2);
        m1.backend = "events";
        m1.name = "";
        assertNotEquals(m1, m2);
        m1.name = "counter";
        m1.realm = "";
        assertNotEquals(m1, m2);
    }

}
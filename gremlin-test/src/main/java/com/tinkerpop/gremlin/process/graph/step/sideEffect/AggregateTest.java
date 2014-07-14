package com.tinkerpop.gremlin.process.graph.step.sideEffect;

import com.tinkerpop.gremlin.AbstractGremlinTest;
import com.tinkerpop.gremlin.LoadGraphWith;
import com.tinkerpop.gremlin.process.Path;
import com.tinkerpop.gremlin.process.Traversal;
import com.tinkerpop.gremlin.process.util.MapHelper;
import com.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.tinkerpop.gremlin.LoadGraphWith.GraphData.CLASSIC;
import static org.junit.Assert.*;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public abstract class AggregateTest extends AbstractGremlinTest {

    public abstract Traversal<Vertex, Vertex> get_g_v1_aggregateXaX_outXcreatedX_inXcreatedX_exceptXaX(final Object v1Id);

    public abstract List<String> get_g_V_valueXnameX_aggregateXaX_iterate_getXaX();

    public abstract List<String> get_g_V_aggregateXa_nameX_iterate_getXaX();

    public abstract Traversal<Vertex, Path> get_g_V_out_aggregateXaX_path();

    @Test
    @LoadGraphWith(CLASSIC)
    public void g_v1_aggregateXaX_outXcreatedX_inXcreatedX_exceptXaX() {
        final Iterator<Vertex> step = get_g_v1_aggregateXaX_outXcreatedX_inXcreatedX_exceptXaX(convertToVertexId("marko"));
        System.out.println("Testing: " + step);
        int counter = 0;
        while (step.hasNext()) {
            counter++;
            Vertex vertex = step.next();
            assertTrue(vertex.value("name").equals("peter") || vertex.value("name").equals("josh"));
        }
        assertEquals(2, counter);
    }

    @Test
    @LoadGraphWith(CLASSIC)
    public void g_V_valueXnameX_aggregateXaX_iterate_getXaX() {
        final List<String> names = get_g_V_valueXnameX_aggregateXaX_iterate_getXaX();
        assert_g_V_valueXnameX_aggregateXaX_iterate_getXaX(names);
    }

    private void assert_g_V_valueXnameX_aggregateXaX_iterate_getXaX(List<String> names) {
        assertEquals(6, names.size());
        assertTrue(names.contains("marko"));
        assertTrue(names.contains("josh"));
        assertTrue(names.contains("peter"));
        assertTrue(names.contains("lop"));
        assertTrue(names.contains("vadas"));
        assertTrue(names.contains("ripple"));
    }

    @Test
    @LoadGraphWith(CLASSIC)
    public void g_V_aggregateXa_nameX_iterate_getXaX() {
        final List<String> names = get_g_V_aggregateXa_nameX_iterate_getXaX();
        assert_g_V_valueXnameX_aggregateXaX_iterate_getXaX(names);
    }

    @Test
    @LoadGraphWith(CLASSIC)
    public void g_V_out_aggregateXaX_path() {
        final Iterator<Path> paths = get_g_V_out_aggregateXaX_path();
        int count = 0;
        final Map<String, Long> firstStepCounts = new HashMap<>();
        final Map<String, Long> secondStepCounts = new HashMap<>();
        while (paths.hasNext()) {
            count++;
            Path path = paths.next();
            String first = path.get(0).toString();
            String second = path.get(1).toString();
            assertNotEquals(first, second);
            MapHelper.incr(firstStepCounts, first, 1l);
            MapHelper.incr(secondStepCounts, second, 1l);
        }
        assertEquals(6, count);
        assertEquals(3, firstStepCounts.size());
        assertEquals(4, secondStepCounts.size());
        assertTrue(firstStepCounts.values().contains(3l));
        assertTrue(firstStepCounts.values().contains(2l));
        assertTrue(firstStepCounts.values().contains(1l));
        assertTrue(secondStepCounts.values().contains(3l));
        assertTrue(secondStepCounts.values().contains(1l));
    }


    public static class JavaAggregateTest extends AggregateTest {

        public Traversal<Vertex, Vertex> get_g_v1_aggregateXaX_outXcreatedX_inXcreatedX_exceptXaX(final Object v1Id) {
            return g.v(v1Id).with("a", new HashSet<>()).aggregate("a").out("created").in("created").except("a");
        }

        public List<String> get_g_V_valueXnameX_aggregateXaX_iterate_getXaX() {
            return g.V().value("name").aggregate("a").iterate().memory().get("a");
        }

        public List<String> get_g_V_aggregateXa_nameX_iterate_getXaX() {
            return g.V().aggregate("a", v -> v.value("name")).iterate().memory().get("a");
        }

        public Traversal<Vertex, Path> get_g_V_out_aggregateXaX_path() {
            return g.V().out().aggregate("a").path();
        }
    }

    /*public static class JavaComputerAggregateTest extends AggregateTest {

        public Traversal<Vertex, Vertex> get_g_v1_aggregateXaX_outXcreatedX_inXcreatedX_exceptXaX(final Object v1Id) {
            return g.v(v1Id).with("x", new HashSet<>()).aggregate("x").out("created").in("created").except("x");
        }

        public List<String> get_g_V_valueXnameX_aggregateXaX_iterate_getXaX() {
            return g.V().value("name").aggregate("x").iterate().memory().get("x");
        }

        public List<String> get_g_V_aggregateXa_nameX_iterate_getXaX() {
            return g.V().aggregate("a", v -> v.value("name")).iterate().memory().get("a");
        }
    }*/
}

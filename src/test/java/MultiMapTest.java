import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class MultiMapTest {
    MultiBiMap<String, Integer> map;

    @Before
    public void setUp() {
        map = new MultiBiMap();

        map.put("A", 1);
        map.put("A", 2);
        map.put("A", 3);
        map.put("B", 4);
        map.put("C", 1);
        map.put("C", 4);
        map.put("C", 5);
    }

    @Test
    public void test_element_not_there() {
        Assertions.assertThat(map.get("D")).isEmpty();
    }

    @Test
    public void test_put_works() {
        Assertions.assertThat(map.get("A")).containsExactlyInAnyOrder(1, 2, 3);
        Assertions.assertThat(map.get("B")).containsExactlyInAnyOrder(4);
        Assertions.assertThat(map.get("C")).containsExactlyInAnyOrder(1, 4, 5);
    }

    @Test
    public void test_put_twice() {
        map.put("A", 2);

        Assertions.assertThat(map.get("A")).containsExactlyInAnyOrder(1, 2, 3);
    }

    @Test
    public void test_remove_works() {
        map.removeValue(1);

        Assertions.assertThat(map.get("A")).containsExactlyInAnyOrder(2, 3);
        Assertions.assertThat(map.get("B")).containsExactlyInAnyOrder(4);
        Assertions.assertThat(map.get("C")).containsExactlyInAnyOrder(4, 5);

        map.removeValue(4);

        Assertions.assertThat(map.get("A")).containsExactlyInAnyOrder(2, 3);
        Assertions.assertThat(map.get("B")).isEmpty();
        Assertions.assertThat(map.get("C")).containsExactlyInAnyOrder(5);

        map.removeValue(5);

        Assertions.assertThat(map.get("A")).containsExactlyInAnyOrder(2, 3);
        Assertions.assertThat(map.get("B")).isEmpty();
        Assertions.assertThat(map.get("C")).isEmpty();
    }

    @Test
    public void clear_works() {
        map.clear();

        Assertions.assertThat(map.get("A")).isEmpty();
        Assertions.assertThat(map.get("B")).isEmpty();
        Assertions.assertThat(map.get("C")).isEmpty();
    }

    @Test
    public void put_after_delete() {
        map.removeValue(1);

        Assertions.assertThat(map.get("A")).containsExactlyInAnyOrder(2, 3);
        Assertions.assertThat(map.get("B")).containsExactlyInAnyOrder(4);
        Assertions.assertThat(map.get("C")).containsExactlyInAnyOrder(4, 5);

        map.put("A", 1);

        Assertions.assertThat(map.get("A")).containsExactlyInAnyOrder(1, 2, 3);
        Assertions.assertThat(map.get("B")).containsExactlyInAnyOrder(4);
        Assertions.assertThat(map.get("C")).containsExactlyInAnyOrder(4, 5);
    }
}

import common.Customer;
import common.LineItem;
import common.Order;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class QueryableMapTest {
    private QueryableMap<String, Order> map;
    private Order order1;
    private Order order2;
    private Order order3;
    private Function<Order, Object> customerIdFunc = order -> order.getCustomer().getId();
    private Function<Order, Object> itemIdFunc = order -> order.getItems().stream().map(LineItem::getItemId).collect(toList());

    @Before
    public void setUp() {
        map = new QueryableMap<>(Order::getId);
        map.addIndex(customerIdFunc);
        map.addIndex(itemIdFunc);

        LineItem lineItem11 = new LineItem("item_1", 1, 1);
        LineItem lineItem12 = new LineItem("item_2", 2, 11);
        LineItem lineItem13 = new LineItem("item_3", 2, 11);

        order1 = new Order("order_1", new Customer("customer_1", "John"), Arrays.asList(lineItem11, lineItem12, lineItem13));

        LineItem lineItem21 = new LineItem("item_1", 13, 1);
        LineItem lineItem22 = new LineItem("item_4", 1, 3);

        order2 = new Order("order_2", new Customer("customer_2", "Pablo"), Arrays.asList(lineItem21, lineItem22));

        LineItem lineItem31 = new LineItem("item_2", 6, 7);
        LineItem lineItem32 = new LineItem("item_5", 3, 3);

        order3 = new Order("order_3", new Customer("customer_1", "John"), Arrays.asList(lineItem31, lineItem32));

        map.put(order1);
        map.put(order2);
        map.put(order3);
    }

    @Test
    public void test_get_existing_works() {
        Assertions.assertThat(map.get("order_1")).isEqualTo(order1);
    }

    @Test
    public void test_get_non_existing_works() {
        Assertions.assertThat(map.get("order_5")).isNull();
    }

    @Test
    public void test_simple_indexing_works() {
        Assertions.assertThat(map.query(customerIdFunc, "customer_1")).containsExactlyInAnyOrder(order1, order3);
        Assertions.assertThat(map.query(customerIdFunc, "customer_2")).containsExactlyInAnyOrder(order2);
        Assertions.assertThat(map.query(customerIdFunc, "customer_12")).isEmpty();
    }

    @Test
    public void test_collection_indexing_works() {
        Assertions.assertThat(map.query(itemIdFunc, "item_2")).containsExactlyInAnyOrder(order1, order3);
        Assertions.assertThat(map.query(itemIdFunc, "item_1")).containsExactlyInAnyOrder(order1, order2);
        Assertions.assertThat(map.query(itemIdFunc, "item_5")).containsExactlyInAnyOrder(order3);
        Assertions.assertThat(map.query(itemIdFunc, "item_6")).isEmpty();
    }


    @Test
    public void test_delete__works() {
        map.delete("order_1");

        Assertions.assertThat(map.get("order_1")).isNull();

        Assertions.assertThat(map.query(itemIdFunc, "item_2")).containsExactlyInAnyOrder(order3);
        Assertions.assertThat(map.query(itemIdFunc, "item_1")).containsExactlyInAnyOrder(order2);
    }
}

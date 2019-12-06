import common.Customer;
import common.LineItem;
import common.Order;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static java.util.stream.Collectors.toList;

public class QueryableMapTest {
    private static final String CUSTOMER_ID = "CUSTOMER_ID";
    private static final String ITEM_ID = "ITEM_ID";
    private QueryableMap<String, Order> map;
    private Order order1;
    private Order order2;
    private Order order3;

    @Before
    public void setUp() {
        QueryableMap.Builder<String, Order> builder = QueryableMap.newBuilder(Order::getId);
        map = builder
                .addIndex(CUSTOMER_ID, order -> order.getCustomer().getId())
                .addIndex(ITEM_ID, order -> order.getItems().stream().map(LineItem::getItemId).collect(toList()))
                .build();

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

        this.map.put(order1);
        this.map.put(order2);
        this.map.put(order3);
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
        Assertions.assertThat(map.query(CUSTOMER_ID, "customer_1")).containsExactlyInAnyOrder(order1, order3);
        Assertions.assertThat(map.query(CUSTOMER_ID, "customer_2")).containsExactlyInAnyOrder(order2);
        Assertions.assertThat(map.query(CUSTOMER_ID, "customer_12")).isEmpty();
    }

    @Test
    public void test_collection_indexing_works() {
        Assertions.assertThat(map.query(ITEM_ID, "item_2")).containsExactlyInAnyOrder(order1, order3);
        Assertions.assertThat(map.query(ITEM_ID, "item_1")).containsExactlyInAnyOrder(order1, order2);
        Assertions.assertThat(map.query(ITEM_ID, "item_5")).containsExactlyInAnyOrder(order3);
        Assertions.assertThat(map.query(ITEM_ID, "item_6")).isEmpty();
    }


    @Test
    public void test_delete__works() {
        map.delete("order_1");

        Assertions.assertThat(map.get("order_1")).isNull();

        Assertions.assertThat(map.query(ITEM_ID, "item_2")).containsExactlyInAnyOrder(order3);
        Assertions.assertThat(map.query(ITEM_ID, "item_1")).containsExactlyInAnyOrder(order2);
    }
}

package common;

import java.util.List;

public class Order {
    private final String id;
    private final Customer customer;
    private final List<LineItem> items;

    public Order(String id, Customer customer, List<LineItem> items) {
        this.id = id;
        this.customer = customer;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<LineItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", customer=" + customer +
                ", items=" + items +
                '}';
    }
}

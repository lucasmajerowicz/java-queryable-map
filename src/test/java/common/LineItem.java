package common;

public class LineItem {
    private final String itemId;
    private final int quantity;
    private final float price;

    public LineItem(String itemId, int quantity, float price) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "LineItem{" +
                "itemId='" + itemId + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}

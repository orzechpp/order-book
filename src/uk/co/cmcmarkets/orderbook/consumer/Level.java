package uk.co.cmcmarkets.orderbook.consumer;


public class Level {
    private static final String PRICE = "price ";
    private static final String SIZE = "size";
    private static final String COUNT = "count";
    public int price;
    public int size;
    public int count;

    public Level(int price) {
        this(price, 0, 0);
    }

    Level(int price, int size, int count) {
        this.price = price;
        this.size = size;
        this.count = count;
    }

    @Override
    public String toString() {
        return PRICE + "= " + price + ", " + SIZE + " = " + size + ", " + COUNT + " = " + count;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Level level = (Level) o;

        if (price != level.price) return false;
        if (size != level.size) return false;
        if (count != level.count) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = price;
        result = 31 * result + size;
        result = 31 * result + count;
        return result;
    }
}

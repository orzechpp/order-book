package uk.co.cmcmarkets.orderbook.consumer;

import uk.co.cmcmarkets.orderbook.iface.Order;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

class OrderBook {
    private static final String METHOD = "Method";
    private static final String BID_PRICES = "Bid Prices";
    private static final String ASK_PRICES = "Ask Prices";
    private static String result = "";
    private final String symbol;
    private final Map<Integer, Level> bidLevelMap = new TreeMap<Integer, Level>(Collections.<Object>reverseOrder());
    private final Map<Integer, Level> askLevelMap = new TreeMap<Integer, Level>();

    public OrderBook(String symbol) {
        this.symbol = symbol;
    }

    public void addOrder(Order order) {
        Level level = obtainLevelFor(order);
        level.size += order.getQuantity();
        level.count += 1;
    }

    public void removeOrder(Order order) {
        Level level = obtainLevelFor(order);
        level.size -= order.getQuantity();
        level.count -= 1;

        if (level.count == 0) {
            removeLevel(order);
        }
    }

    private Level obtainLevelFor(Order order) {
        Map<Integer, Level> levelMap = (order.isBuy() ? bidLevelMap : askLevelMap);
        Level level = levelMap.get(order.getPrice());
        if (level == null) {
            level = new Level(order.getPrice());
            levelMap.put(order.getPrice(), level);
        }
        return level;
    }

    private void removeLevel(Order order) {
        Map<Integer, Level> levelMap = (order.isBuy() ? bidLevelMap : askLevelMap);
        levelMap.remove(order.getPrice());
    }

    @Override
    public String toString() {
        String message = "";
        message += METHOD + ":: " + symbol + "\n";
        message += BID_PRICES + "::\n";
        message += levelToString(bidLevelMap);
        message += ASK_PRICES + "::\n";
        message += levelToString(askLevelMap);
        return message;
    }


    public OrderBook withBidLevel(int price, int quantity, int count) {
        bidLevelMap.put(price, new Level(price, quantity, count));
        return this;
    }

    public OrderBook withAskLevel(int price, int quantity, int count) {
        askLevelMap.put(price, new Level(price, quantity, count));
        return this;
    }

    private static String levelToString(Map<Integer, Level> levelMap) {
        levelMap.forEach((k, v) ->
        {
            result += String.format("\t%s", v.toString()) + "\n";
        });

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderBook orderBook = (OrderBook) o;

        if (symbol != null ? !symbol.equals(orderBook.symbol) : orderBook.symbol != null) return false;
        if (bidLevelMap != null ? !bidLevelMap.equals(orderBook.bidLevelMap) : orderBook.bidLevelMap != null)
            return false;
        return askLevelMap != null ? askLevelMap.equals(orderBook.askLevelMap) : orderBook.askLevelMap == null;

    }

    @Override
    public int hashCode() {
        int result = symbol != null ? symbol.hashCode() : 0;
        result = 31 * result + (bidLevelMap != null ? bidLevelMap.hashCode() : 0);
        result = 31 * result + (askLevelMap != null ? askLevelMap.hashCode() : 0);
        return result;
    }
}

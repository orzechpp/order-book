package uk.co.cmcmarkets.orderbook.consumer;

import uk.co.cmcmarkets.orderbook.iface.Action;
import uk.co.cmcmarkets.orderbook.iface.Log;
import uk.co.cmcmarkets.orderbook.iface.Order;
import uk.co.cmcmarkets.orderbook.iface.OrderConsumer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static uk.co.cmcmarkets.orderbook.iface.LogLevel.INFO;

public class OrderConsumerImpl implements OrderConsumer {


    private static final String ORDER_BOOK = "Order Book:: \n";
    private static final String STARTING_PROCESSING_ORDERS = "Starting processing orders";
    private static final String FINISHED_PROCESSING_ORDERS = "Finished processing orders";
    private final ConcurrentHashMap<String, OrderBook> orderBookConcurrentHashMap = new ConcurrentHashMap<String, OrderBook>(16, 0.9f, 1);
    private final ConcurrentHashMap<Long, Order> orders = new ConcurrentHashMap<Long, Order>(16, 0.9f, 1);
    private final ExecutorService executor = Executors.newWorkStealingPool();
    private volatile String orderSummary = ORDER_BOOK;
    private Log log;

    @Override
    public void startProcessing(Log log) {
        this.log = log;
        log.log(INFO, STARTING_PROCESSING_ORDERS);
    }

    @Override
    public void finishProcessing() {
        log.log(INFO, processedData(orderBookConcurrentHashMap));
        log.log(INFO, FINISHED_PROCESSING_ORDERS);
    }

    @Override
    public void handleEvent(Action action, Order order) {
        switch (action) {
            case ADD:
                orders.put(order.getOrderId(), order);
                OrderBook orderBook = orderBookForSymbol(order.getSymbol());
                orderBook.addOrder(order);
                break;
            case REMOVE:
                Order oldOrder = orders.remove(order.getOrderId());
                orderBook = orderBookForSymbol(oldOrder.getSymbol());
                orderBook.removeOrder(oldOrder);
                break;
            case EDIT:
                oldOrder = orders.remove(order.getOrderId());
                orderBook = orderBookForSymbol(oldOrder.getSymbol());
                orderBook.removeOrder(oldOrder);

                Order newOrder = new Order(
                        oldOrder.getOrderId(),
                        oldOrder.getSymbol(),
                        oldOrder.isBuy(),
                        order.getPrice(),
                        order.getQuantity()
                );
                orderBook.addOrder(newOrder);
                orders.put(newOrder.getOrderId(), newOrder);
                break;
        }   

    }


    public OrderBook orderBookForSymbol(String symbol) {
        OrderBook orderBook = orderBookConcurrentHashMap.get(symbol);

        if (orderBook == null) {
            orderBook = new OrderBook(symbol);
            orderBookConcurrentHashMap.putIfAbsent(symbol, orderBook);
        }

        return orderBook;
    }

    private String processedData(ConcurrentHashMap<String, OrderBook> orderBookConcurrentHashMap) {
        try {
            executor.submit(() ->
                    orderBookConcurrentHashMap.forEach((k, v) -> orderSummary += String.format("\n%s", v.toString()))).get();
        } catch (InterruptedException e) {
            executor.shutdown();
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        } finally {
            executor.shutdownNow();
        }

        return orderSummary;
    }

}
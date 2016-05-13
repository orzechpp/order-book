package uk.co.cmcmarkets.orderbook.consumer;


import org.testng.annotations.Test;
import uk.co.cmcmarkets.orderbook.iface.Order;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static uk.co.cmcmarkets.orderbook.iface.Action.*;

public class OrderConsumerImplTest {

    @Test
    public void testAddOrdersToOrderBook() {
        OrderConsumerImpl orderConsumer = new OrderConsumerImpl();

        orderConsumer.handleEvent(ADD, new Order(1, "METHOD1", true, 543, 7));
        orderConsumer.handleEvent(ADD, new Order(2, "METHOD1", true, 543, 3));
        
        assertThat(orderConsumer.orderBookForSymbol("METHOD1"), equalTo(
                new OrderBook("METHOD1")
                        .withBidLevel(543, 10, 2)

        ));
    }

    @Test
    public void testAddRemoveOrdersFromOrderBook() {
        OrderConsumerImpl orderConsumer = new OrderConsumerImpl();

        orderConsumer.handleEvent(ADD, new Order(1, "METHOD1", true, 765, 4));
        orderConsumer.handleEvent(ADD, new Order(2, "METHOD1", true, 765, 3));
        orderConsumer.handleEvent(ADD, new Order(3, "METHOD1", true, 765, 8));
        orderConsumer.handleEvent(ADD, new Order(4, "METHOD1", false, 444, 8));
        orderConsumer.handleEvent(ADD, new Order(5, "METHOD1", false, 444, 8));
        assertThat(orderConsumer.orderBookForSymbol("METHOD1"), equalTo(
                new OrderBook("METHOD1")
                        .withBidLevel(765, 15, 3)
                        .withAskLevel(444, 16, 2)
        ));

        orderConsumer.handleEvent(REMOVE, new Order(1, null, false, 0, 0));
        orderConsumer.handleEvent(REMOVE, new Order(2, null, false, 0, 0));
        orderConsumer.handleEvent(REMOVE, new Order(3, null, false, 0, 0));
        orderConsumer.handleEvent(REMOVE, new Order(4, null, false, 0, 0));
        assertThat(orderConsumer.orderBookForSymbol("METHOD1"), equalTo(
                new OrderBook("METHOD1")
                        .withAskLevel(444, 8, 1)
        ));
    }

    @Test
    public void testEditOrdersInOrderBook() {
        OrderConsumerImpl orderConsumer = new OrderConsumerImpl();

        orderConsumer.handleEvent(ADD, new Order(1, "METHOD1", true, 321, 2));
        orderConsumer.handleEvent(ADD, new Order(2, "METHOD1", true, 321, 4));
        assertThat(orderConsumer.orderBookForSymbol("METHOD1"), equalTo(
                new OrderBook("METHOD1")
                        .withBidLevel(321, 6, 2)
        ));

        orderConsumer.handleEvent(EDIT, new Order(2, null, false, 322, 6));

        assertThat(orderConsumer.orderBookForSymbol("METHOD1"), equalTo(
                new OrderBook("METHOD1")
                        .withBidLevel(321, 2, 1)
                        .withBidLevel(322, 6, 1)
        ));
    }

    @Test
    public void testScenarioFromOrders1() {
        OrderConsumerImpl orderConsumer = new OrderConsumerImpl();


        orderConsumer.handleEvent(ADD, new Order(1L, "MSFT.L", true, 5, 200));
        orderConsumer.handleEvent(ADD, new Order(2L, "VOD.L", true, 15, 100));
        orderConsumer.handleEvent(ADD, new Order(3L, "MSFT.L", false, 5, 300));
        orderConsumer.handleEvent(ADD, new Order(4L, "MSFT.L", true, 7, 150));
        orderConsumer.handleEvent(EDIT, new Order(1L, "MSFT.L", true, 7, 200));
        orderConsumer.handleEvent(REMOVE, new Order(1L, null, true, -1, -1));
        orderConsumer.handleEvent(ADD, new Order(5L, "VOD.L", false, 17, 300));
        orderConsumer.handleEvent(ADD, new Order(6L, "VOD.L", true, 12, 150));
        orderConsumer.handleEvent(EDIT, new Order(3L, null, true, 7, 200));
        orderConsumer.handleEvent(ADD, new Order(7L, "VOD.L", false, 16, 100));
        orderConsumer.handleEvent(ADD, new Order(8L, "VOD.L", false, 19, 100));
        orderConsumer.handleEvent(ADD, new Order(9L, "VOD.L", false, 21, 112));
        orderConsumer.handleEvent(REMOVE, new Order(5L, null, false, -1, -1));

        assertThat(orderConsumer.orderBookForSymbol("MSFT.L"), equalTo(
                new OrderBook("MSFT.L")
                        .withBidLevel(7, 150, 1)
                        .withAskLevel(7, 200, 1)
        ));
        assertThat(orderConsumer.orderBookForSymbol("VOD.L"), equalTo(
                new OrderBook("VOD.L")
                        .withBidLevel(15, 100, 1)
                        .withBidLevel(12, 150, 1)
                        .withAskLevel(16, 100, 1)
                        .withAskLevel(19, 100, 1)
                        .withAskLevel(21, 112, 1)
        ));
    }
}

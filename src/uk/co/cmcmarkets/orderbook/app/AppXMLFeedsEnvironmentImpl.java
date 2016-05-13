package uk.co.cmcmarkets.orderbook.app;

import uk.co.cmcmarkets.orderbook.iface.LogLevel;


public class AppXMLFeedsEnvironmentImpl extends AppEnvironmentImpl {
    private final String xmlFileName;

    public AppXMLFeedsEnvironmentImpl(LogLevel logLevel, String xmlFileName) {
        super(logLevel);
        this.xmlFileName = xmlFileName;
    }

    /**
     * Sends a stream of orders to the {@link uk.co.cmcmarkets.orderbook.iface.OrderConsumer}s.
     *
     * @throws Exception if there is an error.
     * @see #notifyOrder(uk.co.cmcmarkets.orderbook.iface.Action, uk.co.cmcmarkets.orderbook.iface.Order)
     */
    protected void feedOrders() throws Exception {
        new XMLOrderParser(this).parseDocument(xmlFileName);
    }
}

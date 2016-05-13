package uk.co.cmcmarkets.orderbook;

import java.io.File;

import uk.co.cmcmarkets.orderbook.app.AppXMLFeedsEnvironmentImpl;
import uk.co.cmcmarkets.orderbook.consumer.OrderConsumerImpl;
import uk.co.cmcmarkets.orderbook.iface.AppEnvironment;
import uk.co.cmcmarkets.orderbook.iface.LogLevel;

public class AppRunner {

    /**
     * @param args
     */
    public static void main(String[] args) {


        AppEnvironment environment = new AppXMLFeedsEnvironmentImpl(LogLevel.INFO, new File("resources", "orders1.xml").getPath());
        environment.registerHandler(new OrderConsumerImpl());
        environment.run();
    }
}

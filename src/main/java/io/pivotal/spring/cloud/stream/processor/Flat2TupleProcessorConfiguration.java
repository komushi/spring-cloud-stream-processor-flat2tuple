package io.pivotal.spring.cloud.stream.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;

import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;



import org.springframework.tuple.Tuple;
import org.springframework.tuple.TupleBuilder;

import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.coords.LatLonPoint;

import java.util.Calendar;
import java.util.UUID;
import java.util.Arrays;

/**
 * Created by lei_xu on 6/11/16.
 */
@EnableBinding(Processor.class)
//@Import(SpelExpressionConverterConfiguration.class)
@EnableConfigurationProperties(Flat2TupleProcessorProperties.class)
public class Flat2TupleProcessorConfiguration {
    @Autowired
    private Flat2TupleProcessorProperties properties;

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public Message<Tuple> transform(Message<?> message) {
        Object payloadObj = message.getPayload();
        String payload = null;

        if (payloadObj instanceof String) {
            try
            {
                payload = payloadObj.toString();
            }
            catch (Exception e)
            {
                throw new MessageTransformationException(message, e.getMessage(), e);
            }

        }

        if (payload == null) {
            throw new MessageTransformationException(message, "payload empty");
        }

        String[] tokens = payload.split(delims);

        System.out.println("**********************payload*********************");
        System.out.println(payload);
        System.out.println("**********************payload end*****************");
        System.out.println("**********************tokens*********************");
        System.out.println(Arrays.toString(tokens));
        System.out.println("**********************tokens end*****************");

        double pickupLatitude = java.lang.Double.parseDouble(tokens[9]);
        double pickupLongitude = java.lang.Double.parseDouble(tokens[8]);
        double dropoffLatitude = java.lang.Double.parseDouble(tokens[7]);
        double dropoffLongitude = java.lang.Double.parseDouble(tokens[6]);
        String dropoffDatetime = tokens[3];
        String pickupDatetime = tokens[2];
        String route = null;

        try
        {
            route = generateRoute(pickupLatitude, pickupLongitude, dropoffLatitude, dropoffLongitude);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        Tuple tuple = null;

        if (route != null)
        {

            tuple = TupleBuilder.tuple()
                    .put("uuid", UUID.randomUUID())
                    .put("route", route)
                    .put("timestamp", Calendar.getInstance().getTimeInMillis())
                    .put("pickupLatitude", pickupLatitude)
                    .put("pickupLongitude", pickupLongitude)
                    .put("dropoffLatitude", dropoffLatitude)
                    .put("dropoffLongitude", dropoffLongitude)
                    .put("pickupDatetime", pickupDatetime)
                    .put("dropoffDatetime", dropoffDatetime)
                    .build();


        }

        Message<Tuple> rtnMessage = MessageBuilder.withPayload(tuple).build();
        
        return rtnMessage;
    }


    private static final String delims = "[,]";

    private String generateID(LatLonPoint.Double targetPoint) throws Exception{


        double edgeLength = properties.getEdgeLength().doubleValue();
        double cells = properties.getCells().doubleValue();
        double startLatitude = properties.getStartLatitude().doubleValue();
        double startLongitude = properties.getStartLongitude().doubleValue();

        LatLonPoint.Double originPoint = new LatLonPoint.Double(startLatitude, startLongitude);


        double azimuth = originPoint.azimuth(targetPoint);
        double distance = Length.METER.fromRadians(originPoint.distance(targetPoint));


        double xOffset = distance * Math.sin(azimuth);
        double yOffset = distance * Math.cos(azimuth);

        java.lang.Double xDouble = Math.ceil(xOffset / edgeLength);
        Integer xInt = xDouble.intValue();

        java.lang.Double yDouble = Math.ceil(Math.abs(yOffset) / edgeLength);
        Integer yInt = yDouble.intValue();

        if (xInt < 0 || xInt > cells || yInt < 0 || yInt > cells)
        {
            Exception e = new Exception("coordinates out of boundry! xInt:" + xInt + " yInt" + yInt);
            throw e;
        }

        String id = "C" + xInt + "." +yInt;

        return id;

    }

    private String generateRoute(double pickupLatitude, double pickupLongitude, double dropoffLatitude, double dropoffLongitude) throws Exception
    {

        LatLonPoint.Double toPoint = new LatLonPoint.Double(dropoffLatitude, dropoffLongitude);
        LatLonPoint.Double fromPoint = new LatLonPoint.Double(pickupLatitude, pickupLongitude);

        String fromID = null;
        String toID = null;

        try
        {
            fromID = generateID(fromPoint);
            toID = generateID(toPoint);
        }
        catch (Exception e)
        {
            throw e;
        }

        return fromID + "_" + toID;

    }
}

package demo;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Serialized;

import Util.JsonPOJODeserializer;
import Util.JsonPOJOSerializer;
import Util.MNM;

public class MNMDemo {
	public static void main(String[] args) throws Exception {
    	Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "application-mnm-demo");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        
        final DecimalFormat df2 = new DecimalFormat("#.##");
 
        Map<String, Object> serdeProps = new HashMap<>();
        
        final Serializer<MNM> mnmSerializer = new JsonPOJOSerializer<>();
        /*serdeProps.put("JsonPOJOClass", MNM.class);
        mnmSerializer.configure(serdeProps, false);*/
        
        final Deserializer<MNM> mnmDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", MNM.class);
        mnmDeserializer.configure(serdeProps, false);

        final Serde<MNM> mnmSerde = Serdes.serdeFrom(mnmSerializer, mnmDeserializer);
        
        final StreamsBuilder builder = new StreamsBuilder();
 
//        builder.stream("mnm-input").to("streams-mnm-output");
         
        KStream<String, MNM> source = builder.stream("mnm-input", Consumed.with(Serdes.String(), mnmSerde));
        
        KStream<String, MNM> transformed = source.map(
        	    (key, value) -> KeyValue.pair(String.valueOf(value.getPill_id()), value)
        		);
        
        KStream<String, MNM> transformedTotal = source.map(
        	    (key, value) -> KeyValue.pair(String.valueOf(1), value)
        		);
        
        // Get the total number of MNMs
        KStream<String, Long> totalCount = transformedTotal.groupByKey(Serialized.with(Serdes.String(), mnmSerde))
        		.count()
        		.toStream();
        
       //convert to table
       KTable<String, Long> tableTotalCount = totalCount.groupByKey(Serialized.with(Serdes.String(), Serdes.Long())).reduce(
        	    (aggValue, newValue) -> newValue,
        	    "dummy-aggregation-store0");
        
        // Get the total number of damaged MNMs
        KStream<String, Long> totalDam = transformedTotal
        		.filter((key, value) -> value.getDamaged() == 1)
				.groupByKey(Serialized.with(Serdes.String(), mnmSerde))
        		.count()
        		.toStream();
        
        //convert to table
        KTable<String, Long> tableTotalDam = totalDam.groupByKey(Serialized.with(Serdes.String(), Serdes.Long())).reduce(
         	    (aggValue, newValue) -> newValue,
         	    "dummy-aggregation-store1");
        
        
        KStream<String, MNM> colors = transformed.map(
        	    (key, value) -> KeyValue.pair(String.valueOf(value.getColor()), value)
        		);
        
        // Get the number of MNMs per color
        KStream<String, Long> colorCount = colors.groupByKey(Serialized.with(Serdes.String(), mnmSerde))
        		.count()
        		.toStream();
        
        //convert to table
        KTable<String, Long> tableColorCount = colorCount.groupByKey(Serialized.with(Serdes.String(), Serdes.Long())).reduce(
         	    (aggValue, newValue) -> newValue,
         	    "dummy-aggregation-store2");
        
        // Get the number of damaged MNMs per color
        KStream<String, Long> colorDam = colors
        		.filter((key, value) -> value.getDamaged() == 1)
        		.mapValues(value -> value.getDamaged())
				.groupByKey(Serialized.with(Serdes.String(), Serdes.Integer()))
        		.count()
        		.toStream();
        
       //convert to table
        KTable<String, Long> tableColorDam = colorDam.groupByKey(Serialized.with(Serdes.String(), Serdes.Long())).reduce(
         	    (aggValue, newValue) -> newValue,
         	    "dummy-aggregation-store3");
        
       //ration of damaged MNMs
        KTable<String, String> joinedTotal = tableTotalCount.join(tableTotalDam,
        		(leftValue, rightValue) -> df2.format((double)rightValue/leftValue)); 
        
        
        //ration of damaged MNMs per color
        KTable<String, String> joined = tableColorCount.join(tableColorDam,
        		(leftValue, rightValue) -> df2.format((double)rightValue/leftValue));
//        KStream<String, String> joined = colorsCount.join(colorDem,
//        		(leftValue, rightValue) -> df2.format((double)rightValue/leftValue) ,
//        	    JoinWindows.of(TimeUnit.MINUTES.toMillis(5)),
//        	    Joined.with(
//        	      Serdes.String(), /* key */
//        	      Serdes.Long(),   /* left value */
//        	      Serdes.Long())  /* right value */
//        	  );
        
        
        KStream<String, MNM> damaged = transformed.map(
        	    (key, value) -> KeyValue.pair(String.valueOf(value.getDamaged()), value)
        		);
        
        KGroupedStream<String, MNM> damagedStream = damaged.groupByKey(Serialized.with(Serdes.String(), mnmSerde));
        
        //convert to table
//        KTable<String, Long> tableColorCount = colorCount.groupByKey(Serialized.with(Serdes.String(), Serdes.Long())).reduce(
//        	    (aggValue, newValue) -> newValue,
//        	    "dummy-aggregation-store");
        		        
        transformed.to("streams-mnm-output", Produced.with(Serdes.String(), mnmSerde));
        colors.to("streams-colors-output", Produced.with(Serdes.String(), mnmSerde));
//        colorsCount.to("streams-colors-count-output", Produced.with(Serdes.String(), Serdes.Long()));
//        colorDem.to("streams-colors-damaged-count-output", Produced.with(Serdes.String(), Serdes.Long()));
//        joinedTotal.to("joined-count-total-output", Produced.with(Serdes.String(), Serdes.String()));
//        joined.to("joined-count-output", Produced.with(Serdes.String(), Serdes.String()));
//        tableColorCount.toStream().to("table-colors-count-output", Produced.with(Serdes.String(), Serdes.Long()));
        joined.toStream().to("streams-damaged-count-output", Produced.with(Serdes.String(), Serdes.String()));
        joinedTotal.toStream().to("streams-color-damaged-count-output", Produced.with(Serdes.String(), Serdes.String()));
        
        final Topology topology = builder.build();
        
        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);
 
        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });
 
        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

}

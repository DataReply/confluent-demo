package demo;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

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
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Serialized;

import Util.JsonPOJODeserializer;
import Util.JsonPOJOSerializer;
import Util.JsonParser;
import Util.MNM;

public class MNMDemo {
	public static void main(String[] args) throws Exception {
    	Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "application-mnm-demo");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
 
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
        		);//.to("streams-mnm-output", Produced.with(Serdes.String(), mnmSerde));
//        .map(
//        			(key, value) -> KeyValue.pair(String.valueOf(value.getPill_id()), value)
//        		).to("streams-mnm-output");
        
        KStream<String, MNM> colors = transformed.map(
        	    (key, value) -> KeyValue.pair(String.valueOf(value.getColor()), value)
        		);
        
        KStream<String, Long> colorsCount = colors.groupByKey(Serialized.with(Serdes.String(), mnmSerde))
        		.count()
        		.toStream();
        		        
        transformed.to("streams-mnm-output", Produced.with(Serdes.String(), mnmSerde));
        colors.to("streams-colors-output", Produced.with(Serdes.String(), mnmSerde));
        colorsCount.to("streams-colors-count-output", Produced.with(Serdes.String(), Serdes.Long()));
        
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

package edu.csula.vkc.apps;

import java.util.Collection;
import edu.csula.vkc.models.Make;
import edu.csula.vkc.util.EdmundsSource;
import edu.csula.vkc.util.GenericCollector;
import edu.csula.vkc.util.LemonSource;

public class TheApp {
	public static void main(String[] args) throws Exception {
		
        LemonSource source = new LemonSource();
        GenericCollector collector = new GenericCollector();        
 
        if(source.hasNext()){
            Collection<Make> carMetadata = source.getData();
            //System.out.println("Hello 1");
            //Collection<Make> cleanedTweets = collector.mungee(carMetadata);
            //collector.save(cleanedTweets);
        }
    }
}

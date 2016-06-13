package edu.csula.vkc.third.apps;

import java.util.Collection;
import java.util.List;
import com.google.common.collect.Lists;

import edu.csula.vkc.services.TrueCarService1;
import edu.csula.vkc.third.models.Vehicle;
import edu.csula.vkc.third.util.GenericSource;
import edu.csula.vkc.third.util.VehicleCollector;

public class TheOne {

	public static void main(String[] args) throws Exception {
		List<Vehicle> listVehicals = Lists.newArrayList();

		// Sources
		GenericSource genericSource = new GenericSource();
		VehicleCollector collector = new VehicleCollector();
		
		//MicrosoftService.getMinPrice("Acura", "ILX", "2014");
		
		//TrueCarService1.getListing("Acura", "ILX");
		
		while(genericSource.hasNext()){
			listVehicals = genericSource.next();
			Collection<Vehicle> cleanedVehicals = collector.mungee(listVehicals);
			collector.save(cleanedVehicals);
		}
	}
}

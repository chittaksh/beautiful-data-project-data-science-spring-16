package edu.csula.vkc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.csula.datascience.acquisition.*;
import edu.csula.vkc.models.Make;
import edu.csula.vkc.models.Model;
import edu.csula.vkc.models.Price;
import edu.csula.vkc.models.Styles;
import edu.csula.vkc.models.Years;

public class GenericCollector implements Collector<Make, Make> {

	MongoClient mongoClient;
	MongoDatabase database;
	MongoCollection<Document> collection;

	public GenericCollector() {
		super();
	}

//	@Override
//	public Collection<Make> mungee(Collection<Make> src) {
//
//		// Code to remove cars without price
//		for (Make make : src) {
//			for (Model model : make.getModelList()) {
//				for (Years year : model.getYear()) {
//					for (Styles style : year.getStyles()) {
//						if (style.getPrice().getUsedTmvRetail() == 0) {
//							year.getStyles().remove(style);
//						}
//					}
//				}
//			}
//		}
//
//		return src;
//	}

	@Override
	public Collection<Make> mungee(Collection<Make> makesList) {

		List<Make> makesdataListtoRemove = new ArrayList<Make>();
		List<Price> Price = new ArrayList<Price>();

		for (Make make : makesList) {

			for (Model model : make.getModelList()) {
				for (Years year : model.getYear()) {
					for (Styles style : year.getStyles()) {
						if (style.getPrice().getUsedTmvRetail() == 0 || style.getPrice().getUsedTradeIn() == 0)

							makesdataListtoRemove.add(make);
					}
				}

			}
		}
		makesList.removeAll(makesdataListtoRemove);
		System.out.println(makesList.size());

		return makesList;
	}

	@Override
	public void save(Collection<Make> data) {
		try {
			// establish database connection to MongoDB
			mongoClient = new MongoClient();

			// select `bd-example` as testing database
			database = mongoClient.getDatabase("bd-example");

			// select collection by name `tweets`
			collection = database.getCollection("test");

			ObjectMapper mapper = new ObjectMapper();

			for (Make make : data) {
				collection.insertOne(Document.parse(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(make)));
			}
		} catch (Exception e) {
			System.out.println(e.toString());

		}
	}

}
package edu.csula.vkc.third.util;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.common.collect.Lists;
import com.mashape.unirest.http.JsonNode;
import edu.csula.datascience.acquisition.Source;
import edu.csula.vkc.services.EdmundsService;
import edu.csula.vkc.services.LemonFreeService;
import edu.csula.vkc.services.MicrosoftService;
import edu.csula.vkc.services.TrueCarService;
import edu.csula.vkc.third.models.MPG;
import edu.csula.vkc.third.models.Details;
import edu.csula.vkc.third.models.Vehicle;
import edu.csula.vkc.third.util.WriteToJson;

public class GenericSource implements Source<Vehicle> {

	// List<Vehicle> listVehicle;
	WriteToJson writer;

	JSONArray arrMakes;
	int currentMake = 0;

	public GenericSource() {
		super();
		writer = new WriteToJson();
		JsonNode carMakes = EdmundsService.getMakes();
		arrMakes = (JSONArray) carMakes.getObject().get("makes");
	}

	@Override
	public boolean hasNext() {
		if (currentMake < arrMakes.length()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Vehicle> next() {

		List<Vehicle> listVehicle = Lists.newArrayList();
		try {

			listVehicle = getVehicals(arrMakes.getJSONObject(currentMake));
			currentMake++;

		} catch (Exception e) {
			System.out.print("Source : Generic Source ");
			System.out.println(e.toString());
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
		}

		return listVehicle;
	}

	private List<Vehicle> getVehicals(JSONObject objMakes) throws Exception {

		List<Vehicle> listVehicle = Lists.newArrayList();

		// System.out.println(test);
		// System.out.println(arrayMakes);
		try {
			JSONObject jsonMake = objMakes;

			// System.out.println(make.getMake_id());
			System.out.println(jsonMake.getString("niceName").toString());

			JSONArray arrayModels = jsonMake.getJSONArray("models");

			// Code to retrive Models from Make.
			for (int j = 0; j < arrayModels.length(); j++) {
				JSONObject jsonModel = arrayModels.getJSONObject(j);

				// System.out.println(model.getModel_id());
				System.out.println("\t " + jsonModel.getString("name").toString());

				JSONArray arrayYears = jsonModel.getJSONArray("years");

				// Code for getting Years form Make>Models.
				for (int k = 0; k < arrayYears.length(); k++) {
					JSONObject jsonYear = arrayYears.getJSONObject(k);

					System.out.println(" \t \t" + jsonYear.get("year").toString());

					listVehicle.addAll(getVehicleDetails(jsonMake.getString("niceName").toString(),
							jsonModel.getString("name").toString(), jsonYear.get("year").toString()));
				}
				// Write file as per models
				writer.writeNewVehicleFile(jsonMake.getString("niceName").toString().replace(" ", "-"),
						jsonModel.getString("name").toString().replace(" ", "-"), listVehicle);
			}
			// Write file as per make.
			writer.writeNewVehicleFile(jsonMake.getString("niceName").toString(), listVehicle);
		} catch (Exception e) {
			throw e;
		}
		return listVehicle;
	}

	// Method to fetch vehicle details and prices.
	private List<Vehicle> getVehicleDetails(String make, String model, String year) throws Exception {

		List<Vehicle> listVehicle = Lists.newArrayList();

		try {
			JsonNode carDetails = EdmundsService.getCarDetails(make, model, year);

			if (carDetails.getObject().has("styles")) {
				JSONArray arrayStyles = (JSONArray) carDetails.getObject().get("styles");

				String strMakeModified = make.replace(" ", "%20").replace("-", "%20").trim();
				String strModelModified = model.replace(" ", "%20").replace("-", "%20").trim();

				if (arrayStyles.length() != 0) {

					JSONObject jsonStyle = arrayStyles.getJSONObject(0);

					Vehicle vehicle = new Vehicle();

					vehicle.setMakeName(make);
					vehicle.setModelName(model);
					vehicle.setYear(year);
					vehicle.setId(jsonStyle.has("id") ? jsonStyle.getLong("id") : 0);
					vehicle.setDriveSystem(jsonStyle.has("drivenWheels") ? jsonStyle.getString("drivenWheels") : null);
					vehicle.setNoOfDoors(jsonStyle.has("numOfDoors") ? jsonStyle.getInt("numOfDoors") : 0);
					vehicle.setTrim(jsonStyle.has("trim") ? jsonStyle.getString("trim") : null);
					vehicle.setVehicleName(jsonStyle.has("name") ? jsonStyle.getString("name") : null);

					if (jsonStyle.has("price")) {
						vehicle.setTmv(jsonStyle.getJSONObject("price").has("usedTmvRetail")
								? jsonStyle.getJSONObject("price").getInt("usedTmvRetail") : 0);
					}

					if (jsonStyle.has("MPG")) {
						vehicle.setMilage(new MPG(
								(jsonStyle.getJSONObject("MPG").has("highway")
										? jsonStyle.getJSONObject("MPG").getDouble("highway") : 0.0),
								(jsonStyle.getJSONObject("MPG").has("city")
										? jsonStyle.getJSONObject("MPG").getDouble("city") : 0.0)));
					}
					if (jsonStyle.has("engine")) {
						vehicle.setNoOfCylinder((jsonStyle.getJSONObject("engine").has("cylinder")
								? jsonStyle.getJSONObject("engine").getInt("cylinder") : 0));
						// style.setEngineLocation(jsonStyle.getJSONObject("engine").getString(""));
						vehicle.setFuelType(jsonStyle.getJSONObject("engine").has("fuelType")
								? jsonStyle.getJSONObject("engine").getString("fuelType") : null);
					}

					// Get original price for the car..
					vehicle.setMaxOriginalPrice(MicrosoftService.getMaxPrice(make, model, year));
					vehicle.setMinOriginalPrice(MicrosoftService.getMinPrice(make, model, year));

					if (jsonStyle.has("submodel")) {
						vehicle.setVehicleType(jsonStyle.getJSONObject("submodel").has("body")
								? jsonStyle.getJSONObject("submodel").getString("body") : null);
					}

					// Just for checking.
					// System.out.println("\t \t \t " + vehicle.getTrim());

					if (jsonStyle.has("transmission")) {
						vehicle.setTransmission(jsonStyle.getJSONObject("transmission").has("transmissionType")
								? jsonStyle.getJSONObject("transmission").getString("transmissionType") : null);
					}

					List<Details> listDetails = Lists.newArrayList();

					// Call to Lemon Free API for car listings.
					JsonNode nodeLemon = LemonFreeService.getListingsbyMakeAndModel(strMakeModified, strModelModified);

					// Checking if there are listings.
					if (nodeLemon.getObject().getJSONObject("response").getInt("response_code") == 0) {
						if (nodeLemon.getObject().getJSONObject("response").getJSONObject("result").has("listings")) {
							JSONArray arrayDetails = (JSONArray) nodeLemon.getObject().getJSONObject("response")
									.getJSONObject("result").getJSONArray("listings");

							// Further objects of price as per LemonFree api.
							for (int m = 0; m < arrayDetails.length(); m++) {

								JSONObject objDetails = arrayDetails.getJSONObject(m);

								// Check if there are direct objects.
								if ((objDetails.getInt("year") == (Integer.parseInt(year)))) {

									if (objDetails.has("price") && !objDetails.get("price").equals("")) {
										Details details = getLemonFreeDetails(objDetails);

										listDetails.add(details);
									}
								} else {
									//
								}
							}
						}
					}

					List<Details> trueCar = TrueCarService.getListing(make, model);

					for (Details details : trueCar) {
						if (Integer.parseInt(year) == details.getYearsOld()) {
							System.out.println("\t \t \t " + details.getSource() + " : " + details.getDetailId());
							listDetails.add(details);
						}
					}

					vehicle.setDetail(listDetails);

					listVehicle.add(vehicle);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return listVehicle;
	}

	// To Parse and Object details into JOSNObject.
	private Details getLemonFreeDetails(JSONObject objDetails) {
		Details details = new Details();
		try {
			details.setDetailId(objDetails.getString("id"));
			details.setMilesRun(objDetails.has("mileage") && !objDetails.get("mileage").equals("")
					? objDetails.getInt("mileage") : 0);
			details.setSalePrice(
					objDetails.has("price") && !objDetails.get("price").equals("") ? objDetails.getInt("price") : 0);
			details.setSource("LemonFree");
			details.setYearsOld(0);

			System.out.println("\t \t \t " + details.getSource() + " : " + details.getDetailId());
		} catch (Exception ex) {
			throw ex;
		}
		return details;
	}
}

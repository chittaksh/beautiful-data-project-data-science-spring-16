package edu.csula.vkc.models;

public class Trim {

	long trim_id;
	String trim;
	double price;
	String milage;
	public Trim() {
		super();
	}
	public Trim(long trim_id, String trim, double price, String milage) {
		super();
		this.trim_id = trim_id;
		this.trim = trim;
		this.price = price;
		this.milage = milage;
	}
	public long getTrim_id() {
		return trim_id;
	}
	public void setTrim_id(long trim_id) {
		this.trim_id = trim_id;
	}
	public String getTrim() {
		return trim;
	}
	public void setTrim(String trim) {
		this.trim = trim;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getMilage() {
		return milage;
	}
	public void setMilage(String milage) {
		this.milage = milage;
	}
	
	
}

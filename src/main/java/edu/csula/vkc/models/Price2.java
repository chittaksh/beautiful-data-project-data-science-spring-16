package edu.csula.vkc.models;

public class Price2 {

	private long priceId;
	private double usedTradeIn;
	private double usedTmvRetail;

	public Price2() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Price2( double usedTradeIn, double usedTmvRetail) {
		super();
		this.usedTradeIn = usedTradeIn;
		this.usedTmvRetail = usedTmvRetail;
	}

	public long getPriceId() {
		return priceId;
	}

	public void setPriceId(long priceId) {
		this.priceId = priceId;
	}

	public double getUsedTradeIn() {
		return usedTradeIn;
	}

	public void setUsedTradeIn(double usedTradeIn) {
		this.usedTradeIn = usedTradeIn;
	}

	public double getUsedTmvRetail() {
		return usedTmvRetail;
	}

	public void setUsedTmvRetail(double usedTmvRetail) {
		this.usedTmvRetail = usedTmvRetail;
	}

}

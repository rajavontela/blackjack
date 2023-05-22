package com.rgrv.blackjack.pojo;

public class Card {

	private String value;
	private String shape;
	
	public Card(String value, String shape) {
		this.value = value;
		this.shape = shape;
	}
	
	public String getCardValueWithShape(){
		return this.value + " " + this.shape;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getShape() {
		return shape;
	}
	
	@Override
	public String toString() {
		return getCardValueWithShape();
	}
}
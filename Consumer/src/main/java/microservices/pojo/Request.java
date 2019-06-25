package microservices.pojo;

import java.io.Serializable;

public class Request implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double valeur1;
	private double valeur2;
	private String operator;
	
	public double getValeur1() {
		return valeur1;
	}
	public void setValeur1(double valeur1) {
		this.valeur1 = valeur1;
	}
	public double getValeur2() {
		return valeur2;
	}
	public void setValeur2(double valeur2) {
		this.valeur2 = valeur2;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	
	

}

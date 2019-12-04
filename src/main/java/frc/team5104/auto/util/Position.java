package frc.team5104.auto.util;

import frc.team5104.util.Units;

/** A simple class for sending/saving robot positions. */
public class Position {
	public double x, y; //in feet
	public double theta; //in radians
	
	/**
	 * @param x Sideways translation in feet
	 * @param y Forward translation in feet
	 * @param theta Angle in radians
	 */
	public Position(double x, double y, double theta) {
		set(x, y, theta);
	}
	
	/**
	 * @param x Sideways translation in feet
	 * @param y Forward translation in feet
	 * @param inDegrees if the angle units are in degrees
	 */
	public Position(double x, double y, double angle, boolean inDegrees) {
		set(x, y, inDegrees ? Units.degreesToRadians(angle) : angle);
	}
	
	/**
	 * Sets x, y, and theta all at once
	 * @param x Sideways translation in feet
	 * @param y Forward translation in feet
	 * @param theta Angle in radians
	 */
	public void set(double x, double y, double theta) {
		this.x = x;
		this.y = y;
		this.theta = theta;
	}
    
	/** Adds to the x (sideways) translation */
    public void addX(double by) {
    	this.x += by;
    }
    
    /** Adds to the y (forwards) translation */
    public void addY(double by) {
    	this.y += by;
    }
    
	public String toString() {
		return  "x: " + String.format("%.2f", x) + ", " +
				"y: " + String.format("%.2f", y);
	}
}
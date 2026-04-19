/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.model;

/**
 *
 * @author HP
 */
public class SensorReading {
    private String id;
    private long timestamp;
    private double value;

    public SensorReading() {}
    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getId()               { return id; }
    public void setId(String id)        { this.id = id; }
    public long getTimestamp()          { return timestamp; }
    public void setTimestamp(long t)    { this.timestamp = t; }
    public double getValue()            { return value; }
    public void setValue(double value)  { this.value = value; }
}

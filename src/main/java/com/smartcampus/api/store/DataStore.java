/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.store;

import com.smartcampus.api.model.Room;
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.model.SensorReading;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author HP
 */
public class DataStore {

    private static final DataStore INSTANCE = new DataStore();

    private final Map<String, Room>              rooms    = new ConcurrentHashMap<>();
    private final Map<String, Sensor>            sensors  = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    private DataStore() {}

    public static DataStore getInstance() {
        return INSTANCE;
    }

    public Map<String, Room>                getRooms()    { return rooms; }
    public Map<String, Sensor>              getSensors()  { return sensors; }
    public Map<String, List<SensorReading>> getReadings() { return readings; }
}

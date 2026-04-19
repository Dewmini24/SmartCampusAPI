/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author HP
 */

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    // Jersey auto-scans com.smartcampus.api for all resources,
    // mappers and filters via web.xml — nothing needed here
}
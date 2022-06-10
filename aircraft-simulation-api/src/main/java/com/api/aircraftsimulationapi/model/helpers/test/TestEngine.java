package com.api.aircraftsimulationapi.model.helpers.test;

import com.api.aircraftsimulationapi.model.entities.Aircraft;
import com.api.aircraftsimulationapi.model.entities.Parameter;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Getter
@Setter
public abstract class TestEngine {
    public static Aircraft aircraft;
    public static long time;
    public static int testNumber;
    public static String fileName;
    public static Path file;
    public static List<ParameterGenSample> parameterGenSamples= new Vector<>();
    public static final int SECONDS_PER_MINUTE = 60;
    public static final int CONVERT_TO_MILLIS = 1000;

    public static void runTest(){
        Set<Parameter> parameters = aircraft.getParameters();
        parameterGenSamples.clear();

        time*=SECONDS_PER_MINUTE*CONVERT_TO_MILLIS;

        long initial = System.currentTimeMillis();
        long end = initial+time;

        parameters.forEach((parameter) -> {
            ParameterGenSample parameterGenSample = new ParameterGenSample(parameter.getCode(),
                                                                           parameter.getSamplingRate(),
                                                                           parameter.getMinValue(),
                                                                           parameter.getMaxValue());
            parameterGenSamples.add(parameterGenSample);
        });

        Date date = new Date();
        fileName = aircraft.getAircraftCode()+ "_" +testNumber+ "_" + date.getDay() + "-" + date.getMonth() + "-" + date.getYear() + ".csv";

        // Finding file created
        try{
            file = Paths.get(fileName);
        }catch(InvalidPathException e){
            e.printStackTrace();
        }

        // Writing on file
        try{
            Files.writeString(file,"TimeStamp,AircraftCode,TestNumber,ParameterCode,minValue,maxValue,currentValue,Result\n",StandardOpenOption.CREATE);
        }catch(IOException e){
            e.printStackTrace();
        }

        // Start generating of samples
        for (int i = 0; i < parameterGenSamples.size(); i++) {
            System.out.println(parameterGenSamples.get(i).getCode());
            TestParameter p = new TestParameter();
            p.setParameters(parameterGenSamples.get(i), end);
            p.start();
        }

        try {
            Thread.sleep(time+1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

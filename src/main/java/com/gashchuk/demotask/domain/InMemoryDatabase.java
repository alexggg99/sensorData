package com.gashchuk.demotask.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gashchuk.demotask.domain.entity.ObjectSensorKey;
import com.gashchuk.demotask.domain.entity.SensorData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
In memory DB - core class id DEMO project
InMemoryDatabase knows how get and insert data
 */
@Component
public class InMemoryDatabase implements Database {

    @Value("${data_file}")
    private String dataFileName;

    private long size = 0;

    private final Map<ObjectSensorKey, ArrayList<SensorData>> objectSensorMap = new HashMap<>();
    private final Map<Integer, Set<Integer>> sensorByObject = new HashMap<>();

    final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    @PostConstruct
    public void initDb() {
        JSONParser parser = new JSONParser();
        try {
            try (InputStream inputStream = TypeReference.class.getResourceAsStream(dataFileName);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                JSONArray data = (JSONArray) parser.parse(reader);//path to the JSON file.
                for (Iterator it = data.iterator(); it.hasNext(); ) {
                    JSONObject jsonObject = (JSONObject) it.next();
                    int objectId = ((Number) jsonObject.get("objectId")).intValue();
                    int sensorId = ((Number) jsonObject.get("sensorId")).intValue();
                    long time = (long) jsonObject.get("time");
                    double value = ((Number) jsonObject.get("value")).doubleValue();
                    insertIntoDb(objectId, sensorId, time, value);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public void cleanDb() {
        size = 0;
        this.objectSensorMap.clear();
        this.sensorByObject.clear();
    }

    @Override
    public List<SensorData> getLastSensorData(int objectId) {
        rwl.readLock().lock();
        try {
            List<SensorData> result = new ArrayList<>();
            var sensors = sensorByObject.get(objectId);
            if (sensors == null) {
                return result;
            }
            for (var sensorId : sensors) {
                ObjectSensorKey key = new ObjectSensorKey(objectId, sensorId);
                ArrayList<SensorData> sensorDataList = objectSensorMap.get(key);
                result.add(sensorDataList.get(sensorDataList.size() - 1));
            }
            return result;
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public ArrayList<SensorData> getSensorData(int objectId, int sensorId, long timeFrom, long timeTo) {
        rwl.readLock().lock();
        try {
            ArrayList<SensorData> result = new ArrayList<>();
            ObjectSensorKey key = new ObjectSensorKey(objectId, sensorId);
            ArrayList<SensorData> sensorDataList = objectSensorMap.get(key);
            if (sensorDataList == null) {
                return new ArrayList<>();
            }
            if (sensorDataList.get(0).getTime() > timeTo || sensorDataList.get(sensorDataList.size() - 1).getTime() < timeFrom) {
                return new ArrayList<>();
            }
            SensorData sensorDataKey = new SensorData();
            sensorDataKey.setTime(timeFrom);
            int startIndex = Collections.binarySearch(sensorDataList, sensorDataKey, (o1, o2) -> (int) (o1.getTime() - o2.getTime()));

            if (startIndex >= 0 && sensorDataList.get(startIndex).getTime() < timeFrom) {
                startIndex++;
            }
            if (startIndex < 0) {
                startIndex = Math.abs(startIndex) - 1;
            }
            for (int i = startIndex; i < sensorDataList.size(); i++) {
                SensorData data = sensorDataList.get(i);
                if (data.getTime() > timeTo) {
                    break;
                }
                result.add(data);
            }
            return result;
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public Map<Integer, Double> getAverage() {
        rwl.readLock().lock();
        try {
            var result = new HashMap<Integer, Double>();
            for (var sensorEntry : sensorByObject.entrySet()) {
                Integer objectId = sensorEntry.getKey();
                Set<Integer> sensorSet = sensorEntry.getValue();
                double avg = objectSensorMap.values()
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(it -> sensorSet.contains(it.getKey().getSensorId()) && it.getKey().getObjectId() == objectId)
                        .mapToDouble(SensorData::getValue)
                        .average()
                        .orElse(0.0);
                result.put(objectId, avg);
            }
            return result;
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public void insertIntoDb(int objectId, int sensorId, long time, double value) {
        rwl.writeLock().lock();
        try {
            ObjectSensorKey key = new ObjectSensorKey(objectId, sensorId);
            ArrayList<SensorData> sensorDataList = objectSensorMap.get(key);
            if (sensorDataList == null) {
                sensorDataList = new ArrayList<>();
                objectSensorMap.put(key, sensorDataList);
            }

            SensorData data = new SensorData();
            data.setTime(time);
            data.setKey(key);
            data.setValue(value);

            int lastDataIndex = 0;

            for (lastDataIndex = sensorDataList.size() - 1; lastDataIndex >= 0; lastDataIndex--) {
                SensorData existingData = sensorDataList.get(lastDataIndex);

                if (existingData.getTime() > data.getTime()) {
                    continue;
                } else {
                    break;
                }
            }

            if (lastDataIndex == sensorDataList.size() - 1) {
                sensorDataList.add(data);
            } else {
                sensorDataList.add(lastDataIndex + 1, data);
            }

            Set<Integer> sensorSet = sensorByObject.get(objectId);
            if (sensorSet == null) {
                sensorSet = new HashSet<>();
                sensorByObject.put(objectId, sensorSet);
            }
            sensorSet.add(sensorId);

            size++;
        } finally {
            rwl.writeLock().unlock();
        }
    }

}

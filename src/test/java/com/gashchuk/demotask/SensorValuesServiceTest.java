package com.gashchuk.demotask;

import com.gashchuk.demotask.api.dto.ImmutableObjectValue;
import com.gashchuk.demotask.domain.InMemoryDatabase;
import com.gashchuk.demotask.domain.entity.SensorData;
import com.gashchuk.demotask.infrastructure.SensorValuesService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SensorValuesServiceTest {

    private InMemoryDatabase database = new InMemoryDatabase();
    private SensorValuesService historyService = new SensorValuesService(database);

    @BeforeEach
    public void prepareData() {
        database.cleanDb();
        database.insertIntoDb(1, 1, 1619036320, 82.1);
        database.insertIntoDb(1, 1, 1619036321, 85.1);
        database.insertIntoDb(1, 1, 1619036322, 73.0);
        database.insertIntoDb(1, 2, 1619036320, 88.72);
        database.insertIntoDb(1, 2, 1619036321, 40.0);

    }

    @Test
    void getHistoryNotFound() {
        List<SensorData> values = historyService.getHistory(2, 1, 1619036320, 1619036322);
        assertTrue(values.size() == 0);
        values = historyService.getHistory(1, 3, 1619036320, 1619036322);
        assertTrue(values.size() == 0);
        values = historyService.getHistory(1, 1, 1619036362, 1619036392);
        assertTrue(values.size() == 0);
        values = historyService.getHistory(1, 1, 1619036162, 1619036319);
        assertTrue(values.size() == 0);
    }

    @Test
    void getHistoryFound() {
        List<SensorData> values = historyService.getHistory(1, 1, 1619036321, 1619036322);
        assertTrue(values.size() == 2, "size is not 2");
        assertTrue(values.get(0).getValue() == 85.1, "value assertion failed");
        assertTrue(values.get(1).getValue() == 73.0, "value assertion failed");
    }

    @Test
    void getHistoryEmpty() {
        database.cleanDb();
        List<SensorData> values = historyService.getHistory(1, 1, 1619036320, 1619036320);
        assertTrue(values.size() == 0, "values is not empty");
    }

    @Test
    void getHistoryInsertInWrongOrder() {
        database.insertIntoDb(1, 1, 1619036318, 56.1);
        database.insertIntoDb(1, 1, 1619036319, 56.5);
        database.insertIntoDb(1, 1, 1619036326, 93.2);
        List<SensorData> values = historyService.getHistory(1, 1, 1619036322, 1619036328);
        assertTrue(values.size() == 2, "size is not 2");
        assertTrue(values.get(0).getValue() == 73.0, "value assertion failed");
        assertTrue(values.get(1).getValue() == 93.2, "value assertion failed");
    }

    @Test
    void getHistoryInsertReverseOrder() {
        database.cleanDb();
        database.insertIntoDb(1, 1, 1619036356, 99.2);
        database.insertIntoDb(1, 1, 1619036355, 93.2);
        database.insertIntoDb(1, 1, 1619036354, 56.5);
        database.insertIntoDb(1, 1, 1619036353, 56.1);
        database.insertIntoDb(1, 1, 1619036352, 49.1);
        List<SensorData> values = historyService.getHistory(1, 1, 1619036353, 1619036355);
        assertTrue(values.size() == 3, "size is not 3");
        assertTrue(values.get(0).getValue() == 56.1, "value assertion failed");
        assertTrue(values.get(1).getValue() == 56.5, "value assertion failed");
        assertTrue(values.get(2).getValue() == 93.2, "value assertion failed");
    }

    @Test
    void getHistoryFoundCornerCase() {
        database.cleanDb();
        database.insertIntoDb(1, 1, 1619036320, 82.1);
        List<SensorData> values = historyService.getHistory(1, 1, 1619036320, 1619036320);
        assertTrue(values.size() == 1, "size is not 1");
        assertTrue(values.get(0).getValue() == 82.1, "value assertion failed");
    }

    @Test
    void getHistoryInsertSparse() {
        database.cleanDb();
        database.insertIntoDb(1, 1, 1619036356, 99.2);
        database.insertIntoDb(1, 1, 1619036342, 93.2);
        database.insertIntoDb(1, 1, 1619036346, 66.5);
        List<SensorData> values = historyService.getHistory(1, 1, 1619036345, 1619036349);
        assertTrue(values.size() == 1, "size is not 1");
        assertTrue(values.get(0).getValue() == 66.5, "value assertion failed");
    }

    @Test
    void getLatest() {
        database.cleanDb();
        List<SensorData> values = historyService.getLatest(1);
        assertTrue(values.size() == 0, "values is not empty");
    }

    @Test
    void getLatestOneRecord() {
        database.cleanDb();
        database.insertIntoDb(1, 1, 1619036320, 82.1);
        List<SensorData> values = historyService.getLatest(1);
        assertTrue(values.size() == 1, "size is not 1");
        assertTrue(values.get(0).getValue() == 82.1, "value assertion failed");
    }

    @Test
    void getLatestEmpty() {
        database.insertIntoDb(2, 1, 1619036340, 82.79);
        database.insertIntoDb(2, 1, 1619036351, -1.9);
        List<SensorData> values = historyService.getLatest(1);
        assertTrue(values.size() == 2, "size is not 2");
        assertTrue(values.get(0).getValue() == 73.0, "value assertion failed");
        assertTrue(values.get(1).getValue() == 40.0, "value assertion failed");
        values = historyService.getLatest(2);
        assertTrue(values.size() == 1, "size is not 1");
        assertTrue(values.get(0).getValue() == -1.9, "value assertion failed");
    }

    @Test
    void getAvarage() {
        database.insertIntoDb(2, 1, 1619036340, 82.79);
        database.insertIntoDb(2, 1, 1619036351, -1.9);
        Map<Integer, Double> avgMap = historyService.getAverage();
        assertEquals(avgMap.get(1), ((82.1 + 85.1 + 73.0 + 88.72 + 40.0)/5), 0.000000000001d);
        assertEquals(avgMap.get(2), ((82.79 + -1.9)/2), 0.000000000001d);
    }

    @Test
    void getAvarageEmptyDb() {
        database.cleanDb();
        Map<Integer, Double> avg = historyService.getAverage();
        assertTrue(avg != null, "Average map is null");
        assertTrue(avg.size() == 0, "Average map size is not null");
    }

    @Test
    void getAverageSingleRecord() {
        database.cleanDb();
        database.insertIntoDb(1, 1, 1619036320, 82.1);
        Map<Integer, Double> avg = historyService.getAverage();
        assertTrue(avg.get(1) == 82.1, "value assertion failed");
    }

    @Test
    void saveData() {
        assertTrue(database.size() == 5, "Database size is not 2");
        var data = Lists.list(
                new ImmutableObjectValue(1, 1, 1619036380, 82.1),
                new ImmutableObjectValue(1, 1, 1619036381, 82.1)
        );
        historyService.insertData(data);
        assertTrue(database.size() == 7, "Database size is not 2");
    }
}

package com.gashchuk.demotask;

import com.gashchuk.demotask.domain.Database;
import com.gashchuk.demotask.domain.InMemoryDatabase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryDatabaseTest {

    private Database database = new InMemoryDatabase();

    @Test
    void insertIntoDb() {
        assertTrue(database.size() == 0);
        database.insertIntoDb(1, 1, 1619036320, 82.1);
        assertTrue(database.size() == 1);
        database.insertIntoDb(1, 2, 1619036320, 82.1);
        assertTrue(database.size() == 2);
    }

    @Test
    void clearDb() {
        database.insertIntoDb(1, 1, 1619036320, 82.1);
        assertTrue(database.size() == 1);
        database.cleanDb();
        assertTrue(database.size() == 0);
    }

}

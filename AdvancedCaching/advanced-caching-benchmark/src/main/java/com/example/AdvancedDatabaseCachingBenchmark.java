package com.example;

import java.sql.Connection;
import java.util.Map;
import com.google.common.cache.Cache;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.concurrent.TimeUnit;
import java.util.LinkedHashMap;
import com.google.common.cache.CacheBuilder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvancedDatabaseCachingBenchmark {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/testdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static final int NUM_ELEMENTS = 100000;
    private static final int L1_CACHE_SIZE = 10000;
    private static final int L2_CACHE_SIZE = 10000;
    private static final long L2_CACHE_DURATION_MINUTES = 10;

    private static Connection connection;
    private static Map<Integer, String> l1Cache;
    private static Cache<Integer, String> l2Cache;
    private static AtomicInteger elementCount = new AtomicInteger(0);
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedDatabaseCachingBenchmark.class);

    public static void main(String[] args) {
        try {
            setupDatabase();
            setupCaches();

            ExecutorService executor = Executors.newFixedThreadPool(4);

            Future<Long> dbInsertTime = executor.submit(AdvancedDatabaseCachingBenchmark::benchmarkDatabaseInsert);
            Future<Long> dbRetrieveTime = executor.submit(AdvancedDatabaseCachingBenchmark::benchmarkDatabaseRetrieve);
            Future<Long> l1CacheInsertTime = executor.submit(AdvancedDatabaseCachingBenchmark::benchmarkL1CacheInsert);
            Future<Long> l1CacheRetrieveTime = executor.submit(AdvancedDatabaseCachingBenchmark::benchmarkL1CacheRetrieve);
            Future<Long> l2CacheInsertTime = executor.submit(AdvancedDatabaseCachingBenchmark::benchmarkL2CacheInsert);
            Future<Long> l2CacheRetrieveTime = executor.submit(AdvancedDatabaseCachingBenchmark::benchmarkL2CacheRetrieve);
            Future<Long> multilevelCacheRetrieveTime = executor.submit(AdvancedDatabaseCachingBenchmark::benchmarkMultilevelCacheRetrieve);

            printResults(
                dbInsertTime.get(), dbRetrieveTime.get(),
                l1CacheInsertTime.get(), l1CacheRetrieveTime.get(),
                l2CacheInsertTime.get(), l2CacheRetrieveTime.get(),
                multilevelCacheRetrieveTime.get()
            );

            executor.shutdown();
        } catch (Exception e) {
            LOGGER.error("An error occurred during execution.", e);
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                LOGGER.error("Failed to close the database connection.", e);
            }
        }
    }

    private static void setupDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement statement = connection.createStatement();
            statement.executeUpdate("TRUNCATE TABLE test_table");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS test_table (id INT PRIMARY KEY, value VARCHAR(255))");
            statement.close();
            LOGGER.info("Database setup complete.");
        } catch (SQLException e) {
            LOGGER.error("Database setup failed.", e);
        }
    }

    private static void setupCaches() {
        l1Cache = new LinkedHashMap<Integer, String>(L1_CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
                return size() > L1_CACHE_SIZE;
            }
        };
        l2Cache = CacheBuilder.newBuilder()
            .maximumSize(L2_CACHE_SIZE)
            .expireAfterAccess(L2_CACHE_DURATION_MINUTES, TimeUnit.MINUTES)
            .build();
        LOGGER.info("Caches setup complete with L1 size: {} and L2 size: {}.", L1_CACHE_SIZE, L2_CACHE_SIZE);
    }

    private static long benchmarkDatabaseInsert() throws SQLException {
        long startTime = System.nanoTime();
        String sql = "INSERT INTO test_table (id, value) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        
        for (int i = 1; i <= NUM_ELEMENTS; i++) {
            // Check if the ID already exists before inserting
            String checkSql = "SELECT COUNT(*) FROM test_table WHERE id = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setInt(1, i);
            ResultSet resultSet = checkStatement.executeQuery();
            
            if (resultSet.next() && resultSet.getInt(1) == 0) {
                statement.setInt(1, i);
                statement.setString(2, "Value" + i);
                statement.addBatch();
            }
            resultSet.close();
            checkStatement.close();
            
            if (i % 100 == 0) {
                statement.executeBatch();
            }
        }
        statement.executeBatch();
        statement.close();
        LOGGER.info("Database insert completed.");
        return System.nanoTime() - startTime;
    }

    private static long benchmarkDatabaseRetrieve() throws SQLException {
        long startTime = System.nanoTime();
        String sql = "SELECT * FROM test_table WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        
        for (int i = 1; i <= NUM_ELEMENTS; i++) {
            statement.setInt(1, i);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                resultSet.getString("value");
            }
            resultSet.close();
        }
        statement.close();
        LOGGER.info("Database retrieval completed.");
        return System.nanoTime() - startTime;
    }

    private static long benchmarkL1CacheInsert() {
        long startTime = System.nanoTime();
        for (int i = 1; i <= NUM_ELEMENTS; i++) {
            l1Cache.put(i, "Value" + i);
            elementCount.incrementAndGet();
        }
        LOGGER.info("L1 cache insert completed.");
        return System.nanoTime() - startTime;
    }

    private static long benchmarkL1CacheRetrieve() {
        long startTime = System.nanoTime();
        for (int i = 1; i <= NUM_ELEMENTS; i++) {
            l1Cache.get(i);
        }
        LOGGER.info("L1 cache retrieval completed.");
        return System.nanoTime() - startTime;
    }

    private static long benchmarkL2CacheInsert() {
        long startTime = System.nanoTime();
        for (int i = 1; i <= NUM_ELEMENTS; i++) {
            l2Cache.put(i, "Value" + i);
        }
        LOGGER.info("L2 cache insert completed.");
        return System.nanoTime() - startTime;
    }

    private static long benchmarkL2CacheRetrieve() {
        long startTime = System.nanoTime();
        for (int i = 1; i <= NUM_ELEMENTS; i++) {
            l2Cache.getIfPresent(i);
        }
        LOGGER.info("L2 cache retrieval completed.");
        return System.nanoTime() - startTime;
    }

    private static long benchmarkMultilevelCacheRetrieve() throws SQLException {
        long startTime = System.nanoTime();
        for (int i = 1; i <= NUM_ELEMENTS; i++) {
            String value = l1Cache.get(i);
            if (value == null) {
                value = l2Cache.getIfPresent(i);
                if (value == null) {
                    String sql = "SELECT * FROM test_table WHERE id = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, i);
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        value = resultSet.getString("value");
                        l2Cache.put(i, value);
                        l1Cache.put(i, value);
                    }
                    resultSet.close();
                    statement.close();
                }
            }
        }
        LOGGER.info("Multilevel cache retrieval completed.");
        return System.nanoTime() - startTime;
    }

    private static void printResults(long dbInsertTime, long dbRetrieveTime,
                                     long l1CacheInsertTime, long l1CacheRetrieveTime,
                                     long l2CacheInsertTime, long l2CacheRetrieveTime,
                                     long multilevelCacheRetrieveTime) {
        System.out.println("=== Benchmark Results ===");
        System.out.println("Database Insert Time: " + formatTime(dbInsertTime));
        System.out.println("Database Retrieve Time: " + formatTime(dbRetrieveTime));
        System.out.println("L1 Cache Insert Time: " + formatTime(l1CacheInsertTime));
        System.out.println("L1 Cache Retrieve Time: " + formatTime(l1CacheRetrieveTime));
        System.out.println("L2 Cache Insert Time: " + formatTime(l2CacheInsertTime));
        System.out.println("L2 Cache Retrieve Time: " + formatTime(l2CacheRetrieveTime));
        System.out.println("Multilevel Cache Retrieve Time: " + formatTime(multilevelCacheRetrieveTime));
        System.out.println("=========================");
    }

    private static String formatTime(long timeInNanoseconds) {
        long timeInMilliseconds = timeInNanoseconds / 1_000_000;
        long timeInMinutes = timeInMilliseconds / 60000;
        return String.format("%d ms (%d min)", timeInMilliseconds, timeInMinutes); // Convert nanoseconds to milliseconds and minutes
    }
}

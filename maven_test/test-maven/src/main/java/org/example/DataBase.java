package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:currency.db";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS currency_rates (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "currency_code TEXT, " +
                    "currency_name TEXT, " +
                    "units TEXT, " +
                    "rate TEXT, " +
                    "course TEXT" +
                    ")");
            System.out.println("Таблица currency_rates создана успешно.");
        } catch (SQLException e) {
            System.out.println("Ошибка при создании таблицы: " + e.getMessage());
        }
    }
}


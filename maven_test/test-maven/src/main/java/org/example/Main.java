package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.sql.*;

public class Main {
    public static void main(String[] args) throws IOException {
        //Подключаемся к странице ЦБ РФ
        Document doc = Jsoup.connect("https://www.cbr.ru/currency_base/daily/").get();
        //Достаем оттуда таблицу с курсом валют
        Element table = doc.select("table.data").first();
        //Проверяем наличие страницы
        if (table != null) {
            //Парсим строки таблицы
            Elements rows = table.select("tr");
            //Подключаемся к базе данных
            String url = "jdbc:sqlite:currency.db";
            //Проверка подключения к БД
            try (Connection conn = DriverManager.getConnection(url)) {
                if (conn != null) {
                    System.out.println("Подключение к базе данных успешно");
                }
            } catch (SQLException e) {
                System.out.println("Ошибка подключения к базе данных: " + e.getMessage());
            }
            //Проверяем соединения с БД и возможность внесения в нее данных
            try (Connection conn = DriverManager.getConnection(url)) {
                for (Element row : rows) {
                    //Парсим столбцы таблицы
                    Elements columns = row.select("td");
                    if (columns.size() == 6) {
                        String currencyCode = columns.get(1).text();
                        String currencyName = columns.get(2).text();
                        String units = columns.get(3).text();
                        String rate = columns.get(4).text();
                        String course = columns.get(5).text();
                        //Вносим данные
                        String sql = "INSERT INTO currency_rates(currency_code, currency_name, units, rate, course) VALUES(?, ?, ?)";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, currencyCode);
                            stmt.setString(2, currencyName);
                            stmt.setString(3, units);
                            stmt.setString(4, rate);
                            stmt.setString(5, course);
                            stmt.executeUpdate();
                        } catch (SQLException e) {
                            System.out.println("Ошибка при вставке данных: " + e.getMessage());
                        }
                    }
                }
                System.out.println("Данные успешно добавлены в базу данных");


            } catch (SQLException e) {
                System.out.println("Ошибка при подключении к базе данных: " + e.getMessage());
            }
        } else {
            System.out.println("Таблица курсов валют не найдена на странице.");
        }

        //Выводим собранные данные
        if (table != null) {
            Elements rows = table.select("tr");
            for (Element row : rows) {
                Elements columns = row.select("td, th");
                for (Element column : columns) {
                    String tagName = column.tagName();
                    String text = column.text();
                    if ("th".equals(tagName)) {
                        System.out.printf("%-40s", text);
                    } else {
                        System.out.printf("%-40s", text);
                    }
                }
                System.out.println();
            }
        } else {
            System.out.println("Таблица курсов валют не найдена на странице.");
       }

    }
}
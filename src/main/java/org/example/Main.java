package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class Main {
    static Connection connection = null;
    static Statement statement = null;
    public static void connect() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres?currentSchema=public";
        String user = "postgres";
        String password = "1613";



        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        connect();


        statement.execute("drop table if exists public.progress;\n" +
                "\n" +
                "drop table if exists public.students;\n" +
                "\n" +
                "drop table if exists public.subjects;");
        statement.execute("CREATE TABLE IF NOT EXISTS students\n" +
                "(id serial NOT NULL PRIMARY KEY,\n" +
                " name varchar(30) NOT NULL,\n" +
                " PassportSerial varchar(4) not null,\n" +
                " PassportNumber varchar(6) not null,\n" +
                " UNIQUE (PassportSerial, PassportNumber)\n" +
                ");");
        statement.execute("CREATE TABLE IF NOT EXISTS subjects\n" +
                "(id serial NOT NULL PRIMARY KEY,\n" +
                " name varchar(50) NOT NULL\n" +
                ");");

        statement.execute("CREATE TABLE IF NOT EXISTS progress\n" +
                "(id serial NOT NULL PRIMARY KEY,\n" +
                " student int NOT NULL REFERENCES students(id) ON DELETE CASCADE,\n" +
                " subject int NOT NULL REFERENCES subjects(id),\n" +
                " mark smallint NOT NULL CHECK(mark BETWEEN 2 and 5)\n" +
                ");");
        statement.execute("INSERT into students (id, name, PassportSerial, PassportNumber)\n" +
                "values\n" +
                "    (1, 'Боб', 3319, 123456),\n" +
                "    (2, 'Валера', 3312, 654321),\n" +
                "    (3, 'Казбек', 3313, 123654),\n" +
                "    (4, 'Шахзод', 3314, 321456),\n" +
                "    (5, 'Валентин', 3315, 162534),\n" +
                "    (6, 'Петя', 3316, 342516);");
        statement.execute("INSERT into subjects (id, name)\n" +
                "values\n" +
                "    (1, 'Технологии программирования'),\n" +
                "    (2, 'Алгоритмы решений'),\n" +
                "    (3, 'Методы оптимизации'),\n" +
                "    (4, 'Математика'),\n" +
                "    (5, 'Электротехника');");
        statement.execute("INSERT into progress (id, student, subject, mark)\n" +
                "values\n" +
                "    (1, 1, 1, 5),\n" +
                "    (2, 1, 2, 5),\n" +
                "    (3, 1, 3, 5),\n" +
                "    (4, 1, 4, 5),\n" +
                "    (5, 1, 5, 5),\n" +
                "    (6, 2, 1, 5),\n" +
                "    (7, 2, 3, 3),\n" +
                "    (8, 2, 4, 4),\n" +
                "    (9, 2, 5, 3),\n" +
                "    (10, 2, 4, 4),\n" +
                "    (11, 3, 1, 5),\n" +
                "    (12, 3, 2, 5),\n" +
                "    (13, 3, 3, 5),\n" +
                "    (14, 3, 5, 5),\n" +
                "    (15, 3, 5, 5),\n" +
                "    (16, 4, 1, 4),\n" +
                "    (17, 4, 2, 4),\n" +
                "    (18, 4, 3, 4),\n" +
                "    (19, 4, 4, 4),\n" +
                "    (20, 4, 2, 4),\n" +
                "    (21, 5, 3, 3),\n" +
                "    (22, 5, 4, 3),\n" +
                "    (23, 5, 5, 3),\n" +
                "    (24, 5, 3, 3),\n" +
                "    (25, 6, 1, 5),\n" +
                "    (26, 6, 2, 2),\n" +
                "    (27, 6, 3, 2),\n" +
                "    (28, 6, 4, 3),\n" +
                "    (29, 6, 5, 2),\n" +
                "    (30, 6, 5, 2);\n");
        System.out.println("Вывести список студентов, сдавших определенный предмет, на оценку выше 3");
        var res = statement.executeQuery("Select s.name, p.Mark, ss.name from Students s\n" +
                "INNER JOIN Progress p ON s.id = p.student\n" +
                "INNER JOIN Subjects ss ON p.subject = ss.id\n" +
                "WHERE p.mark > 3 AND ss.name = 'Математика';");
        while (res.next()) {
            String subjName = res.getString(1);
            int mark = res.getInt(2);
            String studName = res.getString(3);
            System.out.println(subjName + " " + mark + " " + studName);
        }

        System.out.println("Посчитать средний бал по определенному предмету");
        var res2 = statement.executeQuery("select avg(p.mark) as \"Средний балл\" from progress p\n" +
                "inner join subjects s on p.subject = s.id\n" +
                "where s.name = 'Математика';");
        while (res2.next()) {
            double avg = res2.getDouble(1);
            System.out.println(avg);
        }
        System.out.println("Посчитать средний балл по определенному студенту");
        var res3 = statement.executeQuery("select avg(p.mark) as \"Средний балл\" from progress p\n" +
                "inner join subjects s on p.subject = s.id\n" +
                "inner join students s2 on p.student = s2.id\n" +
                "where s2.name = 'Казбек';");
        while (res3.next()) {
            double avg = res3.getDouble(1);
            System.out.println(avg);
        }
        System.out.println("Найти три премета, которые сдали наибольшее количество студентов");
        var res4 = statement.executeQuery("SELECT count(*), s.name from progress p\n" +
                "inner join subjects s on s.id = p.subject\n" +
                "where p.mark > 2\n" +
                "group by s.name\n" +
                "order by count(*) desc limit 3;");
        while (res4.next()) {
            int cnt = res4.getInt(1);
            String name = res4.getString(2);
            System.out.println(cnt + " " + name);
        }
        System.out.println();

        System.out.println("Вывести студентов, которые не сдали любой экзамен или получили двойку");
        var res5 = statement.executeQuery("SELECT s.name, ss.name, p.mark FROM Students s\n" +
                "CROSS JOIN Subjects ss\n" +
                "LEFT JOIN Progress p ON s.id = p.student AND ss.id = p.subject\n" +
                "WHERE p.mark IS NULL OR p.mark = 2\n" +
                "OFFSET 3;");
        while (res5.next()) {
            String studName = res5.getString(1);
            String subjName = res5.getString(2);
            int mark = res5.getInt(3);
            if (res5.wasNull()) {
                System.out.println(studName + " " + subjName + " оценка отсутствует");
            } else {
                System.out.println(studName + " " + subjName + " " + mark);
            }
        }

        disconnect();
    }
}
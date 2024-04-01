package application;

import application.model.CourseOfferings;
import application.model.CsvParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
//        CourseOfferings courseOfferings = new CourseOfferings("data/course_data_2018.csv");
        CourseOfferings courseOfferings = new CourseOfferings("data/small_data.csv");
    }
}

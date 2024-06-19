package application;

import application.model.Course;
import application.model.CoursePlanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main driver of SpringBoot Application
 */
@SpringBootApplication
public class Application {
    public static CoursePlanner coursePlanner = new CoursePlanner("data/course_data_2022.csv");

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    public static CoursePlanner getCoursePlanner() {
        return coursePlanner;
    }

}
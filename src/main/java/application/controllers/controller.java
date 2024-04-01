package application.controllers;

import application.model.CsvParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class controller {
//    CsvParser csvParser = new CsvParser("data/course_data_2018.csv");
    @GetMapping("test")
    public String getTest() {
        return "success";
    }
    @GetMapping("api/dump-model")
    public void dumpModel() {
//       csvParser.dumpCourses();
    }
}

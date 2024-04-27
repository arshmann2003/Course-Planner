package application.AllApiDtoClasses;

import application.model.Course;

public class ApiCourseDTO {
    public long courseId;
    public String catalogNumber;

    public ApiCourseDTO(String catalogNumber, long courseId) {
        this.courseId = courseId;
        this.catalogNumber = catalogNumber;
    }
}

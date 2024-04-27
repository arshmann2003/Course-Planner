package application.AllApiDtoClasses;
import application.model.Course;

import java.util.List;

public class ApiWatcherDTO {
    public long id;
    public ApiDepartmentDTO department;
    public ApiCourseDTO course;
    public List<String> events;
    public long courseId;
    public long deptId;

    public ApiWatcherDTO() {

    }
}

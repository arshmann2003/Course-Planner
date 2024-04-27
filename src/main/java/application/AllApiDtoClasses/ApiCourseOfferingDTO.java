package application.AllApiDtoClasses;

import application.model.Course;

public class ApiCourseOfferingDTO {
    public long courseOfferingId;
    public String location;
    public String instructors;
    public String term;
    public long semesterCode;
    public int year;

    public ApiCourseOfferingDTO(Course course, long courseOfferingId) {
        this.courseOfferingId = courseOfferingId;
        this.location = course.getLocation();
        this.instructors = course.getInstructors().toString();
        this.term = course.getTerm();
        this.semesterCode = Long.parseLong(course.getSemester());
        this.year = course.getYear();
    }
}

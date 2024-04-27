package application.AllApiDtoClasses;

import application.model.Course;

public class ApiOfferingSectionDTO {
    public String type;
    public int enrollmentCap;
    public int enrollmentTotal;


    public ApiOfferingSectionDTO(Course course) {
        this.type = course.getComponentCode();
        this.enrollmentCap = Integer.parseInt(course.getEnrollmentCap());
        this.enrollmentTotal = Integer.parseInt(course.getEnrollmentTotal());
    }
}

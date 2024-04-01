package application.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Course implements Comparable<Course>{
    String semester;
    String subject;
    String catalogNumber;
    String location;
    String enrollmentCap;
    String enrollmentTotal;
    String componentCode;
    List<String> instructors;

    public String toString() {
        return semester + " " + subject + " " + catalogNumber + " " + location + " " + enrollmentTotal + " " + enrollmentCap + " " + instructors + " " + componentCode;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        this.catalogNumber = catalogNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEnrollmentCap() {
        return enrollmentCap;
    }

    public void setEnrollmentCap(String enrollmentCap) {
        this.enrollmentCap = enrollmentCap;
    }

    public String getEnrollmentTotal() {
        return enrollmentTotal;
    }

    public void setEnrollmentTotal(String enrollmentTotal) {
        this.enrollmentTotal = enrollmentTotal;
    }

    public List<String> getInstructors() {
        return instructors;
    }

    public void setInstructors(String instructors) {
        String[] words = instructors.split("\\s*,\\s*");
        List<String> filteredInstructors = new ArrayList<>();
        for(String word : words) {
            if(!(word.equals("<null>") || word.equals("(null)")))
                filteredInstructors.add(word);
        }
        this.instructors = filteredInstructors;
    }

    public String getComponentCode() {
        return componentCode;
    }

    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode;
    }

    public void addInstructor(List<String> allInstructors) {
        if (allInstructors == null || this.instructors == null) return;
        List<String> newArr = new ArrayList<>(instructors);
        for(String newInstructor : allInstructors) {
            if(!newArr.contains(newInstructor) && !newInstructor.equals("<null>")
                    && !newInstructor.equals("(null)"))
                newArr.add(newInstructor);
        }
        this.instructors = newArr;
    }
    public String getKey() {
        return semester + subject + catalogNumber + location + componentCode;
    }

    @Override
    public int compareTo(Course o) {
        return  (semester+location).compareTo(o.getSemester() + o.getLocation());
    }

    public boolean isSameCourse(Course mainCourse) {
        return (subject + catalogNumber).equals(mainCourse.getSubject() + mainCourse.getCatalogNumber());
    }

    public void updateEnrollement(Course course) {
        int newEnrolmentTotal = Integer.parseInt(course.getEnrollmentTotal());
        newEnrolmentTotal += Integer.parseInt(course.getEnrollmentTotal());
        int newEnrollmentCap = Integer.parseInt(course.getEnrollmentCap());
        newEnrollmentCap+=Integer.parseInt(course.getEnrollmentCap());
        course.setEnrollmentTotal(String.valueOf(newEnrolmentTotal));
        course.setEnrollmentCap(String.valueOf(newEnrollmentCap));
    }
}

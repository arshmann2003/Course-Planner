package application.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Stores data relating to a Course
 * Adds instructors, updates enrollment totals, and adds
 * different course components for the matching Course.
 */
public class Course implements Comparable<Course>{
    String semester;
    String subject;
    String catalogNumber;
    String location;
    String enrollmentCap;
    String enrollmentTotal;
    String componentCode;
    List<String> instructors;
    List<Course> courseComponents = new ArrayList<>();

    public String toString() {
        String header = "\t" + getSemester() + " in " + getLocation() + " by " + getInstructors() + '\n';
        String courseComponentInfos = "";
        int i=0;
        for(Course x : courseComponents) {
            courseComponentInfos += ("\t\tType=" + x.getComponentCode() + " Enrollment=" + x.getEnrollmentTotal()+ "/" + x.getEnrollmentCap());
            if(i!=courseComponentInfos.length()-1) courseComponentInfos+='\n';
            i++;
        }
        return header + courseComponentInfos;
    }

    public void updateEnrollment(Course course) {
        int newEnrolmentTotal = Integer.parseInt(getEnrollmentTotal());
        newEnrolmentTotal += Integer.parseInt(course.getEnrollmentTotal());
        int newEnrollmentCap = Integer.parseInt(getEnrollmentCap());
        newEnrollmentCap+=Integer.parseInt(course.getEnrollmentCap());
        setEnrollmentTotal(String.valueOf(newEnrolmentTotal));
        setEnrollmentCap(String.valueOf(newEnrollmentCap));
    }

    public void addCourseComponent(Course course) {
        if(courseComponents==null)
            courseComponents = new ArrayList<>();
        courseComponents.add(course);
    }

    public List<Course> getSectionTypeOfferings() {
        return courseComponents;
    }

    public void addSingleInstructor(List<String> instructors) {
        for(String instructor : instructors) {
            if(!this.instructors.contains(instructor)){
                this.instructors.add(instructor);
            }
        }
        sortInstructors();
    }

    private void sortInstructors() {
        instructors.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (!Character.isLetter(o1.charAt(0)) && Character.isLetter(o2.charAt(0))) {
                    return 1;
                } else if (Character.isLetter(o1.charAt(0)) && !Character.isLetter(o2.charAt(0))) {
                    return -1;
                } else {
                    return o1.compareTo(o2);
                }
            }
        });
    }

    public void setComponentCode(String componentCode) {
        courseComponents.add(this);
        this.componentCode = componentCode;
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

    public void addInstructor(List<String> allInstructors) {
        if (allInstructors == null || this.instructors == null) return;
        List<String> newArr = new ArrayList<>(instructors);
        for(String newInstructor : allInstructors) {
            if(!newArr.contains(newInstructor) && !newInstructor.equals("<null>")
                    && !newInstructor.equals("(null)"))
                newArr.add(newInstructor);
        }
        this.instructors = newArr;
        sortInstructors();
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

    public String getComponentCode() {
        return componentCode;
    }


    public String getFullKey() {
        return semester + subject + catalogNumber + location + componentCode;
    }

    @Override
    public int compareTo(Course o) {
        return  (semester+location).compareTo(o.getSemester() + o.getLocation());
    }
}

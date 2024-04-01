package application.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CsvParser {
    public String path;
    public List<Course> courses;
    public CsvParser(String path) {
       this.path = path;
       courses = new ArrayList<>();
       populateCourses();
    }

    public List<Course> getData() {
        return courses;
    }
    private void populateCourses() {
        String line;
        String csvSplitBy = ","; // Assuming CSV fields are separated by commas
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // Assuming the first line is header and you want to skip it
            while ((line = br.readLine()) != null) {
                String[] data = splitCSVLine(line);
                addNewCourse(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String[] splitCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder fieldBuilder = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(fieldBuilder.toString());
                fieldBuilder.setLength(0);
            } else {
                fieldBuilder.append(c);
            }
        }
        fields.add(fieldBuilder.toString());
        return fields.toArray(new String[0]);
    }

    private void addNewCourse(String[] data) {
        String semester = data[0].strip();
        String subject = data[1].strip();
        String catalogNumber = data[2].strip();
        String location = data[3].strip();
        String enrollmentCapacity = data[4].strip();
        String enrollmentTotal = data[5].strip();
        String instructors = data[6].strip();
        String componentCode = data[7].strip();
        Course course = new Course();
        course.setSemester(semester);
        course.setCatalogNumber(catalogNumber);
        course.setSubject(subject);
        course.setLocation(location);
        course.setEnrollmentCap(enrollmentCapacity);
        course.setEnrollmentTotal(enrollmentTotal);
        course.setInstructors(instructors);
        course.setComponentCode(componentCode);
        courses.add(course);
    }
}

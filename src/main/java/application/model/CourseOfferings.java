package application.model;

import java.util.*;

/**
 * Class to manipulate and efficiently store csv data
 * Aggregates same courses and sections
 * Dumps courses in a formatted way to servers terminal
 * Sorts courses based on name + catalog number
 */
public class CourseOfferings {
    private TreeMap<String, List<Course>> treeMap;
    private List<Course> courses;

    public CourseOfferings(String path) {
        CsvParser csvParser = new CsvParser(path);
        courses = csvParser.getCsvData();
        aggregateSameCourses();
        createTreeMap();
        sortOfferings();
        aggregateSameSections();
    }

    public void dumpCourses() {
        Collection<List<Course>> allOfferings = treeMap.values();
        for(List<Course> offering : allOfferings) {
            if(offering.isEmpty()) continue;
            System.out.println(offering.getFirst().getSubject() + offering.getFirst().getCatalogNumber());
            for(Course course : offering) {
                System.out.print(course);
            }
        }
    }

    private void aggregateSameCourses() {
        HashMap<String, Course> hashMap = new HashMap<>();
        for(Course course : courses) {
            String key = course.getFullKey();
            if(hashMap.containsKey(key)) {
                Course sameCourse = hashMap.get(key);
                sameCourse.updateEnrollment(course);
                sameCourse.addInstructor(course.getInstructors());
            } else {
                hashMap.put(key, course);
            }
        }
        this.courses = new ArrayList<>();
        courses.addAll(hashMap.values());
    }

    private void createTreeMap() {
        treeMap = new TreeMap<>();
        for(Course course : courses) {
            String key = course.getSubject() + course.getCatalogNumber();
            if(treeMap.containsKey(key)) {
                treeMap.get(key).add(course);
            } else {
                ArrayList<Course> newOffering = new ArrayList<>();
                newOffering.add(course);
                treeMap.put(key, newOffering);
            }
        }
    }

    private void aggregateSameSections() {
        Collection<List<Course>> allOfferings = treeMap.values();
        for(List<Course> offering : allOfferings) {
            HashMap<String, Course> components = new HashMap<>();
            List<Course> removeCourses = new ArrayList<>();
            for(Course course : offering) {
                String key = course.getSemester() + course.getLocation();
                if(components.containsKey(key)) {
                    components.get(key).addCourseComponent(course);
                    components.get(key).addSingleInstructor(course.getInstructors());
                    removeCourses.add(course);
                } else {
                    components.put(key, course);
                }
            }
            for(Course course : removeCourses)
                offering.remove(course);
        }
    }

    private void sortOfferings() {
        Collection<List<Course>> allOfferings = treeMap.values();
        for(List<Course> offerings : allOfferings) {
            offerings.sort(new Comparator<Course>() {
                @Override
                public int compare(Course o1, Course o2) {
                    int val =  o1.getSemester().compareTo(o2.getSemester());
                    if(val==0) return o1.getLocation().compareTo(o2.getLocation());
                    return val;
                }
            });
        }
    }
}

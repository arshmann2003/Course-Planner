package application.model;

import com.sun.source.tree.Tree;

import java.util.*;

public class CourseOfferings {
    List<Course> courses;
    TreeMap<String, List<Course>> treeMap;

    public CourseOfferings(String path) {
        CsvParser csvParser = new CsvParser(path);
        courses = csvParser.getData();
//        sortOfferings();
        createTreeMap();
        aggregateSameCourses();
        dumpCourses();
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

    private void aggregateSameCourses() {
        HashMap<String, Course> hashMap = new HashMap<>();
        for(Course course : courses) {
            String key = course.getKey();
            if(hashMap.containsKey(key)) {
                Course sameCourse = hashMap.get(key);
                sameCourse.updateEnrollement(course);
                sameCourse.addInstructor(course.getInstructors());
            } else {
                hashMap.put(key, course);
            }
        }
        this.courses = new ArrayList<>();
        courses.addAll(hashMap.values());
    }


    public void dumpCourses() {
        Collection<List<Course>> allOfferings = treeMap.values();
        for(List<Course> offering : allOfferings) {
            if(offering.isEmpty()) continue;
            System.out.println(offering.getFirst().getSubject() + offering.getFirst().getCatalogNumber());
            List<String> components = new ArrayList<>();
            for(Course course : offering) {
                String key = course.getSemester() + course.getLocation();
                if(!components.contains(key))
                    System.out.println("\t" + course.getSemester() + " in " + course.getLocation() + " by " + course.getInstructors());
                System.out.println("\t\t" + " Type=" + course.getComponentCode() + " Enrollment=" + course.getEnrollmentTotal()+ "/" + course.getEnrollmentCap());
                components.add(key);
            }
        }
    }
}

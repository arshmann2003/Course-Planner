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
    private List<Department> departments;

    public CourseOfferings(String path) {
        CsvParser csvParser = new CsvParser(path);
        courses = csvParser.getCsvData();
        aggregateSameCourses();
        createTreeMap();
        sortOfferings();
        aggregateSameSections();
        assignDeptIds();
    }

    public void dumpCourses() {
        Collection<List<Course>> allOfferings = treeMap.values();
        for(List<Course> offering : allOfferings) {
            if(offering.isEmpty()) continue;
            Course firstCourse = offering.getFirst();
            System.out.println(firstCourse.getSubject() + firstCourse.getCatalogNumber());
            for(Course course : offering) {
                System.out.print(course);
            }
        }
    }

    public List<String> getCoursesInDept(int deptId) {
        if(deptId > 0 && deptId <= departments.size()) {
            return departments.get(deptId-1).getCourses();
        }
        throw new IllegalArgumentException();
    }


    public List<Department> getDepartments() {
        return departments;
    }

    private void assignDeptIds() {
        departments = new ArrayList<>();
        Collection<List<Course>> allOfferings = treeMap.values();
        HashMap<String, Integer> hashMap = new HashMap<>();
        int deptId = 0;
        boolean newDepartment = false;
        for (List<Course> offerings : allOfferings) {
            if(offerings.isEmpty()) continue;
            Course firstCourse = offerings.getFirst();
            if(!hashMap.containsKey(firstCourse.getDepartmentKey())) {
                hashMap.put(firstCourse.getDepartmentKey(), ++deptId);
                newDepartment = true;
            } else {
                newDepartment = false;
            }
            if(newDepartment) departments.add(new Department(deptId, firstCourse.getSubject()));
            int index = hashMap.get(firstCourse.getDepartmentKey())-1;
            departments.get(index).addCourse(offerings);
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

    public List<Course> getCourseSections(String catalogNumber, String departmentName) {
       return treeMap.get(departmentName + catalogNumber);
    }

    public List<Course> getOfferingSections(String catalogNumber, String name, long semesterCode, String location) {
       List<Course> courses = treeMap.get(name + catalogNumber);
       List<Course> courseSections = null;
       for(Course course : courses) {
           if(course.getSemester().equals(String.valueOf(semesterCode)) && course.getLocation().equals(location)) {
              courseSections = course.getSectionTypeOfferings();
              break;
           }
       }
       return courseSections;
    }
}

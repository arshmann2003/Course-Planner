package application.model;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class to manipulate and efficiently store csv data
 * Aggregates same courses and sections
 * Dumps courses in a formatted way to servers terminal
 * Sorts courses based on name + catalog number
 */
public class CoursePlanner {
    private TreeMap<String, List<Course>> treeMap;
    private List<Course> courses;
    private List<Department> departments;
    private List<Watcher> watchers;
    private final AtomicLong watcherId;

    public CoursePlanner(String path) {
        CsvParser csvParser = new CsvParser(path);
        courses = csvParser.getCsvData();
        watchers = new ArrayList<>();
        watcherId = new AtomicLong(1);
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

    public List<String> getCoursesInDept(String name) {
        for(Department department : departments) {
            if(department.getDeptName().equals(name)) {
                return department.getCourses();
            }
        }
        throw new IllegalArgumentException();
    }


    public List<Department> getDepartments() {
        return departments;
    }

    private void assignDeptIds() {
        departments = new ArrayList<>();
        Collection<List<Course>> allOfferings = treeMap.values();
        HashSet<String> hashSet = new HashSet<>();
        boolean newDepartment = false;
        for (List<Course> offerings : allOfferings) {
            if(offerings.isEmpty()) continue;
            Course firstCourse = offerings.getFirst();
            if(!hashSet.contains(firstCourse.getDepartmentKey())) {
                hashSet.add(firstCourse.getDepartmentKey());
                newDepartment = true;
            } else {
                newDepartment = false;
            }
            if(newDepartment) departments.add(new Department(firstCourse.getSubject()));
            departments.getLast().addCourse(offerings);
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

    public void addNewCourseOffering(Course course) {
        String key = course.getSubject() + course.getCatalogNumber();
        if(treeMap.containsKey(key)) {
            List<Course> courseOfferings = treeMap.get(key);
            for(Course courseOffering : courseOfferings) {
                if(courseOffering.getFullKey().equals(course.getFullKey())) {
                    courseOffering.updateEnrollment(course);
                    updateWatchers(course);
                    return;
                } else if(courseOffering.getSectionKey().equals(course.getSectionKey())) {
                   courseOffering.addCourseComponent(course);
                   updateWatchers(course);
                   return;
                }
            }
            treeMap.get(key).add(course);
        } else {
            List<Course> newOffering = new ArrayList<>();
            newOffering.add(course);
            treeMap.put(key, newOffering);
        }
        assignDeptIds();
        sortOfferings();
        updateWatchers(course);
    }

    private void updateWatchers(Course course) {
        for(Watcher watcher : watchers) {
            if(watcher.key.equals(course.getCatalogKey())) {
                watcher.addEvent(course);
            }
        }
    }

    public void addWatcher(String name, String catalogNumber, long courseId, long deptId) {
        if (watchers == null) watchers = new ArrayList<>();
        Watcher watcher = new Watcher(watcherId.get(), name+catalogNumber, courseId, deptId);
        watchers.add(watcher);
        watcherId.getAndIncrement();
    }

    public List<Watcher> getWatchers() {
        return watchers;
    }

    public void markInActiveWatcher(int watcherId) {
        for(Watcher watcher : watchers) {
            if(watcher.id == watcherId) {
                watcher.markInactive();
            }
        }
    }

    public TreeMap<String, Integer> getNumberOfSeatsInDepartment(String deptName) {
        assignDeptIds();
        Department mainDepartment = null;
        for(Department department : departments) {
            if(department.getDeptName().equals(deptName))
                mainDepartment = department;
        }
        TreeMap<String, Integer> map = mainDepartment.getNumOfSeatsPerSemester();
        return map;
    }
}

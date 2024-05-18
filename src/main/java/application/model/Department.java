package application.model;

import java.util.*;

public class Department {
    private final String name;
    private HashMap<String, List<List<Course>>> hashMap;

    public Department(String name) {
        this.name = name;
        this.hashMap = new HashMap<>();
    }

    public String getDeptName() {
        return name;
    }

    public void addCourse(List<Course> courseOfferings) {
        if(courseOfferings==null) return;
        String key = courseOfferings.get(0).getCatalogNumber();
        if(hashMap.containsKey(key)) {
            hashMap.get(key).add(courseOfferings);
        } else {
            List<List<Course>> newCourseOfferings = new ArrayList<>();
            newCourseOfferings.add(courseOfferings);
            hashMap.put(key, newCourseOfferings);
        }
    }

    public List<String> getCourses() {
        List<String> courses = new ArrayList<>(hashMap.keySet());
        courses.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        return courses;
    }

    public boolean isCourseInDepartment(Course newCourse) {
        return name.equals(newCourse.getDepartmentKey());
    }

    public TreeMap<String, Integer> getNumOfSeatsPerSemester() {
        TreeMap<String, Integer> map = new TreeMap<>();
        for(List<List<Course>> courses : hashMap.values()) {
            for(List<Course> course : courses) {
                for(Course first : course) {
                    if (map.containsKey(first.semester)) {
                        if(first.componentCode.equals("LEC")) {
                            int seats = map.get(first.semester) + Integer.parseInt(first.enrollmentTotal);
                            map.put(first.semester, seats);
                        }
                        for(Course section : first.getSectionTypeOfferings()) {
                            if(section.componentCode.equals("LEC") && first!=section){
                                int seats = map.get(first.semester) + Integer.parseInt(section.enrollmentTotal);
                                map.put(first.semester, seats);
                            }
                        }
                    } else {
                        if(first.componentCode.equals("LEC")) {
                            map.put(first.semester, Integer.valueOf(first.enrollmentTotal));
                        }
                        else
                            map.put(first.semester, 0);
                    }
                }
            }
        }
        return map;
    }
}

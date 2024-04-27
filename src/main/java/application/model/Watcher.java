package application.model;

import application.AllApiDtoClasses.ApiCourseDTO;
import application.AllApiDtoClasses.ApiDepartmentDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Watcher {
    public long id;
    public long courseId;
    public long deptId;
    public Course course;
    public List<String> events;
    public String key;
    private boolean active;

    public Watcher(long id, String key, long courseId, long deptId) {
       this.id = id;
       this.key = key;
       this.courseId = courseId;
       this.deptId = deptId;
       active = true;
    }

    public boolean isCourseWatched(Course course) {
       return course.getCatalogKey().equals(key);
    }

    public void addEvent(Course course) {
        if(events==null) events = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("PDT"));
        Date currentDate = new Date();
        String formattedDate = sdf.format(currentDate);
        events.add(formattedDate + " Added section " + course.componentCode + " with enrollment ("
                + course.enrollmentTotal + " / " + course.enrollmentCap + ") to offering " + course.getTerm() + " " + course.getYear());
    }

    public boolean isActive() {
       return active;
    }

    public void markInactive() {
        active = false;
    }
}

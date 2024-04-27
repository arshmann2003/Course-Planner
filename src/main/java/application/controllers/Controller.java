package application.controllers;

import application.AllApiDtoClasses.*;
import application.Application;
import application.model.Course;
import application.model.CoursePlanner;
import application.model.Department;
import application.model.Watcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Rest Controller for Spring Boot application handling all
 * GET and POST requests made by the frontend
 */
@RestController
@RequestMapping("/api")
public class Controller {
    public static final String COURSE_DATA_2018 = "data/course_data_2018.csv";
    public CoursePlanner coursePlanner;
    public List<ApiDepartmentDTO> apiDepartmentDTOS;
    private List<ApiCourseDTO> apiCourseDTOS = new ArrayList<>();
    private List<ApiCourseOfferingDTO> apiCourseOfferingDTOS;
    private List<ApiWatcherDTO> apiWatcherDTOS;

    public Controller() {
        coursePlanner = Application.getCoursePlanner();
    }

    @ResponseStatus(code = HttpStatus.OK, reason = "")
    @GetMapping("dump-model")
    public void dumpModel() {
        coursePlanner.dumpCourses();
    }

    @ResponseStatus(code = HttpStatus.OK, reason = "")
    @GetMapping("about")
    public ApiAboutDTO getName() {
        return new ApiAboutDTO("The Ultimate Course Planner", "Arshdeep Mann");
    }

    @ResponseStatus(code = HttpStatus.OK, reason = "")
    @GetMapping("departments")
    public List<ApiDepartmentDTO> apiDepartmentDTOList() {
        List<Department> departments = coursePlanner.getDepartments();
        AtomicLong deptId = new AtomicLong(1);
        apiDepartmentDTOS = new ArrayList<>();
        for(Department department : departments)
            apiDepartmentDTOS.add(new ApiDepartmentDTO(department, deptId.getAndIncrement()));
        return apiDepartmentDTOS;
    }

    @ResponseStatus(code = HttpStatus.OK, reason = "")
    @GetMapping("departments/{deptId}/courses")
    public List<ApiCourseDTO> getCoursesInDepartment(@PathVariable int deptId) {
        apiDepartmentDTOList();
        if(deptId < 1 || deptId > apiDepartmentDTOS.size())
            throw new IllegalArgumentException();
        apiCourseDTOS = new ArrayList<>();
        AtomicLong courseId = new AtomicLong(1);
        ApiDepartmentDTO apiDepartmentDTO = apiDepartmentDTOS.get(deptId-1);
        List<String> courseOfferingsInDept = coursePlanner.getCoursesInDept(apiDepartmentDTO.name);
        for(String course : courseOfferingsInDept) {
            apiCourseDTOS.add(new ApiCourseDTO(course, courseId.getAndIncrement()));
        }
        return apiCourseDTOS;
    }

    @ResponseStatus(code = HttpStatus.OK, reason = "")
    @GetMapping("departments/{deptId}/courses/{courseId}/offerings")
    public List<ApiCourseOfferingDTO>  getCourseOfferings(@PathVariable int deptId, @PathVariable int courseId) {
        apiCourseOfferingDTOS = new ArrayList<>();
        apiDepartmentDTOList();
        getCoursesInDepartment(deptId);
        if(!(deptId > 0 && deptId <= apiDepartmentDTOS.size() && courseId > 0 && courseId <= apiCourseDTOS.size()))
            throw new IllegalArgumentException();
        ApiCourseDTO apiCourseDTO = apiCourseDTOS.get(courseId-1);
        ApiDepartmentDTO apiDepartmentDTO = apiDepartmentDTOS.get(deptId-1);
        AtomicLong courseOfferingId = new AtomicLong(1);
        List<Course> offerings = coursePlanner.getCourseSections(apiCourseDTO.catalogNumber, apiDepartmentDTO.name);
        for(Course course : offerings)
           apiCourseOfferingDTOS.add(new ApiCourseOfferingDTO(course, courseOfferingId.getAndIncrement()));
        return apiCourseOfferingDTOS;
    }

    @ResponseStatus(code = HttpStatus.OK, reason = "")
    @GetMapping("departments/{deptId}/courses/{courseId}/offerings/{courseOfferingId}")
    public List<ApiOfferingSectionDTO> getOfferingSections(@PathVariable int deptId, @PathVariable int courseId, @PathVariable int courseOfferingId) {
        apiDepartmentDTOList();
        getCoursesInDepartment(deptId);
        getCourseOfferings(deptId, courseId);
        if(!(deptId > 0 && deptId <= apiDepartmentDTOS.size() && courseId > 0 && courseId <= apiCourseDTOS.size()))
            throw new IllegalArgumentException();
        List<ApiOfferingSectionDTO> apiOfferingSectionDTOS = new ArrayList<>();
        ApiCourseDTO apiCourseDTO = apiCourseDTOS.get(courseId-1);
        ApiDepartmentDTO apiDepartmentDTO = apiDepartmentDTOS.get(deptId-1);
        ApiCourseOfferingDTO apiCourseOfferingDTO = apiCourseOfferingDTOS.get(courseOfferingId-1);
        List<Course> courseOfferingSections = coursePlanner.getOfferingSections(apiCourseDTO.catalogNumber, apiDepartmentDTO.name,
                apiCourseOfferingDTO.semesterCode, apiCourseOfferingDTO.location);
        for(Course course : courseOfferingSections) {
            apiOfferingSectionDTOS.add(new ApiOfferingSectionDTO(course));
        }
        return apiOfferingSectionDTOS;
    }

    @ResponseStatus(code = HttpStatus.CREATED, reason = "")
    @PostMapping("addoffering")
    public void addOffering(@RequestBody ApiOfferingDataDTO apiOfferingDataDTO) {
        Course course = new Course();
        course.setSemester(apiOfferingDataDTO.semester);
        course.setInstructors(apiOfferingDataDTO.instructor);
        course.setSubject(apiOfferingDataDTO.subjectName);
        course.setLocation(apiOfferingDataDTO.location);
        course.setCatalogNumber(apiOfferingDataDTO.catalogNumber);
        course.setComponentCode(apiOfferingDataDTO.component);
        course.setEnrollmentCap(String.valueOf(apiOfferingDataDTO.enrollmentCap));
        course.setEnrollmentTotal(String.valueOf(apiOfferingDataDTO.enrollmentTotal));
        coursePlanner.addNewCourseOffering(course);
    }

    @ResponseStatus(code = HttpStatus.CREATED, reason = "")
    @PostMapping("watchers")
    public void addWatcher(@RequestBody ApiWatcherCreateDTO apiWatcherCreateDTO) {
        long deptId = apiWatcherCreateDTO.deptId;
        long courseId = apiWatcherCreateDTO.courseId;
        if(deptId < 1 || courseId < 1 || courseId > apiCourseDTOS.size() || deptId > apiDepartmentDTOS.size())
            throw new IllegalArgumentException();
        apiDepartmentDTOList();
        getCoursesInDepartment((int) deptId);
        ApiDepartmentDTO apiDepartmentDTO = apiDepartmentDTOS.get((int) (deptId-1));
        ApiCourseDTO apiCourseDTO = apiCourseDTOS.get((int) (courseId-1));
        coursePlanner.addWatcher(apiDepartmentDTO.name, apiCourseDTO.catalogNumber, courseId, deptId);
    }

    @ResponseStatus(code = HttpStatus.OK, reason = "")
    @GetMapping("watchers")
    public List<ApiWatcherDTO> getWatchers() {
        apiWatcherDTOS = new ArrayList<>();
        List<Watcher> watchers = coursePlanner.getWatchers();
        AtomicLong watcherId = new AtomicLong(1);
        for(Watcher watcher : watchers) {
            if(!watcher.isActive()) continue;
            getCoursesInDepartment((int) watcher.deptId);
            ApiWatcherDTO apiWatcherDTO = new ApiWatcherDTO();
            apiWatcherDTO.id = watcherId.getAndIncrement();
            watcher.id = watcherId.decrementAndGet();
            watcherId.getAndIncrement();
            apiWatcherDTO.course = apiCourseDTOS.get((int) watcher.courseId-1);
            apiWatcherDTO.department = apiDepartmentDTOS.get((int) watcher.deptId-1);
            apiWatcherDTO.events = Objects.requireNonNullElseGet(watcher.events, ArrayList::new);
            apiWatcherDTO.courseId = watcher.courseId;
            apiWatcherDTO.deptId = watcher.deptId;
            apiWatcherDTOS.add(apiWatcherDTO);
        }
        return apiWatcherDTOS;
    }

    @ResponseStatus(code = HttpStatus.OK, reason = "")
    @GetMapping("watchers/{watcherId}")
    public List<String> getWatcherEvents(@PathVariable int watcherId) {
        if(watcherId < 1 || watcherId > apiWatcherDTOS.size())
            throw new IllegalArgumentException();
       ApiWatcherDTO apiWatcherDTO = apiWatcherDTOS.get(watcherId-1);
       return apiWatcherDTO.events;
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "")
    @DeleteMapping("watchers/{watcherId}")
    public void deleteWatcher(@PathVariable int watcherId) {
        if(!(watcherId > 0 && watcherId <= apiWatcherDTOS.size()))
            throw new IllegalArgumentException();
        coursePlanner.markInActiveWatcher(watcherId);
    }

    @ResponseStatus(code = HttpStatus.OK, reason = "")
    @GetMapping("stats/students-per-semester")
    public List<ApiGraphDataPointDTO> getGraphData(@RequestParam int deptId) {
        apiDepartmentDTOList();
        if(deptId < 1 || deptId > apiDepartmentDTOS.size())
            throw new IllegalArgumentException();
        ApiDepartmentDTO apiDepartmentDTO = apiDepartmentDTOS.get(deptId-1);
        TreeMap<String, Integer> map = coursePlanner.getNumberOfSeatsInDepartment(apiDepartmentDTO.name);
        List<ApiGraphDataPointDTO> apiGraphDataPointDTOS = new ArrayList<>();
        for(String key : map.keySet()) {
            ApiGraphDataPointDTO newApiGraphDataPointDTO = new ApiGraphDataPointDTO();
            newApiGraphDataPointDTO.totalCoursesTaken = map.get(key);
            newApiGraphDataPointDTO.semesterCode = Long.parseLong(key);
            apiGraphDataPointDTOS.add(newApiGraphDataPointDTO);
        }
        apiGraphDataPointDTOS.sort(new Comparator<ApiGraphDataPointDTO>() {
            @Override
            public int compare(ApiGraphDataPointDTO o1, ApiGraphDataPointDTO o2) {
                return Long.compare(o1.semesterCode, o2.semesterCode);
            }
        });
        return apiGraphDataPointDTOS;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Id is not found")
    @ExceptionHandler(IllegalArgumentException.class)
    public void requestNotFoundExceptionHandler() {
    }
}

package application.AllApiDtoClasses;

import application.model.Department;

public class ApiDepartmentDTO {
    public long deptId;
    public String name;

    public ApiDepartmentDTO(Department department, long deptId) {
        this.deptId = deptId;
        name = department.getDeptName();
    }
}

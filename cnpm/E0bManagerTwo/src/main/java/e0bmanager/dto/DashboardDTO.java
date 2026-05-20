package e0bmanager.dto;

public class DashboardDTO {
    private int totalEmployees;
    private int shiftsToday;
    private int employeesWorkingToday;
    private int shiftsInMonth;

    // Constructor mặc định cho GSON
    public DashboardDTO() {}

    public DashboardDTO(int totalEmployees, int shiftsToday, int employeesWorkingToday, int shiftsInMonth) {
        this.totalEmployees = totalEmployees;
        this.shiftsToday = shiftsToday;
        this.employeesWorkingToday = employeesWorkingToday;
        this.shiftsInMonth = shiftsInMonth;
    }

    // Getters
    public int getTotalEmployees() { return totalEmployees; }
    public int getShiftsToday() { return shiftsToday; }
    public int getEmployeesWorkingToday() { return employeesWorkingToday; }
    public int getShiftsInMonth() { return shiftsInMonth; }
}
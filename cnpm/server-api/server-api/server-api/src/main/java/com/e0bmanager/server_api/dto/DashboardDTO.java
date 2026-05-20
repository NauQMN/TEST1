package com.e0bmanager.server_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {
    private int totalEmployees;
    private int shiftsToday;
    private int employeesWorkingToday;
    private int shiftsInMonth;
}

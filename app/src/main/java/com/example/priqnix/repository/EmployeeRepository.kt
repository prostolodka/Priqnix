package com.example.priqnix.repository

import com.example.priqnix.data.Employee
import com.example.priqnix.data.EmployeeDao
import kotlinx.coroutines.flow.Flow

class EmployeeRepository(private val employeeDao: EmployeeDao) {
    fun getAllEmployees(): Flow<List<Employee>> = employeeDao.getAllEmployees()
    suspend fun addEmployee(employee: Employee) = employeeDao.insertEmployee(employee)
    suspend fun updateEmployee(employee: Employee) = employeeDao.updateEmployee(employee)
    suspend fun deleteEmployee(employee: Employee) = employeeDao.deleteEmployee(employee)
    suspend fun getEmployeeById(id: Int): Employee? = employeeDao.getEmployeeById(id)
}
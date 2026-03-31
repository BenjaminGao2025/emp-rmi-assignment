package com.example.rmi;

import com.example.app.EmpApplicationService;
import com.example.model.EMP;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

/**
 * Remote adapter around the shared application service.
 */
public class EmpServiceImpl implements EmpService {
    private final EmpApplicationService applicationService;

    public EmpServiceImpl(String dbPath) throws SQLException {
        this.applicationService = new EmpApplicationService(dbPath);
    }

    @Override
    public List<EMP> listEmployees() throws RemoteException {
        try {
            return applicationService.listEmployees();
        } catch (SQLException ex) {
            throw new RemoteException("Failed to list employees", ex);
        }
    }

    @Override
    public EMP findEmployeeById(String eno) throws RemoteException {
        try {
            return applicationService.findEmployeeById(eno);
        } catch (SQLException ex) {
            throw new RemoteException("Failed to find employee " + eno, ex);
        }
    }

    @Override
    public boolean addEmployee(String eno, String ename, String title) throws RemoteException {
        try {
            return applicationService.addEmployee(eno, ename, title);
        } catch (SQLException ex) {
            throw new RemoteException("Failed to add employee " + eno, ex);
        }
    }

    @Override
    public boolean updateEmployee(String eno, String ename, String title) throws RemoteException {
        try {
            return applicationService.updateEmployee(eno, ename, title);
        } catch (SQLException ex) {
            throw new RemoteException("Failed to update employee " + eno, ex);
        }
    }

    @Override
    public boolean deleteEmployee(String eno) throws RemoteException {
        try {
            return applicationService.deleteEmployee(eno);
        } catch (SQLException ex) {
            throw new RemoteException("Failed to delete employee " + eno, ex);
        }
    }
}

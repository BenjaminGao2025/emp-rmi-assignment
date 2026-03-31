package com.example.rmi;

import com.example.model.EMP;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote CRUD API for the EMP table.
 */
public interface EmpService extends Remote {
    List<EMP> listEmployees() throws RemoteException;

    EMP findEmployeeById(String eno) throws RemoteException;

    boolean addEmployee(String eno, String ename, String title) throws RemoteException;

    boolean updateEmployee(String eno, String ename, String title) throws RemoteException;

    boolean deleteEmployee(String eno) throws RemoteException;
}

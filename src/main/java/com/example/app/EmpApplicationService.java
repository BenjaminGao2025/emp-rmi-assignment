package com.example.app;

import com.example.dao.EMPDAO;
import com.example.db.DBConnection;
import com.example.model.EMP;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Owns connection lifecycle, explicit transactions, and shared business logic.
 */
public class EmpApplicationService {
    private final String dbPath;
    private final EMPDAO empDao;

    public EmpApplicationService(String dbPath) throws SQLException {
        this(dbPath, new EMPDAO());
    }

    EmpApplicationService(String dbPath, EMPDAO empDao) throws SQLException {
        this.dbPath = dbPath;
        this.empDao = empDao;
        DBConnection.initializeDatabase(dbPath);
    }

    public List<EMP> listEmployees() throws SQLException {
        try (Connection conn = DBConnection.getConnection(dbPath)) {
            return empDao.getAllEmployees(conn);
        }
    }

    public EMP findEmployeeById(String eno) throws SQLException {
        try (Connection conn = DBConnection.getConnection(dbPath)) {
            return empDao.findEmployeeById(conn, eno);
        }
    }

    public boolean addEmployee(String eno, String ename, String title) throws SQLException {
        return runInTransaction(conn -> empDao.addNewEmployee(conn, eno, ename, title) == 1);
    }

    public boolean updateEmployee(String eno, String ename, String title) throws SQLException {
        return runInTransaction(conn -> empDao.updateEmployee(conn, eno, ename, title) == 1);
    }

    public boolean deleteEmployee(String eno) throws SQLException {
        return runInTransaction(conn -> empDao.deleteEmployee(conn, eno) == 1);
    }

    private boolean runInTransaction(SqlTransaction transaction) throws SQLException {
        try (Connection conn = DBConnection.getConnection(dbPath)) {
            conn.setAutoCommit(false);
            try {
                boolean result = transaction.execute(conn);
                conn.commit();
                return result;
            } catch (SQLException | RuntimeException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @FunctionalInterface
    private interface SqlTransaction {
        boolean execute(Connection conn) throws SQLException;
    }
}

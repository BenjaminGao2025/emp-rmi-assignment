package com.example.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Serializable employee record shared by local and RMI clients.
 */
public class EMP implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String eno;
    private final String ename;
    private final String title;

    public EMP(String no, String name, String employeeTitle) {
        this.eno = no;
        this.ename = name;
        this.title = employeeTitle;
    }

    public String getENO() {
        return eno;
    }

    public String getName() {
        return ename;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return eno + " " + ename + " " + title;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model;

/**
 *
 * @author 26th June
 */
public class NguoiChoi {
    private String name,mark,status;
    private int ID;

    public NguoiChoi(int ID, String name, String mark, String status) {
        this.name = name;
        this.mark = mark;
        this.status = status;
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
    
    public Object [] toObjects () {
        return new Object[] {ID,name,mark,status};
    }
}

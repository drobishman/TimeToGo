package it.curdrome.timetogo.model;

/**
 * Created by adrian on 16/01/2017.
 */

public class Category {

    private int id;
    private String name;

    public Category(int id, String name){
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return " id: "+id+" name: "+ name+"";
    }
}

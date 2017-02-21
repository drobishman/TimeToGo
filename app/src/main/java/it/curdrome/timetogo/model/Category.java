package it.curdrome.timetogo.model;

/**
 * Created by adrian on 16/01/2017.
 *
 * Model Class for the category
 *
 * @author adrian
 * @version 1
 */

public class Category {

    private int id;
    private String name;

    /**
     * Default constructor
     * @param id the id of the category
     * @param name the namoe of the category
     */
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
        return name+" ";
    }
}

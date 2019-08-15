package com.phoenix.otlobbety.Model;

import java.util.List;

public class ExpandableList {
    private String listId, nameOfParent;
    private List<Food> itemsOfChild;

    public ExpandableList() {
    }

    public ExpandableList(List<Food> itemsOfChild) {
        this.itemsOfChild = itemsOfChild;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getNameOfParent() {
        return nameOfParent;
    }

    public void setNameOfParent(String nameOfParent) {
        this.nameOfParent = nameOfParent;
    }

    public List<Food> getItemsOfChild() {
        return itemsOfChild;
    }

    public void setItemsOfChild(List<Food> itemsOfChild) {
        this.itemsOfChild = itemsOfChild;
    }
}

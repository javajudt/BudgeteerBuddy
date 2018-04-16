package com.ajapplications.budgeteerbuddy.model;
/*
public enum Category{
    Income, Savings, RentMortgage, Transportation, FoodHousehold, Medical, Entertainment, Other
}*/

// TODO change to enum

public class Category {
    private String label;

    public static Category[] generateCategories(){
        Category[] categories = {
                new Category("Income"),
                new Category("Savings"),
                new Category("Rent/Mortgage"),
                new Category("Transportation"),
                new Category("Food/Household"),
                new Category("Medical"),
                new Category("Entertainment"),
                new Category("Other")
        };
        return categories;
    }

    public Category(String label){
        this.label = label;
    }

    public String getLabel(){
        return label;
    }
}

package com.geotop.geotopproject.model.places;

public class CategoryPopularity {
    private Integer categoryId;
    private Integer popularity;

    public CategoryPopularity() {
    }

    public CategoryPopularity(Integer categoryId, Integer popularity) {
        this.categoryId = categoryId;
        this.popularity = popularity;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }
}

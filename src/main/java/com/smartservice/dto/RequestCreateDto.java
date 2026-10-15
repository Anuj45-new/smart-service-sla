package com.smartservice.dto;

import com.smartservice.enums.Priority;

import java.io.Serializable;

public class RequestCreateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String description;
    private Long categoryId;
    private Priority priority;

    public RequestCreateDto() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}

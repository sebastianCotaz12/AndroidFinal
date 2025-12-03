package com.example.myapplication.utils;

public class AnnotatedItem {
    private String name;
    private String displayName;
    private String region;
    private float centerX;
    private float centerY;
    private float width;
    private float height;
    private int priority;

    // Constructor con 8 parámetros
    public AnnotatedItem(String name, String displayName, String region,
                         float centerX, float centerY, float width, float height, int priority) {
        this.name = name;
        this.displayName = displayName;
        this.region = region;
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
        this.priority = priority;
    }

    // Getters
    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public String getRegion() { return region; }
    public float getCenterX() { return centerX; }
    public float getCenterY() { return centerY; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public int getPriority() { return priority; }
}
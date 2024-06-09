package com.MylesAndMore.Tumble.plugin;

// java does not have result types (i miss rust </3) so i did this
public class Result<T> {
    public boolean success;
    public T value;
    public String error;
}

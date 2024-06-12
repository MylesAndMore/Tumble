package com.MylesAndMore.Tumble.plugin;

// java does not have result types (i miss rust </3) so i did this
public class Result<T> {
    public final boolean success;
    public final T value;
    public final String error;

    public Result(String error) {
        success = false;
        this.error = error;
        this.value = null;
    }

    public Result(T value) {
        success = true;
        this.value = value;
        this.error = null;
    }
}

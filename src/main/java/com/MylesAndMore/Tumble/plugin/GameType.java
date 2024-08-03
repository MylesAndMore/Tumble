package com.MylesAndMore.Tumble.plugin;

public enum GameType {
    SHOVELS,
    SNOWBALLS,
    MIXED;

    public String toString() {
        return this.name().toLowerCase();
    }
}

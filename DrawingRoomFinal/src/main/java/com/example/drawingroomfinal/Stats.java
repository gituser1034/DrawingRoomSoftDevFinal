package com.example.drawingroomfinal;

import java.util.List;

public class Stats {

    private String Type;
    private String Count;

    public Stats(String type, String count) {
        Type = type;
        Count = count;
    }

    public String getType() {
        return Type;
    }

    public String getCount() {
        return Count;
    }
}

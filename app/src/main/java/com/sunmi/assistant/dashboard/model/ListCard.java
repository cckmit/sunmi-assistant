package com.sunmi.assistant.dashboard.model;

import java.util.List;

/**
 * @author yinhui
 * @since 2019-06-17
 */
public class ListCard {

    public String title;
    public List<Item> list;

    public ListCard(String title, List<Item> list) {
        this.title = title;
        this.list = list;
    }

    public static class Item {
        public int rank;
        public String name;
        public int count;

        public Item(int rank, String name, int count) {
            this.rank = rank;
            this.name = name;
            this.count = count;
        }
    }
}

package com.sunmi.assistant.dashboard.model;

import com.sunmi.assistant.dashboard.DataRefreshHelper;

import java.util.List;

/**
 * @author yinhui
 * @since 2019-06-17
 */
public class ListCard extends BaseRefreshCard<ListCard> {

    public String title;
    public List<Item> list;

    public ListCard(String title, DataRefreshHelper<ListCard> helper) {
        super(helper);
        this.title = title;
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

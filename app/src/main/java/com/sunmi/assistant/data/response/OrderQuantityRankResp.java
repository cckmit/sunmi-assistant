package com.sunmi.assistant.data.response;

import java.util.List;

public class OrderQuantityRankResp {

    private List<QuantityRankItem> quantity_rank;

    public List<QuantityRankItem> getQuantity_rank() {
        return quantity_rank;
    }

    public static class QuantityRankItem {

        private String name;
        private int quantity;

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}

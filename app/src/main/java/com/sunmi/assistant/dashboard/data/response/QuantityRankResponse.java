package com.sunmi.assistant.dashboard.data.response;

import java.util.List;

public class QuantityRankResponse {

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

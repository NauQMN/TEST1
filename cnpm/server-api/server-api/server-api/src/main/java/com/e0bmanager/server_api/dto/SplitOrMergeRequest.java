package com.e0bmanager.server_api.dto;

import java.util.List;

public class SplitOrMergeRequest {
    private int sourceHoaDonId; // Hóa đơn nguồn (đang mở)
    private int targetTableId;  // ID bàn đích (để chuyển đến)
    private List<ItemDetail> itemsToProcess; // Danh sách các món muốn tách
    private boolean isMerge;    // true: Gộp vào hóa đơn đã có khách, false: Tách sang bàn trống

    // Cần có Constructor không tham số để Jackson/Gson chuyển đổi JSON
    public SplitOrMergeRequest() {}

    public SplitOrMergeRequest(int sourceHoaDonId, int targetTableId, List<ItemDetail> items, boolean isMerge) {
        this.sourceHoaDonId = sourceHoaDonId;
        this.targetTableId = targetTableId;
        this.itemsToProcess = items;
        this.isMerge = isMerge;
    }

    // --- Inner Class ItemDetail ---
    public static class ItemDetail {
        private int sanPhamId; // ID món ăn
        private int quantity;  // Số lượng muốn tách/gộp

        public ItemDetail() {}
        public ItemDetail(int sanPhamId, int quantity) {
            this.sanPhamId = sanPhamId;
            this.quantity = quantity;
        }

        // Getter & Setter
        public int getSanPhamId() { return sanPhamId; }
        public void setSanPhamId(int sanPhamId) { this.sanPhamId = sanPhamId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    // Getter & Setter cho các biến chính
    public int getSourceHoaDonId() { return sourceHoaDonId; }
    public int getTargetTableId() { return targetTableId; }
    public List<ItemDetail> getItemsToProcess() { return itemsToProcess; }
    public boolean isMerge() { return isMerge; }
}
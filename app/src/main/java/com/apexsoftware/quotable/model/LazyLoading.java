package com.apexsoftware.quotable.model;

import com.apexsoftware.quotable.enums.ItemType;

public interface LazyLoading {
    ItemType getItemType();
    void setItemType(ItemType itemType);
}

package com.whitelaning.whitefragment.factory.other.recyclerView;

public interface MultiItemTypeSupport<T> {
    int getLayoutId(int itemType);

    int getItemViewType(int position, T t);
}
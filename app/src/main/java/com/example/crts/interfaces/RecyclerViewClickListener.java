package com.example.crts.interfaces;

public interface RecyclerViewClickListener {
    void onItemClick(int position);

    void onLongItemClick(int position);

    void editButtonClick(int position);
    void deleteButtonClick(int position);
    void doneButtonClick(int position);
}

package com.n157239;

public interface PanelInterface {
    void onSelection(int gridSelected);

    void onSave();

    void onNew();

    void onBranch(int selection);

    void onDelete(int selection);
}

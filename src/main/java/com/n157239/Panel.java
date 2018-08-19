package com.n157239;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

class Panel {
    private PApplet parent;
    private PVector location = null;
    private PVector dimension = null;

    private ArrayList<Grid> grids = new ArrayList<>();

    private int functionalButtons = 4;//save, new, branch, delete
    private int selected = -1;

    /**
     * Constructs a panel given a Processing sketch. After construction, please call {@link Panel#setFrame(int, int, int, int)} to specify the location of the panel.
     *
     * @param parent the Processing sketch
     */
    Panel(PApplet parent) {
        this.parent = parent;
    }

    /**
     * Sets the location of the panel.
     *
     * @param locX   x location
     * @param locY   y location
     * @param width  the width of the panel
     * @param height the height of the panel
     * @throws IllegalArgumentException whenever the com.n157239.Grid lies outside of the Processing sketch
     */
    @SuppressWarnings("SameParameterValue")
    void setFrame(int locX, int locY, int width, int height) {
        if (locX < 0) {
            throw new IllegalArgumentException("The panel will be outside of the sketch");
        }
        if (locX + width > parent.width) {
            throw new IllegalArgumentException("The panel will be outside of the sketch");
        }
        if (locY < 0) {
            throw new IllegalArgumentException("The panel will be outside of the sketch");
        }
        if (locY + height > parent.height) {
            throw new IllegalArgumentException("The panel will be outside of the sketch");
        }
        location = new PVector(locX, locY);
        dimension = new PVector(width, height);
    }

    /**
     * Draws the panel out into the sketch. This will draw all the com.n157239.Sudoku board options and then 4 extra buttons: Save, New, Branch and Delete.
     */
    void draw() {
        parent.textSize(Env.panelFontSize);
        //background
        parent.fill(Env.backgroundColor);
        parent.stroke(Env.backgroundColor);
        parent.rect(location.x, location.y, dimension.x, dimension.y);
        //card frames
        parent.stroke(Env.gridColor);
        parent.strokeWeight(Env.heavyLine);
        for (int i = 0; i < grids.size() + functionalButtons; i++) {
            if (selected == i) {
                parent.fill(Env.focusedBackgroundColor);
            } else {
                parent.fill(Env.backgroundColor);
            }
            parent.rect(location.x, location.y + dimension.y * i / (grids.size() + functionalButtons), dimension.x, dimension.y / (grids.size() + functionalButtons));
        }
        //descriptions
        parent.fill(Env.fontColor);
        parent.noStroke();
        for (int i = 0; i < grids.size(); i++) {
            if (grids.get(i).saved) {
                setCardText(i, "Version " + String.valueOf(i + 1));
            } else {
                setCardText(i, "Version " + String.valueOf(i + 1) + "*");
            }
        }
        setCardText(grids.size(), "Save");
        setCardText(grids.size() + 1, "New");
        setCardText(grids.size() + 2, "Branch");
        setCardText(grids.size() + 3, "Delete");
    }

    /**
     * Sets the text of the card at a specific index and with a specified description.
     *
     * @param cardIndex   the index of the card to set to
     * @param cardContent the description of the card to set to
     */
    private void setCardText(int cardIndex, String cardContent) {
        if (cardIndex < 0 || cardIndex >= grids.size() + functionalButtons) {
            throw new IllegalArgumentException("The card index is out of bounds");
        }
        parent.textAlign(PApplet.CENTER, PApplet.CENTER);
        parent.text(cardContent, location.x + dimension.x / 2, location.y + dimension.y * cardIndex / (grids.size() + functionalButtons) + dimension.y / (2 * (grids.size() + functionalButtons)));
    }

    /**
     * Adds a com.n157239.Grid to this com.n157239.Panel and selects that com.n157239.Grid.
     *
     * @param grid the com.n157239.Grid to add to
     */
    @SuppressWarnings("WeakerAccess")
    public void addGrid(Grid grid) {
        grids.add(grid);
        selected = grids.size() - 1;
        ((DrawInterface) parent).drawStuff();
    }

    /**
     * Deletes the selected com.n157239.Grid and selects the first com.n157239.Grid if there are any left.
     *
     * @param selection the com.n157239.Grid index to delete
     */
    @SuppressWarnings("WeakerAccess")
    public void delete(int selection) {
        grids.remove(selection);
        if (grids.size() > 0) {
            selected = 0;
        } else {
            selected = -1;
        }
        ((DrawInterface) parent).drawStuff();
    }

    /**
     * Simulates what happens when you click the sketch at any location. If the location is outside of the com.n157239.Panel's frame then the click will be ignored.
     *
     * @param x the x location of the mouse
     * @param y the y location of the mouse
     */
    @SuppressWarnings({"ConstantConditions", "WeakerAccess"})
    public void onClick(int x, int y) {
        if (x <= location.x || y >= location.x + dimension.x || y < location.y || y > location.y + dimension.y) {
            return;
        }
        int focused = PApplet.floor((grids.size() + functionalButtons) * (y - location.y) / dimension.y);
        if (focused < grids.size()) {
            selected = focused;
            if (selected >= 0 && selected < grids.size()) {
                ((PanelInterface) parent).onSelection(selected);
            }
        } else if (focused == grids.size()) {
            if (selected >= 0 && selected < grids.size()) {
                ((PanelInterface) parent).onSelection(selected);
                ((PanelInterface) parent).onSave();
            }
        } else if (focused == grids.size() + 1) {
            ((PanelInterface) parent).onNew();
            ((DrawInterface) parent).drawStuff();
        } else if (focused == grids.size() + 2) {
            if (selected >= 0 && selected < grids.size()) {
                ((PanelInterface) parent).onBranch(selected);
            }
            ((DrawInterface) parent).drawStuff();
        } else if (focused == grids.size() + 3) {
            if (selected >= 0 && selected < grids.size()) {
                ((PanelInterface) parent).onDelete(selected);
            }
            ((DrawInterface) parent).drawStuff();
        } else {
            throw new AssertionError();
        }
    }
}

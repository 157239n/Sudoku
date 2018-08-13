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

    Panel(PApplet parent) {
        this.parent = parent;
    }

    @SuppressWarnings("SameParameterValue")
    void setFrame(int locX, int locY, int width, int height) {
        location = new PVector(locX, locY);
        dimension = new PVector(width, height);
    }

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

    private void setCardText(int cardIndex, String cardContent) {
        parent.textAlign(PApplet.CENTER, PApplet.CENTER);
        parent.text(cardContent, location.x + dimension.x / 2, location.y + dimension.y * cardIndex / (grids.size() + functionalButtons) + dimension.y / (2 * (grids.size() + functionalButtons)));
    }

    void addGrid(Grid grid) {
        grids.add(grid);
        selected = grids.size() - 1;
        ((DrawInterface) parent).drawStuff();
    }

    void delete(int selection) {
        grids.remove(selection);
        if (grids.size() > 0) {
            selected = 0;
        } else {
            selected = -1;
        }
        ((DrawInterface) parent).drawStuff();
    }

    @SuppressWarnings("ConstantConditions")
    void onClick(int x, int y) {
        if (x <= location.x || y >= location.x + dimension.x || y < location.y || y > location.y + dimension.y) {
            return;
        }
        int focused = PApplet.floor((grids.size() + functionalButtons) * (y - location.y) / dimension.y);
        if (focused < grids.size()) {
            selected = focused;
            if(selected>=0 && selected<grids.size()){
                ((PanelInterface) parent).onSelection(selected);
            }
        } else if (focused == grids.size()) {
            if(selected>=0 && selected<grids.size()){
                ((PanelInterface) parent).onSave(selected);
            }
        } else if (focused == grids.size() + 1) {
            ((PanelInterface) parent).onNew();
            ((DrawInterface) parent).drawStuff();
        } else if (focused == grids.size() + 2) {
            if(selected>=0 && selected<grids.size()){
                ((PanelInterface) parent).onBranch(selected);
            }
            ((DrawInterface) parent).drawStuff();
        } else if (focused == grids.size() + 3) {
            if(selected>=0 && selected<grids.size()){
                ((PanelInterface) parent).onDelete(selected);
            }
            ((DrawInterface) parent).drawStuff();
        } else {
            throw new AssertionError();
        }
    }
}

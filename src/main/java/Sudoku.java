import processing.core.PApplet;

import java.io.File;
import java.util.ArrayList;

public class Sudoku extends PApplet implements PanelInterface, DrawInterface {
    private ArrayList<Grid> grids = new ArrayList<>();
    private Grid mainGrid = null;
    private Panel panel;
    private int gridSize = 400;
    @SuppressWarnings("FieldCanBeLocal")
    private int panelSize = 150;
    int rippleSignal=0;
    private int millis=0;

    public static void main(String[] args) {
        PApplet.main("Sudoku");
    }

    public void settings() {
        size(gridSize + panelSize, 400);
    }

    public void setup() {
        background(150);
        panel = new Panel(this);
        panel.setFrame(gridSize, 0, panelSize, gridSize);
        panel.draw();
    }

    public void draw() {
        if(rippleSignal>0 && millis()-millis>25){
            rippleSignal--;
            drawStuff();
            millis+=25;
        }
    }

    public void drawStuff() {
        panel.draw();
        if (mainGrid != null) {
            mainGrid.draw();
        }
    }

    public void mouseClicked() {
        if (mainGrid != null) {
            mainGrid.onClick(mouseX, mouseY);
        }
        panel.onClick(mouseX, mouseY);
        drawStuff();
    }

    public void keyTyped() {
        if (key >= '1' && key <= '9') {
            if (mainGrid != null) {
                mainGrid.onKeyTyped(((int) key) - ((int) '0'));
            }
        }
        if (key == DELETE) {
            mainGrid.onKeyTyped(0);
        }
        drawStuff();
    }

    public void keyPressed() {
        if (key == CODED) {
            if (mainGrid != null) {
                if (keyCode == UP) {
                    mainGrid.up();
                } else if (keyCode == DOWN) {
                    mainGrid.down();
                } else if (keyCode == LEFT) {
                    mainGrid.left();
                } else if (keyCode == RIGHT) {
                    mainGrid.right();
                }
            }
            drawStuff();
        }
    }

    @Override
    public void onSelection(int gridSelected) {
        mainGrid = grids.get(gridSelected);
        drawStuff();
    }

    @Override
    public void onSave(int gridSelection) {
        if(mainGrid!=null){
            if (!mainGrid.exportGame()) {
                selectInput("Save as", "onSaveFileSelected");
            }
        }
        drawStuff();
    }

    @Override
    public void onNew() {
        selectInput("Load sudoku board", "onNewFileSelected");
        drawStuff();
    }

    @SuppressWarnings("unused")
    public void onNewFileSelected(File selection) {
        if (selection == null) {//create a blank grid
            if (mainGrid != null) {
                mainGrid = mainGrid.copyFrame();
            } else {
                mainGrid = new Grid(this).setFrame(0, 0, gridSize, gridSize);
            }
            grids.add(mainGrid);
            panel.addGrid(mainGrid);
        } else {//load the data
            if (mainGrid != null) {
                mainGrid = mainGrid.copyFrame();
            } else {
                mainGrid = new Grid(this).setFrame(0, 0, gridSize, gridSize);
            }
            mainGrid.specifyFilePath(selection.getAbsolutePath());
            mainGrid.importGame();
            grids.add(mainGrid);
            panel.addGrid(mainGrid);
        }
        drawStuff();
        rippleSignal=10;
    }

    @SuppressWarnings("unused")
    public void onSaveFileSelected(File selection) {
        if (selection != null) {
            mainGrid.specifyFilePath(selection.getAbsolutePath());
            mainGrid.exportGame();
        }
        drawStuff();
    }

    @Override
    public void onBranch(int selection) {
        mainGrid = grids.get(selection).branch();
        grids.add(mainGrid);
        panel.addGrid(mainGrid);
        drawStuff();
    }

    @Override
    public void onDelete(int selection) {
        grids.remove(selection);
        panel.delete(selection);
        if (grids.size() > 0) {
            mainGrid = grids.get(0);
        } else {
            mainGrid = null;
            background(150);
        }
        drawStuff();
    }
}
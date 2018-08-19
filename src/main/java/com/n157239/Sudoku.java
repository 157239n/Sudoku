package com.n157239;

import processing.core.PApplet;

import java.io.File;
import java.util.ArrayList;

/**
 * Main entry point for the application. This application uses the processing core libraries.
 *
 * The core.jar file from Processing 3.3.7 is stored in the lib/ folder.
 * */
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
        PApplet.main("com.n157239.Sudoku");
        System.out.println("com.n157239.Sudoku starting...");
    }

    public void settings() {
        size(gridSize + panelSize, 400);
    }

    public void setup() {
        //setting things up by drawing the background and initializes a com.n157239.Panel that will be used to control every com.n157239.Sudoku board versions
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

    /**
     * Handle keys from 1 through 9 and the delete key.
     */
    public void keyTyped() {
        if (key >= '1' && key <= '9') {
            if (mainGrid != null) {
                mainGrid.onKeyTyped(((int) key) - ((int) '0'));
            }
        }
        if (key == DELETE) {
            mainGrid.onKeyTyped(0);
        }
        if (key == ' ') {
            saveFrame("frame.png");
        }
        drawStuff();
    }

    /**
     * Handles arrow keys.
     */
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

    /**
     * Called by a com.n157239.Panel to select a particular com.n157239.Grid to play.
     *
     * @param gridSelected the index of the grid to be selected
     */
    @Override
    public void onSelection(int gridSelected) {
        mainGrid = grids.get(gridSelected);
        drawStuff();
    }

    /**
     * Called by a com.n157239.Panel to save the current com.n157239.Grid.
     *
     * If the selected com.n157239.Grid is not associated with a file then prompts the user a file so that it can save the com.n157239.Grid to. The prompt result will be processed by {@link Sudoku#onSaveFileSelected(File)}
     */
    @Override
    public void onSave() {
        if(mainGrid!=null){
            if (!mainGrid.exportGrid()) {//if export grid is successful, meaning the com.n157239.Grid is already associated with a file then exits, otherwise prompts the location of the file to save
                selectInput("Save as", "onSaveFileSelected");
            }
        }
        drawStuff();
    }

    /**
     * Prompts the user to open a com.n157239.Grid from a file. The prompt result will be processed by {@link Sudoku#onNewFileSelected(File)}
     */
    @Override
    public void onNew() {
        selectInput("Load sudoku board", "onNewFileSelected");
        drawStuff();
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
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
            mainGrid.importGrid();
            grids.add(mainGrid);
            panel.addGrid(mainGrid);
        }
        drawStuff();
        rippleSignal=10;
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public void onSaveFileSelected(File selection) {
        if (selection != null) {
            mainGrid.specifyFilePath(selection.getAbsolutePath());
            mainGrid.exportGrid();
        }
        drawStuff();
    }

    /**
     * Called by a com.n157239.Panel to branch the current com.n157239.Grid into another com.n157239.Grid.
     *
     * @param selection the index of the com.n157239.Grid to branch to a new com.n157239.Grid
     */
    @Override
    public void onBranch(int selection) {
        mainGrid = grids.get(selection).branch();
        grids.add(mainGrid);
        panel.addGrid(mainGrid);
        drawStuff();
    }

    /**
     * Called by a com.n157239.Panel to delete a specified com.n157239.Grid.
     *
     * @param selection the com.n157239.Grid to delete
     */
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
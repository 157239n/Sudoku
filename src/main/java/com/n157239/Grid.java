package com.n157239;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Represents a com.n157239.Sudoku com.n157239.Grid.
 * <p>
 * Every value inside a com.n157239.Grid can either be versioned or not versioned. A versioned value is displayed in blue and the normal value is displayed in black.
 * <p>
 * This is because we want to be able to 'branch' a com.n157239.Grid into 2 independent Grids so the normal values are the values when we just branched and the versioned ones are the new values we add after branching.
 */
class Grid {
    private class NoFileSpecified extends RuntimeException {
        private NoFileSpecified() {
            super();
        }
    }

    private class Notification {
        private int columnIndex = -1, rowIndex = -1, squareIndex = -1;
        private int columnTime = 0, rowTime = 0, squareTime = 0, defaultTime = 60;//40fps

        private Notification() {
        }

        private void checkout(int x, int y) {
            int[] data = new int[10];
            //check column
            clearData(data);
            for (int i = 0; i < 9; i++) {
                data[numbers[x][i]]++;
            }
            if (ensureData(data)) {
                columnIndex = x;
                columnTime = defaultTime;
                ((Sudoku) parent).rippleSignal = defaultTime * 2;
            }
            //check row
            clearData(data);
            for (int i = 0; i < 9; i++) {
                data[numbers[i][y]]++;
            }
            if (ensureData(data)) {
                rowIndex = y;
                rowTime = defaultTime;
                ((Sudoku) parent).rippleSignal = defaultTime;
            }
            //check square
            clearData(data);
            int locX = (x / 3);
            int locY = (y / 3);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    data[numbers[locX * 3 + i][locY * 3 + j]]++;
                }
            }
            if (ensureData(data)) {
                squareIndex = locY * 3 + locX;
                squareTime = defaultTime;
                ((Sudoku) parent).rippleSignal = defaultTime;
            }
        }

        private void decrement() {
            columnTime = columnTime > 0 ? columnTime - 1 : columnTime;
            rowTime = rowTime > 0 ? rowTime - 1 : rowTime;
            squareTime = squareTime > 0 ? squareTime - 1 : squareTime;
        }

        private void clearData(int[] array) {
            if (array.length != 10) {
                throw new AssertionError();
            }
            for (int i = 0; i < array.length; i++) {
                array[i] = 0;
            }
        }

        private boolean ensureData(int[] array) {
            if (array.length != 10) {
                throw new AssertionError();
            }
            if (array[0] != 0) {
                return false;
            }
            for (int i = 1; i < array.length; i++) {
                if (array[i] != 1) {
                    return false;
                }
            }
            return true;
        }
    }

    private int[][] numbers;//(column, row) or (x, y).
    private boolean[][] versioned;//a boolean array to indicate whether a value at a location is the versioned value or not.
    private boolean versioning;

    private PApplet parent;//the Processing sketch
    private Notification noti = new Notification();
    private PVector location = null;//location of the com.n157239.Grid
    private PVector dimension = null;//dimension of the com.n157239.Grid

    private int focusedX = -1, focusedY = -1;//what the com.n157239.Grid is currently focusing on

    private String filePath = null;
    boolean saved = false;

    private void init(PApplet parent){
        numbers = new int[9][9];
        versioned = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                numbers[i][j] = 0;
                versioned[i][j] = false;
            }
        }
        versioning = false;
        this.parent = parent;
    }

    /**
     * Constructs a grid given a Processing sketch
     *
     * @param parent the Processing sketch
     */
    @SuppressWarnings("WeakerAccess")
    public Grid(PApplet parent) {
        init(parent);
    }

    /**
     * Sets the location and size of the com.n157239.Grid.
     *
     * @param locX   the x location
     * @param locY   the y location
     * @param width  the width of the com.n157239.Grid
     * @param height the height of the com.n157239.Grid
     * @return itself
     * @throws IllegalArgumentException whenever the com.n157239.Grid lies outside of the Processing sketch
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public Grid setFrame(int locX, int locY, int width, int height) {
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
        return this;
    }

    /**
     * Sets the value at a particular location on the grid.
     *
     * @param x     the x location, ranging from 0 through 8
     * @param y     the y location, ranging from 0 through 8
     * @param value the value you want to set to, ranging from 0 through 9. 0 means that the value is empty
     */
    private void set(int x, int y, int value) {
        if (x < 0 || x > 8) {
            throw new IllegalArgumentException("X value outside of bounds");
        }
        if (y < 0 || y > 8) {
            throw new IllegalArgumentException("Y value outside of bounds");
        }
        if (value < 0 || value > 9) {
            throw new IllegalArgumentException("Value outside of bounds");
        }
        set(x, y, value, versioning);
    }

    /**
     * Sets the value at a particular location on the grid.
     *
     * @param x          the x location, ranging from 0 through 8
     * @param y          the y location, ranging from 0 through 8
     * @param value      the value you want to set to, ranging from 0 through 9. 0 means that the value is empty
     * @param versioning whether this value is the versioning version
     */
    private void set(int x, int y, int value, boolean versioning) {
        saved = false;
        if (value < 0 || value > 9) {
            throw new IndexOutOfBoundsException();
        }
        numbers[x][y] = value;
        versioned[x][y] = versioning;
        noti.checkout(x, y);
    }

    /**
     * Draws the com.n157239.Grid onto the Processing sketch.
     */
    @SuppressWarnings("WeakerAccess")
    public void draw() {
        if (location == null) {
            throw new NullPointerException("Must configure this grid's location using setFrame(int, int, int, int)");
        }
        if (dimension == null) {
            throw new NullPointerException("Must configure this grid's size using setFrame(int, int, int, int)");
        }
        parent.textAlign(PApplet.CENTER, PApplet.CENTER);
        //background
        parent.fill(Env.backgroundColor);
        parent.stroke(Env.backgroundColor);
        parent.rect(location.x, location.y, dimension.x, dimension.y);
        //highlighted areas
        int highlightedNumber = 0;
        parent.fill(Env.focusedBackgroundColor);
        parent.noStroke();
        if (isFocused()) {
            for (int i = 0; i < 9; i++) {
                parent.rect(location.x + dimension.x * (i) / 9, location.y + dimension.y * (focusedY) / 9, dimension.x / 9, dimension.y / 9);
                parent.rect(location.x + dimension.x * (focusedX) / 9, location.y + dimension.y * (i) / 9, dimension.x / 9, dimension.y / 9);
            }
            highlightedNumber = numbers[focusedX][focusedY];
        }
        //highlighted areas when finished
        if (noti.columnTime > 0) {
            parent.fill(0, 255, 0, (float) (255.0 - Math.abs((noti.columnTime / (noti.defaultTime / 2.0) - 1) * 255.0)));
            for (int i = 0; i < 9; i++) {
                parent.rect(location.x + dimension.x * (noti.columnIndex) / 9, location.y + dimension.y * (i) / 9, dimension.x / 9, dimension.y / 9);
            }
        }
        if (noti.rowTime > 0) {
            parent.fill(0, 255, 0, (float) (255.0 - Math.abs((noti.rowTime / (noti.defaultTime / 2.0) - 1) * 255.0)));
            for (int i = 0; i < 9; i++) {
                parent.rect(location.x + dimension.x * (i) / 9, location.y + dimension.y * (noti.rowIndex) / 9, dimension.x / 9, dimension.y / 9);
            }
        }
        if (noti.squareTime > 0) {
            parent.fill(0, 255, 0, (float) (255.0 - Math.abs((noti.squareTime / (noti.defaultTime / 2.0) - 1) * 255.0)));
            parent.rect(location.x + dimension.x * (noti.squareIndex % 3) / 3, location.y + dimension.y * (noti.squareIndex / 3) / 3, dimension.x / 3, dimension.y / 3);
        }
        noti.decrement();
        //numbers
        parent.textSize(Env.fontSize);
        parent.noStroke();
        parent.fill(Env.fontColor);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                float xPosition = (float) (location.x + dimension.x * i / 9.0 + dimension.x / 18.0);
                float yPosition = (float) (location.x + dimension.y * j / 9.0 + dimension.y / 18.0);
                if (numbers[i][j] > 0) {
                    if (numbers[i][j] == highlightedNumber) {
                        parent.fill(Env.focusedFontColor);
                    } else if (versioned[i][j]) {
                        parent.fill(Env.versionedFontColor);
                    } else {
                        parent.fill(Env.fontColor);
                    }
                    parent.text(numbers[i][j], xPosition, yPosition);
                }
            }
        }
        //heavy grid lines
        parent.stroke(Env.gridColor);
        parent.noFill();
        parent.strokeWeight(Env.heavyLine);
        for (int i = 0; i <= 3; i++) {
            float xPosition = (float) (location.x + dimension.x * i / 3.0);
            float yPosition = (float) (location.y + dimension.y * i / 3.0);
            parent.line(xPosition, location.y, xPosition, location.y + dimension.y);
            parent.line(location.x, yPosition, location.x + dimension.x, yPosition);
        }
        //light grid lines
        parent.strokeWeight(Env.lightLine);
        for (int i = 0; i <= 9; i++) {
            float xPosition = (float) (location.x + dimension.x * i / 9.0);
            float yPosition = (float) (location.y + dimension.y * i / 9.0);
            parent.line(xPosition, location.y, xPosition, location.y + dimension.y);
            parent.line(location.x, yPosition, location.x + dimension.x, yPosition);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void specifyFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Imports a Grid from a file.
     *
     * @param file the path to the file
     * @return whether the operation was successful
     */
    private boolean importGrid(String file) {
        try {
            filePath = file;
            String[] strings = parent.loadStrings(file);
            for (int j = 0; j < 9; j++) {
                int[] ints = PApplet.parseInt(PApplet.splitTokens(strings[j], " "));
                for (int i = 0; i < 9; i++) {
                    if(ints[i] == 0){
                        numbers[i][j] = 0;
                        versioned[i][j] = false;
                    } else if (ints[i] < 0 && ints[i] >= -9) {
                        numbers[i][j] = -ints[i];
                        versioned[i][j] = true;
                    } else if(ints[i] > 0 && ints[i] <= 9) {
                        numbers[i][j] = ints[i];
                        versioned[i][j] = false;
                    } else {
                        throw new RuntimeException();
                    }
                }
            }
            return true;
        } catch (RuntimeException e){
            //something wrong, create a blank Grid
            filePath = null;
            init(parent);
            return false;
        }
    }

    private void exportGrid(String file) {
        filePath = file;
        String strings[] = new String[9];
        for (int j = 0; j < 9; j++) {
            strings[j] = "";
            for (int i = 0; i < 9; i++) {
                strings[j] = strings[j] + String.valueOf(versioned[i][j] ? -numbers[i][j] : numbers[i][j]) + " ";
            }
        }
        parent.saveStrings(file, strings);
        saved = true;
    }

    /**
     * Tries to import a com.n157239.Grid from a file
     */
    @SuppressWarnings("WeakerAccess")
    public boolean importGrid() {
        if (filePath != null) {
            return importGrid(filePath);
        } else {
            throw new NoFileSpecified();
        }
    }

    /**
     * Tries to export the com.n157239.Grid onto a file.
     *
     * @return whether the action was successful
     */
    @SuppressWarnings("WeakerAccess")
    public boolean exportGrid() {
        if (filePath == null) {
            return false;
        } else {
            exportGrid(filePath);
            return true;
        }
    }

    /**
     * Branches off this com.n157239.Grid into a new com.n157239.Grid. The location and size is copied. All the values are copied but will be marked as normal values, not versioned values. This com.n157239.Grid will stay the same.
     *
     * @return the new com.n157239.Grid that is branched off
     */
    @SuppressWarnings("WeakerAccess")
    public Grid branch() {
        Grid newGrid = new Grid(parent);
        for (int i = 0; i < 9; i++) {
            System.arraycopy(numbers[i], 0, newGrid.numbers[i], 0, 9);
        }
        newGrid.location = new PVector(location.x, location.y);
        newGrid.dimension = new PVector(dimension.x, dimension.y);
        newGrid.versioning = true;
        return newGrid;
    }

    /**
     * Copies the location and size of this com.n157239.Grid to a new com.n157239.Grid. The contents will be blank.
     *
     * @return the new com.n157239.Grid with this com.n157239.Grid's location and size
     */
    @SuppressWarnings("WeakerAccess")
    public Grid copyFrame() {
        Grid newGrid = new Grid(parent);
        newGrid.location = new PVector(location.x, location.y);
        newGrid.dimension = new PVector(dimension.x, dimension.y);
        return newGrid;
    }

    /**
     * Simulates what happens when a click happens anywhere on the sketch. This will highlight the cell that was clicked. If the mouse location is outside of this com.n157239.Grid's frame then it will be ignored.
     * If the cell clicked is highlighted already then this will un-select that cell.
     *
     * @param x the x location of the mouse
     * @param y the y location of the mouse
     */
    @SuppressWarnings("WeakerAccess")
    public void onClick(int x, int y) {
        int focusedX, focusedY;
        if (x <= location.x || y >= location.x + dimension.x || y < location.y || y > location.y + dimension.y) {
            return;
        }
        focusedX = PApplet.floor(9 * (x - location.x) / dimension.x);
        focusedY = PApplet.floor(9 * (y - location.y) / dimension.y);
        boolean sameAsBefore = this.focusedX == focusedX && this.focusedY == focusedY;
        this.focusedX = sameAsBefore ? -1 : focusedX;
        this.focusedY = sameAsBefore ? -1 : focusedY;
        ((DrawInterface) parent).drawStuff();
    }

    /**
     * @return whether any cell is focused
     */
    private boolean isFocused() {
        return focusedX >= 0 && focusedX <= 8 && focusedY >= 0 && focusedY <= 8;
    }

    /**
     * Simulates what happens when someone types a number. If a cell is selected inside the com.n157239.Grid then this will change the cell's value. If no cell is selected then the signal is ignored.
     *
     * @param number the number typed
     */
    @SuppressWarnings("WeakerAccess")
    public void onKeyTyped(int number) {
        if (isFocused()) {
            set(focusedX, focusedY, number);
        }
        ((DrawInterface) parent).drawStuff();
    }

    /**
     * Selects the cell above the current selected cell if available.
     */
    @SuppressWarnings("WeakerAccess")
    public void up() {
        if (focusedY > 0) {
            focusedY--;
        }
        ((DrawInterface) parent).drawStuff();
    }

    /**
     * Selects the cell under the current selected cell if available.
     */
    @SuppressWarnings("WeakerAccess")
    public void down() {
        if (focusedY != -1 && focusedY < 8) {
            focusedY++;
        }
        ((DrawInterface) parent).drawStuff();
    }

    /**
     * Selects the cell to the left of the current selected cell if available.
     */
    @SuppressWarnings("WeakerAccess")
    public void left() {
        if (focusedX > 0) {
            focusedX--;
        }
        ((DrawInterface) parent).drawStuff();
    }

    /**
     * Selects the cell to the right of the current selected cell if available.
     */
    @SuppressWarnings("WeakerAccess")
    public void right() {
        if (focusedX != -1 && focusedX < 8) {
            focusedX++;
        }
        ((DrawInterface) parent).drawStuff();
    }
}

import processing.core.PApplet;
import processing.core.PVector;

class Grid {
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

    private int[][] numbers;//(column, row) or (x, y)
    private boolean[][] versioned;
    private boolean versioning;

    private PApplet parent;
    private Notification noti = new Notification();
    private PVector location = null;
    private PVector dimension = null;

    private int focusedX = -1, focusedY = -1;

    private String filePath = null;
    boolean saved = false;


    Grid(PApplet parent) {
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

    @SuppressWarnings("SameParameterValue")
    Grid setFrame(int locX, int locY, int width, int height) {
        location = new PVector(locX, locY);
        dimension = new PVector(width, height);
        return this;
    }

    private void set(int x, int y, int value) {
        set(x, y, value, versioning);
    }

    private void set(int x, int y, int value, boolean versioning) {
        saved = false;
        if (value < 0 || value > 9) {
            throw new IndexOutOfBoundsException();
        }
        numbers[x][y] = value;
        versioned[x][y] = versioning;
        noti.checkout(x, y);
    }

    void draw() {
        if (location == null) {
            throw new RuntimeException("Must configure this grid's location using setFrame(int, int, int, int)");
        }
        draw(location.x, location.y, dimension.x, dimension.y);
    }

    private void draw(float locX, float locY, float width, float height) {
        parent.textAlign(PApplet.CENTER, PApplet.CENTER);
        //background
        parent.fill(Env.backgroundColor);
        parent.stroke(Env.backgroundColor);
        parent.rect(locX, locY, width, height);
        //highlighted areas
        int highlightedNumber = 0;
        parent.fill(Env.focusedBackgroundColor);
        parent.noStroke();
        if (isFocused()) {
            for (int i = 0; i < 9; i++) {
                parent.rect(locX + width * (i) / 9, locY + height * (focusedY) / 9, width / 9, height / 9);
                parent.rect(locX + width * (focusedX) / 9, locY + height * (i) / 9, width / 9, height / 9);
            }
            highlightedNumber = numbers[focusedX][focusedY];
        }
        //highlighted areas when finished
        if (noti.columnTime > 0) {
            parent.fill(0, 255, 0, (float) (255.0 - Math.abs((noti.columnTime / (noti.defaultTime / 2.0) - 1) * 255.0)));
            for (int i = 0; i < 9; i++) {
                parent.rect(locX + width * (noti.columnIndex) / 9, locY + height * (i) / 9, width / 9, height / 9);
            }
        }
        if (noti.rowTime > 0) {
            parent.fill(0, 255, 0, (float) (255.0 - Math.abs((noti.rowTime / (noti.defaultTime / 2.0) - 1) * 255.0)));
            for (int i = 0; i < 9; i++) {
                parent.rect(locX + width * (i) / 9, locY + height * (noti.rowIndex) / 9, width / 9, height / 9);
            }
        }
        if (noti.squareTime > 0) {
            parent.fill(0, 255, 0, (float) (255.0 - Math.abs((noti.squareTime / (noti.defaultTime / 2.0) - 1) * 255.0)));
            parent.rect(locX + width * (noti.squareIndex % 3) / 3, locY + height * (noti.squareIndex / 3) / 3, width / 3, height / 3);
        }
        noti.decrement();
        //numbers
        parent.textSize(Env.fontSize);
        parent.noStroke();
        parent.fill(Env.fontColor);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                float xPosition = (float) (locX + width * i / 9.0 + width / 18.0);
                float yPosition = (float) (locX + height * j / 9.0 + height / 18.0);
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
            float xPosition = (float) (locX + width * i / 3.0);
            float yPosition = (float) (locY + height * i / 3.0);
            parent.line(xPosition, locY, xPosition, locY + height);
            parent.line(locX, yPosition, locX + width, yPosition);
        }
        //light grid lines
        parent.strokeWeight(Env.lightLine);
        for (int i = 0; i <= 9; i++) {
            float xPosition = (float) (locX + width * i / 9.0);
            float yPosition = (float) (locY + height * i / 9.0);
            parent.line(xPosition, locY, xPosition, locY + height);
            parent.line(locX, yPosition, locX + width, yPosition);
        }
    }

    void specifyFilePath(String filePath) {
        this.filePath = filePath;
    }

    private void importGame(String file) {
        filePath = file;
        String[] strings = parent.loadStrings(file);
        for (int j = 0; j < 9; j++) {
            int[] ints = PApplet.parseInt(PApplet.splitTokens(strings[j], " "));
            for (int i = 0; i < 9; i++) {
                if (ints[i] < 0) {
                    numbers[i][j] = -ints[i];
                    versioned[i][j] = true;
                } else {
                    numbers[i][j] = ints[i];
                    versioned[i][j] = false;
                }
            }
        }
    }

    private void exportGame(String file) {
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

    void importGame() {
        if (filePath != null) {
            importGame(filePath);
        }
    }

    boolean exportGame() {
        if (filePath == null) {
            return false;
        } else {
            exportGame(filePath);
            return true;
        }
    }

    Grid branch() {
        Grid newGrid = new Grid(parent);
        for (int i = 0; i < 9; i++) {
            System.arraycopy(numbers[i], 0, newGrid.numbers[i], 0, 9);
        }
        newGrid.location = new PVector(location.x, location.y);
        newGrid.dimension = new PVector(dimension.x, dimension.y);
        newGrid.versioning = true;
        return newGrid;
    }

    Grid copyFrame() {
        Grid newGrid = new Grid(parent);
        newGrid.location = new PVector(location.x, location.y);
        newGrid.dimension = new PVector(dimension.x, dimension.y);
        return newGrid;
    }

    void onClick(int x, int y) {
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

    private boolean isFocused() {
        return focusedX >= 0 && focusedX <= 8 && focusedY >= 0 && focusedY <= 8;
    }

    void onKeyTyped(int number) {
        if (isFocused()) {
            set(focusedX, focusedY, number);
        }
        ((DrawInterface) parent).drawStuff();
    }

    void up() {
        if (focusedY > 0) {
            focusedY--;
        }
        ((DrawInterface) parent).drawStuff();
    }

    void down() {
        if (focusedY != -1 && focusedY < 8) {
            focusedY++;
        }
        ((DrawInterface) parent).drawStuff();
    }

    void left() {
        if (focusedX > 0) {
            focusedX--;
        }
        ((DrawInterface) parent).drawStuff();
    }

    void right() {
        if (focusedX != -1 && focusedX < 8) {
            focusedX++;
        }
        ((DrawInterface) parent).drawStuff();
    }
}

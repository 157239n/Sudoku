package com.n157239;

/**
 * Stores the colors used inside the application and some other settings.
 */
class Env {
    final static int gridColor = color(0);
    final static int fontColor = color(0);
    final static int fontSize = 25;
    final static int panelFontSize = 15;
    final static int backgroundColor = color(255);
    final static int focusedBackgroundColor = color(220);
    final static int versionedFontColor = color(100, 100, 255);
    final static int focusedFontColor = color(255, 0, 0);
    final static int heavyLine = 3;
    final static int lightLine = 1;

    /**
     * Returns an integer describing the color when input the color's red, green and
     * blue elements.
     *
     * @param r how much red? Values ranging from 0 to 255
     * @param g how much green? Values ranging from 0 to 255
     * @param b how much blue? Values ranging from 0 to 255
     * @return the integer describing the color
     */
    private static int color(int r, int g, int b) {
        return -1 - (255 - b) - (255 - g) * 256 - (255 - r) * 256 * 256;
    }

    /**
     * Returns an integer describing the color when input the color on a grey scale.
     *
     * @param c how much color? Values ranging from 0 to 255
     * @return the integer describing the color
     */
    private static int color(int c) {
        return color(c, c, c);
    }

    /**
     * Returns an integer describing the color when input the color on a grey scale.
     *
     * @param c how much color? Values ranging from 0 to 255
     * @return the integer describing the color
     */
    private static int color(double c) {
        return color((int) c, (int) c, (int) c);
    }

    /**
     * Returns an integer describing the redness of a particular color.
     *
     * @param color the color we want the redness of
     * @return the redness of the color
     */
    private static int red(int color) {
        return 255 - (-color - 1) / (256 * 256);
    }

    /**
     * Returns an integer describing the greenness of a particular color.
     *
     * @param color the color we want the greenness of
     * @return the greenness of the color
     */
    private static int green(int color) {
        return 255 - ((-color - 1) % (256 * 256)) / 256;
    }

    /**
     * Returns an integer describing the blueness of a particular color.
     *
     * @param color the color we want the blueness of
     * @return the blueness of the color
     */
    private static int blue(int color) {
        return 255 - (-color - 1) % 256;
    }
}

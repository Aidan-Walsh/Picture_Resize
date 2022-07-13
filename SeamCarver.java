import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class SeamCarver {


    // double array to store integer RGB values
    private int[][] storeRgb;

    // variable to store current width
    private int width;

    // variable to store height
    private int height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {

        // make sure no null argument
        if (picture == null) throw new IllegalArgumentException("no null argument");

        // initialize instance variables
        height = picture.height();
        width = picture.width();
        storeRgb = new int[height][width];

        // place RGB value from argument into 2-d array
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                storeRgb[j][i] = picture.getRGB(i, j);

            }
        }
    }

    // current picture
    public Picture picture() {

        // make new picture object so that it is immutable
        Picture returnPic = new Picture(width, height);

        // copy rgb values into new picture object to be returned
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                returnPic.setRGB(i, j, storeRgb[j][i]);
            }
        }
        return returnPic;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        // make sure valid x and y arguments
        if (x >= width() || y >= height() || x < 0 || y < 0) {
            throw new IllegalArgumentException("x or y out of bounds");
        }
        // store location of values around x and y for future computation
        double deltaXsq;
        double deltaYsq;
        int colBack = x - 1;
        int colFront = x + 1;
        int rowBack = y - 1;
        int rowFront = y + 1;
        // corner case where width and height is 1, we just use the column 0
        // and row 0 respectively
        if (width == 1) {
            colFront = 0;
            colBack = 0;
        }
        if (height == 1) {
            rowFront = 0;
            rowBack = 0;
        }
        // wrap around case where we use location at other end if we are at index
        // 0 or at the end
        if (y == 0) rowBack = height - 1;
        else if (y >= height - 1) rowFront = 0;
        if (x == 0) colBack = width - 1;
        else if (x >= width - 1) colFront = 0;
        // since RGB is just one integer, we can use bitwise operations to
        // isolate the red, green, and blue values
        // we store the differences between the surrounding rgb values
        int deltaXred = ((storeRgb[y][colBack] >> 16) & 0xFF) -
                ((storeRgb[y][colFront] >> 16) & 0xFF);
        int deltaXgr = ((storeRgb[y][colBack] >> 8) & 0xFF) -
                ((storeRgb[y][colFront] >> 8) & 0xFF);
        int deltaXbl = ((storeRgb[y][colBack]) & 0xFF) -
                ((storeRgb[y][colFront]) & 0xFF);
        // compute the sum of squares of changes in the x direction (x gradient)
        deltaXsq = (deltaXred * deltaXred) + (deltaXgr * deltaXgr) +
                (deltaXbl * deltaXbl);
        int deltaYred = ((storeRgb[rowBack][x] >> 16) & 0xFF) -
                ((storeRgb[rowFront][x] >> 16) & 0xFF);
        int deltaYgr = ((storeRgb[rowBack][x] >> 8) & 0xFF) -
                ((storeRgb[rowFront][x] >> 8) & 0xFF);
        int deltaYbl = ((storeRgb[rowBack][x]) & 0xFF) -
                ((storeRgb[rowFront][x]) & 0xFF);
        // compute sum of squares of changes in the y direction (y gradient)
        deltaYsq = (deltaYred * deltaYred) + (deltaYgr * deltaYgr) +
                (deltaYbl * deltaYbl);
        // finally, take square root
        return Math.sqrt(deltaXsq + deltaYsq);
    }


    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        // call transpose method which will allow us to perform the vertical
        // operation
        transpose();
        int[] horizSeam = findVerticalSeam();
        // must transpose again so that we may perform more operations on what
        // the picture is supposed to be
        transpose();
        return horizSeam;
    }

    // helper method that transposes
    private void transpose() {
        // we have to create a new RGB 2-d array because we cannot change the
        // height or width of an already initialized array
        int[][] storeRGB = new int[width][height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // make the column values of the instance variable equal to
                // the row values of the local variable when taking the transpose
                storeRGB[j][i] = storeRgb[i][j];
            }
        }
        // point the instance variable to the local so that it is changed by
        // reference
        storeRgb = storeRGB;
        // we also need to swap the height and width values
        int temp = height();
        height = width();
        width = temp;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        // make new 2-d arrays, one to store energy of all pixels, the other
        // that is used for the memoization, and the other to store the
        // edges of the minimum paths
        double[][] calculatedMins = new double[height][width];
        double[][] storeEnergy = new double[height][width];
        int[][] edgeTo = new int[height][width];
        int[] storeMin = new int[height];
        double aboveLeft;
        double aboveRight;
        double above;
        double min;
        int minIndex = 0;
        // create energy array
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double energy = energy(i, j);
                storeEnergy[j][i] = energy;
            }
        }
        // put all values in first row in energy array into first row of
        // memoization array since no values above to compute for path
        for (int i = 0; i < width; i++) {
            calculatedMins[0][i] = storeEnergy[0][i];
        }
        // begin at next row for memoization, and to store length of shortest
        // path, we are to take the minimum of the summed pixel energy above,
        // above to the right, and above to the left
        // this is because a path can only go down, to the left or right
        for (int i = 1; i < height; i++) {
            for (int j = 0; j < width; j++) {
                above = calculatedMins[i - 1][j];
                // in cases where we are at an end, we do not worry about the
                // energy that is out of bounds
                if (j == 0) aboveLeft = Integer.MAX_VALUE;
                else aboveLeft = calculatedMins[i - 1][j - 1];
                if (j == width - 1) aboveRight = Integer.MAX_VALUE;
                else aboveRight = calculatedMins[i - 1][j + 1];
                // find minimum of 3 values
                min = Math.min(above, Math.min(aboveLeft, aboveRight));
                // add min to the current energy and store that distance
                // in the memoization array
                calculatedMins[i][j] = storeEnergy[i][j] + min;
                // depending on which is the minimum sum/distance that we found
                // we add the index of that into our edgeTo array
                if (min == above) edgeTo[i][j] = j;
                else if (min == aboveLeft) edgeTo[i][j] = j - 1;
                else edgeTo[i][j] = j + 1;
            }
        }
        // find index of minimum distance that is located in bottom row
        min = calculatedMins[height - 1][0];
        for (int i = 1; i < width; i++) {
            // iterate through bottom row, keeping track of lowest value and
            // corresponding index
            if (min > calculatedMins[height - 1][i]) {
                min = calculatedMins[height - 1][i];
                minIndex = i;
            }
        }
        // put index of minimum value of bottom row into the array to be returned
        storeMin[height - 1] = minIndex;
        // retrace path to return array
        for (int i = height - 1; i > 0; i--) {
            // since our edgeTo array store the index of the edge that the
            // minimum came from, we just go up the array, moving to the
            // corresponding index that is returned by edgeTo and adding
            // this value into our array that stores the indices
            storeMin[i - 1] = edgeTo[i][minIndex];
            minIndex = storeMin[i - 1];
        }
        return storeMin;
    }


    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        // like before, we must transpose to perform the vertical operation
        transpose();
        removeVerticalSeam(seam);
        // transpose again to put back to original state
        transpose();
    }

    // helper method to test if argument array is valid
    private void isValid(int[] seam) {

        // make sure that width is not 1 to perform the removal of vertical
        // seam
        if (width() == 1)
            throw new IllegalArgumentException("Cannot remove");
        // cannot have null argument
        if (seam == null) throw new IllegalArgumentException("No null argument");
        // store value at first index for comparison
        int storeI = seam[0];
        // make sure that length is same as height for it to be legitimate removal
        if (seam.length != height())
            throw new IllegalArgumentException("wrong length");
        // iterate through
        for (int i = 0; i < seam.length; i++) {
            // make sure point is within legal bounds
            if (seam[i] >= width() || seam[i] < 0)
                throw new IllegalArgumentException("Illegal point");
            // compare current value to last value and make sure that they
            // are either equal or off by one
            if (i > 0) {
                if ((seam[i] - 1 == storeI || seam[i] + 1 == storeI ||
                        seam[i] == storeI)) {
                    storeI = seam[i];
                }
                else {
                    throw new IllegalArgumentException("Illegal point present");
                }
            }
        }
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        // make sure seam is valid
        isValid(seam);
        // create new rgb array since we cannot change width or height of
        // an already initialized array
        // Picture newPic = new Picture(width - 1, height);
        int[][] newRgb = new int[height][width - 1];
        // iterate through all of new array, where width is 1 less
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width - 1; j++) {
                // when we hit index of what is to be removed, and beyond,
                // set value in new RGB array to be the index + 1 of the original
                // in that row
                // since have removed a spot
                if (j >= seam[i]) {
                    newRgb[i][j] = storeRgb[i][j + 1];

                }
                else {
                    // if we hit before the index, then we just copy contents
                    // of row into new RGB array
                    newRgb[i][j] = storeRgb[i][j];
                }
            }
        }
        // point instance variable to new RGB array
        storeRgb = newRgb;
        // update width
        width = width - 1;
    }

    // test methods - used similar testing as in ResizeDemo.java except also
    // test for energy and use random picture
    public static void main(String[] args) {

        Picture pic = new Picture(args[0]);
        int width = Integer.parseInt(args[1]);
        int height = Integer.parseInt(args[2]);


        SeamCarver sc = new SeamCarver(pic);
        StdOut.printf("%d-by-%d image\n", sc.picture().width(), sc.picture().height());

        // begin stopwatch for timing
        Stopwatch sw = new Stopwatch();

        // remove vertical seams, thus reducing width
        for (int i = 0; i < width; i++) {
            int[] verticalSeam = sc.findVerticalSeam();
            sc.removeVerticalSeam(verticalSeam);
        }

        // remove horizontal seams, thus reducing height
        for (int i = 0; i < height; i++) {
            int[] horizontalSeam = sc.findHorizontalSeam();
            sc.removeHorizontalSeam(horizontalSeam);
        }


        StdOut.printf("new image size is %d columns by %d rows\n", sc.width(),
                      sc.height());

        StdOut.println("Resizing time: " + sw.elapsedTime() + " seconds.");

        sc.picture().show();
                
    }


}


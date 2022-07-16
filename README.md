# Picture_Resize
Seam Carver algorithm (content aware image resizing) that trims an image by a specified amount. Using special algorithms, it looks for the least important seams of an image and removes them. It takes an image file name from the command line, as well as the width and height that the image is to be reduced by and outputs the trimmed image. It also outputs the size of the original image, the size of the new image, and the total time to resize the image. 

# Before Use
Please note that this code uses a library of methods (seen imported at the top of the files). 
Please follow this link(https://algs4.cs.princeton.edu/code/) to download the correct path to this library so that you may be able to run and use this code correctly. 

# To use
1. Download Java file in this repo and ensure that your png or jpg is in the same directory 
2. Compile with the command:
    - "javac-algs4 SeamCarver.java"
3. Run with the command, where "picture.png" is the name of the png or jpg, "100" specifies the width that the picture will be reduced by (in pixels),
and "200" specifies the height that the picture will be reduced by (in pixels). 
    - "java-algs4 SeamCarver picture.png 100 200"
    
A new, resized picture should appear on your computer, as well as the dimensions of the old and new image, and the total resizing time!

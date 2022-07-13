# Picture_Resize
Seam Carver algorithm (content aware image resizing) that trims an image by a specified amount. It takes an image file as standard input, as well as 
width and height that the image is to be reduced by

# Before Use
Please note that this code uses Princeton University's library of methods (seen imported at the top of the files). 
If one runs into issues using these libraries, please import other libraries that can get the same job done. This code is also compiled and ran 
using "javac-algs4" and "java-algs4". Please use "javac" and "java" instead if these do not work.

# To use
1. Download Java file in this repo and ensure that your png or jpg is in the same directory 
2. Compile with the command:
    - "javac-algs4 SeamCarver.java"
3. Run with the command, where "picture.png" is the name of the png or jpg, "100" specifies the width that the picture will be reduced by (in pixels),
and "200" specifies the height that the picture will be reduced by (in pixels). 
    - "java-algs4 SeamCarver picture.png 100 200"
    
A new, resized picture should appear on your computer!

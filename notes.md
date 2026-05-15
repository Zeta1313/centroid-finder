# Notes
- public main method will take a String array that contain three things (these are strings):
    - input_image: the image path string that you find and read load
    - hex_target_color: the string color code.
    - threshold: a string that will determine the intensity of the image
... using java ImageSummaryApp <input_image> <hex_target_color> <threshold>
1. Has no fallback for oversized argument arrays. Possible problem or irrelevant?
2. Not familiar with "writing to disk" need to look into that.
3. What are the benifits of not allowing diagonal movmeent? Just simplicity?
4. What is printStackTrace()? Where is the source?

Important files:
    ImageSummaryApp - Main file, what actually runs the program
        BinarizingGroupFinder - accepts takes ImageBinarizer and groupfinder to create array based on image
        DistanceImageBinarizer - extends ImageBinarizer, converts image from varied colors to only black and white, based on recieved color and threshold.
        DFSBinaryGroupFinder - finds groups from the binarized image


## DistanceImageBinarizer
- first method takes in BurredImage type variable and converts into a 2d array that we return.
    - each entry is the returned array is either 0 or 1, representing a black or white pizel.


# hi
- javac -cp "lib/*;target" -d target src/*.java
- find src -name "*.class" -delete && echo "Cleaned .class files" || echo "No .class files found or error"
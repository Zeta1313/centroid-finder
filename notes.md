# Notes
- public main method will take a String array that contain three things (these are strings):
    - input_image: the image path string that you find and read load
    - hex_target_color: the string color code.
    - threshold: a string that will determine the intensity of the image
... using java ImageSummaryApp <input_image> <hex_target_color> <threshold>
1. Has no fallback for oversized argument arrays. Possible problem or irrelevant?
2. Not familiar with "writing to disk" need to look into that.
3. What are the benifits of not allowing diagonal movmeent? Just simplicity?

## DistanceImageBinarizer
- first method takes in BurredImage type variable and converts into a 2d array that we return.
    - each entry is the returned array is either 0 or 1, representing a black or white pizel.

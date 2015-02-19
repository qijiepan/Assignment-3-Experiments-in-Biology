# Assignment-3-Experiments-in-Biology
how to analyze tables of numbers
#The dataset

    Title: BUPA liver disorders
    Source information:
    -- Creators: BUPA Medical Research Ltd.
    -- Donor: Richard S. Forsyth
             8 Grosvenor Avenue
             Mapperley Park
             Nottingham NG3 5DX
             0602-621676
     -- Date: 5/15/1990
    https://archive.ics.uci.edu/ml/machine-learning-databases/liver-disorders/
#The major transformations
        Put the column name and data in a same csv. When i do kmeans to classify the data, I normalize all the data to (0,1)
#Usage
        the model can be used in different data set,although in different dimension or different number of rows, the weakness is I use the city block distance and assume k =3 and the number of iteration is only 100. 
#Code
        the thought is come from http://scivisarunbaskar.blogspot.com/2012/10/parallel-coordinates.html?view=snapshot
        And some of the code part is from http://stackoverflow.com/questions/19213961/parallel-coordinates-program-written-with-processing-cant-show-anything-in-mac
        I made the interation part and clustering part for it.
#Addition
        tech: kmeans and interactivity.
        Bio: Normalize the dataset.
#Run
        Just download the file and click the .pde~ it works, but you need to change the args.

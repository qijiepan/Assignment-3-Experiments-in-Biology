import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class sketch_03table extends PApplet {

FloatTable data;

String dataPath = "/Users/Kevin/desktop/cars.csv";
int numRows;
int numCols;
int[] colMin;
int[] colMax;
String[] colNames;
float[][] store;
int[] mark;

float plotX1, plotY1;
float plotX2, plotY2;
float diffBetweenXCoords;
PFont titleFont;
PFont labelFont;
PFont axisLimitsFont;

int[] axisColor       = { 0xff333333, 0xff000000 };
int[] fontAxisColor   = { 0xff333333, 0xffFF2222 };
int[] fontLimitsColor = { 0xff555555, 0xffFF2222 };
int triangleColor     = 0xff888888;
int[] linesColor      = { 0xffED1317, 0xff1397ED };

int[] axisOrder;
boolean[] axisFlipped;

// Setup
public void setup()
{
  size(1000, 500);
  //frameRate(1);
  // Read data
  data = new FloatTable(dataPath);
  numRows = data.getRowCount();
  numCols = data.getColumnCount();
  colNames = data.getColumnNames();
  store = new float[numRows][numRows];
  mark = new int[numRows];

  colMin  = new int[ numCols ];
  colMax  = new int[ numCols ];
  axisOrder = new int[ numCols ];
  axisFlipped = new boolean[ numCols ];

  for(int col = 0; col < numCols; col++)
  {
    float maxNumber = data.getColumnMax(col);
    float minNumber = data.getColumnMin(col);

    colMin[col] = PApplet.parseInt(floor(minNumber));
    colMax[col] = PApplet.parseInt(ceil(maxNumber));

    axisOrder[col] = col;
    axisFlipped[col] = false;
  }

  // Fonts
  titleFont = createFont("Verdana", 16);
  labelFont = createFont("Verdana Bold", 11);
  axisLimitsFont = createFont("Georgia", 11);

  // Plot area limits
  plotX1 = 30;
  plotX2 = width - plotX1;
  plotY1 = 60;
  plotY2 = height - plotY1;

  diffBetweenXCoords = (plotX2 - plotX1) / (numCols - 1);

  smooth();
  
  mark=kmeans();
  /*for(int i =0;i<100;i++){
    print(mark[i]);
  }*/

}
     // Draw
public void draw()
{
    // Background
    background(240);

    // Draw the plot area
    fill(240);
    noStroke();
    rect(plotX1, plotY1, plotX2 - plotX1, plotY2 - plotY1);

    drawAxis();
    drawLines();
    
   }

public void drawAxis()
{
  float xCoordsForAxis = plotX1;//30
  float yAxisLbl = plotY2 + 40;//480
  float yMinLbl  = plotY2 + 15;//445
  float yMaxLbl  = plotY1 - 7;//53
  float yTriMin  = plotY1 - 25;//35
  float yTriMax  = plotY1 - 35;//25

  strokeCap(PROJECT);
  strokeWeight(1);
  stroke(0);

  for( int col = 0; col < numCols; col++, xCoordsForAxis += diffBetweenXCoords )
  {
    int colToDraw = axisOrder[col];

    // Draw Axis
    stroke(axisColor[0]);
    rect(xCoordsForAxis, plotY1, 4, plotY2-plotY1);
    //line(xCoordsForAxis, plotY1, xCoordsForAxis, plotY2);

    // Label min/max
    textAlign(CENTER);
    textFont(axisLimitsFont);
    fill(fontLimitsColor[0]);
    if( !axisFlipped[colToDraw])
    {
      text( colMin[colToDraw], xCoordsForAxis, yMinLbl);
      text( colMax[colToDraw], xCoordsForAxis, yMaxLbl);
    }
    /*else
    {
      text( colMin[colToDraw], xCoordsForAxis, yMinLbl);
      text( colMax[colToDraw], xCoordsForAxis, yMaxLbl);
    }
    */

    // Axis label
    textFont( labelFont );
    fill(fontAxisColor[0]);
    text( colNames[colToDraw], xCoordsForAxis, yAxisLbl );

    // Triangle
    fill(triangleColor);
    noStroke();
    if( axisFlipped[colToDraw] )
    {
      triangle(xCoordsForAxis - 3, yTriMax, xCoordsForAxis, yTriMin, xCoordsForAxis + 3, yTriMax);
    }
    else
    {
      triangle(xCoordsForAxis - 3, yTriMin, xCoordsForAxis, yTriMax, xCoordsForAxis + 3, yTriMin);
    }
  }
}

public void drawLines()
{
  noFill();
  strokeWeight(1);

  for(int row = 0; row < numRows; row++)
  {
    beginShape();
    if(mark[row]==1)
      stroke(0,255,0);
    if(mark[row]==2)
      stroke(0,0,255);
    for(int column = 0; column < numCols; column++)
    {
      int colToDraw = axisOrder[column];
      
      
        float cMax = ( axisFlipped[colToDraw] ? colMin[colToDraw] : colMax[colToDraw] );
        float cMin = ( axisFlipped[colToDraw] ? colMax[colToDraw] : colMin[colToDraw] );
        float value = data.getFloat(row, colToDraw);

        float x = plotX1 + diffBetweenXCoords * colToDraw;
        float y = map(value, cMin, cMax, plotY2, plotY1);

        if(colToDraw == 0)
        {
          stroke( lerpColor(linesColor[0], linesColor[1],  map(value, cMin, cMax, 0.f, 1.f) ), 150 );
          //this is the kmeans works, but it looks like the graph will be a little bit 
          
          if(mark[row]==0)
            stroke(255, 0, 0,150);
          if(mark[row]==1)
            stroke(0,255,0,150);
          if(mark[row]==2)
            stroke(0,0,255,150);
          


        }
        vertex(x, y);
        store[row][column] = y;//store the points
    }
    endShape();
  }
   for(int k = 0; k<=numCols;k++){
    if((mouseX<=plotX1+k*diffBetweenXCoords+4)&&(mouseX>=plotX1+k*diffBetweenXCoords)){
      for(int i=0;i<numRows;i++){
        if((mouseY<=store[i][k]+1)&&(mouseY>=store[i][k]-1)){
          noFill();
          stroke(0);
          strokeWeight(1);
          beginShape();
          for(int j=0;j<numCols;j++){
            vertex(plotX1+j*diffBetweenXCoords,store[i][j]);
          }
          endShape();
        }
        
      }
    }
  }
}
//we assume k =3 in kmeans
public int[] kmeans(){
  int[] k;
  float[][] data2;
  float[][] sumdata;
  float[][] center;
  k = new int[numRows];
  data2 = new float[numRows][numCols];
  sumdata = new float[3][numCols];
  center = new float[3][numCols];
  for(int i = 0; i<numRows;i++){
    k[i]=PApplet.parseInt(random(3));
    }
  //normalization
  for(int i=0;i<numRows;i++){
    for(int j=0; j<numCols;j++){
      data2[i][j] =(data.getFloat(i,j)-colMin[j])/(colMax[j]-colMin[j]);
    }
  }
  //we use manhantan distance/city block distance
  //get the central point
  int count = 0;
  while(count <=100){
    for(int i = 0; i<3;i++){
      for (int j = 0; j<numCols;j++){
        sumdata[i][j] = 0;//initialize the sum
      }
    }
    for(int j = 0; j<numCols;j++){
      int countred = 0;
      int countgreen = 0;
      int countblue = 0;
      for(int i = 0; i<numRows;i++){
        if(k[i]==0){
          sumdata[0][j]+=data2[i][j];
          countred++;
        }else if(k[i]==1){
          sumdata[1][j]+=data2[i][j];
          countgreen++;
        }else{
          sumdata[2][j]+=data2[i][j];
          countblue++;
        }
      }
    center[0][j]=sumdata[0][j]/PApplet.parseFloat(countred);
    center[1][j]=sumdata[1][j]/PApplet.parseFloat(countgreen);
    center[2][j]=sumdata[2][j]/PApplet.parseFloat(countblue);
    }
    float distance1;
    float distance2;
    float distance3;

    for (int i=0;i<numRows;i++){
      distance1 = 0.0f;
      distance2 = 0.0f;
      distance3 = 0.0f;
      for (int j=0;j<numCols;j++){
        distance1+= Math.abs(data2[i][j]-center[0][j]);
        distance2+= Math.abs(data2[i][j]-center[1][j]);
        distance3+= Math.abs(data2[i][j]-center[2][j]);
      }
      if ((distance1<=distance2)&&(distance1<=distance3)){
        k[i]=0;
      }else if((distance2<distance1)&&(distance2<=distance3)){
        k[i]=1;
      }else{
        k[i]=2;
      }
    }
    //println(center[0][0],center[1][0],center[2][0]);
    //println(distance1,distance2,distance3);
    //println(k[0],k[1],k[2]);
    count++;
  }

  return k;

  //print(data2[0][0],data2[0][1]);

}



class FloatTable {
  int rowCount;
  int columnCount;
  float[][] data;
  String[] rowNames;
  String[] columnNames;


  FloatTable(String filename) {
    String[] rows = loadStrings(filename);

    String[] columns = split(rows[0], ',');
    columnNames = subset(columns, 0); // upper-left corner ignored
    //scrubQuotes(columnNames);
    columnCount = columnNames.length;

    rowNames = new String[rows.length-1];
    data = new float[rows.length-1][];

    // start reading at row 1, because the first row was only the column headers
    for (int i = 1; i < rows.length; i++) {
      if (trim(rows[i]).length() == 0) {
        continue; // skip empty rows
      }
    
      // split the row on the tabs
      String[] pieces = split(rows[i], ',');
     

      // copy row title
      rowNames[rowCount] = pieces[0];
      // copy data into the table starting at pieces[1]
      data[rowCount] = parseFloat(subset(pieces, 0));

      // increment the number of valid rows found so far
      rowCount++;      
    }
    // resize the 'data' array as necessary
    data = (float[][]) subset(data, 0, rowCount);
  }




  public int getRowCount() {
    return rowCount;
  }


  public String getRowName(int rowIndex) {
    return rowNames[rowIndex];
  }


  public String[] getRowNames() {
    return rowNames;
  }


  // Find a row by its name, returns -1 if no row found. 
  // This will return the index of the first row with this name.
  public int getRowIndex(String name) {
    for (int i = 0; i < rowCount; i++) {
      if (rowNames[i].equals(name)) {
        return i;
      }
    }
  
    return -1;
  }


  // technically, this only returns the number of columns 
  // in the very first row (which will be most accurate)
  public int getColumnCount() {
    return columnCount;
  }


  public String getColumnName(int colIndex) {
    return columnNames[colIndex];
  }


  public String[] getColumnNames() {
    return columnNames;
  }


  public float getFloat(int rowIndex, int col) {
    // Remove the 'training wheels' section for greater efficiency
    // It's included here to provide more useful error messages

    // begin training wheels
    if ((rowIndex < 0) || (rowIndex >= data.length)) {
      throw new RuntimeException("There is no row " + rowIndex);
    }
    if ((col < 0) || (col >= data[rowIndex].length)) {
      throw new RuntimeException("Row " + rowIndex + " does not have a column " + col);
    }
    // end training wheels

    return data[rowIndex][col];
  }




  public float[] getColumnMinMax(int col) {
    float Min =  Float.MAX_VALUE;
    float Max = -Float.MAX_VALUE;
    for (int i = 0; i < rowCount; i++) {

      if (!Float.isNaN(data[i][col])) {

        if (data[i][col] < Min) {
          Min = data[i][col];
        }

        if (data[i][col] > Max) {
          Max = data[i][col];
        }
      }
    }
    float[] toRet = { Min, Max };
    return toRet;
  }


  public float getColumnMin(int col) {
    float m = Float.MAX_VALUE;
    for (int i = 0; i < rowCount; i++) {
      if (!Float.isNaN(data[i][col])) {
        if (data[i][col] < m) {
          m = data[i][col];
        }
      }
    }
    return m;
  }


  public float getColumnMax(int col) {
    float m = -Float.MAX_VALUE;
    for (int i = 0; i < rowCount; i++) {
      
        if (data[i][col] > m) {
          m = data[i][col];
        }
      
    }
    return m;
  }


  public float getRowMin(int row) {
    float m = Float.MAX_VALUE;
    for (int i = 0; i < columnCount; i++) {
     
        if (data[row][i] < m) {
          m = data[row][i];
        }
      
    }
    return m;
  } 


  public float getRowMax(int row) {
    float m = -Float.MAX_VALUE;
    for (int i = 1; i < columnCount; i++) {
      if (!Float.isNaN(data[row][i])) {
        if (data[row][i] > m) {
          m = data[row][i];
        }
      }
    }
    return m;
  }


  public float getTableMin() {
    float m = Float.MAX_VALUE;
    for (int i = 0; i < rowCount; i++) {
      for (int j = 0; j < columnCount; j++) {
     
          if (data[i][j] < m) {
            m = data[i][j];
          }
        
      }
    }
    return m;
  }


  public float getTableMax() {
    float m = -Float.MAX_VALUE;
    for (int i = 0; i < rowCount; i++) {
      for (int j = 0; j < columnCount; j++) {
      
          if (data[i][j] > m) {
            m = data[i][j];
          }
        
      }
    }
    return m;
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "sketch_03table" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

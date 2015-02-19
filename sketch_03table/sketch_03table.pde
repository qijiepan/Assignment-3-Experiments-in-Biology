FloatTable data;

String dataPath = "/Users/Kevin/desktop/BUPA liver disorders.csv";
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

color[] axisColor       = { #333333, #000000 };
color[] fontAxisColor   = { #333333, #FF2222 };
color[] fontLimitsColor = { #555555, #FF2222 };
color triangleColor     = #888888;
color[] linesColor      = { #ED1317, #1397ED };

int[] axisOrder;
boolean[] axisFlipped;

// Setup
void setup()
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

    colMin[col] = int(floor(minNumber));
    colMax[col] = int(ceil(maxNumber));

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
void draw()
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

void drawAxis()
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

void drawLines()
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
          stroke( lerpColor(linesColor[0], linesColor[1],  map(value, cMin, cMax, 0., 1.) ), 150 );
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
int[] kmeans(){
  int[] k;
  float[][] data2;
  float[][] sumdata;
  float[][] center;
  k = new int[numRows];
  data2 = new float[numRows][numCols];
  sumdata = new float[3][numCols];
  center = new float[3][numCols];
  for(int i = 0; i<numRows;i++){
    k[i]=int(random(3));
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
    center[0][j]=sumdata[0][j]/float(countred);
    center[1][j]=sumdata[1][j]/float(countgreen);
    center[2][j]=sumdata[2][j]/float(countblue);
    }
    float distance1;
    float distance2;
    float distance3;

    for (int i=0;i<numRows;i++){
      distance1 = 0.0;
      distance2 = 0.0;
      distance3 = 0.0;
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




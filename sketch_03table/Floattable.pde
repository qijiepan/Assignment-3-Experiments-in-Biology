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




  int getRowCount() {
    return rowCount;
  }


  String getRowName(int rowIndex) {
    return rowNames[rowIndex];
  }


  String[] getRowNames() {
    return rowNames;
  }


  // Find a row by its name, returns -1 if no row found. 
  // This will return the index of the first row with this name.
  int getRowIndex(String name) {
    for (int i = 0; i < rowCount; i++) {
      if (rowNames[i].equals(name)) {
        return i;
      }
    }
  
    return -1;
  }


  // technically, this only returns the number of columns 
  // in the very first row (which will be most accurate)
  int getColumnCount() {
    return columnCount;
  }


  String getColumnName(int colIndex) {
    return columnNames[colIndex];
  }


  String[] getColumnNames() {
    return columnNames;
  }


  float getFloat(int rowIndex, int col) {
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




  float[] getColumnMinMax(int col) {
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


  float getColumnMin(int col) {
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


  float getColumnMax(int col) {
    float m = -Float.MAX_VALUE;
    for (int i = 0; i < rowCount; i++) {
      
        if (data[i][col] > m) {
          m = data[i][col];
        }
      
    }
    return m;
  }


  float getRowMin(int row) {
    float m = Float.MAX_VALUE;
    for (int i = 0; i < columnCount; i++) {
     
        if (data[row][i] < m) {
          m = data[row][i];
        }
      
    }
    return m;
  } 


  float getRowMax(int row) {
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


  float getTableMin() {
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


  float getTableMax() {
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

/*
 * The MIT License
 *
 * Copyright 2015 eplightning <eplightning at outlook dot com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package excel.exportimport.formats;

import excel.exportimport.ExportImportLocation;
import excel.exportimport.ExportImportData;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Format CSV
 * 
 * @author eplightning <eplightning at outlook dot com>
 */
public class SheetFormatCsv implements SheetFormat {

    /**
     * Wczytywanie danych z pliku
     * 
     * @param  file Plik  do wczytania
     * @return Struktura danych
     * @throws IOException 
     */
    @Override
    public ExportImportData loadFile(File file) throws IOException
    {
        ExportImportData data = new ExportImportData();
        FileReader reader = new FileReader(file);

        // stany
        int character;
        boolean stringRead = false;
        boolean insideText = false;
        boolean slash = false;
        StringBuilder text = new StringBuilder();
        int row = 1;
        int column = 1;
        
        // przynajmniej jedna kolumna
        data.setColumns(1);

        // znak po znaku
        while ((character = reader.read()) != -1) {
            if (insideText) {
                if (slash) {
                    text.appendCodePoint(character);
                    slash = false;
                } else if (character == '\\') {
                    slash = true;
                } else if (character == '"') {
                    insideText = false;
                    stringRead = true;

                    if (text.length() > 0) {
                        data.getCells().put(new ExportImportLocation(column, row), text.toString());
                        text = new StringBuilder();
                    }
                } else {
                    text.appendCodePoint(character);
                }
            } else if (character == '"') {
                if (stringRead) {
                    throw new IOException("Seperator needed between each cell");
                } else {
                    insideText = true;
                }
            } else if (character == ' ' || character == '\r') {
            } else if (character == '\n') {
                row++;
                column = 1;
                stringRead = false;
            } else if (character == ',') {
                column++;
                stringRead = false;

                if (column > data.getColumns())
                    data.setColumns(column);
            } else {
                throw new IOException("Invalid character between cells");
            }
        }
        
        reader.close();

        data.setRows(row);
        
        return data;
    }

    /**
     * Zapis danych do pliku
     * 
     * @param file Plik do zapisania
     * @param data Struktura danych
     * @throws IOException 
     */
    @Override
    public void saveFile(File file, ExportImportData data) throws IOException
    {
        FileWriter writer = new FileWriter(file);
        
        for (int i = 1; i < data.getRows(); i++) {
            for (int j = 1; i < data.getColumns(); j++) {
                if (j != 1)
                    writer.write(',');
        
                // pobieramy wartość
                String val = data.getCells().get(new ExportImportLocation(j, i));
                
                // jeśli nie znaleźliśmy to znaczy że puste
                if (val != null && !val.isEmpty()) {
                    writer.write('"');
                    
                    final int len = val.length();
                    
                    for (int k = 0; k < len; k++) {
                        char character = val.charAt(k);
                        
                        if (character == '"' || character == '\\') {
                            writer.write('\\');
                        }
                        
                        writer.write(character);
                    }
                    
                    writer.write('"');
                }
            }
            
            writer.write('\n');
        }
        
        writer.close();
    }

}

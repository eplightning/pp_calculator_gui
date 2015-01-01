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

import excel.exportimport.ExportImportData;
import java.io.File;
import java.io.IOException;

/**
 * Interfejs formatu importu exportu
 * 
 * @author eplightning <eplightning at outlook dot com>
 */
public interface SheetFormat {

    /**
     * Wczytywanie danych z pliku
     * 
     * @param  file Plik  do wczytania
     * @return Struktura danych
     * @throws IOException 
     */
    public ExportImportData loadFile(File file) throws IOException;
    
    /**
     * Zapis danych do pliku
     * 
     * @param file Plik do zapisania
     * @param data Struktura danych
     * @throws IOException 
     */
    public void saveFile(File file, ExportImportData data) throws IOException;
}

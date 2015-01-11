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
package excel.sheet;

import java.util.HashMap;

/**
 * Interfejs dostępu do komórek tabeli dla wyrażeń
 *
 * @author eplightning <eplightning at outlook dot com>
 */
public class CellAccessor {

    /**
     * Oryginalne komórki których accessor nie modyfikuje
     */
    protected final HashMap<Location, Cell> cells;

    /**
     * Komórki wyprodukowane w procesie obliczeń
     */
    protected final HashMap<Location, Cell> producedCells;

    public CellAccessor(HashMap<Location, Cell> cells)
    {
        this.cells = cells;
        this.producedCells = new HashMap<>();
    }

    public HashMap<Location, Cell> getProducedCells()
    {
        return producedCells;
    }

    /**
     * Najpierw próbuje z wyprodukowanych, jak nie znajdzie to próbuje w poprzednich komórkach
     *
     * @param col
     * @param row
     * @return
     */
    public Cell get(int col, int row)
    {
        Location loc = new Location(col, row);
        Cell firstTry = producedCells.get(loc);

        if (firstTry == null)
            return cells.get(loc);

        return firstTry;
    }

    /**
     * Sprawdza czy komórka była już wyprodukowana
     * 
     * @param loc
     * @return
     */
    public boolean isCalculated(Location loc)
    {
        Cell firstTry = producedCells.get(loc);

        return firstTry != null && firstTry.isCalculated();
    }

    public void set(int col, int row, Cell cell)
    {
        producedCells.put(new Location(col, row), cell);
    }
}

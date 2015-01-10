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

import excel.Logger;
import excel.calc.Calculator;
import excel.main.StatusBar;
import excel.sheet.parser.Expression;
import excel.sheet.parser.Parser;
import excel.sheet.token.Tokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

/**
 * Model tabeli
 * 
 * @author eplightning <eplightning at outlook dot com>
 */
public class Model extends AbstractTableModel {

    protected ReentrantLock lock;
    protected HashMap<Location, Cell> cells;
    protected int rows;
    protected int columns;
    
    protected Logger logger;
    protected StatusBar statusBar;
    protected Calculator calc;
    
    public Model(Logger logger, StatusBar statusBar, Calculator calc)
    {
        cells = new HashMap<>();
        rows = 50;
        columns = 50;
        lock = new ReentrantLock();
        this.statusBar = statusBar;
        this.logger = logger;
        this.calc = calc;
    }
    
    public HashMap<Location, Cell> getCells()
    {
        return cells;
    }

    public ReentrantLock getLock()
    {
        return lock;
    }

    public void setRows(int rows)
    {
        if (rows > this.rows) {
            int old = this.rows;
            
            this.rows = rows;
        
            fireTableRowsInserted(old, rows - 1);
        }
    }

    public void setColumns(int columns)
    {
        // tylko obsługujemy rozszerzanie
        if (columns > this.columns) {
            int old = this.columns;
            
            this.columns = columns;
            
            fireTableStructureChanged();
        }
    }
    
    @Override
    public int getRowCount()
    {
        return rows;
    }

    @Override
    public int getColumnCount()
    {
        return columns;
    }
    
    @Override
    public String getColumnName(int i)
    {
        return Integer.toString(i + 1);
    }
    
    @Override
    public Object getValueAt(int i, int i1)
    {
        lock.lock();
        Cell out;
        
        try {
            out = cells.get(new Location(i1 + 1, i + 1));
        } finally {
            lock.unlock();
        }
        
        return out;
    }

    @Override
    public Class<?> getColumnClass(int i)
    {
        return Cell.class;
    }
    
    @Override
    public void setValueAt(Object o, int i, int i1)
    {
        lock.lock();
        
        try {
            Cell cell = new Cell();
            
            if (o instanceof Cell) {
                Cell o2 = (Cell) o;
                
                cell.setFormula(o2.getFormula());
            } else {
                cell.setFormula(o.toString());
            }
            
            cells.put(new Location(i1 + 1, i + 1), cell);
            
            startRecalculationThread();
        } finally {
            lock.unlock();
        }
    }

    public void startRecalculationThread()
    {
        (new RecalculationThread()).start();
    }
    
    @Override
    public boolean isCellEditable(int i, int i1)
    {
        return true;
    }
    
    protected class RecalculationThread extends Thread {

        @Override
        public void run()
        {
            lock.lock();
            
            try {
                statusBar.setState("Wątek przeliczenia arkusza w trakcie ...");
                
                // ustawiamy wszystkie wartości jako nieprzeliczone
                for (Cell cell : cells.values()) {
                    if (!cell.isOrdinaryText())
                        cell.setCalculated(false);
                }
                
                // Tokenizer i parser, CellAccessor
                Tokenizer tokenizer = new Tokenizer();
                Parser parser = new Parser();
                CellAccessor accessor = new CellAccessor(cells);
                
                // i po kolei ...
                for (Map.Entry<Location, Cell> entry : cells.entrySet()) {
                    // na szczęście nic do roboty
                    if (entry.getValue().isOrdinaryText() || accessor.isCalculated(entry.getKey()))
                        continue;
                    
                    // info
                    statusBar.setState(String.format("Wątek liczy komórkę $(%d, %d) ...", entry.getKey().getColumn(), entry.getKey().getRow()));
                    logger.addLine(String.format("Wątek liczy komórkę $(%d, %d) ...", entry.getKey().getColumn(), entry.getKey().getRow()));
                    
                    // Na kopii robimy
                    Cell newCell = new Cell(entry.getValue());
                    
                    // ustawiamy już stack
                    HashSet<Location> stack = new HashSet<>();
                    stack.add(entry.getKey());
                    
                    try {
                        ArrayList<Expression> expressions = parser.parse(tokenizer.tokenize(newCell.getFormula()));
                        
                        StringBuilder str = new StringBuilder();

                        ListIterator<Expression> iterator = expressions.listIterator();

                        while (iterator.hasNext()) {
                            str.append(iterator.next().evaluate(accessor, stack, calc));
                        }

                        String result = calc.calculateExpression(str.toString());

                        newCell.setValue(result);
                    } catch (Exception e) {
                        newCell.setValue("");
                        newCell.setError(e.getMessage());
                    }
                    
                    newCell.setCalculated(true);
                    
                    accessor.set(entry.getKey().getColumn(), entry.getKey().getRow(), newCell);
                }
                
                // i ostatnie
                statusBar.setState("Wątek wprowadza zmiany do tabeli ...");
                
                for (Map.Entry<Location, Cell> entry : accessor.getProducedCells().entrySet()) {
                    cells.put(entry.getKey(), entry.getValue());
                }
                
                // przemalowanie tabeli (w wątku Swinga)
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        fireTableDataChanged();
                    }
                });
                
                statusBar.setState("Gotowe");
            } finally {
                lock.unlock();
            }
        }
        
    }
}

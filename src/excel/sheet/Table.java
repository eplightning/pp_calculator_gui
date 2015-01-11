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

import excel.sheet.parser.Expression;
import excel.sheet.parser.Parser;
import excel.sheet.token.Tokenizer;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

/**
 * Rozszerzenie JTable dla arkusza
 *
 * @author eplightning <eplightning at outlook dot com>
 */
public class Table extends JTable {

    protected JTextField editorField;

    public Table()
    {
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setColumnSelectionAllowed(false);
        setFillsViewportHeight(true);
        setRowSelectionAllowed(true);
        setRowHeight(25);
        setAutoCreateRowSorter(false);
        getTableHeader().setReorderingAllowed(false);
        setTransferHandler(new DragDrop());
        setDropMode(DropMode.ON);
        setDragEnabled(true);

        editorField = new JTextField();
        setDefaultEditor(Cell.class, new CellEditor(editorField));
    }

    /**
     * Edytor komórek który bierze do edycji formułe a nie wartość
     */
    private class CellEditor extends DefaultCellEditor {

        public CellEditor(JTextField jtf)
        {
            super(jtf);
        }

        @Override
        public Component getTableCellEditorComponent(JTable jtable, Object o, boolean bln, int i, int i1)
        {
            if (o instanceof Cell) {
                Cell c = (Cell) o;
                return super.getTableCellEditorComponent(jtable, c.getFormula(), bln, i, i1);
            }

            return super.getTableCellEditorComponent(jtable, o, bln, i, i1); //To change body of generated methods, choose Tools | Templates.
        }

    }

    /**
     * Horror drag & dropa w Swingu
     */
    protected class DragDrop extends TransferHandler {

        @Override
        protected Transferable createTransferable(JComponent jc)
        {
            JTable tab = (JTable) jc;

            int col = tab.getSelectedColumn();
            int row = tab.getSelectedRow();

            if (col == -1 || row == -1) {
                return new StringSelection("");
            }

            Object out = tab.getModel().getValueAt(row, col);

            if (out == null) {
                return new StringSelection("");
            }

            return new LocationTransfer(col + 1, row + 1, out.toString());
        }

        @Override
        public int getSourceActions(JComponent jc)
        {
            return COPY;
        }

        @Override
        public boolean canImport(TransferSupport ts)
        {
            return !(!ts.isDataFlavorSupported(DataFlavor.stringFlavor) && !ts.isDataFlavorSupported(LocationTransfer.locFlavor));
        }

        @Override
        public boolean importData(TransferSupport ts)
        {
            int col;
            int row;

            // jeśli drag & drop to pobieramy lokalizacje z JTable
            // w przeciwnym razie mamy paste i po prostu bierzemy obecnie wybraną komórke
            if (ts.isDrop()) {
                JTable.DropLocation drop2 = (JTable.DropLocation) ts.getDropLocation();
                col = drop2.getColumn();
                row = drop2.getRow();
            } else {
                col = getSelectedColumn();
                row = getSelectedRow();
            }

            try {
                if (ts.isDataFlavorSupported(LocationTransfer.locFlavor)) {
                    Location loc = (Location) ts.getTransferable().getTransferData(LocationTransfer.locFlavor);

                    Object obj = getModel().getValueAt(loc.getRow() - 1, loc.getColumn() - 1);
                    Cell cell = (Cell) obj;

                    if (cell == null || cell.isOrdinaryText()) {
                        getModel().setValueAt(obj, row, col);
                    } else {
                        Tokenizer tokenizer = new Tokenizer();
                        Parser parser = new Parser();

                        ArrayList<Expression> expressions = parser.parse(tokenizer.tokenize(cell.getFormula()));

                        StringBuilder str = new StringBuilder(cell.getFormula().length());

                        str.append('=');

                        for (Expression expr : expressions) {
                            expr.relativeMove(col + 1 - loc.getColumn(), row + 1 - loc.getRow());
                            str.append(expr.evaluateAsFormula());
                        }

                        getModel().setValueAt(str.toString(), row, col);
                    }
                } else if (ts.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    getModel().setValueAt((String) ts.getTransferable().getTransferData(DataFlavor.stringFlavor), row, col);
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }

            return true;
        }

    }
}

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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.activation.ActivationDataFlavor;

/**
 * Adres komórki, do copy paste
 *
 * @author eplightning <eplightning at outlook dot com>
 */
public class LocationTransfer extends Location implements Transferable, ClipboardOwner {

    public static DataFlavor locFlavor = new ActivationDataFlavor(LocationTransfer.class, DataFlavor.javaJVMLocalObjectMimeType, "Location");

    protected String plainText;

    public LocationTransfer(int col, int row, String plainText)
    {
        super(col, row);
        this.plainText = plainText;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass() && obj.getClass() != Location.class) {
            return false;
        }

        final Location other = (Location) obj;

        if (this.column != other.column) {
            return false;
        }

        return this.row == other.row;
    }

    public String getPlainText()
    {
        return plainText;
    }

    public void setPlainText(String plainText)
    {
        this.plainText = plainText;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors()
    {
        DataFlavor[] flavors = new DataFlavor[3];

        flavors[0] = DataFlavor.stringFlavor;
        flavors[1] = locFlavor;

        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor df)
    {
        return df.equals(locFlavor) || df.equals(DataFlavor.stringFlavor);
    }

    @Override
    public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException
    {
        if (df.equals(locFlavor))
            return this;

        if (df.equals(DataFlavor.stringFlavor))
            return getPlainText();

        throw new UnsupportedFlavorException(df);
    }

    @Override
    public void lostOwnership(Clipboard clpbrd, Transferable t)
    {
    }

}

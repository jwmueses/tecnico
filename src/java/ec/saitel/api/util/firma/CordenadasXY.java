/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.saitel.api.util.firma;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;


/**
 *
 * @author wilso
 */
public class CordenadasXY extends PDFTextStripper {

    StringBuilder tWord = new StringBuilder();
    String seek;
    List wordList = new ArrayList();
    boolean is1stChar = true;
    boolean lineMatch;
    int pageNo = 1;
    double lastYVal;
    String clave = "";
    double pagina_x = 0;
    double pagina_y = 0;

    public CordenadasXY()
            throws IOException {
        super.setSortByPosition(true);
    }

    public List CordenadasXY(File input, String palabra) throws IOException {
        super.setSortByPosition(true);
        PDDocument document = null;
        try {
            this.seek = palabra;
            document = PDDocument.load(input);
            List allPages = document.getDocumentCatalog().getAllPages();

            for (int i = 0; i < allPages.size(); i++) {
                PDPage page = (PDPage) allPages.get(i);
                PDStream contents = page.getContents();
                if (contents != null) {
                    pagina_x = page.getMediaBox().getWidth();
                    pagina_y = page.getMediaBox().getHeight();
                    processStream(page, page.findResources(), page.getContents().getStream());
                }
                pageNo += 1;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (document != null) {
                document.close();
            }
        }
        return wordList;
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        String tChar = text.getCharacter();
        String REGEX = "[,.\\[\\](:;!?)/]";
        char c = tChar.charAt(0);
        lineMatch = matchCharLine(text);
        if ((!tChar.matches(REGEX)) && (!Character.isWhitespace(c))) {
            if ((!is1stChar) && (lineMatch == true)) {
                appendChar(tChar);
            } else if (is1stChar == true) {
                setWordCoord(text, tChar);
            }
        } else {
            endWord();
        }

    }

    protected void appendChar(String tChar) {
        tWord.append(tChar);
        is1stChar = false;
    }

    protected void setWordCoord(TextPosition text, String tChar) {
        tWord.append(roundVal(Float.valueOf(text.getXDirAdj()))).append(":").append(roundVal(Float.valueOf(text.getYDirAdj()))).append(" ").append(tChar);
        is1stChar = false;
    }

    protected void endWord() {
        String newWord = tWord.toString().replaceAll("[^\\x00-\\x7F]", "");
        String sWord = newWord.substring(newWord.lastIndexOf(' ') + 1);
        if (!"".equals(sWord)) {
            if (Arrays.asList(seek).contains(sWord)) {
                newWord = newWord.substring(0, newWord.lastIndexOf(' ')) + ":" + pageNo + ":" + pagina_x + ":" + pagina_y;
                wordList.add(newWord);
            }
        }
        tWord.delete(0, tWord.length());
        is1stChar = true;
    }

    protected boolean matchCharLine(TextPosition text) {
        Double yVal = roundVal(Float.valueOf(text.getYDirAdj()));
        if (yVal.doubleValue() == lastYVal) {
            return true;
        }
        lastYVal = yVal.doubleValue();
        endWord();
        return false;
    }

    protected Double roundVal(Float yVal) {
        DecimalFormat rounded = new DecimalFormat("0.0'0'");
        Double yValDub = new Double(rounded.format(yVal).replaceAll(",", "."));
        return yValDub;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

}

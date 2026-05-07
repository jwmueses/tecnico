/*
 * Copyright 2009-2018 Rubrica
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ec.saitel.api.util.firma;

import com.itextpdf.text.Rectangle;
import java.util.Properties;
import java.util.logging.Logger;

public class PdfUtil {

    public static final String positionOnPageLowerLeftX = "PositionOnPageLowerLeftX";
    public static final String positionOnPageLowerLeftY = "PositionOnPageLowerLeftY";
    public static final String positionOnPageUpperRightX = "PositionOnPageUpperRightX";
    public static final String positionOnPageUpperRightY = "PositionOnPageUpperRightY";
    public static int ancho = 150;
    public static int largo = 50;
    private static final Logger logger = Logger.getLogger(PdfUtil.class.getName());

    public static Rectangle getPositionOnPage(Properties extraParams) {
        if (extraParams == null) {
            logger.severe("Se ha pedido una posicion para un elemento grafico nulo");
            return null;
        }

        if (extraParams.getProperty(positionOnPageLowerLeftX) != null
                && extraParams.getProperty(positionOnPageLowerLeftY) != null
                && extraParams.getProperty(positionOnPageUpperRightX) != null
                && extraParams.getProperty(positionOnPageUpperRightY) != null) {
            try {
//                return new Rectangle(Float.parseFloat(extraParams.getProperty(positionOnPageLowerLeftX).trim()),
//                        Float.parseFloat(extraParams.getProperty(positionOnPageLowerLeftY).trim()),
//                        Float.parseFloat(extraParams.getProperty(positionOnPageUpperRightX).trim()),
//                        Float.parseFloat(extraParams.getProperty(positionOnPageUpperRightY).trim()));
                return new Rectangle(Float.parseFloat(extraParams.getProperty(positionOnPageLowerLeftX).trim()),
                        Float.parseFloat(extraParams.getProperty(positionOnPageLowerLeftY).trim()),
                        (Float.parseFloat(extraParams.getProperty(positionOnPageLowerLeftX).trim()) + Float.parseFloat(extraParams.getProperty(positionOnPageUpperRightX).trim())),
                        (Float.parseFloat(extraParams.getProperty(positionOnPageLowerLeftY).trim()) - Float.parseFloat(extraParams.getProperty(positionOnPageUpperRightY).trim())));
            } catch (final Exception e) {
                logger.severe("Se ha indicado una posicion invalida para la firma: " + e);
            }
        }

        if (extraParams.getProperty(positionOnPageLowerLeftX) != null
                && extraParams.getProperty(positionOnPageLowerLeftY) != null
                && extraParams.getProperty(positionOnPageUpperRightX) == null
                && extraParams.getProperty(positionOnPageUpperRightY) == null) {
            try {
                return new Rectangle(Float.parseFloat(extraParams.getProperty(positionOnPageLowerLeftX).trim()),
                        Float.parseFloat(extraParams.getProperty(positionOnPageLowerLeftY).trim()),
                        Float.parseFloat(extraParams.getProperty(positionOnPageLowerLeftX).trim()) + ancho,
                        Float.parseFloat(extraParams.getProperty(positionOnPageLowerLeftY).trim()) - largo);
            } catch (final Exception e) {
                logger.severe("Se ha indicado una posicion invalida para la firma: " + e);
            }
        }

        return null;
    }
}

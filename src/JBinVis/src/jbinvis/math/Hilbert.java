/*
 *  
 */
package jbinvis.math;

/**
 *
 * @author Billy
 */
public class Hilbert {

    private static Hilbert _singleton = null;

    public static Hilbert getInstance() {
        if (_singleton == null) {
            _singleton = new Hilbert();
        }
        return _singleton;
    }

    /**
     * Convert from distance to y*512+x
     */
    public static int xy(int d) {
        return getInstance().d2xy[d];
    }

    private final int MAX = 512 * 512;
    private final int[] d2xy; // conversion table

    private Hilbert() {
        d2xy = new int[MAX];

        // populate table
        for (int i = 0; i < MAX; i++) {
            d2xy[i] = convertDtoXY(i);
        }
    }

    // Adapted from 
    //   https://en.wikipedia.org/wiki/Hilbert_curve

    private int convertDtoXY(int d) {
        int rx, ry, s, t = d, temp;
        int _x = 0,  _y = 0;
        
        for (s = 1; s < 512; s <<= 1) {
            ry = 1 & (t >> 1);
            rx = 1 & (t ^ ry);

            if (rx == 0) {
                if (ry == 1) {
                    _x = s - 1 - _x;
                    _y = s - 1 - _y;
                }
                temp = _x;
                _x = _y;
                _y = temp;
            }

            _x += s * rx;
            _y += s * ry;
            t >>= 2;
        }
        return _x + _y * 512;
    }
}

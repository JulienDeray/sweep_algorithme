/**
 * Created with IntelliJ IDEA.
 * Autor: julienderay
 * Company : SERLI
 * Date: 13/10/14
 * Time: 09:00
 */
package fr.emn.fil;

public class Algorythm {

    /**
     *
     * @param rj Region du rectangle déjà placé, tous les attributs sont connus
     * @param ri Region du rectangle à placer, on ne connait que les width et height
     * @return Forbidden region
     */
    public static Region computeForbiddenRegion(PlacementRegion rj, PlacementRegion ri) {
        int xMin = rj.getxMax() - ri.getWidth() + 1;
        int xMax = rj.getxMin() + rj.getWidth() - 1;
        int yMin = rj.getyMax() - ri.getHeight() + 1;
        int yMax = rj.getyMin() + rj.getHeight() - 1;

        return new Region(xMin, xMax, yMin, yMax);
    }
}

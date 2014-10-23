/**
 * Created with IntelliJ IDEA.
 * Autor: julienderay
 * Company : SERLI
 * Date: 20/10/14
 * Time: 12:37
 */
package fr.emn.fil.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Domain {
    private int x;
    private int y;

    private List<Constraint> constraints;

    public Domain(int y, int x) {
        this.y = y;
        this.x = x;
        this.constraints = new ArrayList<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void add(Constraint constraint) {
        constraints.add(constraint);
    }

    /**
     * Permet de créer la liste des évènements (débuts et fins des forbidden régions)
     *
     * @param rectangle
     * @return la liste d'évènements
     * @throws Exception
     */
    public Position findMinimum(Constraint rectangle) throws Exception {
        // Liste des régions interdites du rectangle
        List<ForbiddenRegion> forbiddenRegions = getForbiddenRegionsFor( rectangle );
        // Liste qui contient les évènements
        List<Event> qEvent = new ArrayList<Event>();

        //Nous cherchons à trouver le minimum dans le domaine possible de la contrainte
        for ( int delta = rectangle.getxMin(); delta < rectangle.getxMax(); delta++ ) {
            //Nous faisons une itération sur les ForbiddenRegions
            for (ForbiddenRegion forbiddenRegion : forbiddenRegions) {
                //Si il y un début ou fin de forbidden région sur ce delta, alors nous créons un Event correspondant
                if( delta == forbiddenRegion.getxMin() || delta == forbiddenRegion.getxMax()){
                    Event event = new Event(delta, forbiddenRegion.getyMin(), forbiddenRegion.getyMax());
                    qEvent.add(event);
                }
            }
        }

        //A présent que les évènements sont définis, nous cherchons à trouver l'emplacement libre minimum
        // Vecteur associé à une colonne, indiquant les cases bloquées par des régions interdites
        Integer[] pStatus = new Integer[this.y];
        //La coordonnée en abcisse du minimum
        Integer delta = -1;
        //Liste des cases disponibles dans la colonne delta
        List[] availablesY = new ArrayList[]();
        //Nous parcourons chaque colonne, puis chaqaue case pour trouver les emplacements libres
        do{
            //Initialise chaque case de la colonne à 0
            pStatus.initialize();
            //Nous allons à la colonne suivante
            delta++;
            //Nous remplissons le pStatus pour connaître les emplacements libres de la colonne
            pStatus = handleEvent(delta, pStatus, qEvent);

            //Nous vérifions dans chaque case du PStatus s'il y a un/des emplacements
            for (int i = 0; i < pStatus.length; i++) {
                Integer pStatu = pStatus[i];
                if(pStatu.equals(0)){
                    availablesY.add(i);
                }
            }
        }while (0 == availablesY.length());

        //On random la valeur de y par rapport aux y disponibles
        Integer yMinimum = randomY(availablesY);

        //Nous retournons le minimum
        return new Position(delta,yMinimum);

    }

    /**
     * Remplit le pStatus avec le nombre de régions interdites, par case (0,1,2,etc)
     *
     * @param positionX la colonne à étudier
     * @param pStatus où indiquer les emplacements libres ou occupés
     * @param events les évènements de début et fins de forbidden régions
     * @return le pStatus, contient les emplacements libres ou occupés de la colonne delta
     * @throws Exception
     */
    public Integer[] handleEvent(Integer positionX, Integer[] pStatus, List<Event> events) throws Exception {

    }


    /**
     * TODO a supprimer
     *
     * @param yMax
     * @param forbiddenRegions
     * @return
     */
    private static Integer[] initQevent(int yMax, List<ForbiddenRegion> forbiddenRegions) {
        Integer[] qEvent = new Integer[yMax];

        for (int y = 0; y < qEvent.length; y++) {
            qEvent[y] = 1;
        }
        for (ForbiddenRegion forbiddenRegion : forbiddenRegions) {
            for ( int y = forbiddenRegion.getyMin(); y < forbiddenRegion.getyMax(); y++ ) {
                qEvent[y] = 0;
            }
        }

        return qEvent;
    }

    /**
     * TODO a supprimer
     *
     * @param qEvent
     * @return
     */
    private List<Integer> find0(Integer[] qEvent) {
        List<Integer> res = new ArrayList<>();
        for (int y = 0; y < qEvent.length; y++) {
            Integer integer = qEvent[y];

            if ( integer == 0 ) {
                res.add(y);
            }
        }
        return res;
    }

    /**
     * TODO a supprimer
     *
     * @param forbiddenRegions
     * @param qEvent
     * @param delta
     * @return
     */
    private static Integer[] fillQevent(List<ForbiddenRegion> forbiddenRegions, Integer[] qEvent, int delta) {
        for (ForbiddenRegion forbiddenRegion : forbiddenRegions) {
            if ( forbiddenRegion != null && forbiddenRegion.getxMin() <= delta && forbiddenRegion.getxMax() >= delta ) {
                for ( int involvedY = forbiddenRegion.getyMin(); involvedY < forbiddenRegion.getyMax(); involvedY++ ) {
                    qEvent[involvedY] += 1;
                }
            }
        }
        return qEvent;
    }

    /**
     * Calcule puis retourne la liste des régions interdites du rectangle par rapport aux contraintes
     *
     * @param rectangle le rectangle que l'on veut placer.
     * @return la liste des régions interdites
     */
    private List<ForbiddenRegion> getForbiddenRegionsFor(Constraint rectangle) {
        List<ForbiddenRegion> forbiddenRegions = new ArrayList<>();

        for (Constraint c : constraints) {
            ForbiddenRegion forbiddenRegion = computeForbiddenRegion(c, rectangle.getWidth(), rectangle.getHeight());
            if ( forbiddenRegion != null ) {
                forbiddenRegions.add( forbiddenRegion );
            }
        }

        return forbiddenRegions;
    }

    /**
     *Permet de calculer la région interdite d'un rectangle dont on connait la hauteur et largeur par rapport à une contrainte rJ
     *
     * @param rj Constrainte à prendre en compte pour placer le rectangle
     * @param width Largeur du rectangle à placer (ri)
     * @param height Hauteur du rectangle à placer (ri)
     * @return La région interdite de ri par rapport à rj
     */
    public static ForbiddenRegion computeForbiddenRegion(Constraint rj, int width, int height) {

        int xMin = rj.getxMax() - width + 1;
        int xMax = rj.getxMin() + rj.getWidth() - 1;
        int yMin = rj.getyMax() - height + 1;
        int yMax = rj.getyMin() + rj.getHeight() - 1;

        if (xMin > xMax || yMin > yMax) {
            return null;
        }
        else {
            return new ForbiddenRegion(xMin, xMax, yMin, yMax);
        }
    }
}

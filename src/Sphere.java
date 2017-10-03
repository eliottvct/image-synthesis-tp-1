/**
 * Cette classe représente une sphère à dessiner
 */
public class Sphere
{
    // note: toutes ces variables sont publiques afin de ne pas recourir à des getters

    /// coordonnées du centre
    protected Point centre = new Point();

    /// demi-diamètre
    protected float rayon = 0.0f;

    /// couleur diffuse
    protected Couleur Kd = new Couleur();

    /// couleur réfléchie
    protected Couleur Ks = new Couleur();
    protected float Ns = 0.0f;


    /**
     * constructeur par défaut
     */
    public Sphere()
    {
    }


    /**
     * constructeur
     * @param centre
     * @param rayon
     */
    public Sphere(Point centre, float rayon)
    {
        this.centre = centre;
        this.rayon = rayon;
    }


    /**
     * calcule la distance du point d'intersection entre this et le rayon
     * ne renvoie pas de point situé "derrière" le rayon
     * @param incident
     * @return Constantes.INFINI si pas d'intersection correcte
     */
    public float Intersection(Rayon incident)
    {
        Vecteur CP = new Vecteur(centre, incident.P);

        float mB = -incident.V.dot(CP);
        float C = CP.dot(CP) - rayon*rayon;

        // résoudre en k
        float delta = mB*mB - C;
        if (delta <= 0.0f) return Constantes.INFINI;

        float racdelta = (float) Math.sqrt(delta);

        float d1 = mB-racdelta;
        float d2 = mB+racdelta;

        // ignorer les contacts en arrière
        if (d1 <= Constantes.EPSILON) d1 = Constantes.INFINI;
        if (d2 <= Constantes.EPSILON) d2 = Constantes.INFINI;

        // choisir la plus petite des deux racines
        if (d2 < d1) return d2; else return d1;
    }


    /**
     * calcule la couleur de la sphère au point désigné par incident
     * si la sphère est réfléchissante, on peut relancer au maximum
     * profondeur rayons indirects
     * @param scene
     * @param incident
     * @param profondeur
     * @return
     */
    public Couleur Phong(final Scene scene, final Rayon incident, int profondeur)
    {
        // couleur finale = somme des contributions des lampes
        Couleur finale = new Couleur(0,0,0);
        Couleur kdMod = Kd;
        Couleur ksMod = Ks;

        // calculer la normale en ce point
        Vecteur N = new Vecteur(centre, incident.contact);
        if (!N.normaliser()) return new Couleur(1,0,0);


        // obtenir les coordonées sphériques du point de contact
        float longitude = (float) Math.atan2(N.z, N.x);    //-pi..+pi
        float latitude = (float) Math.asin(N.y);   //-pi/2..+pi/2

        // convertir les coordonnées
        longitude = (float) (longitude / Math.PI * 8.0);
        latitude = (float) (latitude / Math.PI * 8.0);









        // DAMIER
        /*
        float longitude_entiere = (float) Math.floor(longitude);
        float latitude_entiere = (float) Math.floor(latitude);

        if ((longitude_entiere + latitude_entiere) % 2 == 0) {
            kdMod = Constantes.POIS_COULEUR;
            ksMod = Ks.mul(0.1f);
        }
        */


        // POIS ET CARRÉS
        /*
        // centre du pois le plus proche
        float longitude_pois = (float) Math.round(longitude);
        float latitude_pois = (float) Math.round(latitude);

        // carré de la distance entre contact et le POIS
        float dPois =
                (longitude-longitude_pois)*(longitude-longitude_pois) +
                (latitude-latitude_pois)*(latitude-latitude_pois);


        // carrés
        // carré de la distance entre contact et le CARRÉ
        final float n = 3.5f;
        float dCarre = (float) Math.pow(
                Math.pow(Math.abs(longitude-longitude_pois), n) +
                Math.pow(Math.abs(latitude-latitude_pois), n), 1.0/n
        );

        // selon la distance, définir les couleurs Kd et Ks
        // si pas dans un point
        //if (dPois >= 0.1f) {
        if (dCarre >= 0.3f) {
            kdMod = Constantes.POIS_COULEUR;
            ksMod = Ks.mul(0.1f);
        }
        */

        // BOSSES
        // centre du pois le plus proche
        float longitude_pois = (float) Math.round(longitude);
        float latitude_pois = (float) Math.round(latitude);

        // carré de la distance entre contact et le POIS
        float dPois =
                (longitude-longitude_pois)*(longitude-longitude_pois) +
                        (latitude-latitude_pois)*(latitude-latitude_pois);

        // selon la distance, altérer ou non le vecteur N
        // si pas dans un point
        if (dPois < 0.1f) {

            // retour dans le domaine -pi..+pi (revenir à des angles en radians)
            longitude_pois = (float) (longitude_pois * Math.PI / 8.0);
            latitude_pois = (float) (latitude_pois * Math.PI / 8.0);

            // coordonnées cartésiennes du pois
            float x = (float) (rayon * Math.cos(latitude_pois) * Math.cos(longitude_pois));
            float y = (float) (rayon * Math.sin(latitude_pois));
            float z = (float) (rayon * Math.sin(latitude_pois) * Math.sin(longitude_pois));

            // centre du pois à la surface de la sphère, en coordonnées globales
            Point pois_surface = new Point(x + centre.x, y + centre.y, z + centre.z);

            // coordonnées 3D du centre de la bosse
            Vecteur V = new Vecteur(centre, pois_surface).mul(1.2f);
            Point pois_normale = centre.add(V);

            // modifier N : le faire pencher hors du centre du pois
            N = new Vecteur(pois_normale, incident.contact);
            if (!N.normaliser()) return new Couleur(1,0,0);
        }







        // produit scalaire N.V attention V est inversé
        float psNV = -N.dot(incident.V);

        // reflet de la vue
        Vecteur Rv = N.mul(2.0f*psNV).add(incident.V);

        //// éclairements des lampes
        for (Lampe lampe: scene.getLampes()) {

            // calculer la lumière en ce point
            Vecteur L = new Vecteur(incident.contact, lampe.getPosition());
            if (!L.normaliser()) continue;

            // produit scalaire N.L
            float psNL = N.dot(L);

            // test d'ombrage
            if (psNL <= 0.0) continue;

            // l'objet est-il dans l'ombre d'un autre ?
            Rayon r2 = new Rayon(lampe.getPosition(), incident.contact);
            r2.P = lampe.getPosition(); // sinon c'est contact !
            if (scene.ChercherIntersection(r2, null)) {
                if (r2.objet != this) continue;
            }

            // ajouter la contribution diffuse
            finale = finale.add( lampe.getCouleur().mul(kdMod).mul(psNL) );

            /// éclairement spéculaire de Phong

            // on a déjà calculé Rv = le reflet de V % N

            // angle entre Rv et L
            float psRvL = Rv.dot(L);
            if (psRvL > 0) {
                finale = finale.add(
                        lampe.getCouleur()
                                .mul(ksMod)
                                .mul((float) Math.pow(psRvL, Ns)) );
            }
        }

        // reflets des autres objets
        if (profondeur > 0) {

            //// calcul du rayon réfléchi dans les autres objets

            // lancer un rayon partant de p, allant vers Rv
            Rayon r3 = new Rayon(incident.contact, Rv);
            Couleur couleurReflexion;

            if (scene.ChercherIntersection(r3, this)) {
                // chercher la couleur Phong de ce contact
                couleurReflexion = r3.objet.Phong(scene, r3, profondeur-1);
            } else {
                couleurReflexion = r3.Ciel();
            }

            // couleurReflexion = incident.Brume(couleurReflexion);

            // ajouter la contribution du reflet modulée par Ks
            finale = finale.add(couleurReflexion.mul(ksMod));
        }

        return finale;
    }


    public Point getCentre()
    {
        return centre;
    }


    public void setCentre(final Point centre)
    {
        this.centre = centre;
    }


    public float getRayon()
    {
        return rayon;
    }


    public void setRayon(float rayon)
    {
        this.rayon = rayon;
    }


    public Couleur getKd()
    {
        return Kd;
    }


    public void setKd(final Couleur kd)
    {
        Kd = kd;
    }


    public Couleur getKs()
    {
        return Ks;
    }


    public void setKs(final Couleur ks)
    {
        Ks = ks;
    }


    public float getNs()
    {
        return Ns;
    }


    public void setNs(float ns)
    {
        Ns = ns;
    }
}

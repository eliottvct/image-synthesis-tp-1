/**
 * Cette classe définit toutes les constantes utilisées dans l'application
 */
public final class Constantes
{
    // distance maximale (dépend de la précision des nombres)
    public static final float INFINI = 1e38f;

    // distance minimale (dépend de la taille de la scène)
    public static final float EPSILON = 1e-5f;

    // caméra
    public static final float DISTECRAN = 10.0f;
    public static final float CHAMP = 0.25f;

    // scène à dessiner dans le canvas
    public static final String NOM_SCENE = "scenes/scene2.txt";

    // dimensions (initiales) et nom du fichier PNG de sortie
    public static final int LARGEUR_IMAGE = 800;
    public static final int HAUTEUR_IMAGE = 600;
    public static final String NOM_IMAGE = "image.png";

    // nombre de rayons réfléchis successifs
    public static final int MAX_REFLETS = 5;

    // brume
    public static final float BRUME_DENSITE = 30.0f;
    public static final Couleur BRUME_COULEUR = new Couleur(0.8, 0.8, 0.8);

    // antialiasing
    public static final float ANTIALISING_PRECISION = 3.0f;

    // textures
    public static final Couleur POIS_COULEUR = new Couleur(0.8, 0.8, 0.8);

}

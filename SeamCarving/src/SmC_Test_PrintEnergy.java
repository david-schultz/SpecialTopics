/******************************************************************************
 *  Compilation:  javac PrintEnergy.java
 *  Execution:    java PrintEnergy input.png
 *  Dependencies: SeamCarver.java
 *                
 *
 *  Read image from file specified as command line argument. Print energy
 *  of each pixel as calculated by SeamCarver object. 
 * 
 ******************************************************************************/


public class SmC_Test_PrintEnergy {

    public static void main(String[] args) {
        SmC_Picture picture = new SmC_Picture("testInput/6x5.png");
        StdOut.printf("image is %d pixels wide by %d pixels high.\n", picture.width(), picture.height());
        
        SeamCarver sc = new SeamCarver(picture);
        
        StdOut.printf("Printing energy calculated for each pixel.\n");     
		
        for (int j = 0; j < sc.height(); j++) {
            for (int i = 0; i < sc.width(); i++)
                StdOut.printf("%9.0f ", sc.energy(i, j));
            	//StdOut.printf("%9.0f ", sc.previous(j, i));
            StdOut.println();
        }
        StdOut.println();
        
        StdOut.println("Printing distance calculated for each pixel.");
        sc.findVerticalSeam();
        for (int j = 0; j < sc.height(); j++) {
            for (int i = 0; i < sc.width(); i++) {
                //StdOut.printf("%9.0f ", sc.dist(i, j));
            
            }
            StdOut.println();
        }
        StdOut.println();
        
        StdOut.println("Printing prev calculated for each pixel.");
        for (int j = 0; j < sc.height(); j++) {
            for (int i = 0; i < sc.width(); i++) {
                //StdOut.printf("%9.0f ", sc.pre(i, j));
            
            }
            StdOut.println();
        }
        
        
        
    }
    
	/*
    public static void main(String[] args) {
        SmC_Picture picture = new SmC_Picture("testInput/6x5.png");
        StdOut.printf("image is %d pixels wide by %d pixels high.\n", picture.width(), picture.height());
        
        SeamCarver sc = new SeamCarver(picture);
        
        StdOut.printf("Printing distTo calculated for each pixel.\n");        

        for (int j = 0; j < sc.height(); j++) {
            for (int i = 0; i < sc.width(); i++)
                StdOut.printf("%9.0f ", sc.du(j, i));
            StdOut.println();
        }
    }
	*/

}

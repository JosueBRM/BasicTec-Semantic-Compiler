/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: AGO-DIC/2013     HORA: xx - xx HRS
 *:                                   
 *:               
 *:    # Clase que implementa la funcionalidad del Manejador de Errores del Compilador.
 *                 
 *:                           
 *: Archivo       : ManejErrores.java
 *: Autor         : Fernando Gil  
 *: Fecha         : 03/SEP/2013
 *: Compilador    : Java JDK 7
 *: Descripción   : Esta clase recibe la invocacion de alguno de sus metodos 
 *:                 error ()  y transfiere la llamada a la Interfaz de Usuario.
 *:                  
 *:           	     
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *:-----------------------------------------------------------------------------
 */



package compilador;

public class ManejErrores {

    private Compilador compilador;
    private int        totErrLexico      = 0;
    private int        totErrSintacticos = 0;
    private int        totErrSemanticos  = 0;
    private int        totErrCodInt      = 0;
    private int        totErrCodObj      = 0;
    private int        totWarningsSem    = 0;
    
    //--------------------------------------------------------------------------
    
    public ManejErrores ( Compilador c ) {
        compilador = c;
    }

    //--------------------------------------------------------------------------
	
	//--------------------------------------------------------------------------
    public void inicializar () {
        totErrLexico = totErrSintacticos = totErrSemanticos = totErrCodInt = 0;
        totWarningsSem = 0;
    }
    //--------------------------------------------------------------------------
    public int getTotErrLexico      () { return totErrLexico;      }
    //--------------------------------------------------------------------------
    public int getTotErrSintacticos () { return totErrSintacticos; }
    //--------------------------------------------------------------------------
    public int getTotErrSemanticos  () { return totErrSemanticos;  }
    //--------------------------------------------------------------------------
    public int getTotErrCodInt      () { return totErrCodInt;      }
    //--------------------------------------------------------------------------
    public int getTotErrCodObj      () { return totErrCodObj;      }
    //--------------------------------------------------------------------------
    public int getTotWarningsSem    () { return totWarningsSem;    }
    //--------------------------------------------------------------------------
    
    public void error ( int tipoError, String errorMensaje ) {
      // Contabilizar el error
        switch  ( tipoError ) {
           case Compilador.ERR_LEXICO      : totErrLexico++; 
		                             errorMensaje = "ERROR LEXICO: " + errorMensaje;  
                                             break;
           case Compilador.ERR_SINTACTICO  : totErrSintacticos++;
		                             errorMensaje = "ERROR SINTACTICO: " + errorMensaje; 
                                             break;
           case Compilador.ERR_SEMANTICO   : totErrSemanticos++;
		                             errorMensaje = "ERROR SEMANTICO: " + errorMensaje; 
                                             break;
           case Compilador.ERR_CODINT      : totErrCodInt++;
		                             errorMensaje = "ERROR COD. INT.: " + errorMensaje; 
                                             break;
           case Compilador.ERR_CODOBJ      : totErrCodObj++;
		                             errorMensaje = "ERROR COD. OBJ.: " + errorMensaje; 
                                             break;                                             
           case Compilador.WARNING_SEMANT  : totWarningsSem++;
		                             errorMensaje = "WARNING SEMANTICO: " + errorMensaje; 
                                             break;
		}

        if ( tipoError == Compilador.WARNING_SEMANT )
            // Manejo de Warnings
            compilador.iuListener.mostrarWarning ( errorMensaje );
        else
            // Invocar el despliegue del error a la GUI
            compilador.iuListener.mostrarErrores ( errorMensaje );
    }
    
    //--------------------------------------------------------------------------
}

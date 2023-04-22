/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:        SEMESTRE: ______________            HORA: ______________ HRS
 *:                                   
 *:               
 *:    # Clase que implementa la Tabla de Simbolos del Compilador
 *                 
 *:                           
 *: Archivo       : TablaSimbolos.java
 *: Autor         : Fernando Gil  
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   : 
 *:                  
 *:           	     
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *:-----------------------------------------------------------------------------
 */



package compilador;
import java.util.Vector;
import java.util.ArrayList;
import general.Linea_TS;

public class TablaSimbolos 
{
    private Vector < Linea_TS > arrTabla = null;
    private int                 intInd;
    private Compilador          compilador;
    
    //--------------------------------------------------------------------------
    
    public TablaSimbolos( Compilador c )
    {
        arrTabla   = new Vector < Linea_TS > ( );
        intInd     = 0;
        compilador = c;
    }
    
    //--------------------------------------------------------------------------
    
    public void inicializar()
    {
        arrTabla = new Vector < Linea_TS > ( );
        intInd   = 0;
    }
    
    //--------------------------------------------------------------------------
    
    public int insertar( Linea_TS l )
    {
        int entrada;
        if ( ( entrada = buscar ( l.getLexema (), l.getAmbito () ) )  > 0  )
            return entrada;
        else {
            arrTabla.add( l );
            return intInd++;
        }
    }
    
    //--------------------------------------------------------------------------
    
    public Linea_TS obt_elemento( int n )
    {
        return arrTabla.get( n );
    }
    
    //--------------------------------------------------------------------------
    
    public void  anadeTipo( int p , String t )
    {
        arrTabla.get( p ).setTipo( t );
    }
    
    //--------------------------------------------------------------------------
    
    public String buscaTipo( int n )
    {
        return arrTabla.get( n ).getTipo( );
    }
    
    //--------------------------------------------------------------------------
    
    public int buscar( String lex )
    {
        
        for( int i = 0 ; i < arrTabla.size( ) ; i++ )
            if( arrTabla.get( i ).getLexema( ).equals( lex ) )
                return i; //+ 1;
                return 0;
    }
       
    //--------------------------------------------------------------------------
    
    public int getTamaño( ){
        return arrTabla.size( );
    }

    //--------------------------------------------------------------------------

    public void anadeAmbito ( int p, String t )
    {
        arrTabla.get( p ).setAmbito( t );
    }

    //--------------------------------------------------------------------------

    public String buscaAmbito ( int n )
    {
        return arrTabla.get( n ).getAmbito ( );
    }

    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------
    // 07/DIC/2012:
    // Busca en la Tabla de SImbolos por un lexema y un ambito, devuelve la
    // posicion si lo encuentra sino devuelve cero.

    public int buscar ( String lex, String amb )
    {

        for( int i = 0 ; i < arrTabla.size( ) ; i++ )
            if (  arrTabla.get( i ).getLexema( ).equals( lex ) &&
                  arrTabla.get( i ).getAmbito( ).equals( amb )  )
                return i;
        return 0;
    }

    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------
    // Busca el lexema segun su posicion
    //
    public String buscaLexema ( int pos )
    {
        return arrTabla.get ( pos ).getLexema ( );
    }
    // Fin de buscaLexema
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------
    // Devuelve una lista de todas las entradas que sean num, numnum y literal
    // para asignarles tipo
    //
    public ArrayList sinTipo ( )
    {
        // Creamos la lista
        ArrayList ids = new ArrayList ( );

        // Revisar
        for ( int i = 0; i < arrTabla.size ( ); i++ )
            if ( arrTabla.get ( i ).getComplex ( ).equals ( "num" ) ||
                 arrTabla.get ( i ).getComplex ( ).equals ( "numnum" ) ||
                 arrTabla.get ( i ).getComplex ( ).equals ( "literal" ) )
                ids.add ( i ); // Agregamos la entrada de la tabla simbolos

        // Devolvemos la lista
        return ids;
    }
    // Fin de sinTipo
    //--------------------------------------------------------------------------
}

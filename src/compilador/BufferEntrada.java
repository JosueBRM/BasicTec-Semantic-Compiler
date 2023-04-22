/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:        SEMESTRE: ______________            HORA: ______________ HRS
 *:                                   
 *:               
 *:	       # Clase que representa al Buffer de entrada del compilador *                 
 *:                           
 *: Archivo       : BufferEntrada.java
 *: Autor         : Fernando Gil   
 *: 
 *: Fecha         : 03/SEP/2013
 *: Compilador    : Java JDK 7
 *: Descripción   : Clase Buffer de entrada en la que se declararan los metodos necesarios para 
 *:                 realizar el inicializado, insertado y moverse dentro de el buffer tambien se declaran   
 *:           	    las variables a utilizar y el vector que sera necesario para formar nuestro buffer. 
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 22/Feb/2015 FGil               -Se crearon setPtr() y getPrt (). Cada fila del  
 *:                                 buffer tiene ahora 4 columnas, la 4a es el numero
 *:                                 de renglon del codigo fuente.
 *: 08/Sep/2015 FGil               -Se agrego el metodo anterior ().
 *:-----------------------------------------------------------------------------
 */


package compilador;
import java.util.Vector;
import general.Linea_BE;

public class BufferEntrada {
    
/*------------------------------------------------------------------------------------------------------------*/
 
//Declaracion de vector y variables que se utilizaran    
     
        private int UltimoElemento;       
        private Vector<Linea_BE> Lis_BE;
        public Linea_BE preAnalisis;
        private int Ptr; 
        Compilador compilador;
/*------------------------------------------------------------------------------------------------------------*/
//Constructor de Buffer de entrada que recibe una variable del tipo Compilador
        
        public BufferEntrada (Compilador c)
        {
            compilador = c;
            UltimoElemento = 0;
            Lis_BE = new Vector<Linea_BE>();            
            preAnalisis = new Linea_BE( );
            Ptr = 0;              
        }
/*------------------------------------------------------------------------------------------------------------*/
    
        public void setprt(int PTR)
        {
            Ptr = PTR;
        }
        
/*------------------------------------------------------------------------------------------------------------*/
       
        public int getptr()
        {
            return Ptr;
        }
        
/*------------------------------------------------------------------------------------------------------------*/
//Metodo que inicializa nuestro buffer de entrada
        
        public void inicializar()
        {
            UltimoElemento = 0;
            Lis_BE.clear();
            Lis_BE.add(new Linea_BE("$", "$", 0, 999));     // FGil: 4o argumento es el num de linea en codigo fuente
            Ptr = 0;
            preAnalisis = obtElemento(Ptr);
        }
/*------------------------------------------------------------------------------------------------------------*/
//Metodo que inserta el elemento en la linea del buffer 
        public void insertar(Linea_BE objTL)
        {
            //Lis_BE.add(Lis_BE.elementAt(UltimoElemento));//agregamos linea de vector
            Lis_BE.add(UltimoElemento, objTL); 
            UltimoElemento++;             
        }
/*------------------------------------------------------------------------------------------------------------*/
// Metodo para continuar con la siguiente linea del buffer
        
        public void siguiente()
        {
            if (Ptr < Lis_BE.size() - 1)
            {
                Ptr += 1;
            }
            preAnalisis = obtElemento(Ptr);          
        }
/*------------------------------------------------------------------------------------------------------------*/
// Metodo para retroceder el preAnalisis en 1 posicion.
        
        public void anterior ()
        {
            if ( Ptr > 0 )
            {
                Ptr -= 1;
            }
            preAnalisis = obtElemento(Ptr);          
        }
/*------------------------------------------------------------------------------------------------------------*/
// Metodo que obtiene el elemento de la posicion deseada en el buffer    
      
        public Linea_BE obtElemento(int intPosicion)
        { 
            
            return Lis_BE.elementAt(intPosicion); 
            
        }
/*------------------------------------------------------------------------------------------------------------*/
//Metodo que reestablece nuestro buffer de entrada
        
        public void restablecer()
        {
            Ptr = 0;
            preAnalisis = obtElemento(Ptr);
        }
/*------------------------------------------------------------------------------------------------------------*/
        public void setPrt ( int pos )
        {
            Ptr = pos;
            preAnalisis = obtElemento(Ptr);
        }
/*------------------------------------------------------------------------------------------------------------*/
        public int getPrt ()
        {
            return Ptr;
        }
/*------------------------------------------------------------------------------------------------------------*/
//Propiedad que obtiene el tamaño del buffer 
        
        public int getTamaño()
        {
            
            return Lis_BE.size();
            
        }
 /*------------------------------------------------------------------------------------------------------------*/
         
}

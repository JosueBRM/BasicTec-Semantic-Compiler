/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: ENERO-JUNIO 2023    HORA: 6:00 - 7:00 HRS
 *:                                   
 *:               
 *:         Clase con la funcionalidad del Analizador Sintactico
 *                 
 *:                           
 *: Archivo       : SintacticoSemantico.java
 *: Autor         : Fernando Gil  ( Estructura general de la clase  )
 *:                 Grupo de Lenguajes y Automatas II ( Procedures  )
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   : Esta clase implementa un parser descendente del tipo 
 *:                 Predictivo Recursivo. Se forma por un metodo por cada simbolo
 *:                 No-Terminal de la gramatica mas el metodo emparejar ().
 *:                 El analisis empieza invocando al metodo del simbolo inicial.
 *: Ult.Modif.    : Agergaron nuevas acciones semanticas 
 *: Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 22/Feb/2015 FGil                -Se mejoro errorEmparejar () para mostrar el
 *:                                 numero de linea en el codigo fuente donde 
 *:                                 ocurrio el error.
 *: 08/Sep/2015 FGil                -Se dejo lista para iniciar un nuevo analizador
 *:                                 sintactico.
 *: 15/Abr/2023 JosueBRM            -Se agregaron el resto de las acciones semanticas
 *:             CarlosCL
 *:-----------------------------------------------------------------------------
 */
package compilador;

import javax.swing.JOptionPane;
import general.Linea_BE;

public class SintacticoSemantico {

    private Compilador cmp;
    private boolean    analizarSemantica = false;
    private String     preAnalisis;
    private static final String VACIO = "VACIO";
    private static final String ERROR_TIPO = "ERROR_TIPO";


    public SintacticoSemantico(Compilador c) {
        cmp = c;
    }
    
    //--------------------------------------------------------------------------
    // Funciones  D -> R
    
    public String getDomain(String t) {
        String[] partes = t.split("->");
        return partes[0];
        //return (t.split("->"))[0];
    }
    
    public String getRange(String t) {
        //return (t.split("->"))[1];
        String[] partes = t.split("->");
        return partes[1];
    }

    //--------------------------------------------------------------------------
    // Metodo que inicia la ejecucion del analisis sintactico predictivo.
    // analizarSemantica : true = realiza el analisis semantico a la par del sintactico
    //                     false= realiza solo el analisis sintactico sin comprobacion semantica

    public void analizar(boolean analizarSemantica) {
        this.analizarSemantica = analizarSemantica;
        preAnalisis = cmp.be.preAnalisis.complex;

        // * * *   INVOCAR AQUI EL PROCEDURE DEL SIMBOLO INICIAL   * * *
        Atributo atributos = new Atributo ();
        programa ( atributos );
        
    }

    //--------------------------------------------------------------------------

    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
            preAnalisis = cmp.be.preAnalisis.complex;            
        } else {
            errorEmparejar( t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------
 
    private void errorEmparejar(String _token, String _lexema, int numLinea ) {
        String msjError = "";

        if (_token.equals("id")) {
            msjError += "Se esperaba un identificador";
        } else if (_token.equals("num")) {
            msjError += "Se esperaba una constante entera";
        } else if (_token.equals("num.num")) {
            msjError += "Se esperaba una constante real";
        } else if (_token.equals("literal")) {
            msjError += "Se esperaba una literal";
        } else if (_token.equals("oparit")) {
            msjError += "Se esperaba un operador aritmetico";
        } else if (_token.equals("oprel")) {
            msjError += "Se esperaba un operador relacional";
        } else if (_token.equals("opasig")) {
            msjError += "Se esperaba operador de asignacion";
        } else {
            msjError += "Se esperaba " + _token;
        }
        msjError += " se encontró " + ( _lexema.equals ( "$" )? "fin de archivo" : _lexema ) + 
                    ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

        cmp.me.error(Compilador.ERR_SINTACTICO, msjError);
    }

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico

    private void error(String _descripError) {
        cmp.me.error(cmp.ERR_SINTACTICO, _descripError);
    }

    // Fin de error
    //--------------------------------------------------------------------------
    //  *  *   *   *    PEGAR AQUI EL CODIGO DE LOS PROCEDURES  *  *  *  *
    //--------------------------------------------------------------------------
    
    //Metodo para saber si es palabra reservada
    private boolean isReservedWord(String preanalisis){
           switch(preanalisis){
               case "dim":
               case "function":
               case "sub":
               case "id":
               case "if":
               case "call":
               case "do":
               case "end":
                   return true;
           }
           return false;
    }
    
    private void programa ( Atributo prog ) {
        
        Atributo declaraciones = new Atributo();
        Atributo declaraciones_subprogramas = new Atributo();
        Atributo proposiciones_optativas = new Atributo();
        
        if ( isReservedWord(preAnalisis) ) {
            
            // programa -> declaraciones declaraciones_subprogramas proposiciones_optativas end {1}
            declaraciones ( declaraciones );
            declaracionesSubprogramas ( declaraciones_subprogramas );
            proposicionesOptativas ( proposiciones_optativas );
            emparejar ( "end" );
            

            // ----------------------- { 1 } -----------------------------------
            if ( analizarSemantica ) {
                if ( declaraciones.tipo.equals( VACIO ) &&
                        declaraciones_subprogramas.tipo.equals("VACIO") &&
                            proposiciones_optativas.tipo.equals("VACIO"))
                    prog.tipo = VACIO;
                else
                {
                    prog.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, 
                                " [programa]: error en programa"
                                + "No.Linea: " + cmp.be.preAnalisis.numLinea );
                }
            }
            // -----------------------------------------------------------------            
        } else {
            error ( "[programa]: Error iniciando el programa." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }

//------------------------------------------------------------------------------
   
    private void declaraciones ( Atributo declaraciones ) {
        
        Atributo lista_declaraciones = new Atributo();
        Atributo declaraciones_2     = new Atributo();     
        
        if ( "dim".equals(preAnalisis) ) {
            // declaraciones -> dim lista_declaraciones declaraciones_2 {2}
            emparejar ( "dim" );
            listaDeclaraciones ( lista_declaraciones );
            declaraciones ( declaraciones_2 );
            
            // ----------------------- { 2 }-----------------------

            if ( analizarSemantica ) {
                if ( !lista_declaraciones.tipo.equals ( ERROR_TIPO ) &&
                     !declaraciones_2.tipo.equals ( ERROR_TIPO ) )
                    declaraciones.tipo = VACIO;
                else
                {
                    declaraciones.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, 
                                "{4} : lista_declaraciones = ERROR_TIPO o "
                                + "declaraciones2.tipo = ERROR_TIPO" );
                }
            }
        } 
        else 
        {
            // declaraciones -> empty {3}

            // ----------------------- { 3 } -----------------------
                        
            if ( analizarSemantica ) {
                declaraciones.tipo = VACIO;
            }
        }
    }
//------------------------------------------------------------------------------------
    
    private void listaDeclaraciones ( Atributo lista_dec ) {
        
        Linea_BE id                         = new Linea_BE   ();
        Atributo tipo                       = new Atributo ();
        Atributo listaDeclaracionesPrima    = new Atributo ();
        
        if ( preAnalisis.equals ( "id" ) ) {
            
            // Se salvan los atributos de id
            id = cmp.be.preAnalisis;
            
            // lista_declaraciones -> id as tipo lista_declaraciones_prima {4}
            emparejar ( "id" );
            emparejar ( "as" );
            tipo ( tipo );
            listaDeclaracionesPrima ( listaDeclaracionesPrima );
            

            // --------------------------- { 4 } -------------------------------

            if ( analizarSemantica ) {
                if ( cmp.ts.buscaTipo ( id.entrada ).equals ( "" ) ) {
                    // Lo busca en la tabla de simbolos, y si no esta
                    // entonces si lo agregamos
                    cmp.ts.anadeTipo( id.entrada, tipo.tipo );
                    
                    // Declaracion
                    if ( listaDeclaracionesPrima.tipo.equals( VACIO ) ) {
                        lista_dec.tipo = tipo.tipo;
                        
                    // Concatenacion con 'x'
                    } else if ( !lista_dec.tipo.equals( ERROR_TIPO ) ) {
                        lista_dec.tipo = tipo.tipo + "x" + listaDeclaracionesPrima.tipo;
                        
                    // Error en lista declaraciones
                    } else {
                        lista_dec.tipo = ERROR_TIPO;
                        cmp.me.error( cmp.ERR_SEMANTICO,
                          "{4} : lista_declaraciones = ERROR_TIPO" );
                    }
                    
                //Si ya esta declarado
                } else {
                    lista_dec.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO,
                      "{4} : Identificador ya declarado" );
                }
            }
        } else {
            error ( "[lista_declaraciones]: Se esperaba una declaración." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
//------------------------------------------------------------------------------------
    
    
    private void listaDeclaracionesPrima ( Atributo listaDeclaracionesPrima ) {
        
        Atributo listaDeclaraciones = new Atributo ();
        
        if ( ",".equals(preAnalisis) ) {
            
            emparejar ( "," );
            listaDeclaraciones ( listaDeclaraciones );
            
// -----------------------------------{ 5 }---------------------------------------
            if ( analizarSemantica ) {
                listaDeclaracionesPrima.tipo = listaDeclaraciones.tipo;
            }
            
        } else {
// -----------------------------------{ 6 }-------------------------------------
            if ( analizarSemantica ) {
                listaDeclaracionesPrima.tipo = VACIO;
            }
        }
    }  
//------------------------------------------------------------------------------
    private void tipo ( Atributo tipo ) {
        if ( "integer".equals(preAnalisis) ) {
            emparejar ( "integer" );
           
// -----------------------------------{ 7 }-------------------------------------
            if ( analizarSemantica ) 
                tipo.tipo = "INTEGER";
            
        } else if ( "single".equals(preAnalisis) ) {
            emparejar ( "single" );
// -----------------------------------{ 8 }-------------------------------------
            if ( analizarSemantica ) 
                tipo.tipo = "SINGLE";
            
        } else if ( "string".equals(preAnalisis) ) {
            emparejar ( "string" );
// -----------------------------------{ 9 }-------------------------------------
            if ( analizarSemantica ) 
                tipo.tipo = "STRING";

        } else {
            error ( "[tipo]: Tipo de dato erroneo." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
//------------------------------------------------------------------------------
    
    private void declaracionesSubprogramas ( Atributo declaracionesSubprogramas ) {
        
        Atributo declaracion_subprograma = new Atributo ();
        Atributo declaraciones_subprogramas_1 = new Atributo ();
        
        if ( preAnalisis.equals ( "function" ) ||
                preAnalisis.equals ( "sub" ) ) {
            declaracionSubprograma ( declaracion_subprograma );
            declaracionesSubprogramas ( declaraciones_subprogramas_1 );
            
// ----------------------------------{ 10 }-------------------------------------
            if ( analizarSemantica ) {
                if ( declaracion_subprograma.tipo.equals ( VACIO ) &&
                     declaraciones_subprogramas_1.tipo.equals ( VACIO ) ) {
                    declaracionesSubprogramas.tipo = VACIO;
                } else {
                    declaracionesSubprogramas.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO,
                            "{10} : declaracion_subprogrma = ERROR_TIPO"
                             + "declaraciones_subprogramas1 = ERROR_TIPO" );
                }
            }
        } else {
            // empty {11}
            
// ----------------------------------{ 11 }---------------------------------------
            if ( analizarSemantica ) {
                declaracionesSubprogramas.tipo = VACIO;
            }
            // -----------------------------------------------------------------
        }
    }
    
    //------------------------------------------------------------------------------------
    
    
    private void declaracionSubprograma ( Atributo declaracionSubprograma ) {
        
        Atributo declaracionFuncion = new Atributo ();
        Atributo declaracionSubrutina = new Atributo ();
        
        if ( "function".equals(preAnalisis) ) {
            declaracionFuncion ( declaracionFuncion );
            
// ----------------------------------{ 12 }---------------------------------------
            if ( analizarSemantica ) 
                declaracionSubprograma.tipo = declaracionFuncion.tipo;
            
        } else if ( "sub".equals(preAnalisis) ) {
            declaracionSubrutina ( declaracionSubrutina );
            
// ----------------------------------{ 13 }---------------------------------------
            if ( analizarSemantica ) 
                declaracionSubprograma.tipo = declaracionSubrutina.tipo;
            
        } else {
            error ( "[declaracion_subprograma]: Error de función." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
//------------------------------------------------------------------------------------
    
   
    private void declaracionFuncion ( Atributo df ) {
        
        Linea_BE id = new Linea_BE ();
        Atributo argumentos = new Atributo ();
        Atributo tipo = new Atributo ();
        Atributo proposicionesOpt = new Atributo ();
        
        if ( "function".equals(preAnalisis) ) {
            emparejar ( "function" );
            
            // atributos de id
            id = cmp.be.preAnalisis;
            
            emparejar ( "id" );
            argumentos ( argumentos );
            emparejar ( "as" );
            tipo ( tipo );
            proposicionesOptativas ( proposicionesOpt );
            
// ----------------------------------{ 14 }---------------------------------------
            if ( analizarSemantica ) {
                //Si no esta en la tabla de simbolos declarada
                if ( cmp.ts.buscaTipo ( id.entrada ).equals("") ) {
                    //Y si los argumentos no tienen error
                    if ( !argumentos.tipo.equals ( ERROR_TIPO ) ) {
                        cmp.ts.anadeTipo( id.entrada , argumentos.tipo + "->" + tipo.tipo );
                        //Y si las proposiciones optativas estan bien
                        if ( proposicionesOpt.tipo.equals( VACIO ) ) 
                            df.tipo = VACIO;
                        
                        else {
                            df.tipo = ERROR_TIPO;
                            cmp.me.error( cmp.ERR_SEMANTICO, 
                              "{14} : proposiciones optativas erroneas" );
                        }
                    } else {
                        df.tipo = ERROR_TIPO;
                        cmp.me.error( cmp.ERR_SEMANTICO,
                          "{14} : error en los argumentos de la funcion" );
                    }
                } else {
                    df.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO,
                     "{14} : La funcion ya ha sido declarada" );
                }
            }
            emparejar ( "end" );
            emparejar ( "function" );
        } else {
            error ( "[declaracionFuncion]: Error en la declaracion de funcion" +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    
    
    private void declaracionSubrutina ( Atributo ds ) {
        
        Linea_BE id = new Linea_BE ();
        Atributo argumentos = new Atributo ();
        Atributo proposicionesOpt = new Atributo ();
        
        if ( "sub".equals(preAnalisis) ) {
            emparejar ( "sub" );
            
            id = cmp.be.preAnalisis;
            
            emparejar ( "id" );
            argumentos ( argumentos );
            proposicionesOptativas ( proposicionesOpt );
            
// ----------------------------------{ 15 }---------------------------------------
            if ( analizarSemantica ) {
                //Si aun no esta en la T.S
                if ( cmp.ts.buscaTipo ( id.entrada ).equals("") ) {
                    if ( !argumentos.tipo.equals ( ERROR_TIPO ) ) {
                        cmp.ts.anadeTipo( id.entrada , argumentos.tipo + "->" + "VOID" );
                        if ( proposicionesOpt.tipo.equals( VACIO ) ) 
                            ds.tipo = VACIO;
                        else {
                            ds.tipo = ERROR_TIPO;
                            cmp.me.error( cmp.ERR_SEMANTICO, 
                              "{14} : error en proposiciones optativas" );
                        }
                    } else {
                        ds.tipo = ERROR_TIPO;
                        cmp.me.error( cmp.ERR_SEMANTICO,
                          "{14} : argumentos erroneos" );
                    }
                } else {
                    ds.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO,
                     "{14} : funcion ya declarada" );
                }
            }
            // -----------------------------------------------------------------
            
            emparejar ( "end" );
            emparejar ( "sub" );
        } else {
            error ( "[declaracion_subrutina]: Error de subrutina" +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
//------------------------------------------------------------------------------
    
    private void argumentos ( Atributo argumentos ) {
        
        Atributo ls = new Atributo ();
        
        if ( "(".equals(preAnalisis) ) {
            // argumentos -> ( lista_declaraciones ) {16}
            emparejar ( "(" );
            listaDeclaraciones ( ls );
            emparejar ( ")" );
            
// ----------------------------------{ 16 }-------------------------------------
            if ( analizarSemantica ) 
                argumentos.tipo = ls.tipo;
            
        } else {
            // empty
// ----------------------------------{ 17 }-------------------------------------
            if ( analizarSemantica ) {
                argumentos.tipo = "VOID";
            }
        }
    }
//------------------------------------------------------------------------------
    
    private void proposicionesOptativas ( Atributo propOpt ) {
        
        Atributo proposicion = new Atributo ();
        Atributo proposiciones_optativas1 = new Atributo ();
        
        if ( preAnalisis.equals ( "id" ) || 
                preAnalisis.equals ( "call" ) ||
                    preAnalisis.equals ( "if" ) ||
                        preAnalisis.equals ( "do" ) ) {
            
            // proposiciones_optativas -> proposicion proposiciones_optativas {18}
            proposicion ( proposicion );
            proposicionesOptativas ( proposiciones_optativas1 );
            
            
// ----------------------------------{ 18 }-------------------------------------
            if ( analizarSemantica ) {
                if ( proposicion.tipo.equals( VACIO ) && 
                     proposiciones_optativas1.tipo.equals( VACIO ) ) {
                    propOpt.tipo = VACIO;
                } else {
                    propOpt.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO,
                            "{18} : Error en proposiciones" );
                }
            }
            // -----------------------------------------------------------------
            
        } else {
            // proposiciones_optativas -> empty {19}
            
// ----------------------------------{19}---------------------------------------
            if ( analizarSemantica ) {
                propOpt.tipo = VACIO;
            }
        }
    }
    
//------------------------------------------------------------------------------
    
    private void proposicion ( Atributo proposicion ) {
        
        Atributo expresion = new Atributo ();
        Atributo propPrima = new Atributo ();
        Atributo condicion = new Atributo ();
        Atributo proposicionesOptativas_1 = new Atributo ();
        Atributo proposicionesOptativas_2 = new Atributo ();
        Atributo condicion_1 = new Atributo ();
        Atributo proposicionesOptativas_3 = new Atributo ();
        Linea_BE id = new Linea_BE ();
        
        if ( preAnalisis.equals ( "id" ) ) {
            // proposicion -> id opasig expresion {20}
            
            id = cmp.be.preAnalisis;
            
            emparejar ( "id" );
            emparejar ( "opasig" );
            expresion ( expresion );
            
// ----------------------------------{ 20 }-------------------------------------
            if ( analizarSemantica ) {
                //Primero va y revisa la TS, si no encuentra el id, entonces no
                //esta declarado aun
                if(!cmp.ts.buscaTipo(id.entrada).isEmpty()){
                    
                    if ( cmp.ts.buscaTipo( id.entrada ).equals ( expresion.tipo ))
                        proposicion.tipo = VACIO;

                    else if(cmp.ts.buscaTipo(id.entrada).equals("SINGLE") 
                             && expresion.tipo.equals("INTEGER"))
                        proposicion.tipo = VACIO;
                    else {
                        proposicion.tipo = ERROR_TIPO;
                        cmp.me.error( cmp.ERR_SEMANTICO,
                        "{20} : ERROR, El tipo de la variable y expresion no concuerdan " 
                        + "Linea No." + cmp.be.preAnalisis.numLinea);
                    }
                }else{
                    cmp.me.error( cmp.ERR_SEMANTICO,
                                "{20} : ERROR, La variable no esta declarada " 
                                + "Linea No." + cmp.be.preAnalisis.numLinea);
                }   
            }
            // -----------------------------------------------------------------
            
        } else if ( "call".equals(preAnalisis) ) {
            // proposicion -> call id proposicion_prima {21}
            
            emparejar ( "call" );
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            proposicionPrima( propPrima );
            
            
// ----------------------------------{ 21 }-------------------------------------
            if ( analizarSemantica ) {
                if ( propPrima.tipo.equals( getDomain (
                                        cmp.ts.buscaTipo( id.entrada )))) 
                    proposicion.tipo = VACIO;
                else {
                    proposicion.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO,
                            "{21} : Error en los argumentos" );
                }
                    
            }
            // -----------------------------------------------------------------
            
        } else if ( "if".equals(preAnalisis) ) {
            // proposicion -> if condicion then proposiciones_optativas 
            //      else proposiciones_optativas {22} end if
            emparejar ( "if" );
            condicion ( condicion );
            emparejar ( "then" );
            proposicionesOptativas ( proposicionesOptativas_1 );
            emparejar ( "else" );
            proposicionesOptativas ( proposicionesOptativas_2 );
            
            
// ----------------------------------{ 22 }---------------------------------------
            if ( analizarSemantica ) {
                if ( condicion.tipo.equals("BOOLEAN")) {
                    if ( proposicionesOptativas_1.tipo.equals( VACIO ) &&
                            proposicionesOptativas_2.tipo.equals( VACIO ) ) 
                        proposicion.tipo = VACIO;
                    else {
                        proposicion.tipo = ERROR_TIPO;
                        cmp.me.error( cmp.ERR_SEMANTICO,
                                "{22} : Error en las proposiciones" );
                    }
                        
                } else {
                    proposicion.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO,
                            "{22} : Expresion no es BOOLEAN" );
                }
            }
            emparejar ( "end" );
            emparejar ( "if" );
        } else if ( "do".equals(preAnalisis) ) {
            // proposicion -> do while condicion proposiciones_optativas {23} loop
            emparejar ( "do" );
            emparejar ( "while" );
            condicion ( condicion_1 );
            proposicionesOptativas ( proposicionesOptativas_3 );
            
            
// ----------------------------------{ 23 }---------------------------------------
            if ( analizarSemantica ) {
                if ( condicion_1.tipo.equals( "BOOLEAN" )) {
                    if ( proposicionesOptativas_3.tipo.equals( VACIO ))
                        proposicion.tipo = VACIO;
                    else {
                        proposicion.tipo = ERROR_TIPO;
                        cmp.me.error( cmp.ERR_SEMANTICO,
                       "{23} : ERROR, Proposiciones optativas incorrectas " + 
                                   "No. Linea: " + cmp.be.preAnalisis.numLinea);
                    }
                } else {
                    proposicion.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO,
                      "{23} : Expresion no booleana" );
                }
            }
            // -----------------------------------------------------------------
            
            emparejar ( "loop" );
        } else {
            error ( "[proposicion]: Error" +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    
    
    private void proposicionPrima ( Atributo propPrim ) {
        
        Atributo le = new Atributo ();
        
        if ( preAnalisis.equals( "(" ) ) {
            // proposicion_prima -> ( lista_expresiones ) {24}
            emparejar ( "(" );
            listaExpresiones ( le );
            emparejar ( ")" );
            
// ----------------------------------{ 24 }-------------------------------------
            if ( analizarSemantica ) 
                propPrim.tipo = le.tipo;
            
        } else if ( analizarSemantica ) 
                propPrim.tipo = "VOID";
            // -> empty {24}
            
    }
    
    private void listaExpresiones ( Atributo le ) {
        
        Atributo expresion = new Atributo ();
        Atributo lista_expresiones_prima = new Atributo ();
        
        if ( preAnalisis.equals ( "id" ) || 
                preAnalisis.equals ( "num" ) ||  
                    preAnalisis.equals ( "num.num" ) || 
                        preAnalisis.equals ( "(" ) ||
                            preAnalisis.equals ( "literal" )) {
            // lista_expresiones -> expresion lista_expresiones_prima {26}
            expresion ( expresion );
            listaExpresionesPrima ( lista_expresiones_prima );
            
            
// ----------------------------------{ 25 }-------------------------------------
            if ( analizarSemantica ) {
                if ( !expresion.tipo.equals( ERROR_TIPO ) 
                        && !lista_expresiones_prima.tipo.equals( 
                                                            ERROR_TIPO ))
                    if ( lista_expresiones_prima.tipo.equals( VACIO ))
                        le.tipo = expresion.tipo;
                    else
                        le.tipo = expresion.tipo + "x" + lista_expresiones_prima.tipo;
                else {
                    le.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO,
                      "{25} : ERROR en lista de expresiones" );
                }
            }
            // -----------------------------------------------------------------
            
        } else {
            // lista_expresiones -> empty {26}
            
// ----------------------------------{ 26 }-------------------------------------
            if ( analizarSemantica ) {
                le.tipo = VACIO;
            }
        }
    }
//------------------------------------------------------------------------------    
    
    private void listaExpresionesPrima ( Atributo listaEP ) {
        
        Atributo expresion = new Atributo ();
        Atributo lista_expresiones_prima1 = new Atributo ();
        
        if ( ",".equals(preAnalisis) ) {
            // lista_expresiones_prima -> , expresion lista_expresiones_prima {27}
            emparejar ( "," );
            expresion ( expresion );
            listaExpresionesPrima ( lista_expresiones_prima1 );
            
// ----------------------------------{ 27 }-------------------------------------
            if ( analizarSemantica ) {
                if ( !expresion.tipo.equals( ERROR_TIPO ) 
                        && !lista_expresiones_prima1.tipo.equals(
                                                        ERROR_TIPO ))
                    if ( lista_expresiones_prima1.tipo.equals( VACIO )) 
                        listaEP.tipo = expresion.tipo;
                    else
                        listaEP.tipo = expresion.tipo + "x" 
                                        + lista_expresiones_prima1.tipo;
                else {
                    listaEP.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, 
                      "{27} : ERROR en la Expresión" );
                }
            }
        } else {
            // lista_expresiones_prima -> empty {28}
            
// ----------------------------------{ 28 }-------------------------------------
            if ( analizarSemantica ) {
                listaEP.tipo = VACIO;
            }
        }
    }    
//------------------------------------------------------------------------------
    
    
    private void condicion ( Atributo condicion ) {
        
        Atributo expr1 = new Atributo ();
        Atributo expr2 = new Atributo ();
        
        if ( preAnalisis.equals ( "id" )        ||
                preAnalisis.equals ( "num" )       ||
                    preAnalisis.equals ( "num.num" )   ||
                        preAnalisis.equals ( "(" ) ||
                            preAnalisis.equals ( "literal" )) {
            // condicion -> expresion oprel expresion {29}
            expresion ( expr1 );
            emparejar ( "oprel" );
            expresion ( expr2 );
            
            
// ----------------------------------{ 29 }-------------------------------------
            if ( analizarSemantica ) {
                if ( expr1.tipo.equals( expr2.tipo ) ) {
                    condicion.tipo = "BOOLEAN";
                    
                }else if(expr1.tipo.equals("INTEGER") 
                            && expr2.tipo.equals("SINGLE") 
                                || expr1.tipo.equals("SINGLE") 
                                    && expr2.tipo.equals("INTEGER")){
                         condicion.tipo = "BOOLEAN";
                         
                } else {
                    condicion.tipo = "ERROR_TIPO";
                    cmp.me.error( cmp.ERR_SEMANTICO,
                      "{29} : Las expresiones no concuerdan" );
                }
            }
            
        } else {
            error ( "[condicion]: Error de condición." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    
    
    private void expresion ( Atributo expresion ) {
        
        Atributo termino = new Atributo ();
        Atributo exprPrima = new Atributo ();
        
        if ( preAnalisis.equals ( "id" )        ||
                preAnalisis.equals ( "num" )       ||
                    preAnalisis.equals ( "num.num" )   ||
                        preAnalisis.equals ( "(" ) ) {
            // expresion -> termino {30} expresion_prima {31}
            termino ( termino );
            
            
// ----------------------------------{ 30 }-------------------------------------
            if ( analizarSemantica ) 
                exprPrima.h = termino.tipo;
            
            expresionPrima ( exprPrima );
            
// ----------------------------------{ 31 }-------------------------------------
            if ( analizarSemantica ) {
                if ( !exprPrima.h.equals( ERROR_TIPO ) && 
                        !exprPrima.tipo.equals( ERROR_TIPO ) )
                    expresion.tipo = exprPrima.tipo;
                else {
                    expresion.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO,
                      "{31} : ERROR de Tipos" );
                }
            }
// -----------------------------------------------------------------------------
            
        } else if ( "literal".equals(preAnalisis) ) {
            // expresion -> literal {32}
            emparejar ( "literal" );
            
// ----------------------------------{ 32 }-------------------------------------
            if ( analizarSemantica ) {
                expresion.tipo = "STRING";
            }
            // -----------------------------------------------------------------
            
        } else {  
            error ( "[expresion]: Expresión no valida." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
//------------------------------------------------------------------------------
    
    
    private void expresionPrima ( Atributo expresion_prima ) {
        
        Atributo termino = new Atributo ();
        Atributo ePrim_1 = new Atributo ();
        
        if ( preAnalisis.equals ( "opsuma" ) ) {
            // expresion_prima -> opsuma termino {33} expresion_prima {34}
            emparejar ( "opsuma" );
            termino ( termino );
            
            
// ----------------------------------{ 33 }-------------------------------------
            if ( analizarSemantica ) {
                if ( expresion_prima.h.equals( termino.tipo ) )
                    ePrim_1.h = termino.tipo;
                else if ( expresion_prima.h.equals( "SINGLE" ) && 
                            termino.tipo.equals( "INTEGER" ) ||
                                expresion_prima.h.equals( "INTEGER" )
                                    && termino.tipo.equals( "SINGLE" ) ) 
                    ePrim_1.h = "SINGLE";
                else {
                    ePrim_1.h = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, 
                      "{33} : ERROR de Expresion" );
                }
            }
// -----------------------------------------------------------------------------
            
            expresionPrima ( ePrim_1 ); 
            
// ----------------------------------{ 34 }-------------------------------------
            if ( analizarSemantica ) {
                if ( !ePrim_1.h.equals( ERROR_TIPO ) && 
                        !ePrim_1.tipo.equals( ERROR_TIPO ) )
                    expresion_prima.tipo = ePrim_1.tipo;
                else {
                    expresion_prima.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, 
                      "{34} : ERROR de Expresion_prima" );
                }
            }
// -----------------------------------------------------------------------------
            
        } else {
            // expresion_prima -> empty {35}            
            
// ----------------------------------{ 35 }-------------------------------------
            if ( analizarSemantica ) {
                expresion_prima.tipo = expresion_prima.h;
            }
            // -----------------------------------------------------------------
        }
    }
    
    //--------------------------------------------------------------------------
    
    
    private void termino ( Atributo termino ) {
        
        Atributo factor = new Atributo ();
        Atributo termino_prima = new Atributo ();
        
        if ( preAnalisis.equals ( "id" )        ||
                preAnalisis.equals ( "num" )       ||
                    preAnalisis.equals ( "num.num" )   ||
                        preAnalisis.equals ( "(" ) ) {
            factor ( factor );
            
            
// ----------------------------------{ 36 }-------------------------------------
            if ( analizarSemantica ) {
                termino_prima.h = factor.tipo;
            }
// -----------------------------------------------------------------
            
            terminoPrimo ( termino_prima );
            
// ----------------------------------{ 37 }-------------------------------------
            if ( analizarSemantica ) {
                if ( !termino_prima.h.equals( ERROR_TIPO ) &&
                     !termino_prima.tipo.equals( ERROR_TIPO ) ) 
                    termino.tipo = termino_prima.tipo;
                else {
                    termino.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, 
                      "{37} : ERROR de Tipos" );
                }
            }
            // -----------------------------------------------------------------
            
        } else {
            error ( "[termino]: Error de término." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    
    private void terminoPrimo ( Atributo terminoPrimo ) {
        
        Atributo factor = new Atributo ();
        Atributo tp_1 = new Atributo ();
        
        if ( "opmult".equals(preAnalisis) ) {
            emparejar ( "opmult" );
            factor ( factor );
            
            
// ----------------------------------{ 38 }---------------------------------------
            if ( analizarSemantica ) {
                if ( terminoPrimo.h.equals( factor.tipo ) ) {
                    tp_1.h = factor.tipo;
                }
                else if ( terminoPrimo.h.equals( "SINGLE" ) && 
                            factor.tipo.equals( "INTEGER" ) ||
                                terminoPrimo.h.equals( "INTEGER" ) && 
                                    factor.tipo.equals( "SINGLE" ) ){
                    tp_1.h = "SINGLE";
                
                }else if(terminoPrimo.h.equals(getRange(factor.tipo))) {
                    tp_1.h = getRange(factor.tipo);
                }
                else {
                    tp_1.h = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, 
                            "{38} : Error de tipo" );
                }
            }
// -----------------------------------------------------------------
            
            terminoPrimo ( tp_1 );            
            
// ----------------------------------{ 39 }-------------------------------------
            if ( analizarSemantica ) {
                if ( !tp_1.h.equals( ERROR_TIPO ) && 
                        !tp_1.tipo.equals( ERROR_TIPO ) )
                    terminoPrimo.tipo = tp_1.tipo;
                else {
                    terminoPrimo.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO,
                            "{39} : ERROR de Tipos" );
                }
            }
            // -----------------------------------------------------------------
            
        } else {
            // termino -> empty {40}
            
// ----------------------------------{ 40 }-------------------------------------
            if ( analizarSemantica ) {
                terminoPrimo.tipo = terminoPrimo.h;
            }
// -----------------------------------------------------------------
        }
    }
//------------------------------------------------------------------------------
    
    private void factor ( Atributo factor ) {
        
        Linea_BE id = new Linea_BE ();
        Atributo fp = new Atributo ();
        Atributo expresion = new Atributo ();
        
        if ( "id".equals(preAnalisis) ) {
            // factor -> id factor_prima {41}
            
            id = cmp.be.preAnalisis;
            
            emparejar ( "id" );
            factorPrimo ( fp );
            
            
// ----------------------------------{ 41 }-------------------------------------
            if ( analizarSemantica ) {
                if ( fp.tipo.equals( VACIO ) ) {
                    factor.tipo = cmp.ts.buscaTipo( id.entrada );
                } else if ( getDomain ( cmp.ts.buscaTipo( id.entrada ))
                        .equals( getArgumentos(fp.tipo) ) )
                    factor.tipo = getRange ( cmp.ts.buscaTipo( id.entrada ) );
                else {
                    factor.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, 
                      "{41} : ERROR de Tipos" );
                }
            }
            // -----------------------------------------------------------------
            
        } else if ( "num".equals(preAnalisis)) {
            // factor -> num {42}
            emparejar ( "num" );
            
            
// ----------------------------------{ 42 }-------------------------------------
            if ( analizarSemantica ) 
                factor.tipo = "INTEGER";
// -----------------------------------------------------------------------------
            
        } else if ( "num.num".equals(preAnalisis) ) {
            // factor -> num.num {43}
            emparejar ( "num.num" );
            
            
// ----------------------------------{ 43 }-------------------------------------
            if ( analizarSemantica ) {
                factor.tipo = "SINGLE";
            }
            // -----------------------------------------------------------------
            
        } else if ( preAnalisis.equals ( "(" ) ) {
            // factor -> ( expresion ) {44}
            emparejar ( "(" );
            expresion ( expresion );
            emparejar ( ")" );
            
            
// ----------------------------------{ 44 }-------------------------------------
            if ( analizarSemantica ) {
                factor.tipo = expresion.tipo;
            }
            // -----------------------------------------------------------------
            
        } else {
            error ( "[factor]: Error de variables." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    
    
    private void factorPrimo ( Atributo factorPrimo ) {
        
        Atributo le = new Atributo ();
        
        if ( preAnalisis.equals( "(" ) ) {
            // factor_prima -> ( lista_expresiones ) {45}
            emparejar ( "(" );
            listaExpresiones ( le );
            emparejar ( ")" );
            
            
// ----------------------------------{ 45 }-------------------------------------
            if ( analizarSemantica ) {
                factorPrimo.tipo = le.tipo;
            }
            
        } else {
            // factor_prima -> empty {46}
            
            
// ----------------------------------{ 46 }-------------------------------------
            if ( analizarSemantica ) {
                factorPrimo.tipo = VACIO;
            }
// -----------------------------------------------------------------
            
        }
    }
    
public String getArgumentos(String tipo){
        return tipo.toString();
}
    
}
//------------------------------------------------------------------------------

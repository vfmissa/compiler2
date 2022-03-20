import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SintaticoProgramaV2 {

    // variaveis do codigo intermediario

    ArrayList<String> codigoHip = new ArrayList<String>();
    //Stack<String> codigoHip = new Stack<String>();
    ArrayList<String> ARRAY_MAIN = new ArrayList<String>();
    int indice=0; // index de codigoHIP
    int desvioIF=0; // desvio depois do IF
    int E; // fim do procedimento e desalocação das variaveis
    int M=1; //variaveis alocadas no procedimento
    int NARG; // NUMERO DE PARAMETROS
    boolean End=false;

    int temp; // manipular o array PIlhaD
    int escopo = 0; // escopo 0 = main, escopo 1 = procedimento 2=parametros
    int prim_instru; // onde começa o procedimento

    int SOMA = 0; // verfica se houve soma , se houve mutiplicação, Inversao
    int mult = 0;
    Boolean INV = false;
    int relacionais = 0; // verifica os relacionais <><=>= etc etc

    String isCod = ""; // indica se está acontecendo um comando de leitura
    int tempAloc = 0;
    String VAR = ""; // referencia da variavel na ARRAY_PROCEDURE


    //EXECUÇÃO

    ArrayList<Double> pilhaD = new ArrayList<Double>();
    int S=0;  // topo da pilha



    private Lexico lexico;
    private Token simbolo;
    private Map<String, Simbolo> tabelaSimbolo = new HashMap<>();
    private Map<String, Simbolo> tabelaSimboloProcedimento = new HashMap<>();
    private Map<String, Simbolo> tabelaProcedimento = new HashMap<>();

    private int tipo;

    public void outputtxt(String Code) throws IOException {

        codigoHip.add(Code + "\n");
        

        File file2 = new File("pilhavar.txt");
        FileWriter fwv = new FileWriter(file2, false);
        PrintWriter pwv = new PrintWriter(fwv);

        File file = new File("output.txt");
        FileWriter fw = new FileWriter(file, false);
        PrintWriter pw = new PrintWriter(fw);

        if (End) {
            for (int indice = 0; indice < codigoHip.size(); indice++) {
                pw.write(codigoHip.get(indice));
                // System.out.println(this.codigoHip.get(indice));
            }
            
                pwv.write(codigoHip.toString());
                
                // System.out.println(this.codigoHip.get(indice));
            

            pwv.close();
            pw.close();
        }
    }

    // SINTATICO
    // ****************************************************************************************************************************************

    public SintaticoProgramaV2(String arq) {
        lexico = new Lexico(arq);
        
    }

    private void obtemSimbolo() {
        simbolo = lexico.proxToken();
    }

    private boolean verificaSimbolo(String termo) {
        return (simbolo != null && simbolo.getValor().equals(termo));
    }

    private void Prog() {
        System.out.println("prog");
        System.out.println("simbolo antes de prog>>" + simbolo.getValor());

        if (!verificaSimbolo("program")) {
            throw new RuntimeException("Erro sintático Programa não declarado: " + simbolo.getValor());
        } else {
            try {
                outputtxt(indice + ".INPP");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            obtemSimbolo();
            System.out.println("simbolo antes de corpo>>" + simbolo.getValor());
            if (simbolo.getTipo() != Token.INDENTIFICADOR) {
                throw new RuntimeException("Erro esperado indetificador antes de " + simbolo.getValor());
            } else {
                Corpo();
                if (verificaSimbolo(".")) {
                    obtemSimbolo();
                }
            }

        }

    }

    private void Corpo() {
        System.out.println("simbolo corpo>>" + simbolo.getValor());
        obtemSimbolo();
        DC();
        System.out.println("simbolo antes de begin>> " + simbolo.getValor());
        if ((verificaSimbolo("begin"))) {
            obtemSimbolo();
            
                codigoHip.add(prim_instru,prim_instru + ".DSVI " +(indice+1)+"\n");
        
           
            Comandos();
        }
        if (verificaSimbolo("end")) {
            obtemSimbolo();

        }

    }

    private void DC() {
        System.out.println("simbolo DC>>" + simbolo.getValor());
        if (verificaSimbolo("begin") || verificaSimbolo("procedure")) {

        } else {
            DC_V();
            MaisDC();
            DC_P();
        }
        System.out.println("saida DC>> " + simbolo.getValor());
    }

    private void DC_V() {
        System.out.println("simbolo DCV>>" + simbolo.getValor());
        Tipovari();
        if (verificaSimbolo(":")) {
            obtemSimbolo();
            Variaveis();
        }
        System.out.println("saida de  DCV>>" + simbolo.getValor());
    }

    private void MaisDC() {
        System.out.println("simbolo +DC>>" + simbolo.getValor());
        if (verificaSimbolo(";")) {
            obtemSimbolo();
            DC();
        } else {
            throw new RuntimeException("Erro faltou ; depois de : " + simbolo.getValor());
        }
    }

    private void DC_P() {
        System.out.println("simbolo inicio DC_P>>" + simbolo.getValor());
        if (!verificaSimbolo("procedure")) {

        } else {
            escopo = 1;
            indice=indice+1;
            prim_instru = indice; //INICIO DO PROCEDIMENTO DSVi
           

           

            obtemSimbolo();
            System.out.println("simbolo antes de parametros>>" + simbolo.getValor());
            if (simbolo.getTipo() != Token.INDENTIFICADOR) {
                throw new RuntimeException("Erro esperado indetificador antes de " + simbolo.getValor());
            } else {
                if (tabelaProcedimento.containsKey(simbolo.getValor())) {
                    throw new RuntimeException("Erro semântico procedimento já declarado: " + simbolo.getValor());
                } else {
                    tabelaProcedimento.put(simbolo.getValor(), new Simbolo(this.tipo, simbolo.getValor()));

                    Parametros();
                    obtemSimbolo();
                    Corpo_P();
                }

            }

        }
    }

    private void Parametros() {
        obtemSimbolo();
        System.out.println("simbolo parametros >> " + simbolo.getValor());
        if (!verificaSimbolo("(")) {
            throw new RuntimeException("Erro sintático esperado ( : " + simbolo.getValor());
        } else {
            obtemSimbolo();
            lista_par();

        }
        if (!verificaSimbolo(")")) {

            throw new RuntimeException("Erro parametros esperado ) antes de : " + simbolo.getValor());

        }

    }

    private void lista_par() {
        escopo = 2;
        System.out.println("simbolo list_par>>" + simbolo.getValor());
        Tipovari();
        if (verificaSimbolo(":")) {
            obtemSimbolo();
            Variaveis();
            mais_par();
        }
        escopo = 1;
    }

    private void mais_par() {
        System.out.println("simbolo mais_par>>" + simbolo.getValor());
        if (verificaSimbolo(";")) {
            obtemSimbolo();
            lista_par();
        }
    }

    private void Corpo_P() {
        System.out.println("simbolo CorpoP>>" + simbolo.getValor());

        DC_loc();

        if ((verificaSimbolo("begin"))) {
            obtemSimbolo();
            Comandos();
        }
        if (verificaSimbolo("end")) {
            try {
                indice=indice+1;
                outputtxt(indice+".DESM "+M);
                indice=indice+1;
                outputtxt(indice+".RTPR ");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            escopo = 0;
            obtemSimbolo(); // fim procedimento
            

        }
        System.out.println("SAIDA CorpoP>>" + simbolo.getValor());
    }

    private void DC_loc() {
        System.out.println("simbolo DC_loc>>" + simbolo.getValor());
        DC_V();
        MaisDC_loc();

    }

    private void MaisDC_loc() {
        System.out.println("simbolo +DC_loc>>" + simbolo.getValor());
        if (verificaSimbolo(";")) {
            obtemSimbolo();
            DC_loc();
        } else {

            // do nothing
            // throw new RuntimeException("Erro faltou ; depois de : " +
            // simbolo.getValor());
        }

    }

    private void lista_arg() {
        System.out.println("simbolo list arg>>" + simbolo.getValor());

        if (verificaSimbolo("(")) {
            obtemSimbolo();
            if (!tabelaProcedimento.containsKey(simbolo.getValor())) {
                System.out.println("simbolo de argumentos " + simbolo.getValor());
                   
                   // outputtxt(indice + ".PUSHER " + (E));
               
                }
                indice = indice + 1;
                desvioIF=indice;
                argumentos();

              
                codigoHip.add(desvioIF,desvioIF + ".PUSHER " + (desvioIF+NARG+2)+"\n");
                //CALCULAR O DESVIO DO PUSHER  usando indice=desvioIF + N parametros
                
                try {
                    indice = indice + 1;
                    outputtxt(indice + ".CHPR " + (prim_instru+1));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

        }
        if (verificaSimbolo(")")) {
            obtemSimbolo();
        } else {
            throw new RuntimeException("Erro sintático esperado ) no fim de argumentos " + simbolo.getValor());
        }

        System.out.println("saindo list_Arg>>" + simbolo.getValor());
    }

    private void argumentos() {
        System.out.println("simbolo ARGUMENTOS>>" + simbolo.getValor());
        if (simbolo.getTipo() != Token.INDENTIFICADOR) {
            throw new RuntimeException("Erro sintático esperado indentificador antes de " + simbolo.getValor());
        } else {
            try {
                NARG = NARG + 1;
                VAR = simbolo.getValor();
                temp = ARRAY_MAIN.indexOf(VAR);

                indice = indice + 1;
                outputtxt(indice+".PARAM "+temp);



            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            obtemSimbolo();
            Mais_indent();
        }
    }

    private void Mais_indent() {
        System.out.println("simbolo mais_indent>>" + simbolo.getValor());
        if (verificaSimbolo(",")) {
            obtemSimbolo();
            argumentos();
        }
    }

    private void Variaveis() {
        System.out.println("simbolo variaveis>>" + simbolo.getValor());
        if (simbolo.getTipo() != Token.INDENTIFICADOR) {
            throw new RuntimeException("Erro sintático esperado indentificador antes de " + simbolo.getValor());
        } else {
            if (escopo == 0) {
                if (tabelaSimbolo.containsKey(simbolo.getValor())) {
                    throw new RuntimeException("Erro semântico identificador já encontrado: " + simbolo.getValor());
                } else {
                    tabelaSimbolo.put(simbolo.getValor(), new Simbolo(this.tipo, simbolo.getValor()));
                    try {
                        indice = indice + 1;
                        outputtxt(indice + ".ALME 1");
                        ARRAY_MAIN.add(simbolo.getValor());
                        

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    obtemSimbolo();
                    Maisvari();
                }
            }
            if (escopo != 0) {
                if (tabelaSimboloProcedimento.containsKey(simbolo.getValor())) {
                    throw new RuntimeException("Erro semântico identificador já encontrado: " + simbolo.getValor());
                } else {
                    tabelaSimboloProcedimento.put(simbolo.getValor(), new Simbolo(this.tipo, simbolo.getValor()));
                    try {
                        indice = indice + 1;
                        outputtxt(indice + ".ALME 1");
                        ARRAY_MAIN.add(simbolo.getValor());
                            M=M+1;
        


                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    obtemSimbolo();
                    Maisvari();
                }
            }

        }
    }

    private void Maisvari() {
        System.out.println("simbolo maisvari>>" + simbolo.getValor());
        if (verificaSimbolo(",")) {
            obtemSimbolo();
            Variaveis();
        } else {
            // do nothing
        }

    }

    private void Tipovari() {
        System.out.println("simbolo tipovari>>" + simbolo.getValor());
        if (!verificaSimbolo("real") && !verificaSimbolo("integer")) {
            throw new RuntimeException("Erro sintático esperado real /integer antes de " + simbolo.getValor());
        }

        obtemSimbolo();

    }

    void Comandos() {
        Comando();
        Mais_Comandos();
    }

    private void Comando() {
        System.out.println("simbolo dentro de comando >> " + simbolo.getValor());
        if (verificaSimbolo("read")) {
            obtemSimbolo();
            if (verificaSimbolo("(")) {
                obtemSimbolo();
                if (simbolo.getTipo() != Token.INDENTIFICADOR) {
                    throw new RuntimeException("Erro semântico esperado identificador em Read: " + simbolo.getValor());
                } else {
                    System.out.println("READ >> " + simbolo.getValor());
                    try {
                       
                        VAR=simbolo.getValor();
                        if (escopo==1) {
                            temp = ARRAY_MAIN.lastIndexOf(VAR);
                            indice = indice + 1;
                            outputtxt(indice + ".LEIT ");

                            indice=indice+1;
                            outputtxt(indice + ".ARMZ " + temp);
                           
                            
                        }
                        if (escopo==0) {
                            temp = ARRAY_MAIN.indexOf(VAR);
                            indice = indice + 1;
                            outputtxt(indice + ".LEIT ");
                            indice=indice+1;
                            outputtxt(indice + ".ARMZ "+ temp);
                          
                            
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    obtemSimbolo();
                    if (verificaSimbolo(")")) {
                        obtemSimbolo();
                    } else {
                        throw new RuntimeException("Erro semântico esperado ) " + simbolo.getValor());
                    }
                }
            }
        }
        if (verificaSimbolo("write")) {
            obtemSimbolo();
            if (verificaSimbolo("(")) {
                obtemSimbolo();
                if (simbolo.getTipo() != Token.INDENTIFICADOR) {
                    throw new RuntimeException(
                            "Erro semântico identificador não encontrado em write: " + simbolo.getValor());
                } else {
                    System.out.println("escreveu >> " + simbolo.getValor());
                    try {
                        VAR = simbolo.getValor();
                        temp = ARRAY_MAIN.indexOf(VAR);
                        if(escopo==1){
                            temp = ARRAY_MAIN.lastIndexOf(VAR);
                        }
                        
                
                        indice = indice + 1;
                        outputtxt(indice + ".CLVR "+ temp);
                        

                        indice = indice + 1;
                        outputtxt(indice + ".IMPR " + simbolo.getValor());
                        
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    obtemSimbolo();
                    if (verificaSimbolo(")")) {
                        obtemSimbolo();
                    } else {
                        throw new RuntimeException("Erro semântico esperado ) " + simbolo.getValor());
                    }
                }
            }
        }

        if (verificaSimbolo("if")) {
            obtemSimbolo();
            condicao();
           

        }
        if (verificaSimbolo("then")) {
            obtemSimbolo();
            System.out.println("comando do if >> " + simbolo.getValor());
            Comandos();
            pfalsa();

        }

        if (verificaSimbolo("while")) {
            obtemSimbolo();
            condicao();
            
        }
        if (verificaSimbolo("do")) {
            obtemSimbolo();
            Comandos();
            codigoHip.add(desvioIF,desvioIF+".DSVF "+(indice+1)+"\n");
        }

        if (verificaSimbolo("$")) {
            obtemSimbolo();
        }

        if (simbolo.getTipo() == Token.INDENTIFICADOR) {

            if (verificaSimbolo("else") || verificaSimbolo("end")) {
                // do nothing
            } else {

                if (!tabelaProcedimento.containsKey(simbolo.getValor())) {

                    VAR = simbolo.getValor();

                }

                resto_indent();
                try {
                    
                    indice = indice + 1;
                    
                    
                    if (escopo==1) {
                        temp = ARRAY_MAIN.lastIndexOf(VAR);
                        outputtxt(indice + ".ARMZ "+ temp);
                       
                        
                    }

                    if (escopo == 0) {
                        temp = ARRAY_MAIN.indexOf(VAR);
                        outputtxt(indice + ".ARMZ "+ temp);
                       
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        System.out.println("saida em comando>> " + simbolo.getValor());

    }

    private void Mais_Comandos() {
        System.out.println("simbolo +comandos>>" + simbolo.getValor());
        if (verificaSimbolo(";")) {
            obtemSimbolo();
            Comandos();
        }

    }

    private void resto_indent() {
        obtemSimbolo();
        System.out.println("simbolo resto indent>>" + simbolo.getValor());

        if (verificaSimbolo(":=")) {
            obtemSimbolo();
            expressao();
            // throw new RuntimeException("Erro sintático esperado := encontrado: " +
            // simbolo.getValor());
        } else {
            lista_arg();
        }

    }

    private void expressao() {
        System.out.println("entrada expressão>> " + simbolo.getValor());
        Termo();
        Outros_termos();

    }

    private void Termo() {
        System.out.println("termo >> " + simbolo.getValor());
        op_un();
        fator();
        if (INV) {
            try {
                indice = indice + 1;
                outputtxt(indice + ".INVE");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            INV = false;
        }

        mais_fatores();
    }

    private void op_un() {
        if (verificaSimbolo("-")) {
            System.out.println("op_UN   " + simbolo.getValor());
            INV = true;
            obtemSimbolo();

        }

    }

    private void op_mul() {
        if (verificaSimbolo("*") || verificaSimbolo("/")) {
            System.out.println("op_MUl " + simbolo.getValor());

            if (verificaSimbolo("*")) {
                mult = 1;
            }
            if (verificaSimbolo("/")) {
                mult = 2;
            }

            obtemSimbolo();
        }
    }

    private void op_ad() {
        if (verificaSimbolo("+")) {
            System.out.println("op_AD   " + simbolo.getValor());

            SOMA = 1;

        }
        if (verificaSimbolo("-")) {

            SOMA = 2;

        }
        obtemSimbolo();
    }

    private void fator() {
        System.out.println("fator " + simbolo.getValor());

        if (simbolo.getTipo() == Token.INDENTIFICADOR) {
            try {

                VAR=simbolo.getValor();
                if (escopo==1) {
                    temp = ARRAY_MAIN.lastIndexOf(VAR);
                    indice=indice+1;
                    outputtxt(indice + ".CRVL " + temp);
                    
                    
                    pilhaD.add((double) temp); 
                    S=S+1;
                   
                    
                }
                if (escopo==0) {
                    temp = ARRAY_MAIN.indexOf(VAR);
                    indice=indice+1;
                    outputtxt(indice + ".CRVL "+ temp);
                    pilhaD.add((double) temp); 
                    S=S+1;
                  
                    
                }








            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            obtemSimbolo();
            /*
             * if (tabelaSimbolo.containsKey(simbolo.getValor())){
             * 
             * }else if(!verificaSimbolo("then")){
             * throw new RuntimeException("Erro sintatico não declarado " +
             * simbolo.getValor());
             * }
             */
        }
        if (simbolo.getTipo() == Token.REAL) {
            try {
                // pilhaD.add(Double.parseDouble(simbolo.getValor()));
                indice = indice + 1;
                outputtxt(indice + ".CRCT " + simbolo.getValor());

                

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            obtemSimbolo();
        }
        if (simbolo.getTipo() == Token.INTEIRO) {
            try {

                indice = indice + 1;
                outputtxt(indice + ".CRCT " + simbolo.getValor());

                pilhaD.add(Double.parseDouble(simbolo.getValor())); 
                S=S+1;


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            obtemSimbolo();
        }
        if (verificaSimbolo("(")) {
            obtemSimbolo();
            expressao();

            if (verificaSimbolo(")")) {
                obtemSimbolo();

            } else {
                throw new RuntimeException("Erro faltou )" + simbolo.getValor());
            }
        } else {

            System.out.println("saida fator " + simbolo.getValor());
        }

    }

    private void mais_fatores() {
        if (verificaSimbolo("*") || verificaSimbolo("/")) {
            System.out.println("mais fatores: " + simbolo.getValor());
            op_mul();
            fator();
            if (mult == 1) {
                try {
                    indice = indice + 1;
                    outputtxt(indice + ".MULT ");

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (mult == 2) {
                try {
                    indice = indice + 1;
                    outputtxt(indice + ".DIVI ");
                    
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            mult = 0;

            mais_fatores();

        }
        if (SOMA == 1) {
            try {
                indice = indice + 1;
                outputtxt(indice + ".SOMA ");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            SOMA = 0;

        }
        if (SOMA == 2) {
            try {
                indice = indice + 1;
                outputtxt(indice + ".SUBT ");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            SOMA = 0;

        }
    }

    private void Outros_termos() {
        if (verificaSimbolo("+") || verificaSimbolo("-")) {
            System.out.println("outros termos " + simbolo.getValor());
            op_ad();
            Termo();
            Outros_termos();
        }

    }

    private void condicao() {
        System.out.println("entrada condicao  " + simbolo.getValor());
        expressao();
        relacao();
        expressao();
        if (relacionais > 0){
            try {
                indice = indice + 1;
                if (relacionais == 1){
                outputtxt(indice + ".CPME ");
                }
                if (relacionais == 2){
                    outputtxt(indice + ".CPMA ");
                }
                if (relacionais == 3){
                    outputtxt(indice + ".CDES ");
                }
                if (relacionais == 4){
                    outputtxt(indice + ".CPMI ");
                }
                if (relacionais == 5){
                    outputtxt(indice + ".CMAI ");
                }
                if (relacionais == 6){
                    outputtxt(indice + ".CPIG ");
                }
                
                relacionais = 0;
                
                indice = indice + 1;
                desvioIF = indice;  //DSVF
                //outputtxt(indice+"---DSVF AQUI----");
                
                




               
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                
            }
        }
    }

    private void relacao() {
        System.out.println("relação  " + simbolo.getValor());
        if (verificaSimbolo("<") || verificaSimbolo(">") || verificaSimbolo("<>")) {
            if (verificaSimbolo("<")) {

                relacionais = 1;

            }
            if (verificaSimbolo(">")) {
                relacionais = 2;

            }
            if (verificaSimbolo("<>")) {
                relacionais = 3;

            }
            obtemSimbolo();

        } else if (verificaSimbolo("=") || verificaSimbolo(">=") || verificaSimbolo("<=")) {
            if (verificaSimbolo(">=")) {
                relacionais = 4;

            }
            if (verificaSimbolo("<=")) {
                relacionais = 5;

            }
            if (verificaSimbolo("=")) {
                relacionais = 6;

            }
            obtemSimbolo();
        } else {
            throw new RuntimeException("Esperado relacional antes de " + simbolo.getValor());
        }

    }

    private void pfalsa() {
        System.out.println("pfalsa: " + simbolo.getValor());
        if (verificaSimbolo("else")) {
            obtemSimbolo();

            /* por algum motivo o primeiro DSVF é inserido uma linha a +
            quando dentro do prcedimento, tentie por HORAS arrumar , não consegui
            descobrir o porque então fiz essa gambiarra*/

            if (escopo==1){ 

                codigoHip.add((desvioIF-1),desvioIF+".DSVF "+(indice+1)+"\n");

            }else{
                codigoHip.add(desvioIF,desvioIF+".DSVF "+(indice+1)+"\n");
            }
            

           

            System.out.println("simbolo dentro do else: " + simbolo.getValor());
            Comandos();
        }

    }

    public void Analisador() {
        obtemSimbolo();
        Prog();
        if (simbolo == null) {
            System.out.println("Tudo Certo!");
            try {
               indice = indice + 1;
               //outputtxt(indice+"ultimo index");
               End=true;
               outputtxt(indice+".PARA");
               
               
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            throw new RuntimeException("Erro sintático esperado fim de cadeia encontrado: " + simbolo.getValor());
        }
    }

}
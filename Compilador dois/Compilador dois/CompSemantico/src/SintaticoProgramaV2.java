import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SintaticoProgramaV2 {

    // variaveis do codigo intermediario

    Stack<String> codigoHip = new Stack<String>();
    ArrayList<Double> pilhaD = new ArrayList<Double>();
    ArrayList<String> ARRAY_MAIN = new ArrayList<String>();
    ArrayList<String> ARRAY_PROCEDURE = new ArrayList<String>();
    int i; // index de codigoHIP
    int desvioIF; // desvio depois do IF
    int E; // fim do procedimento e desalocação das variaveis
    int N; // NUMERO DE PARAMETROS
    int S = 0; // topo pilhaD
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

        if (Code == "PARA") {
            for (int i = 0; i < codigoHip.size(); i++) {
                pw.write(codigoHip.get(i));
                // System.out.println(this.codigoHip.get(i));
            }
            for (int i = 0; i < pilhaD.size(); i++) {
                pwv.write(ARRAY_MAIN.toString());
                // System.out.println(this.codigoHip.get(i));
            }

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
                outputtxt(i + ".INPP");
                S = S - 1;
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
            i = i + 1;
            codigoHip.add(prim_instru, prim_instru + ".DSVI " + i);
            try {
                outputtxt("begin");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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

            try {

                i = i + 1;
                prim_instru = i;
                outputtxt(""); // inicio do procedure vai aqui
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

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
            escopo = 0;
            obtemSimbolo(); // fim procedimento
            i = i + 1;
            E = i + N + 1;
            try {
                outputtxt(i + ".PUSHER " + E);
                outputtxt(i + ".CHPR " + prim_instru);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

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
                argumentos();
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
                        i = i + 1;
                        outputtxt(i + ".ALME " + simbolo.getValor());
                        ARRAY_MAIN.add(simbolo.getValor());
                        S = S + 1;
                        // pilhaD.add(simbolo.getValor()); ADICIONAR VARIAVEL

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
                        i = i + 1;
                        outputtxt(i + ".ALME2 " + simbolo.getValor());
                        ARRAY_PROCEDURE.add(simbolo.getValor());

                        if (escopo == 2) {
                            N = N + 1;
                        }
                        S = S + 1;
                        // pilhaD.push(simbolo.getValor()); ADICIONAR VARIVAVEL

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
                        i = i + 1;
                        outputtxt(i + ".LEIT " + simbolo.getValor());
                        S = S + 1;
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
                        temp = ARRAY_PROCEDURE.indexOf(VAR);
                
                        
                        i = i + 1;
                        outputtxt(i + ".CLVR " + VAR + " POSICAO " + temp);
                        i = i + 1;
                        outputtxt(i + ".IMPR " + simbolo.getValor());
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
            if (relacionais > 0)
                try {
                    i = i + 1;
                    outputtxt(i + ".COMPARACAO ");
                    relacionais = 0;
                    i = i + 1;
                    desvioIF = i;
                    outputtxt(""); // inicio do if vai aqui

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    
                }

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
                    i = i + 1;

                    temp = ARRAY_PROCEDURE.indexOf(VAR);
                    if (temp != -1) {
                        outputtxt(i + ".ARMZlocal " + VAR + " POSICAO " + temp);
                        S = S - 1;

                    }

                    if (temp == -1) {
                        temp = ARRAY_MAIN.indexOf(VAR);
                        outputtxt(i + ".ARMZ " + VAR + " POSICAO " + temp);
                        S = S - 1;
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
                i = i + 1;
                outputtxt(i + ".INV");
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

                i = i + 1;
                // pilhaD.add(Double.parseDouble(simbolo.getValor()));
                // S = S + 1;
                outputtxt(i + ".CRVL " + simbolo.getValor());

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
                i = i + 1;
                outputtxt(i + ".CRCT " + simbolo.getValor());

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            obtemSimbolo();
        }
        if (simbolo.getTipo() == Token.INTEIRO) {
            try {

                // pilhaD.add(simbolo.getValor()); adicionar endereço da variavel
                i = i + 1;
                outputtxt(i + ".CRCT " + simbolo.getValor());
                S = S + 1;
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
                    i = i + 1;
                    outputtxt(i + ".MULT ");// String.valueOf(PILHAREAL.get(0))
                    /*
                     * temp = (pilhaD.get(S-1)*pilhaD.get(S));
                     * pilhaD.remove(S-1);
                     * pilhaD.remove(S);
                     * pilhaD.add(temp);
                     * S = S - 1;
                     */
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (mult == 2) {
                try {
                    i = i + 1;
                    outputtxt(i + ".DIV ");// String.valueOf(PILHAREAL.get(0))
                    /*
                     * temp = (pilhaD.get(S-1)/pilhaD.get(S));
                     * pilhaD.remove(S-1);
                     * pilhaD.remove(S);
                     * pilhaD.add(temp);
                     * S = S - 1;
                     */
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
                i = i + 1;
                outputtxt(i + ".SOMA ");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            SOMA = 0;

        }
        if (SOMA == 2) {
            try {
                i = i + 1;
                outputtxt(i + ".SUB ");
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
            obtemSimbolo();
        } else {
            throw new RuntimeException("Esperado relacional antes de " + simbolo.getValor());
        }

    }

    private void pfalsa() {
        System.out.println("pfalsa: " + simbolo.getValor());
        if (verificaSimbolo("else")) {
            obtemSimbolo();
            if(desvioIF>0){
                i = i + 1;
                codigoHip.add(desvioIF, desvioIF + ".DSVF " + (i+1));
                
            }
            desvioIF=0;


            try {
                outputtxt("ELSE");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
                i = i + 1;
                outputtxt("PARA");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            throw new RuntimeException("Erro sintático esperado fim de cadeia encontrado: " + simbolo.getValor());
        }
    }

}
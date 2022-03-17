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
    ArrayList<Double>pilhaD = new ArrayList<Double>();

    int i; // index de codigoHIP
    int P; // desvio depois do procedure
    int E; // fim do procedimento e desalocação das variaveis
    int N; // NUMERO DE PARAMETROS
    int S = 0; // topo pilhaD
    Double temp; //manipular o array PIlhaD
    int escopo = 0; // escopo 0 =  main, escopo 1 = procedimento
    int end_rel; // posição da variavel na pilha
    int prim_instru; // onde começa o procedimento

    String isCod = ""; // indica se está acontecendo um comando de leitura
    int tempAloc = 0;
    String rel = ""; // operador relacional atual
    int procStartLine[] = new int[256];
    int numProcDec = 0;

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
                pwv.write(pilhaD.get(i).toString());
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
            escopo=1;

            try {

                i = i + 1;
                prim_instru = i;
                outputtxt("");     //inicio do procedure vai aqui
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
        escopo=2;
        System.out.println("simbolo list_par>>" + simbolo.getValor());
        Tipovari();
        if (verificaSimbolo(":")) {
            obtemSimbolo();
            Variaveis();
            mais_par();
        }
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
        obtemSimbolo();
        DC_loc();
        obtemSimbolo();
        if ((verificaSimbolo("begin"))) {
            obtemSimbolo();
            Comandos();
        }
        if (verificaSimbolo("end")) {
            escopo=0;
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
    }

    private void DC_loc() {
        System.out.println("simbolo DC_loc>>" + simbolo.getValor());
        if (verificaSimbolo("begin")) {

        } else {
            DC_V();
            MaisDC_loc();
        }
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
                        S = S + 1;
                       // pilhaD.add(simbolo.getValor());  ADICIONAR VARIAVEL
                        end_rel = i;
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
                        if(escopo==2){
                            N=N+1;
                        }
                        S = S + 1;
                        //pilhaD.push(simbolo.getValor()); ADICIONAR VARIVAVEL
                        end_rel = S;
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
                        i=i+1;
                        outputtxt(i+".LEIT "+simbolo.getValor());
                        S=S+1;
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
                        i=i+1;
                        outputtxt(i+".IMPR "+simbolo.getValor());
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
        }

        if (verificaSimbolo("$")) {
            obtemSimbolo();
        }

        if (simbolo.getTipo() == Token.INDENTIFICADOR) {

            if (verificaSimbolo("else") || verificaSimbolo("end")) {
                // do nothing
            } else {

                if (!tabelaProcedimento.containsKey(simbolo.getValor())) {
                    try {
                        i = i + 1;
                        int temp = codigoHip.indexOf(simbolo.getValor().toString());

                        outputtxt(i + ".ARMZ " + simbolo.getValor() + " POSICAO " + temp);
                        S = S - 1;

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                

                resto_indent();
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
        mais_fatores();
    }

    private void op_un() {
        if (verificaSimbolo("-")) {
            System.out.println("op_UN   " + simbolo.getValor());
            obtemSimbolo();
            try {
                i = i + 1;
                outputtxt(i + ".SUBT ");// String.valueOf(PILHAREAL.get(0))
               /* temp = (pilhaD.get(S-1)-pilhaD.get(S));
                pilhaD.remove(S-1);
                pilhaD.remove(S);
                pilhaD.add(temp);*/
                S = S - 1;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    private void op_mul() {
        if (verificaSimbolo("*") || verificaSimbolo("/")) {
            System.out.println("op_MUl " + simbolo.getValor());
            if (verificaSimbolo("*")) {
                try {
                    i = i + 1;
                    outputtxt(i + ".MULT ");// String.valueOf(PILHAREAL.get(0))
                   /* temp = (pilhaD.get(S-1)*pilhaD.get(S));
                    pilhaD.remove(S-1);
                    pilhaD.remove(S);
                    pilhaD.add(temp);
                    S = S - 1;*/
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (verificaSimbolo("/")) {
                try {
                    i = i + 1;
                    outputtxt(i + ".DIV ");// String.valueOf(PILHAREAL.get(0))
                   /* temp = (pilhaD.get(S-1)/pilhaD.get(S));
                    pilhaD.remove(S-1);
                    pilhaD.remove(S);
                    pilhaD.add(temp);
                    S = S - 1;*/
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            obtemSimbolo();
        }
    }

    private void op_ad() {
        if (verificaSimbolo("+")) {
            System.out.println("op_AD   " + simbolo.getValor());
            obtemSimbolo();
            try {
                i = i + 1;
                outputtxt(i + ".SOMA ");// String.valueOf(PILHAREAL.get(0))
                /*temp = (pilhaD.get(S-1)+pilhaD.get(S));
                pilhaD.remove(S-1);
                pilhaD.remove(S);
                pilhaD.add(temp);*/
                S = S - 1;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    private void fator() {
        System.out.println("fator " + simbolo.getValor());

        if (simbolo.getTipo() == Token.INDENTIFICADOR) {
            try {
                
                i=i+1;
                //pilhaD.add(Double.parseDouble(simbolo.getValor()));
                //S = S + 1;
                outputtxt(i + ".CRCT " + simbolo.getValor());

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
                //pilhaD.add(Double.parseDouble(simbolo.getValor()));
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

                //pilhaD.add(simbolo.getValor()); adicionar endereço da variavel
                i = i + 1;
                outputtxt(i + ".CRVL " + simbolo.getValor());
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
            mais_fatores();
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
                try {
                    i = i + 1;
                    outputtxt(i + ".CPME ");
                    S = S - 1;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            if (verificaSimbolo(">")) {
                try {
                    i = i + 1;
                    outputtxt(i + ".CPMA ");
                    S = S - 1;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            if (verificaSimbolo("<>")) {
                try {
                    i = i + 1;
                    outputtxt(i + ".CDES ");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            obtemSimbolo();

        } else if (verificaSimbolo("=") || verificaSimbolo(">=") || verificaSimbolo("<=")) {
            if (verificaSimbolo(">=")) {
                try {
                    i = i + 1;
                    outputtxt(i + ".CMAI ");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

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
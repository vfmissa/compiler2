import java.util.HashMap;
import java.util.Map;

public class SintaticoPrograma {

    private Lexico lexico;
    private Token simbolo;
    private Map<String, Simbolo> tabelaSimbolo = new HashMap<>();
    private int tipo;

    public SintaticoPrograma(String arq) {
        lexico = new Lexico(arq);
    }

    private void obtemSimbolo() {
        simbolo = lexico.proxToken();
    }

    private boolean verificaSimbolo(String termo) {
        return (simbolo != null && simbolo.getValor().equals(termo));
    }




    private void Prog(){
        System.out.println("prog");
        System.out.println("simbolo antes de prog>>"+simbolo.getValor());
        
            if(!verificaSimbolo("program")){
                throw new RuntimeException("Erro sintático Programa não declarado: " + simbolo.getValor());
            }
            else{
                obtemSimbolo();
                System.out.println("simbolo antes de corpo>>"+simbolo.getValor());
                if(simbolo.getTipo()!=Token.INDENTIFICADOR){
                    throw new RuntimeException("Erro esperado indetificador antes de " + simbolo.getValor());
                }
                else{
                    Corpo();
                    if(verificaSimbolo(".")){
                        obtemSimbolo();
                    }
                }
                
            }
        
    }

    private void Corpo() {
        System.out.println("simbolo corpo>>"+simbolo.getValor());
        obtemSimbolo();
        DC();
        if((verificaSimbolo("begin"))){
            obtemSimbolo();
            Comandos();
        }
        if(verificaSimbolo("end")){
            obtemSimbolo();
            
        }


    }

    
    private void DC() {
        System.out.println("simbolo DC>>"+simbolo.getValor());
        if(verificaSimbolo("begin")){

        }
        else{
            DC_V();
            MaisDC();
        }
    }
    

    private void DC_V() {
        System.out.println("simbolo DCV>>"+simbolo.getValor());
        Tipovari();
        if(verificaSimbolo(":")){
            obtemSimbolo();
            Variaveis();
        }
        
    }


    private void MaisDC() {
        System.out.println("simbolo +DC>>"+simbolo.getValor());
        if(verificaSimbolo(";")){
            obtemSimbolo();
            DC();
        }
        else{
            throw new RuntimeException("Erro faltou ; depois de : " + simbolo.getValor());
        }
    }


    private void Variaveis() {
        System.out.println("simbolo variaveis>>"+simbolo.getValor());
        if(simbolo.getTipo() != Token.INDENTIFICADOR ){
            throw new RuntimeException("Erro sintático esperado indentificador antes de " + simbolo.getValor());
        }else{
            if (tabelaSimbolo.containsKey(simbolo.getValor())) {
                throw new RuntimeException("Erro semântico identificador já encontrado: " + simbolo.getValor());
            } else {
                tabelaSimbolo.put(simbolo.getValor(), new Simbolo(this.tipo, simbolo.getValor()));
                obtemSimbolo();
                Maisvari();
            }
            
        }
    }

    
    private void Maisvari() {
        System.out.println("simbolo maisvari>>"+simbolo.getValor());
        if(verificaSimbolo(",")){
            obtemSimbolo();
            Variaveis();
        }
    }

    private void Tipovari() {
        System.out.println("simbolo tipovari>>"+simbolo.getValor());
        if(!verificaSimbolo("real") && !verificaSimbolo("integer")){
            throw new RuntimeException("Erro sintático esperado real /integer" + simbolo.getValor());
        }
        obtemSimbolo();
    }

    void Comandos(){
        Comando();
        Mais_Comandos();
    }
    

    

    private void Comando() {
        System.out.println("simbolo dentro de comando >> "+simbolo.getValor());
        if(verificaSimbolo("read")){
            obtemSimbolo();
            if(verificaSimbolo("(")){
                obtemSimbolo();
                if (!tabelaSimbolo.containsKey(simbolo.getValor())) {
                    throw new RuntimeException("Erro semântico identificador não encontrado: " + simbolo.getValor());
                }
                else{
                    obtemSimbolo();
                    if(verificaSimbolo(")")){
                        obtemSimbolo();
                    }else{
                        throw new RuntimeException("Erro semântico esperado ) " + simbolo.getValor());
                    }
                }
            }
        }
        if(verificaSimbolo("write")){
            obtemSimbolo();
            if(verificaSimbolo("(")){
                obtemSimbolo();
                if (!tabelaSimbolo.containsKey(simbolo.getValor())) {
                    throw new RuntimeException("Erro semântico identificador não encontrado: " + simbolo.getValor());
                }
                else{
                    System.out.println("escreveu >> "+simbolo.getValor());
                    obtemSimbolo();
                    if(verificaSimbolo(")")){
                        obtemSimbolo();
                    }else{
                        throw new RuntimeException("Erro semântico esperado ) " + simbolo.getValor());
                    }
                }
            }
        }
        if (tabelaSimbolo.containsKey(simbolo.getValor())) {
            obtemSimbolo();
            if(!verificaSimbolo(":=")){
                throw new RuntimeException("Erro sintático esperado := encontrado: " + simbolo.getValor());
            }else{
                obtemSimbolo();
                expressao();
            }
        }
        if(verificaSimbolo("if")){
            obtemSimbolo();
            condicao();
            
        }
        if(verificaSimbolo("then")){
            obtemSimbolo();
            System.out.println("comando do if >> "+simbolo.getValor());
            Comandos();
            pfalsa();
                       
        }
            
        
        if(verificaSimbolo("$")){
            obtemSimbolo();
        }


    }

    private void Mais_Comandos() {
        System.out.println("simbolo +comandos>>"+simbolo.getValor());
        if(verificaSimbolo(";")){
            obtemSimbolo();
            Comandos();
        }
    
    }


    private void expressao() {
        System.out.println("entrada expressão>> "+simbolo.getValor());
        Termo();
        Outros_termos();


    }
  



    private void Termo() {
        System.out.println("termo >> "+simbolo.getValor());
        op_un();
        fator();
        mais_fatores();
    }

    private void op_un(){
        if(verificaSimbolo("-")){
            System.out.println("op_UN   "+simbolo.getValor());
            obtemSimbolo();
        }
        
    }
    private void op_mul(){
        if(verificaSimbolo("*")||verificaSimbolo("/")){
            System.out.println("op_MUl "+simbolo.getValor());
            obtemSimbolo();
        }
    }

    private void op_ad(){
        if(verificaSimbolo("+")){
            System.out.println("op_AD   "+simbolo.getValor());
            obtemSimbolo();
        }

    }

    private void fator(){
        System.out.println("fator "+simbolo.getValor());
        
        if(simbolo.getTipo()==Token.INDENTIFICADOR){
            if (tabelaSimbolo.containsKey(simbolo.getValor())){
                obtemSimbolo();
            }else if(!verificaSimbolo("then")){
                throw new RuntimeException("Erro sintatico não declarado " + simbolo.getValor());
            }
        } if(simbolo.getTipo()==Token.REAL){
            obtemSimbolo();
        } if(simbolo.getTipo()==Token.INTEIRO){
            obtemSimbolo();
        }
        if(verificaSimbolo("(")){
            obtemSimbolo();
            expressao();
            
            if(verificaSimbolo(")")){
                obtemSimbolo();
                
            }else{
                throw new RuntimeException("Erro faltou )" + simbolo.getValor());
            }
        }
        else{
            System.out.println("saida fator "+simbolo.getValor());
        }
        
    }


    private void mais_fatores(){
        if(verificaSimbolo("*") || verificaSimbolo("/")){
            System.out.println("mais fatores: "+simbolo.getValor());
            op_mul();
            fator();
            mais_fatores();
        }
    }

    private void Outros_termos() {
        if(verificaSimbolo("+") || verificaSimbolo("-")){
            System.out.println("outros termos "+simbolo.getValor());
            op_ad();
            Termo();
            Outros_termos();
        }
        
    }




    private void condicao(){
        System.out.println("entrada condicao  "+simbolo.getValor());
        expressao();
        relacao();
        expressao();
    }
 
    private void relacao() {
        System.out.println("relação  "+simbolo.getValor());
        if(verificaSimbolo("<")||verificaSimbolo(">")||verificaSimbolo("<>")){
            obtemSimbolo();
        }else if(verificaSimbolo("=")||verificaSimbolo(">=")||verificaSimbolo("<=")){
            obtemSimbolo();
        }else{
            throw new RuntimeException("Esperado relacional antes de " + simbolo.getValor());
        }

    }

    private void pfalsa(){
        System.out.println("pfalsa: "+simbolo.getValor());
        if(verificaSimbolo("else")){
            obtemSimbolo();
            System.out.println("simbolo dentro do else: "+simbolo.getValor());
            Comandos();
        }
 
    }
   
    public void Analisador(){
        obtemSimbolo();
        Prog();
        if (simbolo == null) {
        System.out.println("Tudo Certo!");
        } else {
        throw new RuntimeException("Erro sintático esperado fim de cadeia encontrado: " + simbolo.getValor());
        }
    }

  
}
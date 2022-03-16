



public class Token {


    public static final int INDENTIFICADOR=1;
    public static final int INTEIRO=2;
    public static final int REAL=3;
    public static final int SIMBOLO=4;
    public static final int OPERADOR=5;
    

    private String valor;
    private int tipo;
    
    public Token(int tipo, String valor){
        this.valor = valor;
        this.tipo = tipo;
    }

    public String getValor(){
        return this.valor;

    }
    public void SetValor(String valor) {
        this.valor = valor;
        
    }

    public int getTipo(){
        return this.tipo;

    }
    public void SetTipo(int tipo) {
        this.tipo = tipo;
        
    }


    
}

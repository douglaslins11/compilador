public class Simbolo {

    private Token token;
    private int blocoDeclaracao;
    private Enum tipo;
    private String lexema;

    public Simbolo(Token token, int blocoDeclaracao, Enum tipo, String lexema) {
        this.token = token;
        this.blocoDeclaracao = blocoDeclaracao;
        this.tipo = tipo;
        this.lexema = lexema;
    }

    public Simbolo (){

    }

    public Token getToken() {
        return this.token;
    }

    public int getBlocoDeclaracao() {
        return this.blocoDeclaracao;
    }

    public Enum getTipo() {
        return this.tipo;
    }

    public void setTipo(Enum tipo){
        this.tipo = tipo;
    }

    public String getLexema(){
        return this.lexema;
    }

    public void setLexema(String lexema){
        this.lexema = lexema;
    }

    public static boolean compativeis(Simbolo tipoRecebe, Simbolo tipoAtribui) {
        if (tipoRecebe.getTipo().equals(Valores.CHAR) && (!tipoAtribui.getTipo().equals(Valores.CHAR) && (!tipoAtribui.getTipo().equals(Valores.TIPOCHAR)))) {
            return false;
        } else if (tipoRecebe.getTipo().equals(Valores.INT) && (!tipoAtribui.getTipo().equals(Valores.INT) && !tipoAtribui.getTipo().equals(Valores.TIPOINT))) {
            return false;
        } else if (tipoRecebe.getTipo().equals(Valores.FLOAT) && (tipoAtribui.getTipo().equals(Valores.CHAR) || (tipoAtribui.getTipo().equals(Valores.TIPOCHAR)))) {
            return false;
        }
        else{
            return true;
        }
    }

    public static boolean compativeisExpRel (Simbolo operador1, Simbolo operador2){
        if((operador1.getTipo().equals(Valores.CHAR) ||  operador1.getTipo().equals(Valores.TIPOCHAR)) && (!operador2.getTipo().equals(Valores.CHAR) && (!operador2.getTipo().equals(Valores.TIPOCHAR)))){
            return false;
        }
        else if((!operador1.getTipo().equals(Valores.CHAR) && !operador1.getTipo().equals(Valores.TIPOCHAR)) && (operador2.getTipo().equals(Valores.CHAR) ||  operador2.getTipo().equals(Valores.TIPOCHAR))){
            return false;
        }
        else{
            return true;
        }
    }
}
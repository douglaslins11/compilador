public class Token {
    private Valores gramatica;
    private String lexema;

    public Token(Valores gramatica, String lexema) {
        this.gramatica = gramatica;
        this.lexema = lexema;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public Enum getGramatica() {
        return gramatica;
    }

    public void setGramatica(Valores gramatica) {
        this.gramatica = gramatica;
    }
}



import java.io.FileReader;
import java.io.IOException;

public class Scanner {

    public int linha = 1;
    public int coluna = 0;
    private char caractere;
    private String lexema;
    private FileReader arquivo;
    private final char EOF = '\uffff';


    public Scanner (FileReader arquivo) throws Exception {
        this.arquivo = arquivo;
        this.pegarProxCarac();
        //this.Scan();
    }

    private void pegarProxCarac() throws IOException {
        caractere = (char) arquivo.read();
        validador();
    }

    private void validador (){
        if(caractere == '\n') {
            coluna = 0;
            linha = linha + 1;
        }
        else if(caractere == '\t'){//tab
            coluna = coluna + 4;
        }
        else{
            coluna = coluna + 1;
        }
    }

    public Valores verificaPalavraReservada (String lexema){
        switch(lexema){
            case "if":
                return Valores.IF;
                //break;
            case "else":
                return Valores.ELSE;
                //break;
            case "while":
                return Valores.WHILE;
                //break;
            case "do":
                return Valores.DO;
                //break;
            case "for":
                return Valores.FOR;
                //break;
            case "int":
                return Valores.INT;
                //break;
            case "float":
                return Valores.FLOAT;
                //break;
            case "char":
                return Valores.CHAR;
                //break;
            case "main":
                return Valores.MAIN;
                //break;
            default:
                return Valores.ID;
        }
    }

    public Token Scan () throws Exception {

            lexema="";

            while(caractere == '\r' || caractere == '\t' || caractere == ' ' || caractere == '\n'){
                pegarProxCarac();
            }

            if(caractere == EOF){
                return new Token(Valores.ENDOFILE, "EndOfFile");
            }

            else if(Character.isLetter(caractere) || caractere == '_'){ //verifica se começa com letra
                lexema+= caractere;
                pegarProxCarac();
                while(Character.isLetterOrDigit(caractere )|| caractere == '_'){
                    lexema+= caractere;
                    pegarProxCarac();
                }
                return new Token(verificaPalavraReservada(lexema), lexema);
            }

            else if(Character.isDigit(caractere)){// caso entre, começa com um digito e é verificado se é inteiro ou float
                lexema += caractere;
                pegarProxCarac();
                while(Character.isDigit(caractere)){
                    lexema += caractere;
                    pegarProxCarac();
                }
                if(caractere == '.'){
                    lexema+= caractere;
                    pegarProxCarac();

                    if(Character.isDigit(caractere)){
                        lexema += caractere;
                        pegarProxCarac();
                        while(Character.isDigit(caractere)){
                            lexema+= caractere;
                            pegarProxCarac();
                        }
                        return new Token(Valores.TIPOFLOAT, lexema);
                    }
                    else {
                        throw new Exception("ERRO na linha "+linha+", coluna "+coluna+", ultimo token lido "+lexema+": float mal formatado");
                    }
                }
                return new Token(Valores.TIPOINT, lexema);
            }

            else if(caractere == '.'){
                lexema+= caractere;
                pegarProxCarac();
                if(Character.isDigit(caractere)){
                    lexema+= caractere;
                    pegarProxCarac();
                    while(Character.isDigit(caractere)){
                        lexema+= caractere;
                        pegarProxCarac();
                    }
                    return new Token(Valores.TIPOFLOAT, lexema);
                }
                else{
                    throw new Exception("ERRO na linha "+linha+", coluna "+coluna+", ultimo token lido "+lexema+": float mal formatado");
                }
            }

            else if(caractere == '='){
                lexema+= caractere;
                pegarProxCarac();
                if(caractere == '='){
                    lexema+= caractere;
                    pegarProxCarac();
                    return new Token(Valores.IGUALDADE, lexema);
                }
                else{
                    return new Token(Valores.ATRIBUICAO, lexema);
                }
            }

            else if(caractere == '>'){
                lexema+= caractere;
                pegarProxCarac();
                if(caractere == '='){
                    lexema+= caractere;
                    pegarProxCarac();
                    return new Token(Valores.MAIORIGUAL, lexema);
                }
                else{
                    return new Token(Valores.MAIOR, lexema);
                }
            }

            else if(caractere == '<'){
                lexema+= caractere;
                pegarProxCarac();
                if(caractere == '='){
                    lexema+= caractere;
                    pegarProxCarac();
                    return new Token(Valores.MENORIGUAL, lexema);
                }
                else{
                    return new Token(Valores.MENOR, lexema);
                }
            }

            else if(caractere == '('){
                lexema+= caractere;
                pegarProxCarac();
                return new Token(Valores.ABREPARENTESES, lexema);
            }

            else if(caractere == ')'){
                lexema+= caractere;
                pegarProxCarac();
                return new Token(Valores.FECHAPARENTESES, lexema);
            }

            else if(caractere == '{'){
                lexema+= caractere;
                pegarProxCarac();
                return new Token(Valores.ABRECHAVES, lexema);
            }

            else if(caractere == '}'){
                lexema+= caractere;
                pegarProxCarac();
                return new Token(Valores.FECHACHAVES, lexema);
            }

            else if(caractere == '!'){
                lexema+= caractere;
                pegarProxCarac();
                if(caractere == '='){
                    lexema+= caractere;
                    pegarProxCarac();
                    return new Token (Valores.DIFERENTE, lexema);
                }
                else{
                    throw new Exception("ERRO na linha "+linha+", coluna "+coluna+", ultimo token lido "+lexema+": Erro da exclamação");
                }
            }

            else if(caractere == '+'){
                lexema+= caractere;
                pegarProxCarac();
                return new Token (Valores.SOMA, lexema);
            }

            else if(caractere == '-'){
                lexema+= caractere;
                pegarProxCarac();
                return new Token (Valores.SUBTRACAO, lexema);
            }

            else if(caractere == '*'){
                lexema+= caractere;
                pegarProxCarac();
                return new Token (Valores.MULTIPLICACAO, lexema);
            }

            else if(caractere == ';'){
                lexema+= caractere;
                pegarProxCarac();
                return new Token (Valores.PONTOVIRGULA, lexema);
            }

            else if(caractere == ','){
                lexema+= caractere;
                pegarProxCarac();
                return new Token (Valores.VIRGULA, lexema);
            }

            else if(caractere == '/'){
                lexema+=caractere;
                pegarProxCarac();
                if(caractere == '/'){//comentario de uma linha
                    while(caractere != '\n'){//roda até pular pra linha de baixo
                        lexema+=caractere;
                        pegarProxCarac();
                    }
                }
                else if(caractere == '*'){
                    lexema+=caractere;
                    pegarProxCarac();
                    while(true){
                        if(caractere == '*'){
                            pegarProxCarac();
                            while(caractere == '*'){
                                pegarProxCarac();
                            }
                            if(caractere == '/'){
                                pegarProxCarac();
                                break;
                            }
                        }
                        else if(caractere == EOF){
                            throw new Exception("ERRO na linha "+linha+", coluna "+coluna+", ultimo token lido "+lexema+": Bloco de comentario nao fechado");
                        }
                        pegarProxCarac();
                    }
                }
                else{
                    return new Token(Valores.DIVISAO, lexema);
                }

            }

            else if(caractere == 39){//39 é o valor correspondente a aspas simples na tabela ascii
                lexema+= caractere;
                pegarProxCarac();
                if(Character.isLetterOrDigit(caractere)){
                    lexema+= caractere;
                    pegarProxCarac();
                    if(caractere == 39){
                        lexema+= caractere;
                        pegarProxCarac();
                        return new Token(Valores.TIPOCHAR, lexema);
                    }
                    else{
                        throw new Exception("ERRO na linha "+linha+", coluna "+coluna+", ultimo token lido "+lexema+": Erro de caractere mal formatado");
                    }
                }
                else{
                    throw new Exception("ERRO na linha "+linha+", coluna "+coluna+", ultimo token lido "+lexema+": Erro de caractere mal formatado");
                }
            }

            else{
                lexema+= caractere;
                throw new Exception("ERRO na linha "+linha+", coluna "+coluna+", ultimo token lido "+lexema+": Erro de caractere inválido");
            }

        return new Token(Valores.ENDOFILE, "EndOfFile");
    }
}
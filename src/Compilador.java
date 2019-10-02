import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Compilador {

    public static void main(String[] args) throws Exception {

        try{
            FileReader arq = new FileReader((args[0]));
            BufferedReader lerArq = new BufferedReader(arq);
            AnalisadorSintatico analisadorSintatico = new AnalisadorSintatico(arq);
            analisadorSintatico.Parser();
        }catch(FileNotFoundException ex){
            System.out.println("Erro: Arquivo nao encontrado");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

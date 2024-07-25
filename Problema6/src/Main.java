public class Main {
    public static void main(String[] args) {
        boolean genero;
        for (int i = 0; i < 100; i++){
            genero = i % 2 == 0;
            Pessoa pessoa = new Pessoa(i, genero);
            pessoa.start();
        }
    }
}
public class Main {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++){ // 100 Pessoas (50 homens e 50 mulheres) desejam usar o banheiro
            Pessoa pessoa = new Pessoa(i, i % 2 == 0); // Criando cada thread. Se o número da thread for par, a pessao é homem, se for impar é mulher.
            pessoa.start(); // Incializado as threads
        }
    }
}
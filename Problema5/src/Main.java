public class Main {
    public static void main(String[] args) {
        Onibus onibus = new Onibus();
        onibus.start();
        for (int i = 0; i < 100; i++){// 100 passageiros
            Passageiro passageiro = new Passageiro(i, onibus);
            passageiro.start();//inicializando as threads
        }
    }
}

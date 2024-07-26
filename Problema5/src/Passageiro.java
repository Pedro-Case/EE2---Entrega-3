public class Passageiro extends Thread{
    Onibus onibus;
    int nome;
    public Passageiro(int nome, Onibus onibus){
        this.nome = nome;
        this.onibus = onibus;
    }
    public void run(){
        System.out.println("Passageiro " + nome + " anda até a parada de onibus");
        onibus.onibus_passageiro_chegando.lock();
        System.out.println("Passageiro " + nome + " espera na parada para subir no onibus");
        onibus.passageiros_na_parada++;
        onibus.onibus_passageiro_chegando.unlock();
        boolean embarcou = false;
        while (!embarcou) {
            onibus.passageiro_embarcando.lock();
            embarcou = onibus.capacidade_onibus.tryAcquire();
            if (embarcou) {
                System.out.println("Passageiro " + nome + " subiu no onibus. Assentos restantes: " + onibus.capacidade_onibus.availablePermits());
                onibus.passageiros_na_parada--;
                onibus.passageiros_transportados++;
            }
            onibus.passageiro_embarcando.unlock();
        }
    }
}

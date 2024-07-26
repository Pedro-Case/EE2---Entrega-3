public class Passageiro extends Thread{
    Onibus onibus;
    int nome;
    public Passageiro(int nome, Onibus onibus){
        this.nome = nome;
        this.onibus = onibus;
    }
    public void run(){
        System.out.println("Passageiro " + nome + " anda até a parada de onibus");// mostra que a thread foi inicializada
        // se o onibus ja estiver na parada, o lock impedira a execução deste bloco, e a mensagem "espera na parada" nao sera impressa
        onibus.onibus_passageiro_chegando.lock(); // lock para sincronizar a chegada dos passageiros
        System.out.println("Passageiro " + nome + " espera na parada para subir no onibus");
        onibus.passageiros_na_parada++;
        onibus.onibus_passageiro_chegando.unlock();
        boolean embarcou = false;
        while (!embarcou) { // loop até que o passageiro consiga embarcar
            onibus.passageiro_embarcando.lock(); // lock para sincronizar o embarque dos passageiros
            embarcou = onibus.capacidade_onibus.tryAcquire(); // tenta adquirir um assento no ônibus
            if (embarcou) {
                System.out.println("Passageiro " + nome + " subiu no onibus. Assentos restantes: " + onibus.capacidade_onibus.availablePermits());
                onibus.passageiros_na_parada--;
                onibus.passageiros_transportados++;
            }
            onibus.passageiro_embarcando.unlock();// libera o lock para os outros passageiros
        }
    }
}
